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
    public static final ModConfigSpec.ConfigValue<List<? extends String>> SOUL_BLACKLIST;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> SOUL_EXTRA_BOSSES;
    public static final ModConfigSpec.IntValue SOUL_BOSS_TIME_MULTIPLIER;
    public static final ModConfigSpec.IntValue SOUL_BOSS_ENERGY_MULTIPLIER;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> SOUL_LOOT_OVERRIDES;

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

    public static final ModConfigSpec.IntValue PROCESSOR_TICKS;
    public static final ModConfigSpec.IntValue PROCESSOR_ENERGY_PER_ITEM;

    public static final ModConfigSpec.IntValue MANA_TRANSFER_RATE;
    public static final ModConfigSpec.IntValue SPREADER_RANGE;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> GREENHOUSE_FLOWERS;

    public static final ModConfigSpec.IntValue ASSEMBLER_ENERGY_PER_CRAFT;

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
        SOUL_BLACKLIST = b
                .comment("Моби, яких НЕ можна симулювати (id або 'modid:*'). За замовчуванням порожньо — боси дозволені.",
                        "Mobs that can NOT be simulated (id or 'modid:*'). Empty by default — bosses are allowed.")
                .defineListAllowEmpty("simulationBlacklist", List.of(), () -> "minecraft:example", o -> o instanceof String);
        SOUL_EXTRA_BOSSES = b
                .comment("Хто вважається босом на додачу до тегу #c:bosses (душа боса — лише Пробуджений/Хаотичний).",
                        "Extra bosses besides the #c:bosses tag (boss souls need Awakened/Chaotic).")
                .defineListAllowEmpty("extraBosses", List.of(
                        "minecraft:wither", "minecraft:ender_dragon", "minecraft:warden", "minecraft:elder_guardian",
                        "draconicevolution:draconic_guardian"), () -> "minecraft:example", o -> o instanceof String);
        SOUL_BOSS_TIME_MULTIPLIER = b.comment("У скільки разів довший цикл для боса.", "Cycle length multiplier for bosses.")
                .defineInRange("bossCycleMultiplier", 5, 1, 1000);
        SOUL_BOSS_ENERGY_MULTIPLIER = b.comment("У скільки разів більше FE для боса.", "FE multiplier for bosses.")
                .defineInRange("bossEnergyMultiplier", 10, 1, 100_000);
        SOUL_LOOT_OVERRIDES = b
                .comment("Свій дроп замість таблиці луту (для мобів, чий лут видається в коді, а не в таблиці).",
                        "Формат: 'моб=предмет*кількість,предмет*кількість'.",
                        "Custom drops instead of the loot table. Format: 'mob=item*count,item*count'.")
                .defineListAllowEmpty("bossLootOverrides", List.of(
                        "minecraft:ender_dragon=minecraft:dragon_breath*4,minecraft:experience_bottle*16",
                        "minecraft:wither=minecraft:nether_star*1",
                        "draconicevolution:draconic_guardian=draconicevolution:chaos_shard*1"
                ), () -> "minecraft:example=minecraft:stone*1", o -> o instanceof String);
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
        b.comment("Промисловий процесор / Industrial Processor").push("industrial_processor");
        PROCESSOR_TICKS = b.comment("Тіків на одну операцію (стак за раз).", "Ticks per operation (a whole stack).")
                .defineInRange("ticksPerOperation", 100, 1, 6000);
        PROCESSOR_ENERGY_PER_ITEM = b.comment("FE за кожен перероблений предмет.", "FE per processed item.")
                .defineInRange("energyPerItem", 200, 0, 1_000_000);
        b.pop();
        b.comment("Машини мани (Ботанія) / Mana machines (Botania)").push("mana");
        MANA_TRANSFER_RATE = b
                .comment("Скільки мани за тік Великий басейн / Драконієвий розповсюджувач / Мультитеплиця віддають в один приймач.",
                        "Mana per tick the Great Pool / Draconic Spreader / Multi-Greenhouse give to one receiver.")
                .defineInRange("transferRate", 100_000, 1, 100_000_000);
        SPREADER_RANGE = b.comment("Максимальна відстань прив'язки Драконієвого розповсюджувача (блоків).",
                        "Max binding distance of the Draconic Spreader (blocks).")
                .defineInRange("spreaderRange", 64, 4, 256);
        GREENHOUSE_FLOWERS = b
                .comment("Квіти Мультитеплиці: 'id=мана_за_секунду' або 'id=мана:fuel' (квітці потрібне паливо печі).",
                        "Варіанти floating_* рахуються як звичайні.",
                        "Multi-Greenhouse flowers: 'id=mana_per_second' or 'id=mana:fuel' (needs furnace fuel).")
                .defineListAllowEmpty("greenhouseFlowers", List.of(
                        "botania:endoflame=60:fuel", "botania:hydroangeas=20", "botania:thermalily=400:fuel",
                        "botania:rosa_arcana=100", "botania:munchdew=150", "botania:entropinnyum=500:fuel",
                        "botania:kekimurus=300", "botania:gourmaryllis=200", "botania:narslimmus=250",
                        "botania:spectrolus=300", "botania:dandelifeon=500", "botania:rafflowsia=600",
                        "botania:shulk_me_not=800"
                ), () -> "botania:example=10", o -> o instanceof String);
        b.pop();
        b.comment("Квантовий збирач 9×9 / Quantum Assembler").push("quantum_assembler");
        ASSEMBLER_ENERGY_PER_CRAFT = b.comment("FE за один крафт рецепта столу Avaritia.", "FE per Avaritia table craft.")
                .defineInRange("energyPerCraft", 50_000, 0, 100_000_000);
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
