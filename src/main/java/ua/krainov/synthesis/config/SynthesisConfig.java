package ua.krainov.synthesis.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

/**
 * Налаштування мода.
 * <ul>
 *   <li>{@code config/synthesis-common.toml} — машини (Стабілізатор душ тощо);</li>
 *   <li>{@code <світ>/serverconfig/synthesis-server.toml} — Капітал-бокси.</li>
 * </ul>
 */
public final class SynthesisConfig {
    // ---------- COMMON ----------
    public static final ModConfigSpec COMMON_SPEC;
    public static final ModConfigSpec.IntValue SOUL_ENERGY_PER_HEALTH;
    public static final ModConfigSpec.DoubleValue SOUL_MAX_MOB_HEALTH;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> SOUL_BLACKLIST;

    // ---------- SERVER ----------
    public static final ModConfigSpec SERVER_SPEC;
    public static final ModConfigSpec.IntValue BOX_INTERVAL_MINUTES;
    public static final ModConfigSpec.DoubleValue BOX_CHANCE;
    public static final ModConfigSpec.IntValue BOX_WEIGHT_COMMON;
    public static final ModConfigSpec.IntValue BOX_WEIGHT_RARE;
    public static final ModConfigSpec.IntValue BOX_WEIGHT_EPIC;
    public static final ModConfigSpec.IntValue BOX_WEIGHT_LEGENDARY;
    public static final ModConfigSpec.DoubleValue BOX_WEEKEND_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue BOX_WEEKEND_MAX_CHANCE;
    public static final ModConfigSpec.IntValue BOX_AFK_MINUTES;
    public static final ModConfigSpec.BooleanValue BOX_ANNOUNCE_LEGENDARY;

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();
        b.comment("Стабілізатор душ / Soul Stabilizer").push("soul_stabilizer");
        SOUL_ENERGY_PER_HEALTH = b
                .comment("FE за один цикл на 1 одиницю здоров'я моба (зомбі = 20 HP).",
                        "FE per cycle per 1 point of mob max health.")
                .defineInRange("energyPerHealth", 250, 0, 10_000_000);
        SOUL_MAX_MOB_HEALTH = b
                .comment("Моби з більшим максимальним здоров'ям не приймаються (запобіжник від босів). 0 = без ліміту.",
                        "Mobs with more max health are refused (safety net against bosses). 0 = no limit.")
                .defineInRange("maxMobHealth", 300.0, 0.0, 1_000_000.0);
        SOUL_BLACKLIST = b
                .comment("Заборонені моби (на додачу до тегу #c:bosses). Можна 'modid:*' — увесь мод.",
                        "Forbidden mobs (in addition to the #c:bosses tag). 'modid:*' bans a whole mod.")
                .defineListAllowEmpty("blacklist", List.of(
                        "minecraft:wither", "minecraft:ender_dragon", "minecraft:warden", "minecraft:elder_guardian",
                        "draconicevolution:draconic_guardian", "draconicevolution:guardian_wither",
                        "botania:doppleganger", "botania:pink_wither",
                        "ars_nouveau:wilden_boss",
                        "cataclysm:old_netherite_monstrosity", "cataclysm:ancient_ancient_remnant",
                        "cataclysm:the_leviathan_immune",
                        "neovitae:daemonium_ignis",
                        "irons_spellbooks:dead_king", "irons_spellbooks:dead_king_corpse", "irons_spellbooks:fire_boss",
                        "extrabotany:gaia_legacy", "extrabotany:gaia_3rd", "extrabotany:ego", "extrabotany:void_herrscher"
                ), () -> "minecraft:example", o -> o instanceof String);
        b.pop();
        COMMON_SPEC = b.build();

        ModConfigSpec.Builder s = new ModConfigSpec.Builder();
        s.comment("Капітал-бокси / Capital Boxes").push("capital_boxes");
        BOX_INTERVAL_MINUTES = s
                .comment("Кожні N хвилин АКТИВНОЇ гри — кидок шансу отримати бокс.",
                        "Every N minutes of ACTIVE play a box roll happens.")
                .defineInRange("intervalMinutes", 60, 1, 24 * 60);
        BOX_CHANCE = s.comment("Шанс отримати бокс при кидку (0..1).", "Chance to get a box per roll.")
                .defineInRange("chance", 0.35, 0.0, 1.0);
        BOX_WEIGHT_COMMON = s.comment("Ваги рідкостей / rarity weights").defineInRange("weightCommon", 70, 0, 10_000);
        BOX_WEIGHT_RARE = s.defineInRange("weightRare", 22, 0, 10_000);
        BOX_WEIGHT_EPIC = s.defineInRange("weightEpic", 7, 0, 10_000);
        BOX_WEIGHT_LEGENDARY = s.defineInRange("weightLegendary", 1, 0, 10_000);
        BOX_WEEKEND_MULTIPLIER = s
                .comment("У суботу й неділю (час сервера) шанс множиться на це число.",
                        "On Saturday and Sunday (server time) the chance is multiplied by this.")
                .defineInRange("weekendMultiplier", 1.5, 1.0, 10.0);
        BOX_WEEKEND_MAX_CHANCE = s.comment("Максимальний шанс у вихідні.", "Max chance on weekends.")
                .defineInRange("weekendMaxChance", 0.5, 0.0, 1.0);
        BOX_AFK_MINUTES = s
                .comment("Якщо гравець N хвилин не рухався й нічого не робив — лічильник стоїть (AFK).",
                        "If a player did nothing for N minutes, the timer stops (AFK).")
                .defineInRange("afkTimeoutMinutes", 5, 1, 120);
        BOX_ANNOUNCE_LEGENDARY = s.comment("Оголошувати легендарний бокс усім гравцям.", "Announce legendary boxes to everyone.")
                .define("announceLegendary", true);
        s.pop();
        SERVER_SPEC = s.build();
    }

    private SynthesisConfig() {}
}
