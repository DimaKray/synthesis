package ua.krainov.synthesis.compat.ars;

import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/** Ars Nouveau: забрати джерело з банок поруч. Викликати лише якщо ModCompat.ARS_NOUVEAU. */
public final class ArsCompat {
    /** true — якщо вдалося забрати рівно {@code amount} джерела (летять частинки від банок). */
    public static boolean takeSource(Level level, BlockPos pos, int range, int amount) {
        return SourceUtil.takeSourceMultipleWithParticles(pos, level, range, amount) != null;
    }

    private ArsCompat() {}
}
