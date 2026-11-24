from qb import Q, Lane, Chapter

CHAPTER = Chapter(
    'capitalism_s2_industry', 'Важка промисловість',
    'Immersive Engineering, Powah, Oritech, Industrial Foregoing і розширення AE2. Ключі: нітро-кристал, нечестивий інтелект',
    'powah:crystal_nitro', tier=3, group='side', order=11, lanes=[
        Lane('Immersive Engineering', [
            Q('ie_hammer', 'immersiveengineering:hammer', 'Молот інженера', [
                'Ним «збирають» мультиблоки IE: побудуй структуру з блоків і вдар молотом по потрібному.',
                'Книга «Інженерний довідник» пояснює кожну структуру.',
            ], deps=['welcome']),
            Q('ie_coke', 'immersiveengineering:coke', 'Коксова піч', [
                'Мультиблок з коксової цегли: вугілля → кокс і креозот. Кокс потрібен для доменної печі.',
            ], deps=['ie_hammer'], items=['immersiveengineering:cokebrick', 'immersiveengineering:coke']),
            Q('ie_steel', 'immersiveengineering:ingot_steel', 'Сталь IE', [
                'Доменна піч з доменної цегли: залізо + кокс → сталь.',
            ], deps=['ie_coke']),
            Q('ie_light', 'immersiveengineering:light_engineering', 'Легкий інженерний блок', [
                'У збірці робиться з &6латуні Create&r (міст). Основа мультиблоків IE.',
            ], deps=['ie_steel', 'cr_brass']),
            Q('ie_heavy', 'immersiveengineering:heavy_engineering', 'Важкий інженерний блок', [
                'У збірці — з &6насиченого сплаву Mekanism&r (міст). Для дробарки, екскаватора, дугової печі.',
            ], deps=['ie_light', 'mk_infused']),
            Q('ie_crusher', 'immersiveengineering:crusher', 'Дробарка IE', [
                'Мультиблок: руда → 2 пилу, швидко й дешево по енергії.',
            ], deps=['ie_heavy'], reward='none'),
        ]),
        Lane('Powah', [
            Q('pw_book', 'powah:book', 'Книга Powah', [
                'Powah — енергетика на кристалах: генератори, реактори й бездротова передача FE.',
            ], deps=['welcome']),
            Q('pw_orb', 'powah:energizing_orb', 'Заряджальна сфера', [
                'Сфера + стрижні навколо = машина, що «заряджає» предмети в кристали. У збірці потрібне &6кварцове скло AE2&r (міст).',
            ], deps=['pw_book', 'mk_quartz_glass']),
            Q('pw_blazing', 'powah:crystal_blazing', 'Палаючий кристал', [
                'Перший кристал — із заряджених стрижнів іфрита. Далі ніотичний (з діаманта), одухотворений (зі смарагду) і нітро; точні рецепти — у JEI.',
            ], deps=['pw_orb']),
            Q('pw_niotic', 'powah:crystal_niotic', 'Ніотичний кристал', [
                'Мост: потрібен для &6глибинного бура Oritech&r.',
            ], deps=['pw_blazing']),
            Q('pw_spirited', 'powah:crystal_spirited', 'Одухотворений кристал', ['Третій кристал Powah.'], deps=['pw_niotic']),
            Q('pw_nitro', 'powah:crystal_nitro', 'Нітро-кристал', [
                'Найсильніший кристал Powah. &6Один із 21 ключа.&r',
            ], deps=['pw_spirited'], kind='key'),
            Q('pw_reactor', 'powah:reactor_starter', 'Реактор Powah', [
                'Мультиблок, що спалює уранініт. У збірці стоїть на &6сталевому корпусі Mekanism&r (міст). Велике джерело FE.',
            ], deps=['pw_orb', 'mk_casing'], reward='none'),
        ]),
        Lane('Oritech', [
            Q('ot_core', 'oritech:machine_core_1', 'Ядро машини Oritech', [
                'Oritech — індустрія з великими анімованими машинами. Ядра розширюють мультиблоки.',
            ], deps=['welcome']),
            Q('ot_assembler', 'oritech:assembler_block', 'Збирач Oritech', [
                'У збірці крафтиться з &6механічними крафтерами Create&r (міст). Потрібен для більшості компонентів мода.',
            ], deps=['ot_adamant', 'cr_crafter']),
            Q('ot_adamant', 'oritech:adamant_ingot', 'Адамант', ['Перший особливий метал Oritech.'], deps=['ot_core']),
            Q('ot_duratium', 'oritech:duratium_ingot', 'Дуратій', ['Наступний метал — для атомної кузні.'], deps=['ot_assembler']),
            Q('ot_forge', 'oritech:atomic_forge_block', 'Атомна кузня', [
                'У збірці — з &6драконієвим ядром&r (міст). Робить найкращі матеріали Oritech.',
            ], deps=['ot_duratium', 'de_core']),
            Q('ot_unholy', 'oritech:unholy_intelligence', 'Нечестивий інтелект', [
                'З «сумнівного контейнера» в рідинній центрифузі Oritech. Вершина мода. &6Один із 21 ключа.&r',
            ], deps=['ot_forge'], kind='key'),
            Q('ot_drill', 'oritech:deep_drill_block', 'Глибинний бур', [
                'Бурить ресурсні вузли нескінченно. У збірці — з &6ніотичним кристалом Powah&r (міст).',
            ], deps=['ot_forge', 'pw_niotic'], reward='none'),
        ]),
        Lane('Industrial Foregoing', [
            Q('if_plastic', 'industrialforegoing:plastic', 'Пластик', [
                'Латекс з дерев (витягувач рідин) → суха гума → пластик. Основа каркасів машин IF.',
            ], deps=['welcome']),
            Q('if_frame', 'industrialforegoing:machine_frame_simple', 'Простий каркас машини', [
                'У камері розчинення: «жалюгідна» рамка + пластик + пекельна цегла + золота шестерня (і рідина — див. JEI).',
                'Каркас для перших машин IF: ферми, збирачі рослин, дробарки мобів.',
            ], deps=['if_plastic']),
            Q('if_laser', 'industrialforegoing:laser_drill', 'Лазерний бур', [
                'У збірці — зі &6схемою Mekanism&r і &6прецизійним механізмом Create&r (міст). Живить лазерні бази.',
            ], deps=['if_frame', 'cr_precision', 'mk_circuit', 'mk_adv_circuit']),
            Q('if_ore_base', 'industrialforegoing:ore_laser_base', 'Лазерна база руди', [
                'Видобуває руду з повітря — нескінченно. У збірці — з &6драконієвим ядром&r і &6елітними схемами&r (міст).',
            ], deps=['if_laser', 'de_core', 'mk_elite']),
            Q('if_supreme', 'industrialforegoing:machine_frame_supreme', 'Найвищий каркас', [
                'Камера розчинення: просунута рамка + пластик + незеритові злитки + алмази й алмазна шестерня. Для найкращих машин IF.',
            ], deps=['if_frame', 'v_netherite'], reward='none'),
        ]),
        Lane('Розширення AE2', [
            Q('xae_provider', 'extendedae:ex_pattern_provider', 'Розширений постачальник шаблонів', [
                'ExtendedAE: більше шаблонів, більше сторін, менше блоків.',
            ], deps=['ae_autocraft']),
            Q('mega_1m', 'megacells:cell_component_1m', 'Компонент 1M', [
                'MEGA Cells — величезні комірки. У збірці компонент 1M стоїть на &6ультимативній схемі Mekanism&r (міст).',
            ], deps=['xae_provider', 'mk_ultimate', 'ae_256k']),
            Q('aae_core', 'advanced_ae:quantum_core', 'Квантове ядро AdvancedAE', [
                'Квантовий комп\'ютер AdvancedAE — найшвидший автокрафт. У збірці ядро вимагає &64 ядра Віверни&r (міст).',
            ], deps=['xae_provider', 'de_wyvern_core', 'ae_256k', 'ae_sing'], reward='none'),
        ]),
    ])
