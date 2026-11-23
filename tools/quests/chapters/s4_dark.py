from qb import Q, Lane, Chapter

CHAPTER = Chapter(
    'capitalism_s4_dark', 'Темні мистецтва',
    'Witchery, магія крові, окультизм, теургія й Forbidden & Arcanus. Ключі: пекельна анімуса, трансцендентна сфера, марид, вічна стелла',
    'witchery:infernal_animus', tier=3, group='side', order=13, lanes=[
        Lane('Witchery', [
            Q('wi_mutandis', 'witchery:mutandis', 'Мутандіс', [
                'Witchery починається з мутандісу: він перетворює рослини на інші. Так добувають рідкісні трави відьом.',
                'Довідник — книга «Відьмацтво» (рецепт у JEI).',
            ], deps=['welcome']),
            Q('wi_oven', 'witchery:iron_witches_oven', 'Відьомська піч', [
                'Пече не лише їжу, а й дим: постав глечики, щоб ловити пару — з неї роблять есенції.',
            ], deps=['wi_mutandis']),
            Q('wi_altar', 'witchery:altar', 'Вівтар', [
                'Джерело сили вівтаря. Що більше природи навколо, то більше сили. Багато рецептів і ритуалів її потребують.',
            ], deps=['wi_mutandis']),
            Q('wi_stone', 'witchery:attuned_stone', 'Налаштований камінь', [
                'Акумулятор сили вівтаря. Потрібен для дистилятора й прядки.',
            ], deps=['wi_altar']),
            Q('wi_distillery', 'witchery:distillery', 'Дистилятор', [
                'Розділяє варива на складники. У збірці — з &6латуні Create&r (міст). Через нього йде шлях до &6духу Іншосвіту&r.',
            ], deps=['wi_stone', 'cr_brass']),
            Q('wi_spinning', 'witchery:spinning_wheel', 'Прядка', [
                'Пряде магічні нитки. У збірці крутиться на &6шестернях Create&r (міст).',
            ], deps=['wi_stone', 'cr_alloy']),
            Q('wi_chalk', 'witchery:golden_ritual_chalk', 'Ритуальна крейда', [
                'Золота крейда — центр кола, звичайна, пекельна й крейда Іншосвіту — кільця. Ритуали запускаються ПКМ по центру.',
            ], deps=['wi_stone']),
            Q('wi_demon', 'witchery:demon_heart', 'Серце демона', [
                'Виклич демона ритуалом у пекельному колі й перемож.',
            ], deps=['wi_chalk']),
            Q('wi_animus', 'witchery:infernal_animus', 'Пекельна анімуса', [
                'Сутність пекла, зібрана з серця демона. &6Один із 21 ключа.&r',
            ], deps=['wi_demon'], kind='key'),
        ]),
        Lane('Магія крові', [
            Q('nv_altar', 'neovitae:ara_vitae', 'Вівтар Віте', [
                'Neo Vitae — магія крові. Вівтар перетворює кров на Essentia Vitae. У збірці його вогонь — це &6пальник з іфритом Create&r (міст).',
                'Повний довідник — книга Neo Vitae (вона перекладена).',
            ], deps=['cr_burner']),
            Q('nv_orb1', 'neovitae:blood_orb_weak', 'Сфера крові', [
                'Сфера прив\'язує твою аніму — особисте сховище есенції, з якого працюють сигіли й ритуали.',
            ], deps=['nv_altar']),
            Q('nv_rune', 'neovitae:rune_blank', 'Руни вівтаря', [
                'Руни навколо вівтаря підвищують його рівень і відкривають кращі сфери.',
            ], deps=['nv_altar']),
            Q('nv_ritual', 'neovitae:master_ritual_stone', 'Головний ритуальний камінь', [
                'Центр ритуалів: від автоферм до метеоритів. Віщун ритуалів будує кола сам.',
            ], deps=['nv_orb1', 'nv_rune']),
            Q('nv_forge', 'neovitae:hellfire_forge', 'Пекельна кузня', [
                'Працює на Спірітусі з убитих мобів: розумні інструменти, самоцвіти Спірітус, реагенти.',
            ], deps=['nv_orb1']),
            Q('nv_orb6', 'neovitae:blood_orb_transcendent', 'Трансцендентна сфера', [
                'Найвища сфера крові — вершина Neo Vitae. &6Один із 21 ключа.&r',
            ], deps=['nv_ritual', 'nv_forge'], kind='key'),
        ]),
        Lane('Окультизм', [
            Q('oc_chalk', 'occultism:chalk_white', 'Крейда й чаша', [
                'Occultism: виклик духів пентаклями з крейди. Жертовна чаша в центрі запускає ритуал.',
            ], deps=['welcome'], items=['occultism:chalk_white', 'occultism:golden_sacrificial_bowl']),
            Q('oc_goggles', 'occultism:otherworld_goggles', 'Окуляри Іншосвіту', [
                'Показують приховані блоки й рослини Іншосвіту — без них не знайти ключових матеріалів.',
            ], deps=['oc_chalk']),
            Q('oc_djinni', 'occultism:book_of_binding_bound_djinni', 'Зв\'язаний джин', [
                'Книга зв\'язування з ім\'ям духа. Джини — робітники: шахтарі, торговці, транспорт.',
            ], deps=['oc_chalk']),
            Q('oc_mineshaft', 'occultism:dimensional_mineshaft', 'Вимірна шахта', [
                'Дух-шахтар нескінченно копає руду в іншому вимірі. У збірці ритуал вимагає &6драконієвий злиток&r (міст).',
            ], deps=['oc_djinni', 'de_ingot']),
            Q('oc_afrit', 'occultism:book_of_binding_bound_afrit', 'Зв\'язаний африт', ['Сильніший дух — для небезпечніших ритуалів.'], deps=['oc_djinni']),
            Q('oc_marid', 'occultism:book_of_binding_bound_marid', 'Зв\'язаний марид', [
                'Наймогутніший дух Occultism. &6Один із 21 ключа.&r',
            ], deps=['oc_afrit'], kind='key'),
            Q('oc_storage', 'occultism:storage_controller', 'Магічне сховище', [
                'Сховище, яке обслуговують духи. Альтернатива AE2 з окультним присмаком.',
            ], deps=['oc_djinni'], reward='none'),
        ]),
        Lane('Теургія й природа', [
            Q('th_oven', 'theurgy:calcination_oven', 'Кальцинаційна піч', [
                'Theurgy — алхімія: розкладає речі на солі й ртуть і збирає з них нові. У збірці піч — з &6латуні Create&r (міст).',
            ], deps=['cr_brass']),
            Q('th_mercury', 'theurgy:mercury_crystal', 'Кристал ртуті', [
                'Дистилятор + піромантична жаровня → ртуть: «енергія» алхімії.',
            ], deps=['th_oven']),
            Q('th_reform', 'theurgy:reformation_target_pedestal', 'Реформація', [
                'Перетворює один предмет на інший ціною ртуті — алхімічне копіювання.',
            ], deps=['th_mercury'], reward='none'),
            Q('na_altar', 'naturesaura:nature_altar', 'Вівтар природи', [
                'Nature\'s Aura: сила аури природи навколо. У збірці ритуал дерева вимагає &6манасталь Ботанії&r (міст).',
            ], deps=['bo_manasteel']),
            Q('na_sky', 'naturesaura:sky_ingot', 'Небесний злиток', ['Найкращий метал Nature\'s Aura.'], deps=['na_altar'], reward='none'),
        ]),
        Lane('Forbidden & Arcanus', [
            Q('fa_lens', 'forbidden_arcanus:lens_of_veritatis', 'Лінза Правди', [
                'Показує приховане: таємні кристали в руді й особливі блоки світу.',
            ], deps=['welcome']),
            Q('fa_crystal', 'forbidden_arcanus:arcane_crystal', 'Таємний кристал', ['Основний матеріал мода.'], deps=['fa_lens']),
            Q('fa_deorum', 'forbidden_arcanus:deorum_ingot', 'Деорум', ['Золотий метал богів — для кузні Гефеста.'], deps=['fa_crystal']),
            Q('fa_forge', 'forbidden_arcanus:hephaestus_forge_tier_1', 'Кузня Гефеста', [
                'Ритуальна кузня: нові рівні відкривають кращі ритуали.',
            ], deps=['fa_deorum']),
            Q('fa_star', 'forbidden_arcanus:dark_nether_star', 'Темна зірка Незеру', ['Проклята зірка — для найсильніших ритуалів.'], deps=['fa_forge', 'v_nether_star']),
            Q('fa_stella', 'forbidden_arcanus:eternal_stella', 'Вічна стелла', [
                'Робить предмет незламним. &6Один із 21 ключа.&r',
            ], deps=['fa_star'], kind='key'),
            Q('fa_clibano', 'forbidden_arcanus:clibano_core', 'Клібано', [
                'Піч Forbidden & Arcanus. У збірці — на &6сталевому корпусі Mekanism&r (міст).',
            ], deps=['fa_crystal', 'mk_casing'], reward='none'),
        ]),
    ])
