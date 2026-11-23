from qb import Q, Lane, Chapter

KEYS = ['de_chaotic_core', 'bo_gaia_ingot', 'tc_pearl', 'tt_ichorium', 'mk_antimatter', 'wi_animus', 'ca_cursium',
        'iu_neutronium', 'tf_fiery', 'ars_tribute', 'isb_ink', 'cr_precision', 'ae_qsing', 'pw_nitro', 'ot_unholy',
        'nv_orb6', 'oc_marid', 'fa_stella', 'eb_orichalcos', 'ma_insanium', 'dv_mortum']

CHAPTER = Chapter(
    'capitalism_04_stage4', '4. Етап IV: Хаос і 21 ключ',
    'Хаотичне ядро, антиматерія, квантова сингулярність — і Вівтар Капіталізму',
    'synthesis:capital_altar', tier=4, order=4, lanes=[
        Lane('Огляд', [
            Q('s4_intro', 'synthesis:capital_altar', 'Мета етапу', [
                '&6Infinity Catalyst&r робиться лише на &6Вівтарі Капіталізму&r: Сплав єдності III у центрі й &621 ключ&r — '
                'по одному найцінніших ресурсів кожного мода — на 21 п\'єдесталі навколо.',
                '',
                'Ключі розкидані по всіх главах і позначені як &6ключ&r. Квест «21 ключ» нижче зарахується, '
                'коли всі вони будуть у тебе. Побічні глави — саме для них.',
            ], deps=['unity3'], check=True, kind='info'),
        ]),
        Lane('Draconic Evolution', [
            Q('de_heart', 'draconicevolution:dragon_heart', 'Серце дракона', [
                'Випадає з Дракона Енду, коли ти його вб\'єш (і при повторних викликах).',
            ], deps=['s4_intro']),
            Q('de_awakened_ingot', 'draconicevolution:awakened_draconium_ingot', 'Пробуджений драконій', [
                'Драконій, пробуджений злиттям із серцем дракона на інжекторах Віверни.',
            ], deps=['de_heart', 'de_wyvern_core'], count=8),
            Q('de_awakened_core', 'draconicevolution:awakened_core', 'Пробуджене ядро', [
                'У збірці до рецепта додано &6злиток порожнечі Таумкрафту&r і &6Сплав єдності II&r (міст).',
                'Відкриває драконові інструменти, броню й реактор.',
            ], deps=['de_awakened_ingot', 'tc_void', 'unity2'], count=4),
            Q('de_chaos', 'draconicevolution:large_chaos_frag', 'Хаос-охоронець', [
                'На острові Хаосу в Енді живе Хаос-охоронець — найсильніший бос Draconic. Потрібна драконова броня.',
                'З нього випадають &6уламки хаосу&r.',
            ], deps=['de_awakened_core'], count=4),
            Q('de_chaotic_core', 'draconicevolution:chaotic_core', 'Хаотичне ядро', [
                'У збірці: пробуджені ядра, уламки хаосу + &62 іхоріуми&r, &6ігнітієвий злиток&r і &6Сплав єдності III&r (міст).',
                '&6Один із 21 ключа.&r',
            ], deps=['de_chaos', 'tt_ichorium', 'ca_ignitium', 'unity3'], kind='key'),
        ]),
        Lane('Mekanism', [
            Q('mk_ultimate', 'mekanism:ultimate_control_circuit', 'Ультимативна схема', [
                'Елітна схема + атомний сплав. Потрібна для SPS, реакторів і &6компонента 1M MEGA Cells&r (міст).',
            ], deps=['mk_elite', 'mk_atomic'], count=4),
            Q('mk_sps', 'mekanism:sps_port', 'SPS', [
                'Надкритичний синтезатор фаз: з полонію робить антиматерію. Корпус SPS — мультиблок.',
                'У збірці порт SPS потребує &6ядра Віверни&r і &6Сплаву єдності II&r (міст).',
            ], deps=['mk_ultimate', 'de_wyvern_core']),
            Q('mk_antimatter', 'mekanism:pellet_antimatter', 'Антиматерія', [
                'Полоній (з реактора поділу) → SPS → антиматерія → пелета. Шлях довгий: реактор поділу, обробка відходів, SPS.',
                '&6Один із 21 ключа.&r',
            ], deps=['mk_sps'], kind='key'),
        ]),
        Lane('AE2', [
            Q('ae_256k', 'ae2:cell_component_256k', 'Компонент 256k', [
                'У збірці робиться з &6атомного сплаву Mekanism&r (міст). Велике сховище для пізньої гри.',
            ], deps=['ae_drive', 'mk_atomic']),
            Q('ae_sing', 'ae2:singularity', 'Сингулярність', [
                'Матерія, стиснута в &6конденсаторі матерії&r AE2 (знищує будь-які предмети, накопичуючи енергію).',
            ], deps=['ae_256k']),
            Q('ae_qsing', 'ae2:quantum_entangled_singularity', 'Квантова сингулярність', [
                'Сингулярність + пил з ендерової перлини в вибуху. Пара таких сингулярностей з\'єднує квантові кільця.',
                '&6Один із 21 ключа.&r',
            ], deps=['ae_sing'], kind='key'),
        ]),
        Lane('Синтез', [
            Q('sy_core', 'synthesis:synthesis_core', 'Ядро синтезу', [
                'У синтезаторі: &6Сплав єдності I&r + &6флюїкс&r (плюс енергія й рідина — див. екран машини).',
                'Головний компонент пізніх машин Синтезу й Вівтаря Капіталізму.',
            ], deps=['sy_synth', 'unity2', 'ae_fluix']),
            Q('sy_kinetic', 'synthesis:kinetic_drive', 'Кінетичний привід', [
                'Приймає обертання Create для синтезатора й Вівтаря Капіталізму (той вимагає обертання з навантаженням).',
            ], deps=['sy_synth']),
            Q('sy_quantum', 'synthesis:quantum_assembler', 'Квантовий збирач 9×9', [
                'Автоматизує рецепти екстремального верстака Avaritia: постачальник шаблонів AE2 з шаблоном обробки кладе '
                'інгредієнти, збирач крафтить і віддає результат сусіднім інвентарям.',
            ], deps=['sy_core', 'ae_autocraft', 'av_table']),
            Q('sy_accel16', 'synthesis:accelerator_x16', 'Прискорювач ×16', [
                'Прискорювач ×8 + ядро синтезу + &6Сплав єдності III&r. Найсильніший прискорювач — для повільних машин пізньої гри.',
            ], deps=['sy_accel', 'sy_core', 'unity3']),
            Q('keys', 'synthesis:capital_altar', '21 ключ', [
                'Збери всі ключі. Кожен — окремий квест у своїй главі:',
                'Create, Ботанія, Таумкрафт, Thaumic Tinkerer, Mekanism, AE2, Draconic, Witchery, Ars, Iron\'s Spells,',
                'Twilight Forest, Cataclysm, Industrial Upgrade, Powah, Oritech, Neo Vitae, Occultism, Forbidden & Arcanus,',
                'ExtraBotany, Mystical Agriculture, DivineRPG.',
            ], deps=KEYS, check=True, kind='info'),
            Q('sy_capital', 'synthesis:capital_altar', 'Вівтар Капіталізму', [
                'Поле 9×9 з 21 п\'єдесталом на фіксованих місцях (з півдня — прохід). &6Shift+ПКМ Скіпетром&r підсвічує порожні місця.',
                'Ритуал вимагає ману, джерело, FE, лаву й обертання Create з навантаженням.',
            ], deps=['unity3', 'sy_core', 'sy_kinetic', 'v_nether_star'], kind='gate'),
            Q('catalyst', 'avaritia:infinity_catalyst', 'Infinity Catalyst', [
                'Центр — &6Сплав єдності III&r, на п\'єдесталах — &621 ключ&r. Запусти Скіпетром.',
                'Каталізатор — серце злитка нескінченності. Далі — глава «Нескінченність».',
            ], deps=['sy_capital', 'keys'], kind='gate'),
        ]),
    ])
