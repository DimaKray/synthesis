package com.hollingsworth.arsnouveau.api.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.List;

/** СТАБ для компіляції. Справжній клас — в Ars Nouveau 5.13.5. Сигнатура звірена з jar. */
public final class SourceUtil {
    /** Забирає {@code source} джерела з банок у радіусі {@code range}; null — якщо стільки немає. */
    public static List<?> takeSourceMultipleWithParticles(BlockPos pos, Level level, int range, int source) {
        throw new UnsupportedOperationException("stub");
    }

    private SourceUtil() {}
}
