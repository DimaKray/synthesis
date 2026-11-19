package ua.krainov.synthesis.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import ua.krainov.synthesis.api.RotationSource;
import ua.krainov.synthesis.menu.SynthesizerMenu;
import ua.krainov.synthesis.recipe.SynthesizerRecipe;
import ua.krainov.synthesis.recipe.SynthesizerRecipeInput;
import ua.krainov.synthesis.registry.ModBlockEntities;
import ua.krainov.synthesis.registry.ModRecipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Техно-магічний синтезатор: 3 вхідні слоти, 1 вихід, бак рідини, буфер FE.
 * Якщо рецепт вимагає обертання — потрібен Кінетичний привід Create впритул до машини;
 * що більше RPM понад мінімум, то швидше йде крафт (до ×4).
 */
public class SynthesizerBlockEntity extends BlockEntity implements MenuProvider {
    public static final int INPUT_SLOTS = 3;
    public static final int OUTPUT_SLOT = 3;
    public static final int TANK_CAPACITY = 16_000;
    public static final int ENERGY_CAPACITY = 1_000_000;
    public static final int ENERGY_MAX_RECEIVE = 20_000;
    public static final int MAX_SPEEDUP = 4;

    /** Стан машини (показується в інтерфейсі). */
    public enum Status { IDLE, WORKING, NO_ENERGY, NO_ROTATION, OUTPUT_FULL }

