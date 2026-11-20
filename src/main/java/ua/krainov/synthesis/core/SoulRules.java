package ua.krainov.synthesis.core;

import java.util.List;

/**
 * Правила чорного списку Стабілізатора душ — чиста логіка без Minecraft (для переносу на 26.x).
 * Запис списку: повний id ({@code "minecraft:wither"}) або весь мод ({@code "cataclysm:*"}).
 */
public final class SoulRules {
    public static boolean isListed(String entityId, List<? extends String> blacklist) {
        int colon = entityId.indexOf(':');
        String namespace = colon >= 0 ? entityId.substring(0, colon) : "minecraft";
        for (String entry : blacklist) {
            String e = entry.trim();
            if (e.isEmpty()) continue;
            if (e.equals(entityId)) return true;
            if (e.endsWith(":*") && e.substring(0, e.length() - 2).equals(namespace)) return true;
        }
        return false;
    }

    /** Енергія на один цикл: здоров'я × FE за HP × кількість мобів. */
    public static long cycleCost(double maxHealth, int energyPerHealth, int mobs) {
        return Math.max(0L, Math.round(maxHealth * energyPerHealth) * mobs);
    }

    private SoulRules() {}
}
