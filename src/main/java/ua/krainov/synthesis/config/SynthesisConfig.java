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

    public static final ModConfigSpec.IntValue FORTUNE_TICKS;
    public static final ModConfigSpec.IntValue FORTUNE_ENERGY_BASE;
    public static final ModConfigSpec.IntValue FORTUNE_ENERGY_PER_LEVEL;
    public static final ModConfigSpec.IntValue FORTUNE_ENERGY_SMELT;
    public static final ModConfigSpec.BooleanValue FORTUNE_RAW_MODE;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> FORTUNE_BLACKLIST;

    public static final ModConfigSpec.IntValue ACCEL_ENERGY_PER_TICK;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ACCEL_BLACKLIST;

    public static final ModConfigSpec.IntValue[] SOLAR_DAY = new ModConfigSpec.IntValue[4];
    public static final ModConfigSpec.IntValue[] SOLAR_NIGHT = new ModConfigSpec.IntValue[4];
    public static final ModConfigSpec.DoubleValue SOLAR_RAIN_MULTIPLIER;

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

        b.comment("Перетворювач удачі / Fortune Converter").push("fortune_converter");
        FORTUNE_TICKS = b.comment("Тіків на одну руду (20 = 1 с).", "Ticks per ore block.")
                .defineInRange("ticksPerOperation", 20, 1, 1200);
        FORTUNE_ENERGY_BASE = b.comment("FE за одну руду.", "FE per ore block.")
                .defineInRange("energyBase", 2000, 0, 10_000_000);
        FORTUNE_ENERGY_PER_LEVEL = b.comment("Додатково FE за кожен рівень удачі.", "Extra FE per Fortune level.")
                .defineInRange("energyPerFortuneLevel", 1000, 0, 10_000_000);
        FORTUNE_ENERGY_SMELT = b.comment("Додатково FE, якщо стоїть Модуль плавки.", "Extra FE with the Smelting Module.")
                .defineInRange("energySmelting", 1000, 0, 10_000_000);
        FORTUNE_RAW_MODE = b
                .comment("Запасний режим: приймати «сиру» руду (raw_*) і самоцвіти й множити їх за формулою Fortune.",
                        "Fallback: accept raw ores and gems and multiply them with the vanilla Fortune formula.")
                .define("rawOreMode", false);
        FORTUNE_BLACKLIST = b.comment("Заборонені блоки/предмети (id або 'modid:*').", "Forbidden ores (id or 'modid:*').")
                .defineListAllowEmpty("blacklist", List.of(), () -> "minecraft:example", o -> o instanceof String);
        b.pop();
        b.comment("Прискорювач / Accelerator").push("accelerator");
        ACCEL_ENERGY_PER_TICK = b
                .comment("FE за кожен ДОДАТКОВИЙ тік однієї машини (×4 = 3 додаткові тіки щотіку).",
                        "FE per EXTRA tick of one machine (x4 = 3 extra ticks every tick).")
                .defineInRange("energyPerExtraTick", 40, 0, 1_000_000);
        ACCEL_BLACKLIST = b
                .comment("Блоки, які НЕ прискорюються (id або 'modid:*'). Генератори краще тримати тут —",
                        "інакше Прискорювач стане вічним двигуном. Ще можна додати блок у тег #synthesis:accelerator_blacklist.",
                        "Blocks that are NOT accelerated (id or 'modid:*'). Keep generators here.")
                .defineListAllowEmpty("blacklist", List.of(
                        "minecraft:spawner", "minecraft:trial_spawner", "minecraft:vault",
                        "apotheosis:boss_spawner",
                        "draconicevolution:reactor_core", "draconicevolution:reactor_stabilizer",
                        "draconicevolution:reactor_injector", "draconicevolution:stabilized_spawner",
                        "draconicevolution:crafting_core", "draconicevolution:energy_core",
                        "avaritia:neutron_collector", "avaritia:dense_neutron_collector",
                        "avaritia:denser_neutron_collector", "avaritia:densest_neutron_collector",
                        "industrialforegoing:ore_laser_base", "industrialforegoing:fluid_laser_base",
                        "industrialforegoing:laser_drill",
                        "ars_nouveau:ritual_brazier", "neovitae:master_ritual_stone", "neovitae:inverted_master_ritual_stone",
                        "oritech:spawner_controller_block", "oritech:reactor_controller",
                        "powah:reactor_starter", "powah:reactor_basic", "powah:reactor_hardened", "powah:reactor_blazing",
                        "powah:reactor_niotic", "powah:reactor_spirited", "powah:reactor_nitro"
                ), () -> "minecraft:example", o -> o instanceof String);
        b.pop();
        b.comment("Капітал-панелі / Capital solar panels (FE за тік)").push("solar_panels");
        String[] names = {"hybrid", "ultimate", "quantum", "infinite"};
        int[] day = {64, 512, 4096, 65536};
        int[] night = {8, 64, 512, 65536};
        for (int i = 0; i < 4; i++) {
            SOLAR_DAY[i] = b.defineInRange(names[i] + "Day", day[i], 0, Integer.MAX_VALUE / 2);
            SOLAR_NIGHT[i] = b.defineInRange(names[i] + "Night", night[i], 0, Integer.MAX_VALUE / 2);
        }
        SOLAR_RAIN_MULTIPLIER = b.comment("Множник у дощ/грозу (Нескінченна панель його ігнорує).",
                        "Rain multiplier (the Infinite panel ignores it).")
                .defineInRange("rainMultiplier", 0.5, 0.0, 1.0);
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
