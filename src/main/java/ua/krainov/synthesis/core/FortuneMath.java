package ua.krainov.synthesis.core;

/** Формула удачі без Minecraft (для запасного режиму «сирої» руди). */
public final class FortuneMath {
    /**
     * Як ванільна функція apply_bonus / ore_drops: count × (max(0, rand(fortune + 2) − 1) + 1).
     * Fortune III у середньому дає ×2.2.
     */
    public static int oreDrops(int count, int fortune, int roll) {
        // roll — випадкове ціле 0..fortune+1 (його кидає виклик, щоб тут не залежати від Minecraft)
        if (fortune <= 0) return count;
        int bonus = roll - 1;
        return count * (Math.max(0, bonus) + 1);
    }

    /** FE за операцію: база + рівень × доплата + (плавка ? доплата : 0). */
    public static long operationCost(int base, int perLevel, int smelt, int fortune, boolean smelting) {
        return (long) base + (long) perLevel * Math.max(0, fortune) + (smelting ? smelt : 0);
    }

    private FortuneMath() {}
}
