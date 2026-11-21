package ua.krainov.synthesis.blockentity;

import com.mojang.authlib.GameProfile;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import ua.krainov.synthesis.block.SoulStabilizerBlock;
import ua.krainov.synthesis.block.StabilizerTier;
import ua.krainov.synthesis.config.SynthesisConfig;
import ua.krainov.synthesis.core.SoulRules;
import ua.krainov.synthesis.registry.ModBlockEntities;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Стабілізатор душ: душа моба з Draconic Evolution → лут цього моба в сусідній інвентар.
 * Моб створюється лише в пам'яті (у світ не спавниться); кидається його таблиця луту
 * так, ніби його вбив гравець зброєю з «Добичею» потрібного рівня.
 * Draconic не потрібен для компіляції: id моба читаємо з компонента {@code draconicevolution:soul_id}.
 */
public class SoulStabilizerBlockEntity extends BlockEntity {
    private static final ResourceLocation SOUL_COMPONENT = ResourceLocation.fromNamespaceAndPath("draconicevolution", "soul_id");
    private static final TagKey<EntityType<?>> BOSSES =
            TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("c", "bosses"));
    private static final GameProfile FAKE_PROFILE =
            new GameProfile(UUID.fromString("5f1e7a2b-3c4d-4e5f-8a9b-0c1d2e3f4a5b"), "[Synthesis Soul Stabilizer]");
    private static final int MAX_ENERGY_RECEIVE = 200_000;

    public enum Status { NO_SOUL, WORKING, NO_ENERGY, NO_OUTPUT, OUTPUT_FULL, FORBIDDEN, INVALID }

    private final StabilizerTier tier;
    private final ItemStackHandler soul = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return readSoulId(stack) != null;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            cachedMob = null;
            cachedMobId = null;
            progress = 0;
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
            }
        }
    };
    private final MachineEnergy energy;
    /** Лут, що не влізло в сусідні інвентарі — віддаємо першим, нові цикли не починаємо. */
    private final List<ItemStack> pending = new ArrayList<>();
    private int progress;
    private Status status = Status.NO_SOUL;

    @Nullable
    private LivingEntity cachedMob;
    @Nullable
    private ResourceLocation cachedMobId;

    public SoulStabilizerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SOUL_STABILIZER.get(), pos, state);
        this.tier = state.getBlock() instanceof SoulStabilizerBlock b ? b.getTier() : StabilizerTier.BASIC;
        this.energy = new MachineEnergy(tier.energyCapacity);
    }

    // --- доступ ---

    public ItemStackHandler getSoulSlot() {
        return soul;
    }

    /** Для хоперів/труб: душу можна лише вставити, витягти — ні (щоб хопер знизу її не забрав). */
    public IItemHandler getAutomationSlot() {
        return automation;
    }

    private final IItemHandler automation = new IItemHandler() {
        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return soul.getStackInSlot(slot);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return soul.insertItem(slot, stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return soul.isItemValid(slot, stack);
        }
    };

    public EnergyStorage getEnergy() {
        return energy;
    }

    public StabilizerTier getTier() {
        return tier;
    }

    public ItemStack getSoul() {
        return soul.getStackInSlot(0);
    }

    private static final ResourceLocation MOB_SOUL_ITEM = ResourceLocation.fromNamespaceAndPath("draconicevolution", "mob_soul");
    /** Метод MobSoul.getEntity(ItemStack) самого Draconic (через рефлексію — без залежності при компіляції). */
    @Nullable
    private static java.lang.reflect.Method draconicGetEntity;
    private static boolean draconicLookupFailed;

    /** Id моба з предмета-душі Draconic (або null, якщо це не душа). */
    @Nullable
    public static ResourceLocation readSoulId(ItemStack stack) {
        if (stack.isEmpty() || !MOB_SOUL_ITEM.equals(BuiltInRegistries.ITEM.getKey(stack.getItem()))) return null;

        // 1) Питаємо сам Draconic — він знає, як зберігає моба (і що вважати за замовчуванням)
        if (!draconicLookupFailed) {
            try {
                if (draconicGetEntity == null) {
                    draconicGetEntity = stack.getItem().getClass().getMethod("getEntity", ItemStack.class);
                }
                Object result = draconicGetEntity.invoke(stack.getItem(), stack);
                if (result instanceof ResourceLocation rl) return rl;
            } catch (ReflectiveOperationException | RuntimeException e) {
                draconicLookupFailed = true;
                ua.krainov.synthesis.SynthesisMod.LOGGER.warn("Soul Stabilizer: cannot call MobSoul.getEntity, using data component", e);
            }
        }

        // 2) Запасний шлях: компонент draconicevolution:soul_id напряму
        DataComponentType<?> type = BuiltInRegistries.DATA_COMPONENT_TYPE.get(SOUL_COMPONENT);
        if (type == null) return null;
        Object value = stack.get(type);
        if (value instanceof ResourceLocation rl) return rl;
        if (value instanceof String str) return ResourceLocation.tryParse(str);
        return null;
    }

    // --- тік ---

    public static void serverTick(Level level, BlockPos pos, BlockState state, SoulStabilizerBlockEntity be) {
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
        ResourceLocation id = readSoulId(getSoul());
        if (id == null) return Status.NO_SOUL;
        if (!hasOutput(level)) return Status.NO_OUTPUT;

        LivingEntity mob = getMob(level, id);
        if (mob == null) return Status.INVALID;
        if (isForbidden(mob, id)) return Status.FORBIDDEN;

        long cost = SoulRules.cycleCost(mob.getMaxHealth(), SynthesisConfig.SOUL_ENERGY_PER_HEALTH.get(), tier.mobsPerCycle);
        int share = (int) Math.min(Integer.MAX_VALUE,
                cost * (progress + 1) / tier.cycleTicks - cost * progress / tier.cycleTicks);
        if (energy.getEnergyStored() < share) return Status.NO_ENERGY;
        energy.consume(share);
        progress++;

        if (progress % 10 == 0) {
            level.sendParticles(ParticleTypes.SOUL, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5,
                    worldPosition.getZ() + 0.5, 1, 0.25, 0.25, 0.25, 0.01);
        }
        if (progress >= tier.cycleTicks) {
            progress = 0;
            for (int i = 0; i < tier.mobsPerCycle; i++) {
                pending.addAll(rollLoot(level, mob));
            }
            flushPending(level);
            level.sendParticles(ParticleTypes.SCULK_SOUL, worldPosition.getX() + 0.5, worldPosition.getY() + 0.9,
                    worldPosition.getZ() + 0.5, 4, 0.2, 0.1, 0.2, 0.02);
            level.playSound(null, worldPosition, SoundEvents.BEACON_AMBIENT, SoundSource.BLOCKS, 0.3f, 0.6f);
        }
        setChanged();
        return Status.WORKING;
    }

    @Nullable
    private LivingEntity getMob(ServerLevel level, ResourceLocation id) {
        if (cachedMob != null && id.equals(cachedMobId)) return cachedMob;
        cachedMobId = id;
        cachedMob = null;
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(id).orElse(null);
        if (type == null) return null;
        Entity entity = type.create(level); // лише об'єкт у пам'яті, у світ НЕ додається
        if (entity instanceof LivingEntity living) {
            living.setPos(Vec3.atCenterOf(worldPosition));
            cachedMob = living;
        }
        return cachedMob;
    }

    private static boolean isForbidden(LivingEntity mob, ResourceLocation id) {
        if (mob.getType().is(BOSSES)) return true;
        if (SoulRules.isListed(id.toString(), SynthesisConfig.SOUL_BLACKLIST.get())) return true;
        double maxHealth = SynthesisConfig.SOUL_MAX_MOB_HEALTH.get();
        return maxHealth > 0 && mob.getMaxHealth() > maxHealth;
    }

    /** Таблиця луту моба, ніби його вбив гравець зброєю з «Добичею» рівня tier.looting. */
    private List<ItemStack> rollLoot(ServerLevel level, LivingEntity mob) {
        FakePlayer player = FakePlayerFactory.get(level, FAKE_PROFILE);
        player.setPos(Vec3.atCenterOf(worldPosition));
        ItemStack weapon = new ItemStack(Items.NETHERITE_SWORD);
        if (tier.looting > 0) {
            weapon.enchant(level.registryAccess().registryOrThrow(Registries.ENCHANTMENT)
                    .getHolderOrThrow(Enchantments.LOOTING), tier.looting);
        }
        player.setItemInHand(InteractionHand.MAIN_HAND, weapon);

        DamageSource source = level.damageSources().playerAttack(player);
        LootTable table = level.getServer().reloadableRegistries().getLootTable(mob.getLootTable());
        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.THIS_ENTITY, mob)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(worldPosition))
                .withParameter(LootContextParams.DAMAGE_SOURCE, source)
                .withOptionalParameter(LootContextParams.ATTACKING_ENTITY, player)
                .withOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, player)
                .withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player)
                .withLuck(player.getLuck())
                .create(LootContextParamSets.ENTITY);
        List<ItemStack> drops = new ArrayList<>(table.getRandomItems(params));
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        return drops;
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
                ItemStack left = ItemHandlerHelper.insertItemStacked(handler, pending.get(i), false);
                pending.set(i, left);
            }
            pending.removeIf(ItemStack::isEmpty);
        }
    }

    // --- взаємодія ---

    /** Shift+ПКМ: стан машини в чат. */
    public void describe(Player player) {
        ResourceLocation id = readSoulId(getSoul());
        Component mobName = id == null ? Component.literal("-")
                : BuiltInRegistries.ENTITY_TYPE.getOptional(id).map(EntityType::getDescription).orElse(Component.literal(id.toString()));
        player.sendSystemMessage(Component.translatable("message.synthesis.stabilizer.info",
                mobName, energy.getEnergyStored(), energy.getMaxEnergyStored()).withStyle(ChatFormatting.AQUA));
        ChatFormatting color = status == Status.WORKING ? ChatFormatting.GREEN : ChatFormatting.YELLOW;
        player.sendSystemMessage(Component.translatable(
                "message.synthesis.stabilizer.status." + status.name().toLowerCase(Locale.ROOT),
                progress * 100 / tier.cycleTicks).withStyle(color));
        if (cachedMob != null && status != Status.FORBIDDEN) {
            long cost = SoulRules.cycleCost(cachedMob.getMaxHealth(), SynthesisConfig.SOUL_ENERGY_PER_HEALTH.get(), tier.mobsPerCycle);
            player.sendSystemMessage(Component.translatable("message.synthesis.stabilizer.cost",
                    cost, tier.cycleTicks / 20.0, tier.mobsPerCycle, tier.looting).withStyle(ChatFormatting.GRAY));
        }
    }

    // --- збереження ---

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("soul", soul.serializeNBT(registries));
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
        if (tag.contains("soul")) soul.deserializeNBT(registries, tag.getCompound("soul"));
        energy.set(tag.getInt("energy"));
        progress = tag.getInt("progress");
        pending.clear();
        for (Tag t : tag.getList("pending", Tag.TAG_COMPOUND)) {
            ItemStack.parse(registries, t).ifPresent(pending::add);
        }
        cachedMob = null;
        cachedMobId = null;
    }

    /** Клієнту — лише душа (щоб намалювати мініатюру моба). */
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.put("soul", soul.serializeNBT(registries));
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private class MachineEnergy extends EnergyStorage {
        MachineEnergy(int capacity) {
            super(capacity, MAX_ENERGY_RECEIVE, 0);
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
