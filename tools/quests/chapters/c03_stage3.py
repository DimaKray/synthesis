from qb import Q, Lane, Chapter

CHAPTER = Chapter(
    'capitalism_03_stage3', '3. Етап III: Драконій, Гея й іхор',
    'Ядро Віверни, Альфгейм і Охоронець Геї, атомний сплав — до Сплаву єдності III',
    'synthesis:unity_alloy_3', tier=3, order=3, lanes=[
        Lane('Огляд', [
            Q('s3_intro', 'synthesis:unity_alloy_3', 'Мета етапу', [
                'Сплав єдності III: центр — &6Сплав єдності II&r, п\'єдестали — &6злиток Геї&r (Ботанія), &6атомний сплав&r (Mekanism),',
                '&6іхоріум&r (Thaumic Tinkerer) і &6ігнітієвий злиток&r (бос Ігніс із Cataclysm).',
                'Ресурси ритуалу: &6500 000 мани, 20 000 джерела й 1 000 000 FE&r.',
                '',
                'Тут з\'являються перші серйозні боси — готуй броню (Віверна, нано/квантова IU або магічна).',
            ], deps=['unity2'], check=True, kind='info'),
        ]),
        Lane('Пекло й зірки', [
            Q('v_wither_skull', 'minecraft:wither_skeleton_skull', 'Черепи візер-скелетів', [
                'Візер-скелети живуть у фортецях Незеру. Три черепи + пісок душ = виклик Візера. Меч з «Добиччю» допоможе.',
            ], deps=['s3_intro'], count=3),
            Q('v_nether_star', 'minecraft:nether_star', 'Зірка Незеру', [
                'Нагорода за Візера. Потрібна для &6злитка зірки Незеру IU&r (→ ядро Віверни), &6маяка&r і Avaritia.',
                'Порада: бийся з Візером під землею, щоб він не розніс базу.',
            ], deps=['v_wither_skull'], count=2),
        ]),
        Lane('Industrial Upgrade', [
            Q('iu_macerator', 'industrialupgrade:simplemachine/macerator_iu', 'Подрібнювач IU', [
                'Перша машина Industrial Upgrade (на FE). IU — окрема технологічна гілка з сонячними панелями, схемами й бронею.',
            ], deps=['s3_intro']),
            Q('iu_compressor', 'industrialupgrade:simplemachine/compressor_iu', 'Компресор', [
                'Стискає ресурси: пластини, вугільні кулі, матеріали для корпусів.',
            ], deps=['iu_macerator']),
            Q('iu_adv_casing', 'industrialupgrade:blockresource/advanced_machine', 'Покращений корпус машини', [
                'Основа покращених машин і панелей IU.',
            ], deps=['iu_compressor']),
            Q('iu_nanocircuit', 'industrialupgrade:circuit/nanocircuit', 'Наносхема', [
                'Схема для нано-броні й кращих машин IU. Ланцюжок схем іде далі: квантова → фотонна → спектральна.',
            ], deps=['iu_adv_casing']),
            Q('iu_quantum_circuit', 'industrialupgrade:circuit/quantumcircuit', 'Квантова схема', [
                'Потрібна для квантової броні й &6злитка зірки Незеру&r.',
            ], deps=['iu_nanocircuit']),
            Q('iu_star_ingot', 'industrialupgrade:nether_star_ingot', 'Злиток зірки Незеру', [
                'Мост збірки: без нього не скрафтити &6ядро Віверни Draconic&r. Рецепт — у JEI.',
            ], deps=['iu_quantum_circuit', 'v_nether_star'], count=2),
        ]),
        Lane('Draconic Evolution', [
            Q('de_ingot', 'draconicevolution:draconium_ingot', 'Драконій', [
                'Руда драконію трапляється глибоко у Верхньому світі, у Незері й в Енді (найбільше). '
                'Вона вибухова: копай киркою з Шовковим дотиком або будь готовий до вибуху.',
            ], deps=['s3_intro'], count=16),
            Q('de_core', 'draconicevolution:draconium_core', 'Драконієве ядро', [
                'У збірці: драконій + &6тауміум&r + &6мана-діамант&r (міст). Основа всього Draconic, '
                'а також &6лазерних баз IF&r, &6атомної кузні Oritech&r і &6вівтаря пробудження Mystical Agriculture&r.',
            ], deps=['de_ingot', 'tc_thaumium', 'bo_diamond'], count=4),
            Q('de_fusion', 'draconicevolution:crafting_core', 'Злиття Draconic', [
                'Ядро злиття в центрі, інжектори навколо (на одній лінії, лицем до ядра). '
                'Крафт злиттям бере енергію й предмети з інжекторів.',
            ], deps=['de_core'], items=['draconicevolution:crafting_core', 'draconicevolution:basic_crafting_injector']),
            Q('de_energy_core', 'draconicevolution:energy_core', 'Енергетичне ядро', [
                'Мультиблокове сховище енергії на мільярди FE. Не обов\'язкове, але для пізніх крафтів злиттям дуже допоможе.',
            ], deps=['de_core'], reward='none'),
            Q('de_wyvern_core', 'draconicevolution:wyvern_core', 'Ядро Віверни', [
                'У збірці: драконієві ядра + &6злиток зірки Незеру IU&r + &6Сплав єдності II&r (міст).',
                'Відкриває інструменти й броню Віверни, а також &6порт SPS Mekanism&r.',
            ], deps=['de_fusion', 'iu_star_ingot', 'unity2'], count=2),
            Q('de_wyvern_gear', 'draconicevolution:wyvern_chestpiece', 'Броня Віверни', [
                'Щит, що поглинає шкоду, і політ. Для боїв Етапу III і далі — дуже раджу.',
            ], deps=['de_wyvern_core'], reward='none'),
        ]),
        Lane('Mekanism', [
            Q('mk_reinforced', 'mekanism:alloy_reinforced', 'Зміцнений сплав', [
                'Насичений сплав + алмази в інфузорі. Потрібен для елітних схем.',
            ], deps=['mk_adv_circuit']),
            Q('mk_elite', 'mekanism:elite_control_circuit', 'Елітна схема', [
                'Потрібна для пізніх машин і &6нейтронного збирача Avaritia&r та &6лазерної бази IF&r (мости).',
            ], deps=['mk_reinforced'], count=4),
            Q('mk_obsidian', 'mekanism:ingot_refined_obsidian', 'Очищений обсидіан', [
                'Обсидіан + алмаз у машині пресування. Інгредієнт атомного сплаву.',
            ], deps=['mk_elite']),
            Q('mk_atomic', 'mekanism:alloy_atomic', 'Атомний сплав', [
                'Найвищий сплав Mekanism. Інгредієнт Сплаву єдності III, &6компонента 256k AE2&r (міст) і ультимативних схем.',
            ], deps=['mk_obsidian'], count=4),
        ]),
        Lane('Ботанія', [
            Q('bo_portal', 'botania:alfheim_portal', 'Портал в Альфгейм', [
                'Ядро порталу з терасталі + рамка з живого дерева + пілони на басейнах мани. Кидаєш предмети в портал — '
                'ельфи обмінюють їх на свої матеріали.',
            ], deps=['bo_terrasteel']),
            Q('bo_elementium', 'botania:elementium_ingot', 'Елементіум', [
                'Ельфійський метал: манасталь, кинута в портал. Потрібен для пілона Геї.',
            ], deps=['bo_portal'], count=4),
            Q('bo_dragonstone', 'botania:dragonstone', 'Драконячий камінь', [
                'Мана-діамант, обміняний в Альфгеймі.',
            ], deps=['bo_portal']),
            Q('wi_otherwhere', 'witchery:spirit_of_otherwhere', 'Дух Іншосвіту', [
                'Наповнення Witchery з есенції ендермена (див. главу «Темні мистецтва»).',
                'Мост: потрібен для &6пілона Геї&r.',
            ], deps=['s3_intro', 'wi_distillery']),
            Q('bo_gaia_pylon', 'botania:gaia_pylon', 'Пілони Геї', [
                'У збірці: пілон мани + елементіум + &6дух Іншосвіту Witchery&r (міст). Для ритуалу потрібно 8 пілонів.',
            ], deps=['bo_elementium', 'wi_otherwhere'], count=8),
            Q('bo_gaia_spirit', 'botania:life_essence', 'Охоронець Геї', [
                'Маяк на блоках терасталі, 8 пілонів Геї навколо, кидаєш терасталь у маяк — і з\'являється Охоронець Геї.',
                'Перемога дає &6духи Геї&r. Умови бою (і що з собою брати не можна) — у Лексиконі.',
            ], deps=['bo_gaia_pylon'], count=4),
            Q('bo_gaia_ingot', 'botania:gaia_ingot', 'Злиток Геї', [
                'Терасталь + духи Геї. Інгредієнт Сплаву єдності III і &6один із 21 ключа&r.',
            ], deps=['bo_gaia_spirit'], count=2, kind='key'),
        ]),
        Lane('Таумкрафт', [
            Q('tt_ichor', 'ttinkerer:ichor', 'Іхор', [
                'Thaumic Tinkerer — продовження Таумкрафту. Іхор — «кров богів», з неї роблять іхоріум.',
                'Рецепт відкривається дослідженнями в Таумономіконі (розділ Thaumic Tinkerer).',
            ], deps=['tc_void']),
            Q('tt_ichorium', 'ttinkerer:ichorium', 'Іхоріум', [
                'Найсильніший метал Таумкрафту. Інгредієнт Сплаву єдності III, &6хаотичного ядра&r і &6один із 21 ключа&r.',
            ], deps=['tt_ichor'], count=4, kind='key'),
        ]),
        Lane('Cataclysm', [
            Q('ca_metal', 'cataclysm:ancient_metal_ingot', 'Стародавній метал', [
                'L_Ender\'s Cataclysm — величезні боси в особливих спорудах. Стародавній метал — з першого з них, Незеритової потвори.',
                'Детально про всіх босів — у главі «Пригоди й боси».',
            ], deps=['s3_intro']),
            Q('ca_ignitium', 'cataclysm:ignitium_ingot', 'Ігнітієвий злиток', [
                'Нагорода за Ігніса (Пекельна кузня в Незері). Інгредієнт Сплаву єдності III і &6хаотичного ядра&r.',
            ], deps=['ca_metal'], count=2),
        ]),
        Lane('Ворота', [
            Q('unity3', 'synthesis:unity_alloy_3', 'Сплав єдності III', [
                'Центр — &6Сплав єдності II&r. П\'єдестали: &6злиток Геї, атомний сплав, іхоріум, ігнітієвий злиток&r.',
                '&6500 000 мани, 20 000 джерела, 1 000 000 FE&r — постав поруч великий басейн мани, банки джерела й енергію.',
                '',
                'Відкриває &6Етап IV&r: хаотичне ядро, нейтронний збирач і Вівтар Капіталізму.',
            ], deps=['bo_gaia_ingot', 'mk_atomic', 'tt_ichorium', 'ca_ignitium', 'unity2'], kind='gate', count=2),
        ]),
    ])
