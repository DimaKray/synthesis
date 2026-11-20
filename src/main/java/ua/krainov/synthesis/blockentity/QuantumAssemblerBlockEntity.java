package ua.krainov.synthesis.blockentity;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import ua.krainov.synthesis.config.SynthesisConfig;
import ua.krainov.synthesis.registry.ModBlockEntities;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Квантовий збирач 9×9: автокрафт рецептів Extreme Crafting Table (Avaritia) для AE2.
 * <p>
 * Як підключити: поруч Pattern Provider AE2 з ШАБЛОНОМ ОБРОБКИ (processing pattern):
 * входи — інгредієнти рецепта столу Avaritia, вихід — результат. Провайдер кладе інгредієнти сюди,
 * збирач сам знаходить рецепт, крафтить і віддає результат у сусідні інвентарі (зокрема назад у провайдер).
 * API AE2 не потрібен; рецепти Avaritia читаються через звичайний API Minecraft.
 */
public class QuantumAssemblerBlockEntity extends BlockEntity {
    public static final int BUFFER_SLOTS = 81;
    public static final int ENERGY_CAPACITY = 4_000_000;
    private static final int ENERGY_MAX_RECEIVE = 200_000;
    private static final int CHECK_INTERVAL = 5;
    private static final Set<ResourceLocation> TABLE_TYPES = Set.of(
            ResourceLocation.fromNamespaceAndPath("avaritia", "shaped_table"),
            ResourceLocation.fromNamespaceAndPath("avaritia", "shapeless_table"));

    public enum Status { IDLE, WORKING, NO_ENERGY, NO_OUTPUT, OUTPUT_FULL }

