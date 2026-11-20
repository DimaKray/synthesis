package ua.krainov.synthesis.blockentity;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import ua.krainov.synthesis.api.RotationSource;
import ua.krainov.synthesis.compat.ModCompat;
import ua.krainov.synthesis.compat.ars.ArsCompat;
import ua.krainov.synthesis.recipe.AltarRecipe;
import ua.krainov.synthesis.recipe.FusionRecipeInput;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Спільна логіка вівтарів: центральний предмет + п'єдестали навколо (радіус 3 блоки, ±1 по висоті).
 * Скіпетр запускає ритуал; через {@code time} тіків предмети з п'єдесталів зникають, у центрі — результат.
 * <p>
 * Живлення: власні запаси мани (мана-розповсюджувачі Botania), джерела (банки Ars Nouveau поруч),
 * FE (кабель), рідини (бак — лише якщо capability відкрита, як у Вівтаря Капіталізму) і обертання
 * (Кінетичний привід Create впритул). Ритуал щотіку бере свою частку; якщо чогось бракує — пауза.
 */
public abstract class AltarBlockEntity<R extends Recipe<FusionRecipeInput> & AltarRecipe> extends BlockEntity {
    public static final int RADIUS = 3;
    private static final int CHECK_INTERVAL = 20; // як часто перевіряти, що рецепт ще складений

    public static final int MANA_CAPACITY = 1_000_000;
    public static final int SOURCE_CAPACITY = 50_000;
    public static final int ENERGY_CAPACITY = 2_000_000;
    public static final int ENERGY_MAX_RECEIVE = 100_000;
    public static final int TANK_CAPACITY = 16_000;
    private static final int SOURCE_RANGE = 6;       // радіус пошуку банок джерела
    private static final int SOURCE_PULL_MAX = 1_000; // скільки джерела тягнути за раз

    /** Чого бракує ритуалу зараз (для підказки Скіпетра). */
    public enum Waiting { NONE, MANA, SOURCE, ENERGY, FLUID, ROTATION }

    private int mana;
    private int source;
    private final AltarEnergy energy = new AltarEnergy();
    private int needMana;
    private int needSource;
    private int needEnergy;
    private Waiting waiting = Waiting.NONE;

    private boolean crafting;
    private int progress;
    private int duration;
    @Nullable
    private ResourceLocation recipeId;
    /** Рецепт поточного ритуалу (не зберігається — відновлюється при першій перевірці). */
    @Nullable
    private R activeRecipe;
    /** Позиції п'єдесталів з предметами (для частинок і променів; синхронізується з клієнтом). */
    private List<BlockPos> activePedestals = List.of();

