package ua.krainov.synthesis.blockentity;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import ua.krainov.synthesis.SynthesisMod;
import ua.krainov.synthesis.block.AcceleratorBlock;
import ua.krainov.synthesis.block.AcceleratorTier;
import ua.krainov.synthesis.config.SynthesisConfig;
import ua.krainov.synthesis.core.SoulRules;
import ua.krainov.synthesis.registry.ModBlockEntities;

import java.util.HashSet;
import java.util.Set;

/**
 * Прискорювач: щотіку додатково «тікає» машини з усіх 6 сторін (multiplier − 1 разів),
 * за кожен додатковий тік — FE. Сигнал редстоуну вимикає. Чорний список — конфіг + тег.
 */
public class AcceleratorBlockEntity extends BlockEntity {
    public static final TagKey<Block> BLACKLIST_TAG =
            TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(SynthesisMod.MODID, "accelerator_blacklist"));
    private static final int MAX_ENERGY_RECEIVE = 500_000;

    public enum Status { IDLE, WORKING, NO_ENERGY, DISABLED }

    private final AcceleratorTier tier;
    private final MachineEnergy energy;
    private Status status = Status.IDLE;
    private int acceleratedCount;
    /** Машини, чий тікер упав з помилкою, — більше не чіпаємо (до перезавантаження світу). */
    private final Set<BlockPos> broken = new HashSet<>();

    public AcceleratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ACCELERATOR.get(), pos, state);
        this.tier = state.getBlock() instanceof AcceleratorBlock b ? b.getTier() : AcceleratorTier.X2;
        this.energy = new MachineEnergy(tier.energyCapacity);
    }

    public EnergyStorage getEnergy() {
        return energy;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AcceleratorBlockEntity be) {
        if (level instanceof ServerLevel serverLevel) be.tick(serverLevel);
    }

    private void tick(ServerLevel level) {
        if (level.hasNeighborSignal(worldPosition)) {
            status = Status.DISABLED;
            acceleratedCount = 0;
            return;
        }
        int extra = tier.multiplier - 1;
        int costPerMachine = extra * SynthesisConfig.ACCEL_ENERGY_PER_TICK.get();
        int count = 0;
        boolean starved = false;
        for (Direction dir : Direction.values()) {
            BlockPos target = worldPosition.relative(dir);
            if (broken.contains(target)) continue;
            BlockState state = level.getBlockState(target);
            BlockEntity be = level.getBlockEntity(target);
            if (be == null || be instanceof AcceleratorBlockEntity || isBlacklisted(state)) continue;
            BlockEntityTicker<BlockEntity> ticker = tickerFor(level, state, be);
            if (ticker == null) continue;
            if (energy.getEnergyStored() < costPerMachine) {
                starved = true;
                break;
            }
            energy.consume(costPerMachine);
            try {
                for (int i = 0; i < extra && !be.isRemoved(); i++) {
                    ticker.tick(level, target, state, be);
                }
            } catch (RuntimeException e) {
                broken.add(target.immutable());
                SynthesisMod.LOGGER.warn("Accelerator at {}: ticker of {} at {} failed, skipping it", worldPosition,
                        BuiltInRegistries.BLOCK.getKey(state.getBlock()), target, e);
                continue;
            }
            count++;
            if (level.getGameTime() % 20 == 0) {
                level.sendParticles(ParticleTypes.ELECTRIC_SPARK, target.getX() + 0.5, target.getY() + 0.5,
                        target.getZ() + 0.5, 2 + tier.multiplier / 4, 0.4, 0.4, 0.4, 0.05);
            }
        }
        acceleratedCount = count;
        status = count > 0 ? Status.WORKING : (starved ? Status.NO_ENERGY : Status.IDLE);
        if (count > 0) setChanged();
    }

    @SuppressWarnings("unchecked")
    private static BlockEntityTicker<BlockEntity> tickerFor(Level level, BlockState state, BlockEntity be) {
        return (BlockEntityTicker<BlockEntity>) state.getTicker(level, (BlockEntityType<BlockEntity>) be.getType());
    }

    private static boolean isBlacklisted(BlockState state) {
        if (state.is(BLACKLIST_TAG)) return true;
        return SoulRules.isListed(BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString(),
                SynthesisConfig.ACCEL_BLACKLIST.get());
    }

    /** ПКМ: стан у чат. */
    public void describe(Player player) {
        player.sendSystemMessage(Component.translatable("message.synthesis.accelerator.info",
                tier.multiplier, energy.getEnergyStored(), energy.getMaxEnergyStored(), acceleratedCount)
                .withStyle(ChatFormatting.AQUA));
        player.sendSystemMessage(Component.translatable("message.synthesis.accelerator.status." + status.name().toLowerCase(java.util.Locale.ROOT),
                (tier.multiplier - 1) * SynthesisConfig.ACCEL_ENERGY_PER_TICK.get())
                .withStyle(status == Status.WORKING ? ChatFormatting.GREEN : ChatFormatting.YELLOW));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("energy", energy.getEnergyStored());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        energy.set(tag.getInt("energy"));
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