    private final ItemStackHandler buffer = new ItemStackHandler(BUFFER_SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            dirty = true;
        }
    };

    /** Для AE2/хоперів: лише класти інгредієнти (забирати нічого не можна — результат іде сам). */
    private final IItemHandler automation = new IItemHandler() {
        @Override
        public int getSlots() {
            return BUFFER_SLOTS;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return buffer.getStackInSlot(slot);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return buffer.insertItem(slot, stack, simulate);
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
            return true;
        }
    };

    private final MachineEnergy energy = new MachineEnergy();
    private final List<ItemStack> pending = new ArrayList<>();
    private Status status = Status.IDLE;
    private int crafted;
    /** Вміст буфера змінився — варто знову шукати рецепт. */
    private boolean dirty = true;

    private static RecipeManager cachedManager;
    private static List<Recipe<?>> cachedRecipes = List.of();

    public QuantumAssemblerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.QUANTUM_ASSEMBLER.get(), pos, state);
    }

    public IItemHandler getAutomation() {
        return automation;
    }

    public ItemStackHandler getBuffer() {
        return buffer;
    }

    public EnergyStorage getEnergy() {
        return energy;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, QuantumAssemblerBlockEntity be) {
        if (level instanceof ServerLevel server) be.tick(server);
    }

    private void tick(ServerLevel level) {
        if (!pending.isEmpty()) {
            flushPending(level);
            if (!pending.isEmpty()) {
                status = hasOutput(level) ? Status.OUTPUT_FULL : Status.NO_OUTPUT;
                return;
            }
        }
        if (level.getGameTime() % CHECK_INTERVAL != 0 || !dirty) return;
        int cost = SynthesisConfig.ASSEMBLER_ENERGY_PER_CRAFT.get();
        if (isBufferEmpty()) {
            status = Status.IDLE;
            dirty = false;
            return;
        }
        if (energy.getEnergyStored() < cost) {
            status = Status.NO_ENERGY;
            return;
        }
        if (tryCraft(level)) {
            energy.consume(cost);
            crafted++;
            status = Status.WORKING;
            level.sendParticles(ParticleTypes.PORTAL, worldPosition.getX() + 0.5, worldPosition.getY() + 1.0,
                    worldPosition.getZ() + 0.5, 12, 0.3, 0.2, 0.3, 0.3);
            flushPending(level);
        } else {
            status = Status.IDLE;
            dirty = false; // чекаємо, поки докладуть інгредієнти
        }
    }

    private boolean isBufferEmpty() {
        for (int i = 0; i < BUFFER_SLOTS; i++) {
            if (!buffer.getStackInSlot(i).isEmpty()) return false;
        }
        return true;
    }

    /** Рецепти столів Avaritia (кеш оновлюється після /reload — коли змінюється RecipeManager). */
    private static List<Recipe<?>> tableRecipes(Level level) {
        RecipeManager manager = level.getRecipeManager();
        if (manager != cachedManager) {
            List<Recipe<?>> list = new ArrayList<>();
            for (RecipeHolder<?> holder : manager.getRecipes()) {
                ResourceLocation type = BuiltInRegistries.RECIPE_TYPE.getKey(holder.value().getType());
                if (type != null && TABLE_TYPES.contains(type)) list.add(holder.value());
            }
            cachedRecipes = List.copyOf(list);
            cachedManager = manager;
        }
        return cachedRecipes;
    }

    /** Шукає рецепт, для якого в буфері є всі інгредієнти; крафтить один раз. */
    private boolean tryCraft(ServerLevel level) {
        for (Recipe<?> recipe : tableRecipes(level)) {
            List<Ingredient> needed = new ArrayList<>();
            for (Ingredient ing : recipe.getIngredients()) {
                if (!ing.isEmpty()) needed.add(ing);
            }
            if (needed.isEmpty()) continue;
            int[] take = match(needed);
            if (take == null) continue;

            ItemStack result = recipe.getResultItem(level.registryAccess()).copy();
            if (result.isEmpty()) continue;
            for (int slot = 0; slot < BUFFER_SLOTS; slot++) {
                if (take[slot] <= 0) continue;
                ItemStack stack = buffer.getStackInSlot(slot);
                ItemStack remainder = stack.getCraftingRemainingItem();
                for (int i = 0; i < take[slot]; i++) {
                    if (!remainder.isEmpty()) pending.add(remainder.copy());
                }
                ItemStack left = stack.copy();
                left.shrink(take[slot]);
                buffer.setStackInSlot(slot, left);
            }
            pending.add(result);
            return true;
        }
        return false;
    }

    /** Жадібне зіставлення: скільки предметів з кожного слота забрати; null — якщо чогось бракує. */
    private int[] match(List<Ingredient> needed) {
        int[] take = new int[BUFFER_SLOTS];
        for (Ingredient ing : needed) {
            boolean found = false;
            for (int slot = 0; slot < BUFFER_SLOTS; slot++) {
                ItemStack stack = buffer.getStackInSlot(slot);
                if (stack.getCount() - take[slot] > 0 && ing.test(stack)) {
                    take[slot]++;
                    found = true;
                    break;
                }
            }
            if (!found) return null;
        }
        return take;
    }

    private boolean hasOutput(ServerLevel level) {
        for (Direction dir : Direction.values()) {
            if (level.getCapability(Capabilities.ItemHandler.BLOCK, worldPosition.relative(dir), dir.getOpposite()) != null) {
                return true;
            }
        }
        return false;
    }

    private void flushPending(ServerLevel level) {
        for (Direction dir : Direction.values()) {
            if (pending.isEmpty()) return;
            IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK,
                    worldPosition.relative(dir), dir.getOpposite());
            if (handler == null) continue;
            for (int i = 0; i < pending.size(); i++) {
                pending.set(i, ItemHandlerHelper.insertItemStacked(handler, pending.get(i), false));
            }
            pending.removeIf(ItemStack::isEmpty);
        }
    }

    /** ПКМ: стан у чат. */
    public void describe(Player player) {
        int stacks = 0;
        for (int i = 0; i < BUFFER_SLOTS; i++) {
            if (!buffer.getStackInSlot(i).isEmpty()) stacks++;
        }
        player.sendSystemMessage(Component.translatable("message.synthesis.assembler.info",
                energy.getEnergyStored(), energy.getMaxEnergyStored(), stacks, BUFFER_SLOTS, crafted)
                .withStyle(ChatFormatting.AQUA));
        player.sendSystemMessage(Component.translatable("message.synthesis.assembler.status."
                + status.name().toLowerCase(java.util.Locale.ROOT)).withStyle(
                status == Status.WORKING ? ChatFormatting.GREEN : ChatFormatting.YELLOW));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("buffer", buffer.serializeNBT(registries));
        tag.putInt("energy", energy.getEnergyStored());
        tag.putInt("crafted", crafted);
        ListTag list = new ListTag();
        for (ItemStack stack : pending) {
            if (!stack.isEmpty()) list.add(stack.save(registries));
        }
        tag.put("pending", list);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("buffer")) buffer.deserializeNBT(registries, tag.getCompound("buffer"));
        energy.set(tag.getInt("energy"));
        crafted = tag.getInt("crafted");
        pending.clear();
        for (Tag t : tag.getList("pending", Tag.TAG_COMPOUND)) {
            ItemStack.parse(registries, t).ifPresent(pending::add);
        }
        dirty = true;
    }

    /** Усе, що лежить усередині (для випадіння при зламуванні). */
    public List<ItemStack> contents() {
        List<ItemStack> all = new ArrayList<>(pending);
        for (int i = 0; i < BUFFER_SLOTS; i++) all.add(buffer.getStackInSlot(i));
        return all;
    }

    private class MachineEnergy extends EnergyStorage {
        MachineEnergy() {
            super(ENERGY_CAPACITY, ENERGY_MAX_RECEIVE, 0);
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int received = super.receiveEnergy(maxReceive, simulate);
            if (received > 0 && !simulate) {
                setChanged();
                dirty = true;
            }
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
