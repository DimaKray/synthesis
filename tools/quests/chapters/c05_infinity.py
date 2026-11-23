from qb import Q, Lane, Chapter

CREATIVE = [
    ('cre_mek', 'mekanism:creative_energy_cube', 'Креативний куб енергії'),
    ('cre_de', 'draconicevolution:creative_capacitor', 'Креативний конденсатор'),
    ('cre_bo', 'botania:creative_pool', 'Креативний басейн мани'),
    ('cre_cr', 'create:creative_motor', 'Креативний мотор'),
    ('cre_ae', 'ae2:creative_energy_cell', 'Креативна енергокомірка'),
    ('cre_ars', 'ars_nouveau:creative_source_jar', 'Креативна банка джерела'),
]

CHAPTER = Chapter(
    'capitalism_05_infinity', '5. Нескінченність',
    'Нейтроній, злиток нескінченності й креативні предмети кожного мода',
    'avaritia:infinity_ingot', tier=5, order=5, lanes=[
        Lane('Avaritia', [
            Q('av_table', 'avaritia:extreme_crafting_table', 'Екстремальний верстак', [
                'Верстак 9×9 для рецептів Avaritia. Потрібні стиснуті верстаки й алмазна решітка.',
                'Пізніше його автоматизує квантовий збирач Синтезу.',
            ], deps=['unity3']),
            Q('av_matrix', 'avaritia:crystal_matrix_ingot', 'Кристалічна матриця', [
                'Алмазні решітки + зірки Незеру на екстремальному верстаку.',
            ], deps=['av_table', 'v_nether_star'], count=4),
            Q('av_collector', 'avaritia:neutron_collector', 'Нейтронний збирач', [
                'Повільно збирає нейтроній з нічого. У збірці: &6елітні схеми Mekanism&r, &6блоки флюїксу AE2&r і &6Сплав єдності III&r (міст).',
                'Постав кілька й прискорюй — нейтронію знадобиться дуже багато.',
            ], deps=['av_matrix', 'mk_elite', 'unity3']),
            Q('av_neutronium', 'avaritia:neutron_ingot', 'Нейтронієвий злиток', [
                'Купки нейтронію → самородки → злиток. Основа злитка нескінченності.',
            ], deps=['av_collector'], count=16),
            Q('av_compressor', 'avaritia:neutron_compressor', 'Нейтронний компресор', [
                'Стискає тисячі предметів у сингулярності. Для злитка нескінченності не обов\'язковий, але потрібен для деяких креативних рецептів.',
            ], deps=['av_neutronium'], reward='none'),
            Q('av_ingot', 'avaritia:infinity_ingot', 'Злиток нескінченності', [
                'Нейтроній + кристалічна матриця + &6Infinity Catalyst&r (у збірці — лише один каталізатор на злиток).',
            ], deps=['av_neutronium', 'av_matrix', 'catalyst'], kind='gate'),
            Q('av_sword', 'avaritia:infinity_sword', 'Меч нескінченності', ['Вбиває будь-що одним ударом.'], deps=['av_ingot'], reward='none'),
            Q('av_pick', 'avaritia:infinity_pickaxe', 'Кирка нескінченності', ['Копає все й одразу.'], deps=['av_ingot'], reward='none'),
            Q('av_armor', 'avaritia:infinity_chestplate', 'Броня нескінченності', [
                'Повний захист, політ і безсмертя.',
            ], deps=['av_ingot'], items=['avaritia:infinity_helmet', 'avaritia:infinity_chestplate', 'avaritia:infinity_pants', 'avaritia:infinity_boots'], reward='none'),
        ]),
        Lane('Креатив', [
            Q(key, item, title, ['Креативний предмет крафтиться на екстремальному верстаку зі злитками нескінченності.'],
              deps=['av_ingot']) for key, item, title in CREATIVE
        ] + [
            Q('capitalist', 'mekanism:creative_energy_cube', 'КАПІТАЛІСТ', [
                'Усі шість креативних предметів у тебе. Збірку пройдено — тепер ти справжній капіталіст.',
            ], deps=[k for k, _, _ in CREATIVE], kind='gate', items=[i for _, i, _ in CREATIVE]),
        ]),
    ])
