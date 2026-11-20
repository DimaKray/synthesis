package ua.krainov.synthesis.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.Nullable;
import ua.krainov.synthesis.compat.ModCompat;
import ua.krainov.synthesis.compat.create.CreateProcessing;
import ua.krainov.synthesis.config.SynthesisConfig;
import ua.krainov.synthesis.item.ConverterModuleItem;
import ua.krainov.synthesis.menu.IndustrialProcessorMenu;
import ua.krainov.synthesis.registry.ModBlockEntities;

import java.util.ArrayList;
import java.util.List;

/**
 * Промисловий процесор: переробляє за одну операцію цілу пачку предметів (8 / 16 / 32 / 64 зі Стековим модулем).
 * Режими: плавка (рецепти печі) і дроблення (Create: crushing або milling; режим є лише з Create).
 * Хопери/труби/AE2: класти — у вхід, забирати — з виходів.
 */
public class IndustrialProcessorBlockEntity extends BlockEntity implements MenuProvider {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_MODULE = 1;
    public static final int FIRST_OUTPUT = 2;
    public static final int OUTPUTS = 6;
    public static final int SLOTS = FIRST_OUTPUT + OUTPUTS;
    public static final int ENERGY_CAPACITY = 1_000_000;
    private static final int ENERGY_MAX_RECEIVE = 50_000;

    public enum Mode { SMELTING, CRUSHING }

    public enum Status { IDLE, WORKING, NO_ENERGY, NO_RECIPE, OUTPUT_FULL }

