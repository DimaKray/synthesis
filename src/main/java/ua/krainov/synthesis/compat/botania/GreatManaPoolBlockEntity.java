package ua.krainov.synthesis.compat.botania;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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

    public GreatManaPoolBlockEntity(BlockPos pos, BlockState state) {
        super(BotaniaMachines.GREAT_POOL_BE.get(), pos, state);
        this.capacity = state.getBlock() instanceof GreatManaPoolBlock b ? b.capacity() : 100_000_000;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, GreatManaPoolBlockEntity pool) {
        if (pool.mana <= 0 || level.hasNeighborSignal(pos)) return;
        int sent = ManaTransfer.pushToNeighbours(level, pos, pool.mana, SynthesisConfig.MANA_TRANSFER_RATE.get(),
                r -> r instanceof GreatManaPoolBlockEntity); // між великими басейнами не ганяємо
        if (sent > 0) {
            pool.mana -= sent;
            pool.setChanged();
        }
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
        mana = Math.min(tag.getInt("mana"), capacity);
    }
}