    private final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            // Під час ритуалу і коли зайнято — нічого не приймаємо; кладемо по одному
            if (crafting || stack.isEmpty() || !getStackInSlot(0).isEmpty()) return stack;
            if (!simulate) setStackInSlot(0, stack.copyWithCount(1));
            return stack.copyWithCount(stack.getCount() - 1);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (crafting) return ItemStack.EMPTY;
            return super.extractItem(slot, amount, simulate);
        }

        @Override
        protected void onContentsChanged(int slot) {
            sync();
        }
    };

    private final FluidTank tank = new FluidTank(TANK_CAPACITY) {
        @Override
        protected void onContentsChanged() {
            setChanged();
        }
    };

    protected AltarBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /** Який тип рецептів шукає цей вівтар. */
    protected abstract RecipeType<R> recipeType();

    // --- доступ ---

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public FluidTank getTank() {
        return tank;
    }

    public ItemStack getItem() {
        return inventory.getStackInSlot(0);
    }

    public boolean isEmpty() {
        return getItem().isEmpty();
    }

    public boolean isCrafting() {
        return crafting;
    }

    public List<BlockPos> getActivePedestals() {
        return activePedestals;
    }

    public EnergyStorage getEnergy() {
        return energy;
    }

    public int getMana() {
        return mana;
    }

    public boolean isManaFull() {
        return mana >= MANA_CAPACITY;
    }

    /** Мана з Botania (може бути й від'ємною — тоді забирають). */
    public void receiveMana(int amount) {
        int updated = (int) Math.max(0, Math.min(MANA_CAPACITY, (long) mana + amount));
        if (updated != mana) {
            mana = updated;
            setChanged();
        }
    }

    public boolean placeItem(ItemStack stack) {
        if (crafting || !isEmpty() || stack.isEmpty()) return false;
        inventory.setStackInSlot(0, stack.copyWithCount(1));
        return true;
    }

    /** Забирає весь стек (результат крафту може бути більше одного предмета). */
    public ItemStack takeItem() {
        ItemStack taken = getItem().copy();
        inventory.setStackInSlot(0, ItemStack.EMPTY);
        return taken;
    }

    // --- мультиблок і рецепт ---

    /** Усі п'єдестали в радіусі (порожні теж). */
    public List<PedestalBlockEntity> findPedestals() {
        List<PedestalBlockEntity> result = new ArrayList<>();
        if (level == null) return result;
        for (BlockPos offset : pedestalOffsets()) {
            if (level.getBlockEntity(worldPosition.offset(offset)) instanceof PedestalBlockEntity pedestal) {
                result.add(pedestal);
            }
        }
        return result;
    }

    /** Усі клітинки в квадраті 7×7 (±1 по висоті), крім самого вівтаря — для Вівтаря Злиття. */
    private static final List<BlockPos> AREA_OFFSETS = buildAreaOffsets();

    private static List<BlockPos> buildAreaOffsets() {
        List<BlockPos> list = new ArrayList<>();
        for (int y = -1; y <= 1; y++) {
            for (int x = -RADIUS; x <= RADIUS; x++) {
                for (int z = -RADIUS; z <= RADIUS; z++) {
                    if (x != 0 || y != 0 || z != 0) list.add(new BlockPos(x, y, z));
                }
            }
        }
        return List.copyOf(list);
    }

    /** Де шукати п'єдестали (зсуви від вівтаря). Вівтар Капіталізму задає фіксовану розмітку. */
    protected List<BlockPos> pedestalOffsets() {
        return AREA_OFFSETS;
    }

    /** true — п'єдестали мають стояти рівно на всіх позиціях {@link #pedestalOffsets()} (мультиблок). */
    protected boolean fixedLayout() {
        return false;
    }

    /** Наскільки далеко від центру сягає мультиблок (для рендеру променів). */
    public int structureRadius() {
        return RADIUS;
    }

    /** Позиції розмітки, де ще немає п'єдестала (лише для фіксованої розмітки). */
    public List<BlockPos> missingPedestalPositions() {
        List<BlockPos> missing = new ArrayList<>();
        if (level == null || !fixedLayout()) return missing;
        for (BlockPos offset : pedestalOffsets()) {
            BlockPos p = worldPosition.offset(offset);
            if (!(level.getBlockEntity(p) instanceof PedestalBlockEntity)) missing.add(p);
        }
        return missing;
    }

    /** Повідомляє про недобудований мультиблок і підсвічує порожні позиції. true — якщо недобудований. */
    private boolean reportIncompleteStructure(Player player) {
        List<BlockPos> missing = missingPedestalPositions();
        if (missing.isEmpty()) return false;
        player.sendSystemMessage(Component.translatable("message.synthesis.altar.structure_incomplete",
                missing.size(), pedestalOffsets().size()).withStyle(ChatFormatting.YELLOW));
        if (level instanceof ServerLevel server) {
            for (BlockPos p : missing) {
                server.sendParticles(ParticleTypes.HAPPY_VILLAGER, p.getX() + 0.5, p.getY() + 0.6, p.getZ() + 0.5,
                        8, 0.25, 0.3, 0.25, 0.0);
            }
        }
        return true;
    }

    private static List<PedestalBlockEntity> filled(List<PedestalBlockEntity> pedestals) {
        return pedestals.stream().filter(p -> !p.isEmpty()).toList();
    }

    private FusionRecipeInput buildInput(List<PedestalBlockEntity> filledPedestals) {
        List<ItemStack> items = filledPedestals.stream().map(PedestalBlockEntity::getItem).toList();
        return new FusionRecipeInput(getItem(), items);
    }

    private Optional<RecipeHolder<R>> findRecipe(List<PedestalBlockEntity> filledPedestals) {
        if (level == null || isEmpty()) return Optional.empty();
        return level.getRecipeManager().getRecipeFor(recipeType(), buildInput(filledPedestals), level);
    }

    @Nullable
    private RotationSource findRotationSource() {
        if (level == null) return null;
        for (Direction dir : Direction.values()) {
            if (level.getBlockEntity(worldPosition.relative(dir)) instanceof RotationSource rotation) {
                return rotation;
            }
        }
        return null;
    }

    /** Вівтар зламали — привід більше не навантажує мережу. */
    public void onRemoved() {
        RotationSource rotation = findRotationSource();
        if (rotation != null) rotation.setRequiredStress(0);
    }

    // --- Скіпетр ---

    /** ПКМ Скіпетром: запустити ритуал або пояснити, чого бракує. */
    public void tryStart(Player player) {
        if (crafting) {
            player.displayClientMessage(Component.translatable("message.synthesis.altar.already", percent()), true);
            return;
        }
        if (fixedLayout() && !missingPedestalPositions().isEmpty()) {
            describe(player);
            return;
        }
        List<PedestalBlockEntity> filled = filled(findPedestals());
        Optional<RecipeHolder<R>> holder = findRecipe(filled);
        if (holder.isEmpty()) {
            describe(player);
            return;
        }
        R recipe = holder.get().value();
        crafting = true;
        progress = 0;
        duration = recipe.time();
        needMana = recipe.mana();
        needSource = recipe.source();
        needEnergy = recipe.energy();
        waiting = Waiting.NONE;
        recipeId = holder.get().id();
        activeRecipe = recipe;
        activePedestals = filled.stream().map(BlockEntity::getBlockPos).map(BlockPos::immutable).toList();
        level.playSound(null, worldPosition, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0f, 1.2f);
        player.displayClientMessage(Component.translatable("message.synthesis.altar.started")
                .withStyle(ChatFormatting.LIGHT_PURPLE), true);
        sync();
    }

    /** Shift+ПКМ Скіпетром: стан вівтаря і чого бракує. */
    public void describe(Player player) {
        player.sendSystemMessage(Component.translatable("message.synthesis.altar.stock",
                mana, source, energy.getEnergyStored()).withStyle(ChatFormatting.AQUA));
        if (!tank.isEmpty()) {
            player.sendSystemMessage(Component.translatable("message.synthesis.altar.tank",
                    tank.getFluid().getHoverName(), tank.getFluidAmount(), TANK_CAPACITY).withStyle(ChatFormatting.AQUA));
        }
        if (crafting) {
            player.sendSystemMessage(Component.translatable("message.synthesis.altar.already", percent())
                    .withStyle(ChatFormatting.LIGHT_PURPLE));
            if (waiting != Waiting.NONE) {
                player.sendSystemMessage(Component.translatable(
                        "message.synthesis.altar.waiting." + waiting.name().toLowerCase(Locale.ROOT))
                        .withStyle(ChatFormatting.RED));
            }
            return;
        }
        if (isEmpty()) {
            player.sendSystemMessage(Component.translatable("message.synthesis.altar.no_center")
                    .withStyle(ChatFormatting.YELLOW));
            return;
        }
        if (reportIncompleteStructure(player)) return;
        List<PedestalBlockEntity> pedestals = findPedestals();
        if (pedestals.isEmpty()) {
            player.sendSystemMessage(Component.translatable("message.synthesis.altar.no_pedestals", RADIUS)
                    .withStyle(ChatFormatting.YELLOW));
            return;
        }
        List<PedestalBlockEntity> filled = filled(pedestals);
        Optional<RecipeHolder<R>> ready = findRecipe(filled);
        if (ready.isPresent()) {
            R r = ready.get().value();
            player.sendSystemMessage(Component.translatable("message.synthesis.altar.ready",
                    r.result().getHoverName()).withStyle(ChatFormatting.GREEN));
            if (r.mana() > 0 || r.source() > 0 || r.energy() > 0) {
                player.sendSystemMessage(Component.translatable("message.synthesis.altar.needs",
                        r.mana(), r.source(), r.energy()).withStyle(ChatFormatting.GRAY));
            }
            if (r.fluid().isPresent() || r.minRpm() > 0) {
                int amount = r.fluid().map(SizedFluidIngredient::amount).orElse(0);
                Component fluidName = r.fluid().map(AltarBlockEntity::fluidName).orElse(Component.literal("-"));
                player.sendSystemMessage(Component.translatable("message.synthesis.altar.needs_extra",
                        amount, fluidName, r.minRpm(), r.stress()).withStyle(ChatFormatting.GRAY));
            }
            return;
        }

        // Шукаємо рецепт з таким центром, якому бракує найменше
        List<ItemStack> items = filled.stream().map(PedestalBlockEntity::getItem).toList();
        Diagnosis best = null;
        for (RecipeHolder<R> h : level.getRecipeManager().getAllRecipesFor(recipeType())) {
            R r = h.value();
            if (!r.center().test(getItem())) continue;
            Diagnosis d = Diagnosis.of(r, items);
            if (best == null || d.score() < best.score()) best = d;
        }
        if (best == null) {
            player.sendSystemMessage(Component.translatable("message.synthesis.altar.no_recipe", getItem().getHoverName())
                    .withStyle(ChatFormatting.RED));
            return;
        }
        player.sendSystemMessage(Component.translatable("message.synthesis.altar.recipe",
                best.recipe().result().getHoverName()).withStyle(ChatFormatting.GOLD));
        if (!best.missing().isEmpty()) {
            player.sendSystemMessage(Component.translatable("message.synthesis.altar.missing",
                    ComponentUtils.formatList(best.missing(), Component.literal(", "))).withStyle(ChatFormatting.RED));
        }
        if (!best.extra().isEmpty()) {
            player.sendSystemMessage(Component.translatable("message.synthesis.altar.extra",
                    ComponentUtils.formatList(best.extra(), Component.literal(", "))).withStyle(ChatFormatting.YELLOW));
        }
    }

    private static Component fluidName(SizedFluidIngredient fluid) {
        FluidStack[] options = fluid.getFluids();
        return options.length > 0 ? options[0].getHoverName() : Component.literal("?");
    }

    /** Що бракує / що зайве для конкретного рецепта (жадібне зіставлення, для підказки цього досить). */
    private record Diagnosis(AltarRecipe recipe, List<Component> missing, List<Component> extra) {
        int score() {
            return missing.size() + extra.size();
        }

        static Diagnosis of(AltarRecipe recipe, List<ItemStack> items) {
            boolean[] used = new boolean[items.size()];
            List<Component> missing = new ArrayList<>();
            for (Ingredient ing : recipe.pedestals()) {
                boolean found = false;
                for (int i = 0; i < items.size(); i++) {
                    if (!used[i] && ing.test(items.get(i))) {
                        used[i] = true;
                        found = true;
                        break;
                    }
                }
                if (!found) missing.add(nameOf(ing));
            }
            List<Component> extra = new ArrayList<>();
            for (int i = 0; i < items.size(); i++) {
                if (!used[i]) extra.add(items.get(i).getHoverName());
            }
            return new Diagnosis(recipe, missing, extra);
        }

        static Component nameOf(Ingredient ing) {
            ItemStack[] options = ing.getItems();
            return options.length > 0 ? options[0].getHoverName() : Component.literal("?");
        }
    }

    private int percent() {
        return duration <= 0 ? 0 : progress * 100 / duration;
    }

    // --- тік ---

    public static void serverTick(Level level, BlockPos pos, BlockState state, AltarBlockEntity<?> altar) {
        if (altar.crafting && level instanceof ServerLevel serverLevel) {
            altar.tickRitual(serverLevel);
        }
    }

    private void tickRitual(ServerLevel level) {
        long gameTime = level.getGameTime();
        // Періодично (і одразу після завантаження світу) перевіряємо, що все ще на місці
        if (gameTime % CHECK_INTERVAL == 0 || activeRecipe == null) {
            List<PedestalBlockEntity> filled = filled(findPedestals());
            Optional<RecipeHolder<R>> holder = findRecipe(filled);
            if (holder.isEmpty() || !holder.get().id().equals(recipeId)) {
                cancel(level);
                return;
            }
            activeRecipe = holder.get().value();
            activePedestals = filled.stream().map(BlockEntity::getBlockPos).map(BlockPos::immutable).toList();
        }
        R recipe = activeRecipe;

        // Частка ресурсів на цей тік (сума часток за весь ритуал = точно потрібна кількість)
        int manaShare = share(needMana);
        int sourceShare = share(needSource);
        int energyShare = share(needEnergy);
        int fluidShare = recipe.fluid().map(f -> share(f.amount())).orElse(0);

        if (sourceShare > source && gameTime % 10 == 0) {
            pullSource(level);
        }

        // Обертання: поки йде ритуал, привід навантажує мережу
        boolean rotationOk = true;
        if (recipe.minRpm() > 0) {
            RotationSource rotation = findRotationSource();
            if (rotation == null) {
                rotationOk = false;
            } else {
                rotation.setRequiredStress(recipe.stress());
                rotationOk = rotation.getRpm() >= recipe.minRpm();
            }
        }

        boolean fluidOk = recipe.fluid().isEmpty()
                || (recipe.fluid().get().ingredient().test(tank.getFluid()) && tank.getFluidAmount() >= fluidShare);

        Waiting nowWaiting;
        if (mana < manaShare) nowWaiting = Waiting.MANA;
        else if (source < sourceShare) nowWaiting = Waiting.SOURCE;
        else if (energy.getEnergyStored() < energyShare) nowWaiting = Waiting.ENERGY;
        else if (!fluidOk) nowWaiting = Waiting.FLUID;
        else if (!rotationOk) nowWaiting = Waiting.ROTATION;
        else nowWaiting = Waiting.NONE;

        if (nowWaiting != waiting) {
            waiting = nowWaiting;
            setChanged();
        }
        if (waiting != Waiting.NONE) {
            // Пауза: прогрес стоїть, зрідка показуємо, що вівтар «голодний»
            if (gameTime % 20 == 0) {
                level.sendParticles(ParticleTypes.SMOKE, worldPosition.getX() + 0.5, worldPosition.getY() + 1.1,
                        worldPosition.getZ() + 0.5, 3, 0.2, 0.05, 0.2, 0.01);
            }
            return;
        }

        mana -= manaShare;
        source -= sourceShare;
        energy.consume(energyShare);
        if (fluidShare > 0) tank.drain(fluidShare, IFluidHandler.FluidAction.EXECUTE);

        progress++;
        spawnRitualParticles(level);
        if (progress % 40 == 0) {
            level.playSound(null, worldPosition, SoundEvents.BEACON_AMBIENT, SoundSource.BLOCKS, 0.8f, 1.5f);
        }
        if (progress >= duration) {
            finish(level);
        } else {
            setChanged();
        }
    }

    /** Скільки ресурсу треба на поточний крок прогресу. */
    private int share(int total) {
        if (total <= 0 || duration <= 0) return 0;
        return (int) ((long) total * (progress + 1) / duration - (long) total * progress / duration);
    }

    /** Тягнемо джерело з банок Ars Nouveau поруч (якщо Ars встановлено). */
    private void pullSource(ServerLevel level) {
        if (!ModCompat.ARS_NOUVEAU) return;
        int remaining = needSource - (int) ((long) needSource * progress / duration);
        int wanted = Math.min(Math.min(remaining - source, SOURCE_PULL_MAX), SOURCE_CAPACITY - source);
        if (wanted <= 0) return;
        // Спершу пробуємо великою порцією, потім маленькою (якщо в банках мало)
        if (ArsCompat.takeSource(level, worldPosition, SOURCE_RANGE, wanted)) {
            source += wanted;
        } else {
            int small = Math.min(wanted, Math.max(1, share(needSource) * 10));
            if (small < wanted && ArsCompat.takeSource(level, worldPosition, SOURCE_RANGE, small)) {
                source += small;
            }
        }
    }

    private void spawnRitualParticles(ServerLevel level) {
        double cx = worldPosition.getX() + 0.5;
        double cy = worldPosition.getY() + 1.25;
        double cz = worldPosition.getZ() + 0.5;
        if (progress % 2 == 0) {
            for (BlockPos p : activePedestals) {
                // ENCHANT з'являється в (центр + зсув) і летить до центру — тобто від п'єдестала до вівтаря
                double dx = p.getX() + 0.5 - cx;
                double dy = p.getY() + 1.2 - cy;
                double dz = p.getZ() + 0.5 - cz;
                level.sendParticles(ParticleTypes.ENCHANT, cx, cy, cz, 0, dx, dy, dz, 1.0);
            }
        }
        if (progress % 5 == 0) {
            level.sendParticles(ParticleTypes.END_ROD, cx, cy, cz, 1, 0.15, 0.15, 0.15, 0.01);
        }
    }

    private void finish(ServerLevel level) {
        List<PedestalBlockEntity> filled = filled(findPedestals());
        Optional<RecipeHolder<R>> holder = findRecipe(filled);
        if (holder.isEmpty() || !holder.get().id().equals(recipeId)) {
            cancel(level);
            return;
        }
        ItemStack result = holder.get().value().assemble(buildInput(filled), level.registryAccess());

        for (PedestalBlockEntity pedestal : filled) {
            ItemStack remainder = pedestal.getItem().getCraftingRemainingItem(); // напр. порожнє відро
            pedestal.takeItem();
            if (!remainder.isEmpty()) pedestal.placeItem(remainder);
            level.sendParticles(ParticleTypes.POOF, pedestal.getBlockPos().getX() + 0.5,
                    pedestal.getBlockPos().getY() + 1.2, pedestal.getBlockPos().getZ() + 0.5, 5, 0.1, 0.1, 0.1, 0.02);
        }

        resetRitual();
        inventory.setStackInSlot(0, result);

        double cx = worldPosition.getX() + 0.5, cy = worldPosition.getY() + 1.25, cz = worldPosition.getZ() + 0.5;
        level.sendParticles(ParticleTypes.END_ROD, cx, cy, cz, 40, 0.1, 0.1, 0.1, 0.15);
        level.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, cx, cy, cz, 30, 0.3, 0.3, 0.3, 0.3);
        level.playSound(null, worldPosition, SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 0.8f, 1.2f);
        level.playSound(null, worldPosition, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0f, 1.0f);
        onRitualFinished(level);
        sync();
    }

    /** Додаткові ефекти після успішного ритуалу (для Вівтаря Капіталізму). */
    protected void onRitualFinished(ServerLevel level) {
    }

    private void cancel(ServerLevel level) {
        resetRitual();
        level.playSound(null, worldPosition, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 1.0f, 1.0f);
        level.sendParticles(ParticleTypes.SMOKE, worldPosition.getX() + 0.5, worldPosition.getY() + 1.2,
                worldPosition.getZ() + 0.5, 15, 0.2, 0.2, 0.2, 0.02);
        sync();
    }

    private void resetRitual() {
        if (activeRecipe != null && activeRecipe.minRpm() > 0) {
            RotationSource rotation = findRotationSource();
            if (rotation != null) rotation.setRequiredStress(0);
        }
        crafting = false;
        progress = 0;
        duration = 0;
        needMana = 0;
        needSource = 0;
        needEnergy = 0;
        waiting = Waiting.NONE;
        recipeId = null;
        activeRecipe = null;
        activePedestals = List.of();
    }

    private void sync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    // --- збереження ---

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
        tag.put("tank", tank.writeToNBT(registries, new CompoundTag()));
        tag.putBoolean("crafting", crafting);
        tag.putInt("progress", progress);
        tag.putInt("duration", duration);
        if (recipeId != null) tag.putString("recipe", recipeId.toString());
        tag.putInt("mana", mana);
        tag.putInt("source", source);
        tag.putInt("energy", energy.getEnergyStored());
        tag.putInt("need_mana", needMana);
        tag.putInt("need_source", needSource);
        tag.putInt("need_energy", needEnergy);
        tag.putString("waiting", waiting.name());
        tag.putLongArray("pedestals", activePedestals.stream().mapToLong(BlockPos::asLong).toArray());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("inventory")) inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        tank.readFromNBT(registries, tag.getCompound("tank"));
        crafting = tag.getBoolean("crafting");
        progress = tag.getInt("progress");
        duration = tag.getInt("duration");
        recipeId = tag.contains("recipe") ? ResourceLocation.tryParse(tag.getString("recipe")) : null;
        if (crafting && recipeId == null) crafting = false;
        mana = Math.min(tag.getInt("mana"), MANA_CAPACITY);
        source = Math.min(tag.getInt("source"), SOURCE_CAPACITY);
        energy.set(tag.getInt("energy"));
        needMana = tag.getInt("need_mana");
        needSource = tag.getInt("need_source");
        needEnergy = tag.getInt("need_energy");
        try {
            waiting = Waiting.valueOf(tag.getString("waiting"));
        } catch (IllegalArgumentException e) {
            waiting = Waiting.NONE;
        }
        List<BlockPos> positions = new ArrayList<>();
        for (long packed : tag.getLongArray("pedestals")) positions.add(BlockPos.of(packed));
        activePedestals = List.copyOf(positions);
        activeRecipe = null;
    }

    /** Буфер FE: приймає ззовні (кабелі), віддавати назовні не вміє — лише витрачає на ритуал. */
    private class AltarEnergy extends EnergyStorage {
        AltarEnergy() {
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

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
