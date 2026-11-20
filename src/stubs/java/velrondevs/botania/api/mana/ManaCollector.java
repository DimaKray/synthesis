package velrondevs.botania.api.mana;

import velrondevs.botania.api.internal.ManaBurst;

/** СТАБ для компіляції. Справжній — у Botania Revived 2.0.4 (до нього прив'язуються генеруючі квіти). */
public interface ManaCollector extends ManaReceiver {
    void onClientDisplayTick();

    float getManaYieldMultiplier(ManaBurst burst);

    int getMaxMana();
}
