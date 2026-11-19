package ua.krainov.synthesis.compat;

import net.neoforged.fml.ModList;

/**
 * Які моди встановлено. Класи з compat/botania, compat/ars тощо можна викликати
 * ЛИШЕ якщо відповідний прапорець true — інакше гра впаде (немає класів чужого мода).
 */
public final class ModCompat {
    public static final boolean BOTANIA = ModList.get().isLoaded("botania");
    public static final boolean ARS_NOUVEAU = ModList.get().isLoaded("ars_nouveau");
    public static final boolean CREATE = ModList.get().isLoaded("create");

    private ModCompat() {}
}