    private final ItemStackHandler items = new ItemStackHandler(SLOTS) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == SLOT_MODULE) {
                return stack.getItem() instanceof ConverterModuleItem m && m.getKind() == ConverterModuleItem.Kind.STACK;
            }
            return slot == SLOT_INPUT;
        }

        @Override
        public int getSlotLimit(int slot) {
            return slot == SLOT_MODULE ? 1 : 64;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    /** Для автоматизації: вхід — лише вставити, виходи — лише забрати. */
    private final IItemHandler automation = new IItemHandler() {
        private final RangedWrapper outputs = new RangedWrapper(items, FIRST_OUTPUT, SLOTS);

        @Override
        public int getSlots() {
            return 1 + OUTPUTS;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return slot == 0 ? items.getStackInSlot(SLOT_INPUT) : outputs.getStackInSlot(slot - 1);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return slot == 0 ? items.insertItem(SLOT_INPUT, stack, simulate) : stack;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return slot == 0 ? ItemStack.EMPTY : outputs.extractItem(slot - 1, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return 64;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return slot == 0;
        }
    };

    private final MachineEnergy energy = new MachineEnergy();
    /** Результати, що не влізли у вихідні слоти, — спершу доставляємо їх. */
    private final List<ItemStack> pending = new ArrayList<>();
    private Mode mode = Mode.SMELTING;
    private int progress;
    private Status status = Status.IDLE;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> SynthesisConfig.PROCESSOR_TICKS.get();
                case 2 -> energy.getEnergyStored() & 0xFFFF;
                case 3 -> energy.getEnergyStored() >>> 16;
                case 4 -> status.ordinal();
                case 5 -> mode.ordinal();
                case 6 -> batchSize();
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

    public IndustrialProcessorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.INDUSTRIAL_PROCESSOR.get(), pos, state);
    }

    public ItemStackHandler getItems() {
        return items;
    }

    public IItemHandler getAutomation() {
        return automation;
    }

    public EnergyStorage getEnergy() {
        return energy;
    }

    /** Кнопка в інтерфейсі: наступний режим (дроблення — лише з Create). */
    public void cycleMode() {
        Mode next = mode == Mode.SMELTING && ModCompat.CREATE ? Mode.CRUSHING : Mode.SMELTING;
        if (next != mode) {
            mode = next;
            progress = 0;
            setChanged();
        }
    }

    private int capacity() {
        ItemStack module = items.getStackInSlot(SLOT_MODULE);
        int level = module.getItem() instanceof ConverterModuleItem m && m.getKind() == ConverterModuleItem.Kind.STACK ? m.getLevel() : 0;
        return ConverterModuleItem.stackCapacity(level);
    }

    private int batchSize() {
        return Math.min(capacity(), items.getStackInSlot(SLOT_INPUT).getCount());
    }

    // --- тік ---

    public static void serverTick(Level level, BlockPos pos, BlockState state, IndustrialProcessorBlockEntity be) {
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
            deliverPending();
            if (!pending.isEmpty()) return Status.OUTPUT_FULL;
        }
        ItemStack input = items.getStackInSlot(SLOT_INPUT);
        if (input.isEmpty()) {
            progress = 0;
            return Status.IDLE;
        }
        if (!hasRecipe(level, input)) {
            progress = 0;
            return Status.NO_RECIPE;
        }
        int batch = batchSize();
        int ticks = SynthesisConfig.PROCESSOR_TICKS.get();
        long cost = (long) batch * SynthesisConfig.PROCESSOR_ENERGY_PER_ITEM.get();
        int share = (int) (cost * (progress + 1) / ticks - cost * progress / ticks);
        if (energy.getEnergyStored() < share) return Status.NO_ENERGY;
        energy.consume(share);
        progress++;
        if (progress >= ticks) {
            progress = 0;
            List<ItemStack> results = process(level, input, batch);
            ItemStack left = input.copy();
            left.shrink(batch);
            items.setStackInSlot(SLOT_INPUT, left);
            pending.addAll(results);
            deliverPending();
        }
        setChanged();
        return Status.WORKING;
    }

    private boolean hasRecipe(ServerLevel level, ItemStack input) {
        return switch (mode) {
            case SMELTING -> level.getRecipeManager()
                    .getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(input.copyWithCount(1)), level).isPresent();
            case CRUSHING -> ModCompat.CREATE && CreateProcessing.canCrush(level, input);
        };
    }

    private List<ItemStack> process(ServerLevel level, ItemStack input, int batch) {
        List<ItemStack> out = new ArrayList<>();
        if (mode == Mode.SMELTING) {
            level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(input.copyWithCount(1)), level)
                    .ifPresent(h -> {
                        ItemStack one = h.value().getResultItem(level.registryAccess());
                        addSplit(out, one, (long) one.getCount() * batch);
                    });
        } else if (ModCompat.CREATE) {
            for (int i = 0; i < batch; i++) {
                List<ItemStack> rolled = CreateProcessing.crushOne(level, input);
                if (rolled != null) {
                    for (ItemStack r : rolled) merge(out, r);
                }
            }
        }
        return out;
    }

    /** Додає total штук предмета, ділячи на стаки. */
    private static void addSplit(List<ItemStack> out, ItemStack proto, long total) {
        int max = Math.max(1, proto.getMaxStackSize());
        while (total > 0) {
            int n = (int) Math.min(max, total);
            out.add(proto.copyWithCount(n));
            total -= n;
        }
    }

    private static void merge(List<ItemStack> out, ItemStack stack) {
        if (stack.isEmpty()) return;
        ItemStack rest = stack.copy();
        for (ItemStack existing : out) {
            if (ItemStack.isSameItemSameComponents(existing, rest) && existing.getCount() < existing.getMaxStackSize()) {
                int move = Math.min(rest.getCount(), existing.getMaxStackSize() - existing.getCount());
                existing.grow(move);
                rest.shrink(move);
                if (rest.isEmpty()) return;
            }
        }
        out.add(rest);
    }

    /** Кладе результати у вихідні слоти напряму (isItemValid для виходів = false, щоб гравець туди нічого не клав). */
    private void deliverPending() {
        for (int i = 0; i < pending.size(); i++) {
            ItemStack rest = pending.get(i).copy();
            for (int slot = FIRST_OUTPUT; slot < SLOTS && !rest.isEmpty(); slot++) {
                ItemStack existing = items.getStackInSlot(slot);
                if (existing.isEmpty()) {
                    int n = Math.min(rest.getCount(), rest.getMaxStackSize());
                    items.setStackInSlot(slot, rest.copyWithCount(n));
                    rest.shrink(n);
                } else if (ItemStack.isSameItemSameComponents(existing, rest) && existing.getCount() < existing.getMaxStackSize()) {
                    int n = Math.min(rest.getCount(), existing.getMaxStackSize() - existing.getCount());
                    items.setStackInSlot(slot, existing.copyWithCount(existing.getCount() + n));
                    rest.shrink(n);
                }
            }
            pending.set(i, rest);
        }
        pending.removeIf(ItemStack::isEmpty);
    }

    // --- меню ---

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.synthesis.industrial_processor");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new IndustrialProcessorMenu(containerId, inventory, this, data);
    }

    // --- збереження ---

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("items", items.serializeNBT(registries));
        tag.putInt("energy", energy.getEnergyStored());
        tag.putInt("progress", progress);
        tag.putString("mode", mode.name());
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
        try {
            mode = Mode.valueOf(tag.getString("mode"));
        } catch (IllegalArgumentException e) {
            mode = Mode.SMELTING;
        }
        if (mode == Mode.CRUSHING && !ModCompat.CREATE) mode = Mode.SMELTING;
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
