package velrondevs.botania.api.mana;

/** СТАБ для компіляції (використовуємо лише getMaxMana, щоб не переливати басейни Ботанії). */
public interface ManaPool extends ManaReceiver {
    boolean isOutputtingPower();

    int getMaxMana();
}
