package ua.krainov.synthesis.blockentity;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import ua.krainov.synthesis.config.SynthesisConfig;
import ua.krainov.synthesis.core.FortuneMath;
import ua.krainov.synthesis.core.SoulRules;
import ua.krainov.synthesis.item.ConverterModuleItem;
import ua.krainov.synthesis.menu.FortuneConverterMenu;
import ua.krainov.synthesis.registry.ModBlockEntities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Перетворювач удачі: блок руди + інструмент/Модуль удачі → дроп руди з удачею (як при копанні).
 * Модуль плавки одразу переплавляє дроп, Модуль шовкового дотику дає саму руду.
 * Вхід — зверху (хопер/труби) або руками; вихід — у сусідні інвентарі з усіх боків, крім верху.
 */
public class FortuneConverterBlockEntity extends BlockEntity implements MenuProvider {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_TOOL = 1;
    public static final int SLOT_FORTUNE = 2;
    public static final int SLOT_UPGRADE_A = 3;
    public static final int SLOT_UPGRADE_B = 4;
    public static final int SLOTS = 5;
    public static final int ENERGY_CAPACITY = 1_000_000;
    private static final int ENERGY_MAX_RECEIVE = 50_000;

    private static final TagKey<Item> ORES = itemTag("c", "ores");
    private static final TagKey<Item> RAW = itemTag("c", "raw_materials");
    private static final TagKey<Item> GEMS = itemTag("c", "gems");
    private static final GameProfile FAKE_PROFILE =
            new GameProfile(UUID.fromString("6a2f8b3c-4d5e-4f60-9b1c-2d3e4f5a6b7c"), "[Synthesis Fortune Converter]");

    public enum Status { IDLE, WORKING, NO_ENERGY, NO_OUTPUT, OUTPUT_FULL, INVALID }

