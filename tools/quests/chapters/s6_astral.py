from qb import Q, Lane, Chapter

CHAPTER = Chapter(
    'capitalism_s6_astral', 'Astral Sorcery',
    'Магія зірок і сузір\'їв (альфа-порт — можливі недоробки)',
    'astralsorcery:tome', tier=2, group='side', order=15, lanes=[
        Lane('Зоряне світло', [
            Q('as_crystal', 'astralsorcery:rock_crystal', 'Гірський кришталь', [
                'Руда гірського кришталю — глибоко під землею. Для довідника знадобиться ще &6аквамарин&r (у піску на берегах).',
                'Мод — альфа-порт на 1.21.1: якщо щось не працює, це не твоя помилка.',
            ], deps=['welcome']),
            Q('as_aquamarine', 'astralsorcery:aquamarine', 'Аквамарин', [
                'Аквамариновий сланець лежить у піску й гравії на берегах річок і морів. Дробарка Create дає з нього більше аквамарину.',
                'Аквамарин — у майже кожному рецепті Astral: довідник, пергамент, лінзи, порошки.',
            ], deps=['welcome']),
            Q('as_tome', 'astralsorcery:tome', 'Астральний том', [
                'Довідник мода (книга + аквамарин, див. JEI). Розділи відкриваються поступово — читай по черзі, він веде крок за кроком.',
            ], deps=['as_aquamarine']),
            Q('as_wand', 'astralsorcery:wand', 'Резонуючий жезл', [
                'Мармур + аквамарин (особливий рецепт Astral — див. JEI). Показує, де зоряного світла найбільше, і запускає крафти на вівтарях.',
            ], deps=['as_tome', 'as_aquamarine']),
            Q('as_parchment', 'astralsorcery:parchment', 'Пергамент', [
                'Папір + аквамарин. На пергаменті з\'являються &6сузір\'я&r: дивись на небо вночі з телескопом чи астролябією, і нові сузір\'я запишуться в том.',
            ], deps=['as_aquamarine']),
            Q('as_astrolabe', 'astralsorcery:astrolabe', 'Астролябія', [
                'Підзорна труба + аквамарин + скляна панель. Дивишся на небо — і знаходиш сузір\'я для налаштування.',
            ], deps=['as_parchment'], reward='none'),
            Q('as_altar1', 'astralsorcery:altar_luminance', 'Світний вівтар (2-й рівень)', [
                'Другий рівень вівтаря: мармурові колони й рунічний мармур + &6резонуючий самоцвіт&r + &6зоряний пил&r. Більші рецепти й лінзи.',
            ], deps=['as_altar2', 'as_gem', 'as_stardust']),
            Q('as_lightwell', 'astralsorcery:lightwell', 'Світлова криниця', [
                'Під відкритим небом збирає &6рідке зоряне світло&r. Кинеш у нього предмети — деякі перетворяться (наприклад, кришталь росте).',
            ], deps=['as_attune', 'as_aquamarine']),
            Q('as_infuser', 'astralsorcery:infuser', 'Зоряний інфузор', [
                'Мармурові колони + зоряний метал. Наповнює предмети рідким зоряним світлом: аквамарин → &6резонуючий самоцвіт&r, залізо → зоряний метал і т.д.',
            ], deps=['as_starmetal', 'as_altar2']),
            Q('as_gem', 'astralsorcery:resonating_gem', 'Резонуючий самоцвіт', [
                'Аквамарин в інфузорі. Потрібен для призм, келиха, порталу й жезлів.',
            ], deps=['as_infuser', 'as_aquamarine']),
            Q('as_stardust', 'astralsorcery:stardust', 'Зоряний пил', [
                'Подрібнений зоряний метал (дробарка Create теж підходить). Інгредієнт пізніх рецептів: портал, жезли, амулети.',
            ], deps=['as_starmetal']),
            Q('as_powder', 'astralsorcery:illumination_powder', 'Порошок освітлення', [
                'Аквамарин на вівтарі. Кинеш на землю — світиться, як факел, але невидимо. А ще з нього роблять зірки, що змінюються.',
            ], deps=['as_altar2', 'as_aquamarine'], reward='none'),
            Q('as_starmetal', 'astralsorcery:starmetal_ingot', 'Зоряний метал', [
                'Залізна руда під променем зоряного світла перетворюється на руду зоряного металу (як — у томі). У збірці є й простіший шлях: &6змішувач Create&r робить зоряний метал із заліза (Create Astral).',
            ], deps=['as_altar2']),
            Q('as_altar2', 'astralsorcery:altar_illumination', 'Освітлений стіл (1-й вівтар)', [
                'Перший вівтар Astral: постав звичайний верстак, поруч сирий мармур, і вдар резонуючим жезлом (рецепт «фокусного поєднання» в JEI). Крафтить уночі під відкритим небом, живиться зоряним світлом.',
            ], deps=['as_wand']),
            Q('as_attune', 'astralsorcery:attunement_altar', 'Вівтар налаштування', [
                'Тут налаштовуються на сузір\'я — і ти, і кристали. Налаштування гравця відкриває дерево перків.',
            ], deps=['as_altar1', 'as_stardust', 'as_starmetal']),
            Q('as_altar_res', 'astralsorcery:altar_resonance', 'Резонуючий вівтар', [
                'Ще один рівень вівтаря: потребує &6налаштованого небесного кришталю&r (налаштуй кришталь на вівтарі налаштування) і рунічного мармуру.',
            ], deps=['as_attune', 'as_altar1']),
            Q('as_lens', 'astralsorcery:lens', 'Лінза', [
                'Лінзи й призми передають зоряне світло від криниць і колекторів до вівтарів і ритуалів — так будується зоряна мережа.',
            ], deps=['as_altar_res', 'as_aquamarine']),
            Q('as_prism', 'astralsorcery:prism', 'Призма', [
                'Розділяє промінь зоряного світла на кілька. Резонуючий самоцвіт + налаштований кришталь + лінза.',
            ], deps=['as_lens', 'as_gem'], reward='none'),
            Q('as_chalice', 'astralsorcery:chalice', 'Келих', [
                'Тримає рідини (зоряне світло, лаву, воду) й поєднує їх із сусідніми келихами — так виходять особливі рідини.',
            ], deps=['as_gem', 'as_altar_res', 'as_aquamarine'], reward='none'),
            Q('as_altar3', 'astralsorcery:altar_radiance', 'Сяйний вівтар', [
                'Найвищий вівтар Astral Sorcery.',
            ], deps=['as_altar_res', 'as_gem', 'as_powder', 'as_stardust'], reward='none'),
            Q('as_pick', 'astralsorcery:crystal_pickaxe', 'Кришталева кирка', [
                'Налаштований кришталь + палиці на вівтарі. Кришталеві інструменти дуже міцні; іридесцентні — ще кращі.',
            ], deps=['as_altar_res'], reward='none'),
            Q('as_blink', 'astralsorcery:blink_wand', 'Жезл переміщення', [
                'Телепортує вперед на кілька блоків. Резонуючий самоцвіт + налаштований кришталь + зоряний пил.',
            ], deps=['as_altar_res', 'as_stardust', 'as_gem'], reward='none'),
            Q('as_star', 'astralsorcery:shifting_star', 'Мінлива зірка', [
                'Аквамарин + самоцвіт + зоряний пил + порошок освітлення. З неї — іридесцентні зірки сузір\'їв і амулет-призма (чари без досвіду).',
            ], deps=['as_gem', 'as_stardust', 'as_powder', 'as_aquamarine']),
            Q('as_amulet', 'astralsorcery:enchantment_amulet', 'Сяйна призма', [
                'Амулет, що додає випадкові бонуси до чар на твоєму спорядженні. Носи в слоті Curios.',
            ], deps=['as_star'], reward='none'),
            Q('as_gateway', 'astralsorcery:celestial_gateway', 'Небесна брама', [
                'Портали між брамами по всьому світу й у різних вимірах. Ставиш дві — і ходиш між ними.',
            ], deps=['as_altar_res', 'as_stardust', 'as_powder'], reward='none'),
        ]),
        Lane('Applied AstralSorcery', [
            Q('aas_cell', 'appliedas:lumen_storage_cell_1k', 'Сховище люмену в ME', [
                'Applied AstralSorcery зберігає люмен у мережі AE2 і автоматизує вівтарі. Довідник — у посібнику AE2 (перекладений).',
            ], deps=['as_altar1', 'ae_drive', 'as_starmetal', 'mk_quartz_glass', 'as_stardust']),
            Q('aas_altar', 'appliedas:altar_automation_interface', 'Автоматизація вівтаря', [
                'Постачальник шаблонів AE2 сам кладе інгредієнти й запускає вівтар.',
            ], deps=['aas_cell', 'ae_autocraft', 'as_gem', 'as_powder', 'as_stardust'], reward='none'),
        ]),
    ])
