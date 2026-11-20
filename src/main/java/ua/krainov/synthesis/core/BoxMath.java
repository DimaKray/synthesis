package ua.krainov.synthesis.core;

/** Математика Капітал-боксів без Minecraft (шанси, ваги рідкостей, AFK). */
public final class BoxMath {
    /** Шанс з урахуванням вихідних: base × множник, але не більше max (і не менше base). */
    public static double effectiveChance(double base, boolean weekend, double multiplier, double max) {
        if (!weekend) return base;
        return Math.max(base, Math.min(base * multiplier, max));
    }

    /**
     * Вибір за вагами. {@code roll} — випадкове число 0..1.
     * Повертає індекс; якщо всі ваги 0 — повертає 0.
     */
    public static int pickWeighted(int[] weights, double roll) {
        long total = 0;
        for (int w : weights) total += Math.max(0, w);
        if (total <= 0) return 0;
        double target = roll * total;
        long acc = 0;
        for (int i = 0; i < weights.length; i++) {
            acc += Math.max(0, weights[i]);
            if (target < acc) return i;
        }
        return weights.length - 1;
    }

    private BoxMath() {}
}
