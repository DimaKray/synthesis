from qb import Q, Lane, Chapter

CHAPTER = Chapter(
    'capitalism_s8_energy', 'Енергія й автоматизація',
    'Електрика Create, досвід рідиною, генератори й реактори, доповнення AE2',
    'mekanismgenerators:heat_generator', tier=3, group='side', order=17, lanes=[
        Lane('Create: електрика', [
            Q('ca_mill', 'createaddition:rolling_mill', 'Прокатний стан', [
                'Create Crafts & Additions з\'єднує обертання Create з енергією FE. Прокатний стан (андезитовий корпус + вал + залізний лист) '
                'робить з металів &6дріт і стрижні&r.',
            ], deps=['cr_casing']),
            Q('ca_spool', 'createaddition:copper_spool', 'Котушка мідного дроту', [
                'Мідний дріт з прокатного стану намотується на котушку. Котушки — у кожній електромашині мода, ними ж тягнуть дроти між конекторами.',
            ], deps=['ca_mill']),
            Q('ca_alternator', 'createaddition:alternator', 'Генератор змінного струму', [
                'Механічний крафтер: андезитовий сплав, залізні листи, стрижні й котушка. &6Обертання → FE&r: постав на вал — і живи машини Mekanism від водяних коліс.',
            ], deps=['ca_spool', 'cr_crafter']),
            Q('ca_motor', 'createaddition:electric_motor', 'Електродвигун', [
                'Зворотний напрям: &6FE → обертання&r. Механічний крафтер: латунний лист, конденсатор, стрижні, котушка. Зручно, коли енергії багато, а місця мало.',
            ], deps=['ca_spool', 'cr_crafter', 'cr_brass']),
            Q('ca_tesla', 'createaddition:tesla_coil', 'Котушка Тесли', [
                'Заряджає предмети на депо Create, а з ударом блискавки — робить «заряджені» рецепти. Потрібна електронна лампа й латунний корпус.',
            ], deps=['ca_motor', 'cr_tube'], reward='none'),
            Q('ca_accum', 'createaddition:modular_accumulator', 'Модульний акумулятор', [
                'Латунний корпус + конденсатори + електрумовий дріт. Ставиться блоками в одне велике сховище FE.',
            ], deps=['ca_alternator'], reward='none'),
        ]),
        Lane('Create: досвід', [
            Q('cei_grind', 'create_enchantment_industry:mechanical_grindstone', 'Механічне точило', [
                'Create Enchantment Industry робить з досвіду &6рідину&r. Точило (андезитовий сплав + вал) знімає чари з предметів і віддає досвід.',
            ], deps=['cr_alloy']),
            Q('cei_hatch', 'create_enchantment_industry:experience_hatch', 'Люк досвіду', [
                'Блок досвіду Create, застосований до рідинного люка. Гравець віддає сюди досвід рідиною — і бере назад.',
            ], deps=['cei_grind']),
            Q('cei_template', 'create_dragons_plus:blaze_upgrade_smithing_template', 'Шаблон іфритового покращення', [
                'Ковальський шаблон: копіюється, як ванільні (шаблон + стрижні іфрита + пекельний камінь). Перший — з будівель Незеру або в JEI.',
            ], deps=['cei_grind', 'cr_burner'], reward='none'),
            Q('cei_enchanter', 'create_enchantment_industry:blaze_enchanter', 'Іфритовий зачаровувач', [
                'Ковальський стіл: стіл чар + &6сопло іфрита&r + шаблон. Сам зачаровує предмети з конвеєра рідким досвідом — за шаблоном.',
                'Поряд — &6іфритовий коваль&r (з ковадлом): автоматичне ковадло.',
            ], deps=['cei_template', 'cei_hatch']),
            Q('cei_printer', 'create_enchantment_industry:printer', 'Принтер', [
                'Латунний лист + носик. Копіює книги й зачаровані книги рідким досвідом.',
            ], deps=['cei_hatch', 'cr_brass'], reward='none'),
        ]),
        Lane('Генератори Mekanism', [
            Q('mg_heat', 'mekanismgenerators:heat_generator', 'Теплогенератор', [
                'Піч + осмій + дошки арківуду. Перший генератор Mekanism: палить паливо або бере тепло від лави поруч.',
            ], deps=['mk_osmium']),
            Q('mg_bio', 'mekanismgenerators:bio_generator', 'Біогенератор', [
                'Насичений сплав + схема + &6біомаса&r (Create Crafts & Additions). Енергія з рослин — добре поєднується з фермами.',
            ], deps=['mg_heat', 'mk_infused', 'mk_circuit']),
            Q('mg_wind', 'mekanismgenerators:wind_generator', 'Вітрогенератор', [
                'Чим вище стоїть, тим більше дає. Енергопланшет + насичений сплав + схема.',
            ], deps=['mg_heat', 'mk_infused', 'mk_circuit'], reward='none'),
            Q('mg_gas', 'mekanismgenerators:gas_burning_generator', 'Газовий генератор', [
                'Сталевий корпус + електролітичне ядро. Спалює водень чи етилен — помітно більше енергії.',
            ], deps=['mg_heat', 'mk_casing', 'mk_infused']),
            Q('mg_fission', 'mekanismgenerators:fission_reactor_casing', 'Ядерний реактор', [
                'Корпус реактора: сталевий корпус + свинець (Immersive Engineering). Реактор — мультиблок 3×3×3 і більше; '
                'порти — з &6елітною схемою&r. Палить уран, дає тепло й ядерні відходи (з них — полоній і плутоній).',
                '&6Обережно&r: без охолодження реактор вибухає. Читай опис у JEI/гайді.',
            ], deps=['mg_gas', 'mk_elite']),
            Q('mg_turbine', 'mekanismgenerators:turbine_casing', 'Промислова турбіна', [
                'Корпус турбіни: осмій + сталь. Пара з реактора крутить лопаті (насичений сплав + сталь) — основне джерело енергії Mekanism.',
            ], deps=['mg_fission']),
            Q('mg_fusion', 'mekanismgenerators:fusion_reactor_controller', 'Термоядерний реактор', [
                'Рамка: атомний сплав + &6полоній&r + сталевий корпус. Контролер: найкраща схема + &6веселкова шибка Ботанії&r + хімбак.',
                'Палить дейтерій і тритій — величезна енергія пізньої гри.',
            ], deps=['mg_turbine', 'mk_atomic', 'mk_ultimate', 'bo_elementium']),
        ]),
        Lane('Реактор Дракона', [
            Q('der_core', 'draconicevolution:reactor_core', 'Ядро реактора Дракона', [
                'Злиття: уламок хаосу + пробуджений драконій. Реактор Дракона — найпотужніше джерело FE збірки: ядро, 4 стабілізатори й інжектор.',
                '&6Дуже небезпечний&r: при перегріві знищує все навколо. Перед запуском прочитай інструкцію в JEI.',
            ], deps=['de_chaos', 'de_awakened_ingot', 'de_fusion']),
            Q('der_stab', 'draconicevolution:reactor_stabilizer', 'Стабілізатор реактора', [
                'Злиття з деталей реактора, драконічного енергоядра й &6хаотичного ядра&r. Їх потрібно 4 — тримають поле ядра.',
            ], deps=['der_core', 'de_awakened_core', 'de_chaotic_core']),
            Q('der_injector', 'draconicevolution:reactor_injector', 'Інжектор енергії', [
                'Злиття: ядро віверни + драконій + ротори. Заливає енергію в поле, щоб утримати реактор.',
            ], deps=['der_core', 'de_wyvern_core']),
            Q('rd_core', 'retrodraconic:awakened_chaos_core', 'Пробуджене ядро хаосу', [
                'Retro Draconic додає рівень понад Хаотичний. Злиття: хаотичне ядро + пробуджені хаос-злитки + уламки хаосу.',
                'З нього — пробуджена хаотична броня й зброя (злиттям з хаотичних).',
            ], deps=['de_chaotic_core']),
        ]),
        Lane('Mekanism Extras', [
            Q('mx_radiance', 'mekanism_extras:alloy_radiance', 'Сплав сяйва', [
                'Mekanism Extras додає рівні понад «найкращий»: абсолютний → верховний → космічний → нескінченний. '
                'Перший крок — сплав сяйва: атомний сплав у металургійному інфузорі.',
            ], deps=['mk_atomic']),
            Q('mx_circuit', 'mekanism_extras:absolute_control_circuit', 'Абсолютна схема', [
                'Найкраща схема Mekanism + сплав сяйва. З неї — абсолютні машини, фабрики, кабелі й установники рівня.',
            ], deps=['mx_radiance', 'mk_ultimate']),
            Q('mx_cube', 'mekanism_extras:absolute_energy_cube', 'Абсолютний енергокуб', [
                'Найкращий енергокуб + сплав сяйва + енергопланшети. Далі — верховний, космічний і нескінченний куби й індукційні комірки.',
            ], deps=['mx_circuit', 'mk_casing']),
            Q('mx_supreme', 'mekanism_extras:supreme_control_circuit', 'Верховна схема', [
                'Абсолютна схема + термоядерний сплав. Наступний рівень машин.',
            ], deps=['mx_circuit']),
            Q('mx_naquadah', 'mekanism_extras:naquadah_reactor_controller', 'Наквадовий реактор', [
                'Новий мультиблок-реактор на наквадовому паливі. Контролер — космічна схема + корпус реактора + веселкова шибка Ботанії + абсолютний хімбак. '
                'Наквадова руда — в Енді.',
            ], deps=['mx_supreme', 'bo_elementium'], reward='none'),
        ]),
        Lane('Create: поїзди й декор', [
            Q('sr_handcar', 'railways:handcar', 'Дрезина', [
                "Steam 'n' Rails розширює поїзди Create: нові колії (вузькі, широкі, з різного дерева), семафори, димові труби, "
                'локометал для вагонів і дрезина — маленький поїзд, який крутиш сам.',
            ], deps=['cr_casing', 'cr_alloy', 'cr_tube']),
            Q('sr_semaphore', 'railways:semaphore', 'Семафор', [
                'Андезитовий корпус + паркан + залізний лист + електронна лампа. Показує сигнал світлофора поїздам. Ще є зчіпка колій для довгих маршрутів.',
            ], deps=['sr_handcar', 'cr_train', 'cr_tube'], reward='none'),
            Q('cd_catwalk', 'createdeco:brass_catwalk', 'Декор Create', [
                'Create Deco: містки, ґрати, двері, вікна й опори з металів Create, кольорова цегла, лампи, контейнери й наліпки. '
                'Більшість — на каменерізі з латуні, міді, цинку чи заліза.',
            ], deps=['cr_brass'], reward='none'),
        ]),
        Lane('Доповнення AE2', [
            Q('ae_chem', 'appmek:chemical_storage_cell_1k', 'Хімічна комірка', [
                'Applied Mekanistics: гази й хімікати Mekanism у мережі ME. Корпус — осмій + кварцове скло + редстоун.',
            ], deps=['ae_drive', 'mk_osmium']),
            Q('ae_flux', 'appflux:fe_1k_cell', 'Комірка FE', [
                'Applied Flux зберігає енергію FE в ME і дає &6індукційну карту&r (заряджає машини через інтерфейс). '
                'Енергетичний процесор — у висікачі.',
            ], deps=['ae_drive', 'ae_inscriber']),
            Q('ae_create', 'appliedcreate:kinetic_energy_acceptor', 'Кінетичний приймач енергії', [
                'Applied Create: мережа ME живиться обертанням Create. Є й андезитовий/латунний постачальник шаблонів (автокрафт механічними руками).',
            ], deps=['ae_controller', 'cr_crafter']),
            Q('ae_oritech', 'applied_oritech:me_dock', 'ME-док Oritech', [
                'Applied Oritech: машини Oritech підключаються до ME напряму.',
            ], deps=['ae_controller', 'ot_core', 'ot_duratium'], reward='none'),
            Q('ae_wireless', 'ae2wtlib:wireless_universal_terminal', 'Універсальний бездротовий термінал', [
                'AE2 Wireless Terminals: один термінал на все (предмети, крафт, шаблони). Карта магніту збирає предмети в мережу, '
                'карта квантового мосту дає доступ звідусіль.',
            ], deps=['ae_terminal']),
        ]),
    ])
