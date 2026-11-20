package ua.krainov.synthesis.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Розбір списку квітів Мультитеплиці з конфігу: "id=мана_за_секунду[:fuel]". Без Minecraft. */
public final class FlowerRates {
    public record Rate(double manaPerSecond, boolean needsFuel) {}

    public static Map<String, Rate> parse(List<? extends String> lines) {
        Map<String, Rate> map = new HashMap<>();
        for (String raw : lines) {
            String line = raw.trim();
            int eq = line.indexOf('=');
            if (eq <= 0) continue;
            String id = line.substring(0, eq).trim();
            String rest = line.substring(eq + 1).trim();
            boolean fuel = rest.endsWith(":fuel");
            if (fuel) rest = rest.substring(0, rest.length() - 5);
            try {
                map.put(id, new Rate(Double.parseDouble(rest), fuel));
            } catch (NumberFormatException ignored) {
                // кривий рядок у конфігу — пропускаємо
            }
        }
        return map;
    }

    /** "botania:floating_endoflame" → "botania:endoflame". */
    public static String normalize(String id) {
        return id.replace(":floating_", ":");
    }

    private FlowerRates() {}
}
