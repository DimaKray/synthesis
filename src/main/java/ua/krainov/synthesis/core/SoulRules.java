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

    /** Предмет із кількістю в перевизначенні луту. */
    public record Drop(String itemId, int count) {}

    /**
     * Знаходить для моба рядок 'моб=предмет*кількість,...' і повертає дроп; null — якщо перевизначення немає.
     */
    public static List<Drop> lootOverride(String entityId, List<? extends String> lines) {
        for (String raw : lines) {
            String line = raw.trim();
            int eq = line.indexOf('=');
            if (eq <= 0 || !line.substring(0, eq).trim().equals(entityId)) continue;
            List<Drop> drops = new java.util.ArrayList<>();
            for (String part : line.substring(eq + 1).split(",")) {
                String p = part.trim();
                if (p.isEmpty()) continue;
                int star = p.indexOf('*');
                String item = star > 0 ? p.substring(0, star).trim() : p;
                int count = 1;
                if (star > 0) {
                    try {
                        count = Math.max(1, Integer.parseInt(p.substring(star + 1).trim()));
                    } catch (NumberFormatException ignored) {
                        // кривий рядок — лишаємо 1
                    }
                }
                drops.add(new Drop(item, count));
            }
            return drops;
        }
        return null;
    }

    private SoulRules() {}
}
