package velrondevs.botania.api.mana;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/** СТАБ для компіляції. Справжній клас — у Botania Revived 2.0.4. Сигнатури звірені з jar. */
public interface ManaReceiver {
    Level getManaReceiverLevel();

    BlockPos getManaReceiverPos();

    int getCurrentMana();

    boolean isFull();

    void receiveMana(int mana);

    boolean canReceiveManaFromBursts();
}
