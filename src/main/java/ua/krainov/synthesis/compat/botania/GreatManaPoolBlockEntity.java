package ua.krainov.synthesis.compat.botania;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import velrondevs.botania.api.mana.ManaReceiver;
import ua.krainov.synthesis.config.SynthesisConfig;

/**
 * Великий басейн мани: величезний запас (100 млн / 500 млн / 2 млрд). Приймає постріли розповсюджувачів,
 * сам роздає ману сусіднім приймачам (розповсюджувачам, вівтарям, басейнам Ботанії). Редстоун — лише зберігати.
 */
public class GreatManaPoolBlockEntity extends BlockEntity implements ManaReceiver, ManaSpace {
    private final int capacity;
    private int mana;
    /** Заповненість, яку бачить клієнт (0..1) — для малювання рівня мани. */
    private float clientFill;
    private int lastSyncedPercent = -1;

    public GreatManaPoolBlockEntity(BlockPos pos, BlockState state) {
        super(BotaniaMachines.GREAT_POOL_BE.get(), pos, state);
        this.capacity = state.getBlock() instanceof GreatManaPoolBlock b ? b.capacity() : 100_000_000;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, GreatManaPoolBlockEntity pool) {
        // Раз на пів секунди повідомляємо клієнтів, якщо рівень мани змінився хоча б на 1%
        if (level.getGameTime() % 10 == 0) {
            int percent = (int) (100L * pool.mana / Math.max(1, pool.capacity));
            if (percent != pool.lastSyncedPercent) {
                pool.lastSyncedPercent = percent;
                level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
            }
        }
        if (pool.mana <= 0 || level.hasNeighborSignal(pos)) return;
        int sent = ManaTransfer.pushToNeighbours(level, pos, pool.mana, SynthesisConfig.MANA_TRANSFER_RATE.get(),
                r -> r instanceof GreatManaPoolBlockEntity); // між великими басейнами не ганяємо
        if (sent > 0) {
            pool.mana -= sent;
            pool.setChanged();
        }
    }

    public float getClientFill() {
        return clientFill;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("fill", capacity <= 0 ? 0 : (float) mana / capacity);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public int getCapacity() {
        return capacity;
    }

    public int comparatorSignal() {
        return capacity <= 0 ? 0 : (int) Math.round(15.0 * mana / capacity);
    }

    public void describe(Player player) {
        player.sendSystemMessage(Component.translatable("message.synthesis.great_pool.info",
                String.format("%,d", mana), String.format("%,d", capacity)).withStyle(ChatFormatting.AQUA));
    }

    // --- ManaReceiver ---

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
        return mana >= capacity;
    }

    @Override
    public void receiveMana(int amount) {
        int updated = (int) Math.max(0, Math.min(capacity, (long) mana + amount));
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
        return capacity - mana;
    }

    // --- збереження ---

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("mana", mana);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("mana")) mana = Math.min(tag.getInt("mana"), capacity);
        if (tag.contains("fill")) clientFill = tag.getFloat("fill");
        else clientFill = capacity <= 0 ? 0 : (float) mana / capacity;
    }
}