    private final ItemStackHandler items = new ItemStackHandler(4) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return slot < INPUT_SLOTS;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            recipeDirty = true;
        }
    };

    /** Що бачать хопери й труби: класти можна лише у входи, забирати — лише з виходу. */
    private final IItemHandler automationItems = new IItemHandler() {
        @Override
        public int getSlots() {
            return items.getSlots();
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return items.getStackInSlot(slot);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return slot < INPUT_SLOTS ? items.insertItem(slot, stack, simulate) : stack;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return slot == OUTPUT_SLOT ? items.extractItem(slot, amount, simulate) : ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return items.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return slot < INPUT_SLOTS;
        }
    };

    private final FluidTank tank = new FluidTank(TANK_CAPACITY) {
        @Override
        protected void onContentsChanged() {
            setChanged();
            recipeDirty = true;
            clientSyncNeeded = true;
        }
    };

    private final MachineEnergy energy = new MachineEnergy();

    private int progress;
    private int lastRpm;
    private Status status = Status.IDLE;
    private boolean recipeDirty = true;
    private boolean clientSyncNeeded;
    @Nullable
    private RecipeHolder<SynthesizerRecipe> currentRecipe;

    /** Числа для інтерфейсу (синхронізуються як short, тому енергію ділимо на дві половини). */
    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            SynthesizerRecipe r = currentRecipe != null ? currentRecipe.value() : null;
            return switch (index) {
                case 0 -> progress;
                case 1 -> r != null ? r.time() : 0;
                case 2 -> lastRpm;
                case 3 -> r != null ? r.minRpm() : 0;
                case 4 -> status.ordinal();
                case 5 -> energy.getEnergyStored() & 0xFFFF;
                case 6 -> energy.getEnergyStored() >>> 16;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            // на сервері не використовується
        }

        @Override
        public int getCount() {
            return 7;
        }
    };

    public SynthesizerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SYNTHESIZER.get(), pos, state);
    }

    // --- доступ ---

    public ItemStackHandler getItems() {
        return items;
    }

    public IItemHandler getAutomationItems() {
        return automationItems;
    }

    public FluidTank getTank() {
        return tank;
    }

    public EnergyStorage getEnergy() {
        return energy;
    }

    // --- тік ---

    public static void serverTick(Level level, BlockPos pos, BlockState state, SynthesizerBlockEntity be) {
        if (level instanceof ServerLevel serverLevel) be.tick(serverLevel);
    }

    private void tick(ServerLevel level) {
        if (recipeDirty) {
            RecipeHolder<SynthesizerRecipe> found = findRecipe().orElse(null);
            if (found == null || currentRecipe == null || !found.id().equals(currentRecipe.id())) {
                progress = 0;
            }
            currentRecipe = found;
            recipeDirty = false;
        }

        RotationSource rotation = findRotationSource();
        lastRpm = rotation != null ? rotation.getRpm() : 0;

        Status newStatus = work(rotation);
        if (newStatus != status) {
            status = newStatus;
            setChanged();
        }

        if (clientSyncNeeded && level.getGameTime() % 10 == 0) {
            clientSyncNeeded = false;
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    private Status work(@Nullable RotationSource rotation) {
        if (currentRecipe == null) {
            if (rotation != null) rotation.setRequiredStress(0);
            return Status.IDLE;
        }
        SynthesizerRecipe recipe = currentRecipe.value();
        if (!canOutput(recipe.result())) {
            if (rotation != null) rotation.setRequiredStress(0);
            return Status.OUTPUT_FULL;
        }

        int steps = 1;
        if (recipe.minRpm() > 0) {
            if (rotation == null) return Status.NO_ROTATION;
            rotation.setRequiredStress(recipe.stress());
            int rpm = rotation.getRpm();
            if (rpm < recipe.minRpm()) return Status.NO_ROTATION;
            steps = Math.min(MAX_SPEEDUP, rpm / recipe.minRpm());
        } else if (rotation != null) {
            rotation.setRequiredStress(0);
        }

        // Енергію беремо порціями: сума порцій за весь крафт = recipe.energy()
        int cost = 0;
        int time = recipe.time();
        int stepsDone = Math.min(steps, time - progress);
        for (int i = 0; i < stepsDone; i++) {
            cost += share(recipe.energy(), progress + i, time);
        }
        if (energy.getEnergyStored() < cost) return Status.NO_ENERGY;
        energy.consume(cost);
        progress += stepsDone;
        setChanged();

        if (progress >= time) {
            craft(recipe);
            progress = 0;
            recipeDirty = true;
        }
        return Status.WORKING;
    }

    private static int share(int total, int step, int time) {
        if (total <= 0) return 0;
        return (int) ((long) total * (step + 1) / time - (long) total * step / time);
    }

    private void craft(SynthesizerRecipe recipe) {
        ItemStack result = recipe.assemble(buildInput(), level.registryAccess());
        for (int i = 0; i < INPUT_SLOTS; i++) {
            ItemStack stack = items.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            ItemStack remainder = stack.getCraftingRemainingItem();
            ItemStack left = stack.copy();
            left.shrink(1);
            if (left.isEmpty() && !remainder.isEmpty()) left = remainder;
            items.setStackInSlot(i, left);
        }
        recipe.fluid().ifPresent(f -> tank.drain(f.amount(), IFluidHandler.FluidAction.EXECUTE));

        ItemStack out = items.getStackInSlot(OUTPUT_SLOT);
        if (out.isEmpty()) {
            items.setStackInSlot(OUTPUT_SLOT, result);
        } else {
            ItemStack grown = out.copy();
            grown.grow(result.getCount());
            items.setStackInSlot(OUTPUT_SLOT, grown);
        }
    }

    private boolean canOutput(ItemStack result) {
        ItemStack out = items.getStackInSlot(OUTPUT_SLOT);
        if (out.isEmpty()) return true;
        return ItemStack.isSameItemSameComponents(out, result)
                && out.getCount() + result.getCount() <= out.getMaxStackSize();
    }

    private SynthesizerRecipeInput buildInput() {
        List<ItemStack> inputs = new ArrayList<>();
        for (int i = 0; i < INPUT_SLOTS; i++) {
            ItemStack stack = items.getStackInSlot(i);
            if (!stack.isEmpty()) inputs.add(stack);
        }
        return new SynthesizerRecipeInput(inputs, tank.getFluid());
    }

    private Optional<RecipeHolder<SynthesizerRecipe>> findRecipe() {
        if (level == null) return Optional.empty();
        SynthesizerRecipeInput input = buildInput();
        if (input.items().isEmpty()) return Optional.empty();
        return level.getRecipeManager().getRecipeFor(ModRecipes.SYNTHESIZING_TYPE.get(), input, level);
    }

    @Nullable
    private RotationSource findRotationSource() {
        if (level == null) return null;
        for (Direction dir : Direction.values()) {
            if (level.getBlockEntity(worldPosition.relative(dir)) instanceof RotationSource source) {
                return source;
            }
        }
        return null;
    }

    /** Машину зламали — привід більше не навантажує мережу. */
    public void onRemoved() {
        RotationSource rotation = findRotationSource();
        if (rotation != null) rotation.setRequiredStress(0);
    }

    // --- меню ---

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.synthesis.synthesizer");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new SynthesizerMenu(containerId, inventory, this, data);
    }

    // --- збереження ---

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("items", items.serializeNBT(registries));
        tag.put("tank", tank.writeToNBT(registries, new CompoundTag()));
        tag.putInt("energy", energy.getEnergyStored());
        tag.putInt("progress", progress);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("items")) items.deserializeNBT(registries, tag.getCompound("items"));
        tank.readFromNBT(registries, tag.getCompound("tank"));
        energy.set(tag.getInt("energy"));
        progress = tag.getInt("progress");
        recipeDirty = true;
    }

    /** Клієнту потрібні лише рідина й енергія (для інтерфейсу). */
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.put("tank", tank.writeToNBT(registries, new CompoundTag()));
        tag.putInt("energy", energy.getEnergyStored());
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    /** Буфер FE: лише приймає ззовні, витрачається на крафт. */
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
