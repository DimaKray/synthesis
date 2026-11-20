package ua.krainov.synthesis.compat.botania;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import ua.krainov.synthesis.config.SynthesisConfig;
import ua.krainov.synthesis.core.FlowerRates;

import java.util.List;
import java.util.Map;

/**
 * Мультитеплиця: до 32 генеруючих квітів Ботанії як предметів. Кожна дає фіксовану ману/с (конфіг).
 * Режим «Стак»: стак однакових квітів = стільки ж квітів. Квітам «з паливом» потрібне паливо печі в окремому слоті.
 * Ману сама віддає сусіднім розповсюджувачам / басейнам / вівтарям.
 */
public class GreenhouseBlockEntity extends BlockEntity implements MenuProvider {
    public static final int FLOWER_SLOTS = 32;
    public static final int SLOT_FUEL = FLOWER_SLOTS;
    public static final int CAPACITY = 10_000_000;

    private final ItemStackHandler items = new ItemStackHandler(FLOWER_SLOTS + 1) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == SLOT_FUEL) return stack.getBurnTime(RecipeType.SMELTING) > 0;
            return rateOf(stack) != null;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    /** Для хоперів: лише паливо у слот палива. */
    private final IItemHandler automation = new IItemHandler() {
        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return items.getStackInSlot(SLOT_FUEL);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return items.insertItem(SLOT_FUEL, stack, simulate);
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
            return items.isItemValid(SLOT_FUEL, stack);
        }
    };

    private int mana;
    private double fraction;
    private boolean stackMode = true;
    private int burnTime;
    private int burnTotal;
    private int manaPerSecond;

    private static List<? extends String> cachedLines;
    private static Map<String, FlowerRates.Rate> cachedRates = Map.of();

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> mana & 0xFFFF;
                case 1 -> mana >>> 16;
                case 2 -> manaPerSecond & 0xFFFF;
                case 3 -> manaPerSecond >>> 16;
                case 4 -> stackMode ? 1 : 0;
                case 5 -> burnTotal <= 0 ? 0 : burnTime * 100 / burnTotal;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return 6;
        }
    };

    public GreenhouseBlockEntity(BlockPos pos, BlockState state) {
        super(BotaniaMachines.GREENHOUSE_BE.get(), pos, state);
    }

    @Nullable
    private static FlowerRates.Rate rateOf(ItemStack stack) {
        if (stack.isEmpty()) return null;
        List<? extends String> lines = SynthesisConfig.GREENHOUSE_FLOWERS.get();
        if (lines != cachedLines) {
            cachedRates = FlowerRates.parse(lines);
            cachedLines = lines;
        }
        String id = FlowerRates.normalize(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
        return cachedRates.get(id);
    }

    public ItemStackHandler getItems() {
        return items;
    }

    public IItemHandler getAutomation() {
        return automation;
    }

    public void toggleStackMode() {
        stackMode = !stackMode;
        setChanged();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, GreenhouseBlockEntity be) {
        be.tick(level);
    }

    private void tick(Level level) {
        double plain = 0, fuelled = 0;
        for (int i = 0; i < FLOWER_SLOTS; i++) {
            ItemStack stack = items.getStackInSlot(i);
            FlowerRates.Rate rate = rateOf(stack);
            if (rate == null) continue;
            int count = stackMode ? stack.getCount() : 1;
            if (rate.needsFuel()) fuelled += rate.manaPerSecond() * count;
            else plain += rate.manaPerSecond() * count;
        }

        // Паливо горить лише коли є квіти, яким воно потрібне
        if (fuelled > 0 && burnTime <= 0) {
            ItemStack fuel = items.getStackInSlot(SLOT_FUEL);
            int ticks = fuel.getBurnTime(RecipeType.SMELTING);
            if (ticks > 0) {
                ItemStack remainder = fuel.getCraftingRemainingItem(); // відро з-під лави
                ItemStack left = fuel.copy();
                left.shrink(1);
                items.setStackInSlot(SLOT_FUEL, left.isEmpty() ? remainder : left);
                burnTime = burnTotal = ticks;
            }
        }
        double perSecond = plain + (burnTime > 0 ? fuelled : 0);
        if (burnTime > 0) burnTime--;
        manaPerSecond = (int) Math.min(Integer.MAX_VALUE, Math.round(perSecond));

        if (perSecond > 0 && mana < CAPACITY) {
            fraction += perSecond / 20.0;
            int whole = (int) fraction;
            fraction -= whole;
            mana = (int) Math.min(CAPACITY, (long) mana + whole);
            setChanged();
        }

        if (mana > 0 && !level.isClientSide) {
            int sent = ManaTransfer.pushToNeighbours(level, worldPosition, mana, SynthesisConfig.MANA_TRANSFER_RATE.get(), r -> false);
            if (sent > 0) {
                mana -= sent;
                setChanged();
            }
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.synthesis.multi_greenhouse");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new GreenhouseMenu(containerId, inventory, this, data);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("items", items.serializeNBT(registries));
        tag.putInt("mana", mana);
        tag.putBoolean("stack_mode", stackMode);
        tag.putInt("burn", burnTime);
        tag.putInt("burn_total", burnTotal);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("items")) items.deserializeNBT(registries, tag.getCompound("items"));
        mana = Math.min(tag.getInt("mana"), CAPACITY);
        stackMode = !tag.contains("stack_mode") || tag.getBoolean("stack_mode");
        burnTime = tag.getInt("burn");
        burnTotal = tag.getInt("burn_total");
    }
}
