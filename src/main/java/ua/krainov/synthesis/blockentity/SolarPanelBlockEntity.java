package ua.krainov.synthesis.blockentity;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import ua.krainov.synthesis.block.SolarPanelBlock;
import ua.krainov.synthesis.block.SolarTier;
import ua.krainov.synthesis.config.SynthesisConfig;
import ua.krainov.synthesis.registry.ModBlockEntities;

/**
 * Капітал-панель: виробляє FE, коли видно небо (вдень більше, вночі менше, у дощ ×0.5),
 * і сама віддає енергію в усі сусідні блоки, що її приймають. Кабелі можуть і забирати.
 */
public class SolarPanelBlockEntity extends BlockEntity {
    private final SolarTier tier;
    private final PanelEnergy energy;
    /** Скільки FE/т зараз виробляється (перераховується раз на секунду). */
    private int currentOutput;

    public SolarPanelBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SOLAR_PANEL.get(), pos, state);
        this.tier = state.getBlock() instanceof SolarPanelBlock b ? b.getTier() : SolarTier.HYBRID;
        this.energy = new PanelEnergy(tier.bufferCapacity());
    }

    public IEnergyStorage getEnergy() {
        return energy;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SolarPanelBlockEntity be) {
        if (level instanceof ServerLevel serverLevel) be.tick(serverLevel);
    }

    private void tick(ServerLevel level) {
        if (level.getGameTime() % 20 == 0) {
            currentOutput = computeOutput(level);
        }
        if (currentOutput > 0) energy.generate(currentOutput);
        if (energy.getEnergyStored() > 0) pushEnergy(level);
    }

    private int computeOutput(ServerLevel level) {
        if (!level.dimensionType().hasSkyLight() || !level.canSeeSky(worldPosition.above())) return 0;
        int base = level.isDay() ? tier.dayOutput() : tier.nightOutput();
        if (!tier.ignoresRain && (level.isRaining() || level.isThundering())) {
            base = (int) (base * SynthesisConfig.SOLAR_RAIN_MULTIPLIER.get());
        }
        return base;
    }

    private void pushEnergy(ServerLevel level) {
        for (Direction dir : Direction.values()) {
            if (energy.getEnergyStored() <= 0) return;
            IEnergyStorage target = level.getCapability(Capabilities.EnergyStorage.BLOCK,
                    worldPosition.relative(dir), dir.getOpposite());
            if (target == null || !target.canReceive()) continue;
            int sent = target.receiveEnergy(energy.getEnergyStored(), false);
            if (sent > 0) energy.extractEnergy(sent, false);
        }
    }

    /** ПКМ: стан у чат. */
    public void describe(Player player) {
        player.sendSystemMessage(Component.translatable("message.synthesis.solar.info",
                currentOutput, tier.dayOutput(), tier.nightOutput(), energy.getEnergyStored(), energy.getMaxEnergyStored())
                .withStyle(currentOutput > 0 ? ChatFormatting.GREEN : ChatFormatting.YELLOW));
        if (currentOutput == 0) {
            player.sendSystemMessage(Component.translatable("message.synthesis.solar.no_sky").withStyle(ChatFormatting.GRAY));
        }
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

    /** Буфер: ззовні лише забирають (receive = 0), сама панель додає енергію через generate(). */
    private class PanelEnergy extends EnergyStorage {
        PanelEnergy(int capacity) {
            super(capacity, 0, capacity);
        }

        void generate(int amount) {
            int before = energy;
            energy = (int) Math.min(capacity, (long) energy + amount);
            if (energy != before) setChanged();
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int extracted = super.extractEnergy(maxExtract, simulate);
            if (extracted > 0 && !simulate) setChanged();
            return extracted;
        }

        void set(int value) {
            energy = Math.max(0, Math.min(capacity, value));
        }
    }
}
