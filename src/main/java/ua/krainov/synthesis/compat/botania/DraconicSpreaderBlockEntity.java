package ua.krainov.synthesis.compat.botania;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import velrondevs.botania.api.internal.ManaBurst;
import velrondevs.botania.api.mana.ManaCollector;
import velrondevs.botania.api.mana.ManaReceiver;
import ua.krainov.synthesis.config.SynthesisConfig;

/**
 * Драконієвий розповсюджувач: величезний буфер (25 млн) і пряма передача мани без пострілів.
 * Генеруючі квіти прив'язуються до нього, як до звичайного розповсюджувача (це ManaCollector).
 * Ціль задається Скіпетром: ПКМ по розповсюджувачу, потім ПКМ по приймачу мани.
 */
public class DraconicSpreaderBlockEntity extends BlockEntity implements ManaCollector, ManaSpace {
    public static final int CAPACITY = 25_000_000;
    private static final Vector3f BEAM_COLOR = new Vector3f(0.7f, 0.35f, 1.0f);

    private int mana;
    @Nullable
    private BlockPos target;

    public DraconicSpreaderBlockEntity(BlockPos pos, BlockState state) {
        super(BotaniaMachines.DRACONIC_SPREADER_BE.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, DraconicSpreaderBlockEntity spreader) {
        if (spreader.mana <= 0 || spreader.target == null || level.hasNeighborSignal(pos)) return;
        if (!spreader.target.closerThan(pos, SynthesisConfig.SPREADER_RANGE.get())) return;
        if (!level.isLoaded(spreader.target)) return;
        ManaReceiver receiver = ManaTransfer.find(level, spreader.target, null);
        if (receiver == null || receiver == spreader) return;
        int sent = ManaTransfer.give(receiver, Math.min(spreader.mana, SynthesisConfig.MANA_TRANSFER_RATE.get()));
        if (sent > 0) {
            spreader.mana -= sent;
            spreader.setChanged();
            if (level.getGameTime() % 10 == 0 && level instanceof ServerLevel server) spreader.beam(server);
        }
    }

    /** Промінь частинок від розповсюджувача до цілі. */
    private void beam(ServerLevel level) {
        double sx = worldPosition.getX() + 0.5, sy = worldPosition.getY() + 0.5, sz = worldPosition.getZ() + 0.5;
        double dx = target.getX() + 0.5 - sx, dy = target.getY() + 0.5 - sy, dz = target.getZ() + 0.5 - sz;
        int steps = (int) Math.max(4, Math.sqrt(dx * dx + dy * dy + dz * dz) * 2);
        DustParticleOptions dust = new DustParticleOptions(BEAM_COLOR, 1.0f);
        for (int i = 0; i <= steps; i++) {
            double t = i / (double) steps;
            level.sendParticles(dust, sx + dx * t, sy + dy * t, sz + dz * t, 1, 0, 0, 0, 0);
        }
    }

    public void setTarget(@Nullable BlockPos target) {
        this.target = target == null ? null : target.immutable();
        setChanged();
    }

    public void describe(Player player) {
        Component targetText = target == null ? Component.translatable("message.synthesis.spreader.no_target")
                : Component.literal(target.getX() + " " + target.getY() + " " + target.getZ());
        player.sendSystemMessage(Component.translatable("message.synthesis.spreader.info",
                String.format("%,d", mana), String.format("%,d", CAPACITY), targetText).withStyle(ChatFormatting.AQUA));
    }

    // --- ManaCollector / ManaReceiver ---

    @Override
    public void onClientDisplayTick() {
    }

    @Override
    public float getManaYieldMultiplier(ManaBurst burst) {
        return 1.0f;
    }

    @Override
    public int getMaxMana() {
        return CAPACITY;
    }

    @Override
    public Level getManaReceiverLevel() {
        return level;
    }

    @Override
    public BlockPos getManaReceiverPos() {
        return worldPosition;
    }

    @Override
    public int getCurrentMana() {
        return mana;
    }

    @Override
    public boolean isFull() {
        return mana >= CAPACITY;
    }

    @Override
    public void receiveMana(int amount) {
        int updated = (int) Math.max(0, Math.min(CAPACITY, (long) mana + amount));
        if (updated != mana) {
            mana = updated;
            setChanged();
        }
    }

    @Override
    public boolean canReceiveManaFromBursts() {
        return true;
    }

    @Override
    public int manaSpace() {
        return CAPACITY - mana;
    }

    // --- збереження ---

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("mana", mana);
        if (target != null) tag.putLong("target", target.asLong());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        mana = Math.min(tag.getInt("mana"), CAPACITY);
        target = tag.contains("target") ? BlockPos.of(tag.getLong("target")) : null;
    }
}
