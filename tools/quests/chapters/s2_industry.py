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
            Q('ie_blast', 'immersiveengineering:blastbrick', 'Вогнетривка цегла', [
                'Цегла з магмових блоків. 27 штук — доменна піч 3×3×3 (вдар молотом інженера по центру). Плавить сталь із заліза й коксу.',
            ], deps=['ie_coke']),
            Q('ie_wire', 'immersiveengineering:wirecoil_copper', 'Мідний дріт', [
                'Котушка дроту + конектори НН. Дроти IE тягнуться повітрям між конекторами на будь-яку відстань (у межах довжини дроту).',
            ], deps=['ie_hammer']),
            Q('ie_capacitor', 'immersiveengineering:capacitor_lv', 'Конденсатор НН', [
                'Свинцеві пластини + мідь + базовий інженерний блок. Сховище енергії IE. Є середньої й високої напруги.',
            ], deps=['ie_wire', 'ie_light']),
            Q('ie_windmill', 'immersiveengineering:windmill', 'Вітряк', [
                'Залізо + лопаті вітряка. Крутить &6кінетичне динамо&r (залізні компоненти + котушка) — перше джерело енергії IE. '
                'Водяне колесо — те саме, але на воді.',
            ], deps=['ie_wire']),
            Q('ie_press', 'immersiveengineering:metal_press', 'Металевий прес', [
                'Мультиблок: важкі й легкі інженерні блоки + листовий метал + поршень (схема в Посібнику інженера). Пластини, стрижні, дроти, кулі.',
            ], deps=['ie_heavy', 'ie_capacitor'], reward='none'),
            Q('ie_arc', 'immersiveengineering:arc_furnace', 'Дугова піч', [
                'Великий мультиблок: плавить руду в 2 злитки, робить сплави й сталь швидко. Потребує графітових електродів і багато енергії.',
            ], deps=['ie_heavy', 'ie_crusher']),
            Q('ie_excavator', 'immersiveengineering:excavator', 'Екскаватор', [
                'Знаходить родовище бур для проб, а екскаватор копає його нескінченно: копальне колесо + величезна рама. Найкращий спосіб видобутку руди IE.',
            ], deps=['ie_arc']),
            Q('ie_revolver', 'immersiveengineering:revolver', 'Револьвер', [
                'Збирається на верстаті інженера з деталей (ствол, барабан, курок, руків\'я). Кулі — на збирачі з гільз і пороху.',
            ], deps=['ie_steel'], reward='none'),
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
            ], deps=['pw_paste']),
            Q('pw_orb', 'powah:energizing_orb', 'Заряджальна сфера', [
                'Сфера + стрижні навколо = машина, що «заряджає» предмети в кристали. У збірці потрібне &6кварцове скло AE2&r (міст).',
            ], deps=['pw_book', 'mk_quartz_glass']),
            Q('pw_paste', 'powah:dielectric_paste', 'Діелектрична паста', [
                'Вугілля + глина + відро лави (або порошок іфрита). З пасти й пластика роблять діелектричні корпуси — основу всіх машин Powah.',
            ], deps=['welcome']),
            Q('pw_cell', 'powah:energy_cell_basic', 'Енергетична комірка', [
                'Корпус + конденсатори + залізо. Сховище FE. Кожен наступний рівень (палаючий, ніотичний, одухотворений, нітро) — з відповідного кристала.',
            ], deps=['pw_paste']),
            Q('pw_furnator', 'powah:furnator_basic', 'Фурнатор', [
                'Генератор на твердому паливі. Магматор — на лаві, термогенератор — на теплі від блоків (лава, магма) з охолодженням водою.',
            ], deps=['pw_paste']),
            Q('pw_solar', 'powah:solar_panel_basic', 'Сонячна панель Powah', [
                'Проста й тиха енергія вдень. Покращуй кристалами.',
            ], deps=['pw_paste'], reward='none'),
            Q('pw_rod', 'powah:energizing_rod_basic', 'Заряджальний стрижень', [
                'Стрижні навколо заряджальної сфери живлять її енергією. Кращі стрижні — швидший заряд кристалів.',
            ], deps=['pw_orb', 'pw_paste']),
            Q('pw_ender', 'powah:ender_cell_basic', 'Ендер-комірка', [
                'Бездротова енергомережа: усі ендер-комірки на одному каналі ділять енергію. Передавач гравця заряджає твої предмети.',
            ], deps=['pw_cell'], reward='none'),
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
            Q('ot_generator', 'oritech:basic_generator_block', 'Генератор Oritech', [
                'Піч + мідь + нікель + магнітна котушка. Перша енергія Oritech; далі — парова машина (сама гріє воду) і генератори на рідинах.',
            ], deps=['ot_core']),
            Q('ot_motor', 'oritech:motor', 'Мотор', [
                'Нікель + сталь + магнітні котушки (рецепт — у JEI). Є майже в кожній машині Oritech, зокрема в самому збирачі.',
            ], deps=['ot_core']),
            Q('ot_pulverizer', 'oritech:pulverizer_block', 'Подрібнювач Oritech', [
                'Нікель + залізо + мотор. Руда → пил. Кузня фрагментів робить ще більше: руда → пил + побічні.',
            ], deps=['ot_motor']),
            Q('ot_foundry', 'oritech:foundry_block', 'Ливарня', [
                'Котел + мотор + мідь + електрум. Сплавляє метали: електрум, сталь, енергіт, біосталь.',
            ], deps=['ot_motor']),
            Q('ot_unit', 'oritech:processing_unit', 'Процесорний блок', [
                'Дуропласт + вуглецеве волокно + електрум (на збирачі). Для центрифуги, зарядника, реактора.',
            ], deps=['ot_assembler']),
            Q('ot_laser', 'oritech:laser_arm_block', 'Лазерна рука', [
                'Лазер, що видобуває блоки здалеку або заряджає машини. Ціль задається посохом-цілевказівником.',
            ], deps=['ot_motor', 'ot_unit'], reward='none'),
            Q('ot_exo', 'oritech:exo_chestplate', 'Екзоскелет', [
                'Броня на енергії з вуглецевих плит і просунутої батареї. Є реактивний ранець.',
            ], deps=['ot_unit'], reward='none'),
            Q('ot_assembler', 'oritech:assembler_block', 'Збирач Oritech', [
                'У збірці крафтиться з &6механічними крафтерами Create&r (міст). Потрібен для більшості компонентів мода.',
            ], deps=['ot_adamant', 'cr_crafter', 'ot_motor']),
            Q('ot_adamant', 'oritech:adamant_ingot', 'Адамант', ['Перший особливий метал Oritech.'], deps=['ot_core']),
            Q('ot_duratium', 'oritech:duratium_ingot', 'Дуратій', ['Наступний метал — для атомної кузні.'], deps=['ot_assembler']),
            Q('ot_forge', 'oritech:atomic_forge_block', 'Атомна кузня', [
                'У збірці — з &6драконієвим ядром&r (міст). Робить найкращі матеріали Oritech.',
            ], deps=['ot_duratium', 'de_core', 'ot_unit']),
            Q('ot_unholy', 'oritech:unholy_intelligence', 'Нечестивий інтелект', [
                'З «сумнівного контейнера» в рідинній центрифузі Oritech. Вершина мода. &6Один із 21 ключа.&r',
            ], deps=['ot_forge'], kind='key'),
            Q('ot_drill', 'oritech:deep_drill_block', 'Глибинний бур', [
                'Бурить ресурсні вузли нескінченно. У збірці — з &6ніотичним кристалом Powah&r (міст).',
            ], deps=['ot_forge', 'pw_niotic', 'ot_motor', 'ot_unholy'], reward='none'),
        ]),
        Lane('Industrial Foregoing', [
            Q('if_plastic', 'industrialforegoing:plastic', 'Пластик', [
                'Латекс з дерев (витягувач рідин) → суха гума → пластик. Основа каркасів машин IF.',
            ], deps=['if_extractor']),
            Q('if_frame', 'industrialforegoing:machine_frame_simple', 'Простий каркас машини', [
                'У камері розчинення: «жалюгідна» рамка + пластик + пекельна цегла + золота шестерня (і рідина — див. JEI).',
                'Каркас для перших машин IF: ферми, збирачі рослин, дробарки мобів.',
            ], deps=['if_plastic']),
            Q('if_extractor', 'industrialforegoing:fluid_extractor', 'Екстрактор рідини', [
                'Ставиться до колоди й точить з дерева &6латекс&r. Латекс → суха гума → пластик (див. JEI). Перший крок до пластику.',
            ], deps=['welcome']),
            Q('if_generator', 'industrialforegoing:pitiful_generator', 'Жалюгідний генератор', [
                'Піч + ґрати + жалюгідна рамка. Палить паливо. Далі — біопаливний генератор (біопаливо з біореактора).',
            ], deps=['if_extractor']),
            Q('if_gatherer', 'industrialforegoing:plant_gatherer', 'Збирач рослин', [
                'Сокира + мотика + золота шестерня. Сам збирає ферму й складає врожай. Висівач поруч — сам садить.',
            ], deps=['if_frame']),
            Q('if_dissolution', 'industrialforegoing:dissolution_chamber', 'Камера розчинення', [
                'Робить кращі рамки, пластини й аддони з рідин і предметів. Головний крафтер IF.',
            ], deps=['if_frame']),
            Q('if_advanced', 'industrialforegoing:machine_frame_advanced', 'Просунутий каркас', [
                'Камера розчинення: проста рамка + пластик + незеритовий брухт + алмазна шестерня. Для мобоферм і бурів.',
            ], deps=['if_dissolution']),
            Q('if_crusher', 'industrialforegoing:mob_crusher', 'Дробарка мобів', [
                'Вбиває мобів у зоні, збирає здобич і рідкий досвід. З дублікатором мобів — повна ферма.',
            ], deps=['if_advanced'], reward='none'),
            Q('if_drill', 'industrialforegoing:infinity_drill', 'Нескінченний бур', [
                'Бур, що копає зону до 11×11 і заряджається енергією. Робиться в камері розчинення з алмазних блоків.',
            ], deps=['if_dissolution'], reward='none'),
            Q('if_laser', 'industrialforegoing:laser_drill', 'Лазерний бур', [
                'У збірці — зі &6схемою Mekanism&r і &6прецизійним механізмом Create&r (міст). Живить лазерні бази.',
            ], deps=['if_frame', 'cr_precision', 'mk_circuit', 'mk_adv_circuit']),
            Q('if_ore_base', 'industrialforegoing:ore_laser_base', 'Лазерна база руди', [
                'Видобуває руду з повітря — нескінченно. У збірці — з &6драконієвим ядром&r і &6елітними схемами&r (міст).',
            ], deps=['if_laser', 'de_core', 'mk_elite', 'if_advanced']),
            Q('if_supreme', 'industrialforegoing:machine_frame_supreme', 'Найвищий каркас', [
                'Камера розчинення: просунута рамка + пластик + незеритові злитки + алмази й алмазна шестерня. Для найкращих машин IF.',
            ], deps=['if_frame', 'v_netherite', 'if_advanced'], reward='none'),
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