    private final ItemStackHandler items = new ItemStackHandler(SLOTS) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return switch (slot) {
                case SLOT_INPUT -> isProcessable(stack);
                case SLOT_TOOL -> isTool(stack);
                case SLOT_FORTUNE -> stack.getItem() instanceof ConverterModuleItem m && m.getKind() == ConverterModuleItem.Kind.FORTUNE;
                default -> stack.getItem() instanceof ConverterModuleItem m && m.getKind() != ConverterModuleItem.Kind.FORTUNE;
            };
        }

        @Override
        public int getSlotLimit(int slot) {
            return slot == SLOT_INPUT ? 64 : 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (slot == SLOT_INPUT && getStackInSlot(SLOT_INPUT).isEmpty()) progress = 0;
        }
    };

    /** Для хоперів/труб: лише класти руду у вхідний слот. */
    private final IItemHandler automation = new IItemHandler() {
        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return items.getStackInSlot(SLOT_INPUT);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return items.insertItem(SLOT_INPUT, stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 64;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return items.isItemValid(SLOT_INPUT, stack);
        }
    };

    private final MachineEnergy energy = new MachineEnergy();
    private final List<ItemStack> pending = new ArrayList<>();
    private int progress;
    private Status status = Status.IDLE;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> SynthesisConfig.FORTUNE_TICKS.get();
                case 2 -> energy.getEnergyStored() & 0xFFFF;
                case 3 -> energy.getEnergyStored() >>> 16;
                case 4 -> status.ordinal();
                case 5 -> fortuneLevel();
                case 6 -> (hasSilk() ? 1 : 0) | (hasSmelting() ? 2 : 0);
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return 7;
        }
    };

    public FortuneConverterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FORTUNE_CONVERTER.get(), pos, state);
    }

    private static TagKey<Item> itemTag(String ns, String path) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ns, path));
    }

    // --- доступ ---

    public ItemStackHandler getItems() {
        return items;
    }

    public IItemHandler getAutomation() {
        return automation;
    }

    public EnergyStorage getEnergy() {
        return energy;
    }

    // --- що приймаємо ---

    private boolean isBlacklisted(ItemStack stack) {
        return SoulRules.isListed(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString(),
                SynthesisConfig.FORTUNE_BLACKLIST.get());
    }

    private boolean isOreBlock(ItemStack stack) {
        return stack.is(ORES) && stack.getItem() instanceof BlockItem;
    }

    private boolean isRawMaterial(ItemStack stack) {
        return SynthesisConfig.FORTUNE_RAW_MODE.get() && !(stack.getItem() instanceof BlockItem)
                && (stack.is(RAW) || stack.is(GEMS));
    }

    public boolean isProcessable(ItemStack stack) {
        if (stack.isEmpty() || isBlacklisted(stack)) return false;
        return isOreBlock(stack) || isRawMaterial(stack);
    }

    private boolean isTool(ItemStack stack) {
        return stack.is(ItemTags.PICKAXES) || enchantLevel(Enchantments.FORTUNE, stack) > 0
                || enchantLevel(Enchantments.SILK_TOUCH, stack) > 0;
    }

    @Nullable
    private Holder<Enchantment> enchantment(net.minecraft.resources.ResourceKey<Enchantment> key) {
        if (level == null) return null;
        return level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(key).orElse(null);
    }

    private int enchantLevel(net.minecraft.resources.ResourceKey<Enchantment> key, ItemStack stack) {
        Holder<Enchantment> holder = enchantment(key);
        return holder == null || stack.isEmpty() ? 0 : EnchantmentHelper.getItemEnchantmentLevel(holder, stack);
    }

    /** Рівень удачі: більший з інструмента й Модуля удачі. */
    public int fortuneLevel() {
        int fromTool = enchantLevel(Enchantments.FORTUNE, items.getStackInSlot(SLOT_TOOL));
        int fromModule = items.getStackInSlot(SLOT_FORTUNE).getItem() instanceof ConverterModuleItem m ? m.getLevel() : 0;
        return Math.max(fromTool, fromModule);
    }

    private boolean hasUpgrade(ConverterModuleItem.Kind kind) {
        for (int slot : new int[]{SLOT_UPGRADE_A, SLOT_UPGRADE_B}) {
            if (items.getStackInSlot(slot).getItem() instanceof ConverterModuleItem m && m.getKind() == kind) return true;
        }
        return false;
    }

    public boolean hasSilk() {
        return hasUpgrade(ConverterModuleItem.Kind.SILK_TOUCH)
                || enchantLevel(Enchantments.SILK_TOUCH, items.getStackInSlot(SLOT_TOOL)) > 0;
    }

    public boolean hasSmelting() {
        return hasUpgrade(ConverterModuleItem.Kind.SMELTING);
    }

    // --- тік ---

    public static void serverTick(Level level, BlockPos pos, BlockState state, FortuneConverterBlockEntity be) {
        if (level instanceof ServerLevel serverLevel) be.tick(serverLevel);
    }

    private void tick(ServerLevel level) {
        Status newStatus = work(level);
        if (newStatus != status) {
            status = newStatus;
            setChanged();
        }
    }

    private Status work(ServerLevel level) {
        if (!pending.isEmpty()) {
            flushPending(level);
            if (!pending.isEmpty()) return hasOutput(level) ? Status.OUTPUT_FULL : Status.NO_OUTPUT;
        }
        ItemStack input = items.getStackInSlot(SLOT_INPUT);
        if (input.isEmpty()) return Status.IDLE;
        if (!isProcessable(input)) return Status.INVALID;
        if (!hasOutput(level)) return Status.NO_OUTPUT;

        int ticks = SynthesisConfig.FORTUNE_TICKS.get();
        long cost = FortuneMath.operationCost(SynthesisConfig.FORTUNE_ENERGY_BASE.get(),
                SynthesisConfig.FORTUNE_ENERGY_PER_LEVEL.get(), SynthesisConfig.FORTUNE_ENERGY_SMELT.get(),
                fortuneLevel(), hasSmelting());
        int share = (int) (cost * (progress + 1) / ticks - cost * progress / ticks);
        if (energy.getEnergyStored() < share) return Status.NO_ENERGY;
        energy.consume(share);
        progress++;

        if (progress >= ticks) {
            progress = 0;
            ItemStack one = input.copyWithCount(1);
            List<ItemStack> drops = hasSmelting() ? smelt(level, process(level, one)) : process(level, one);
            ItemStack left = input.copy();
            left.shrink(1);
            items.setStackInSlot(SLOT_INPUT, left);
            pending.addAll(drops);
            flushPending(level);
        }
        setChanged();
        return Status.WORKING;
    }

    /** Дроп однієї руди з поточною удачею / шовковим дотиком. */
    private List<ItemStack> process(ServerLevel level, ItemStack ore) {
        int fortune = fortuneLevel();
        if (isOreBlock(ore) && ore.getItem() instanceof BlockItem blockItem) {
            ItemStack tool = items.getStackInSlot(SLOT_TOOL).isEmpty()
                    ? new ItemStack(Items.NETHERITE_PICKAXE) : items.getStackInSlot(SLOT_TOOL).copy();
            Holder<Enchantment> fortuneHolder = enchantment(Enchantments.FORTUNE);
            Holder<Enchantment> silkHolder = enchantment(Enchantments.SILK_TOUCH);
            if (fortune > 0 && fortuneHolder != null) tool.enchant(fortuneHolder, fortune);
            if (hasSilk() && silkHolder != null) tool.enchant(silkHolder, 1);

            BlockState state = blockItem.getBlock().defaultBlockState();
            LootParams.Builder params = new LootParams.Builder(level)
                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(worldPosition))
                    .withParameter(LootContextParams.TOOL, tool)
                    .withOptionalParameter(LootContextParams.THIS_ENTITY, FakePlayerFactory.get(level, FAKE_PROFILE));
            return new ArrayList<>(state.getDrops(params));
        }
        // Запасний режим: «сира» руда / самоцвіт × формула удачі
        int count = FortuneMath.oreDrops(1, fortune, level.getRandom().nextInt(fortune + 2));
        List<ItemStack> result = new ArrayList<>();
        result.add(ore.copyWithCount(count));
        return result;
    }

    /** Модуль плавки: усе, що плавиться в печі, — одразу переплавлене. */
    private List<ItemStack> smelt(ServerLevel level, List<ItemStack> drops) {
        List<ItemStack> result = new ArrayList<>();
        for (ItemStack drop : drops) {
            var recipe = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(drop), level);
            if (recipe.isPresent()) {
                ItemStack out = recipe.get().value().getResultItem(level.registryAccess());
                if (!out.isEmpty()) {
                    result.add(out.copyWithCount(out.getCount() * drop.getCount()));
                    continue;
                }
            }
            result.add(drop);
        }
        return result;
    }

    private boolean hasOutput(ServerLevel level) {
        for (Direction dir : Direction.values()) {
            if (dir == Direction.UP) continue;
            if (level.getCapability(Capabilities.ItemHandler.BLOCK, worldPosition.relative(dir), dir.getOpposite()) != null) {
                return true;
            }
        }
        return false;
    }

    private void flushPending(ServerLevel level) {
        for (Direction dir : Direction.values()) {
            if (pending.isEmpty()) return;
            if (dir == Direction.UP) continue; // зверху — вхід
            IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK,
                    worldPosition.relative(dir), dir.getOpposite());
            if (handler == null) continue;
            for (int i = 0; i < pending.size(); i++) {
                pending.set(i, ItemHandlerHelper.insertItemStacked(handler, pending.get(i), false));
            }
            pending.removeIf(ItemStack::isEmpty);
        }
    }

    // --- меню ---

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.synthesis.fortune_converter");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new FortuneConverterMenu(containerId, inventory, this, data);
    }

    // --- збереження ---

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("items", items.serializeNBT(registries));
        tag.putInt("energy", energy.getEnergyStored());
        tag.putInt("progress", progress);
        ListTag list = new ListTag();
        for (ItemStack stack : pending) {
            if (!stack.isEmpty()) list.add(stack.save(registries));
        }
        tag.put("pending", list);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("items")) items.deserializeNBT(registries, tag.getCompound("items"));
        energy.set(tag.getInt("energy"));
        progress = tag.getInt("progress");
        pending.clear();
        for (Tag t : tag.getList("pending", Tag.TAG_COMPOUND)) {
            ItemStack.parse(registries, t).ifPresent(pending::add);
        }
    }

    private class MachineEnergy extends EnergyStorage {
        MachineEnergy() {
            super(ENERGY_CAPACITY, ENERGY_MAX_RECEIVE, 0);
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int received = super.receiveEnergy(maxReceive, simulate);
            if (received > 0 && !simulate) setChanged();
            return received;
        }

        void consume(int amount) {
            energy = Math.max(0, energy - amount);
        }

        void set(int value) {
            energy = Math.max(0, Math.min(capacity, value));
        }
    }
}
