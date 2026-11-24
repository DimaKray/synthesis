from qb import Q, Lane, Chapter

CHAPTER = Chapter(
    'capitalism_s4_dark', 'Темні мистецтва',
    "Магія крові, окультизм, теургія, Nature's Aura й Forbidden & Arcanus. Ключі: трансцендентна сфера, марид, вічна стелла",
    'neovitae:blood_orb_weak', tier=3, group='side', order=13, lanes=[
        Lane('Магія крові', [
            Q('nv_altar', 'neovitae:ara_vitae', 'Вівтар Віте', [
                'Neo Vitae — магія крові. Вівтар перетворює кров на Essentia Vitae. У збірці його вогонь — це &6пальник з іфритом Create&r (міст).',
                'Повний довідник — книга Neo Vitae (вона перекладена).',
            ], deps=['cr_burner']),
            Q('nv_orb1', 'neovitae:blood_orb_weak', 'Сфера крові', [
                'Сфера прив\'язує твою аніму — особисте сховище есенції, з якого працюють сигіли й ритуали.',
            ], deps=['nv_altar']),
            Q('nv_orb2', 'neovitae:blood_orb_apprentice', 'Сфера учня', [
                'Наступні сфери робляться на вівтарі вищого рівня: учня → мага → майстра → архімага. Кожна тримає більше есенції.',
            ], deps=['nv_orb1', 'nv_rune']),
            Q('nv_orb4', 'neovitae:blood_orb_master', 'Сфера майстра', [
                'Вівтар IV рівня + уламок слабкої крові (з ритуалу). Майже вершина — далі сфера архімага з пекельно-кованого блока.',
            ], deps=['nv_orb2', 'nv_ritual']),
            Q('nv_rune', 'neovitae:rune_blank', 'Руни вівтаря', [
                'Руни навколо вівтаря підвищують його рівень і відкривають кращі сфери.',
            ], deps=['nv_altar']),
            Q('nv_rune_speed', 'neovitae:rune_speed', 'Руна швидкості', [
                'Порожня руна + цукор + скрижаль. Руни мають роль: швидкості (швидше крафтить), жертви (більше крові з мобів), місткості, заряджання.',
            ], deps=['nv_rune'], reward='none'),
            Q('nv_ritual', 'neovitae:master_ritual_stone', 'Головний ритуальний камінь', [
                'Центр ритуалів: від автоферм до метеоритів. Віщун ритуалів будує кола сам.',
            ], deps=['nv_orb1', 'nv_rune']),
            Q('nv_forge', 'neovitae:hellfire_forge', 'Пекельна кузня', [
                'Працює на Спірітусі з убитих мобів: розумні інструменти, самоцвіти Спірітус, реагенти.',
            ], deps=['nv_orb1']),
            Q('nv_gem', 'neovitae:spiritus_gem_petty', 'Самоцвіт Спірітус', [
                'Робиться в пекельній кузні з кварцового скла. Тримає Спірітус — душі вбитих мобів. Більші самоцвіти тримають більше.',
            ], deps=['nv_forge']),
            Q('nv_sentient', 'neovitae:sentient_sword', 'Розумний меч', [
                'Залізний меч + самоцвіт Спірітус у пекельній кузні. Що більше Спірітусу в самоцвіті, то сильніший меч.',
            ], deps=['nv_gem'], reward='none'),
            Q('nv_sigil', 'neovitae:sigil_water', 'Сигіли', [
                'Сигіли робляться в алхімічному масиві зі скрижалі й реагенту: води, лави, зеленого гаю, ворожіння тощо. Працюють від твоєї аніми.',
            ], deps=['nv_orb1'], reward='none'),
            Q('nv_orb6', 'neovitae:blood_orb_transcendent', 'Трансцендентна сфера', [
                'Робиться на вівтарі Віте найвищого рівня зі скупчення кристалів (пекельна кузня). Найвища сфера крові.',
                '&6Один із 21 ключа.&r',
            ], deps=['nv_ritual', 'nv_forge', 'v_nether_star'], kind='key'),
        ]),
        Lane('Окультизм', [
            Q('oc_chalk', 'occultism:chalk_white', 'Крейда й чаша', [
                'Occultism: виклик духів пентаклями з крейди. Жертовна чаша в центрі запускає ритуал.',
            ], deps=['welcome'], items=['occultism:chalk_white', 'occultism:golden_sacrificial_bowl']),
            Q('oc_dict', 'occultism:dictionary_of_spirits', 'Словник духів', [
                'Книга + насіння дурману. Довідник Occultism — у ньому всі пентаклі (фігури з крейди) і ритуали.',
            ], deps=['oc_chalk']),
            Q('oc_bowl', 'occultism:golden_sacrificial_bowl', 'Золота жертовна чаша', [
                'Центр ритуалу. Навколо — пентакль з крейди й звичайні жертовні чаші з інгредієнтами. Деякі ритуали ще вимагають жертви (моба).',
            ], deps=['oc_dict']),
            Q('oc_goggles', 'occultism:otherworld_goggles', 'Окуляри Іншосвіту', [
                'Показують приховані блоки й рослини Іншосвіту — без них не знайти ключових матеріалів.',
            ], deps=['oc_chalk']),
            Q('oc_djinni', 'occultism:book_of_binding_bound_djinni', 'Зв\'язаний джин', [
                'Книга зв\'язування з ім\'ям духа. Джини — робітники: шахтарі, торговці, транспорт.',
            ], deps=['oc_chalk']),
            Q('oc_lumberjack', 'occultism:book_of_calling_foliot_lumberjack', 'Фоліот-лісоруб', [
                "Книга зв'язування фоліота + сокира. Закликаний фоліот сам рубає дерева й складає колоди в скриню. Є й фермер, прибиральник, носій.",
            ], deps=['oc_djinni'], reward='none'),
            Q('oc_iesnium', 'occultism:iesnium_ingot', 'Ієсній', [
                'Руда ієснію — лише в Незері, і видно її тільки через окуляри Іншосвіту. Найкращий метал Occultism (кирка, чаші).',
            ], deps=['oc_goggles']),
            Q('oc_ring', 'occultism:familiar_ring', 'Перстень фамільяра', [
                'Ритуал джина з душевним самоцвітом і сріблом. Фамільяри — маленькі духи-помічники з бафами.',
            ], deps=['oc_djinni'], reward='none'),
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
            Q('th_brazier', 'theurgy:pyromantic_brazier', 'Піромантична жаровня', [
                'Мідь + камінь. Нагріває алхімічні апарати над собою — кальцинаційну піч, дистилятор, котел зрідження.',
            ], deps=['th_oven']),
            Q('th_distiller', 'theurgy:distiller', 'Дистилятор', [
                'Розкладає предмет на &6алхімічну сірку&r — «душу» речовини. Сірка + сіль + ртуть = копія ресурсу.',
            ], deps=['th_brazier']),
            Q('th_salt', 'theurgy:alchemical_salt_mineral', 'Алхімічна сіль', [
                'Кальцинація в печі дає сіль: мінеральну, рослинну, створінь, шарів. Сіль визначає, ЧИМ стане реформований предмет.',
            ], deps=['th_oven'], reward='none'),
            Q('th_cauldron', 'theurgy:liquefaction_cauldron', 'Котел зрідження', [
                'Казан + мідь + камінь. З сірки й солі робить рідкі розчини для інкубатора.',
            ], deps=['th_distiller'], reward='none'),
            Q('th_incubator', 'theurgy:incubator', 'Інкубатор', [
                'Збирає сіль, сірку й ртуть назад у предмет — так множать руди й самоцвіти.',
            ], deps=['th_cauldron', 'th_mercury']),
            Q('th_mercury', 'theurgy:mercury_crystal', 'Кристал ртуті', [
                '4 уламки ртуті (дистилятор + піромантична жаровня). Ртуть — «енергія» алхімії Theurgy.',
            ], deps=['th_oven']),
            Q('th_reform', 'theurgy:reformation_target_pedestal', 'Реформація', [
                'Перетворює один предмет на інший ціною ртуті — алхімічне копіювання.',
            ], deps=['th_mercury'], reward='none'),
            Q('na_altar', 'naturesaura:nature_altar', 'Вівтар природи', [
                'Nature\'s Aura: сила аури природи навколо. У збірці ритуал дерева вимагає &6манасталь Ботанії&r (міст).',
            ], deps=['bo_manasteel']),
            Q('na_book', 'naturesaura:book', 'Книга природи', [
                "Папір + шкіра + саджанець. Довідник Nature's Aura: аура — це енергія природи в чанку, і її треба відновлювати, садячи рослини.",
            ], deps=['welcome']),
            Q('na_leaf', 'naturesaura:gold_powder', 'Золотий пил', [
                'Золоте листя випадає з дерев, коли поруч є золото (див. книгу). Перемелене — пил для кругів ритуалу дерева.',
            ], deps=['na_book']),
            Q('na_iron', 'naturesaura:infused_iron', 'Наповнене залізо', [
                'Залізо, наповнене аурою на вівтарі природи. Основа майже всіх машин мода.',
            ], deps=['na_altar']),
            Q('na_ritual', 'naturesaura:ancient_sapling', 'Ритуал дерева', [
                "Постав саджанець у центр, навколо — дерев'яні стійки з предметами й золотий пил, і виріст дерево (кістне борошно). "
                'Так роблять стародавній саджанець, око довкілля, каталізатори.',
            ], deps=['na_leaf']),
            Q('na_offering', 'naturesaura:offering_table', 'Стіл підношень', [
                'Наповнене залізо + жетони страху й смутку. Кидаєш предмети — отримуєш рідкісні результати (наприклад, небесний злиток).',
            ], deps=['na_iron']),
            Q('na_generator', 'naturesaura:flower_generator', 'Квітковий генератор', [
                'Генератори аури: квітковий (різні квіти), тваринний, мохований. Без них аура в чанку згасає.',
            ], deps=['na_iron'], reward='none'),
            Q('na_sky', 'naturesaura:sky_ingot', 'Небесний злиток', [
                "Стіл підношень: наповнене залізо + кличний дух. Найкращий метал Nature's Aura.",
            ], deps=['na_altar', 'na_iron', 'na_offering'], reward='none'),
            Q('na_depth', 'naturesaura:depth_ingot', 'Глибинний злиток', [
                "Небесний злиток + незеритовий брухт + заплямоване золото. Найкращий метал Nature's Aura — глибинні інструменти й броня.",
            ], deps=['na_sky', 'v_netherite'], reward='none'),
        ]),
        Lane('Forbidden & Arcanus', [
            Q('fa_lens', 'forbidden_arcanus:lens_of_veritatis', 'Лінза Правди', [
                'Показує приховане: таємні кристали в руді й особливі блоки світу.',
            ], deps=['welcome']),
            Q('fa_crystal', 'forbidden_arcanus:arcane_crystal', 'Таємний кристал', ['Основний матеріал мода.'], deps=['fa_lens']),
            Q('fa_mundabitur', 'forbidden_arcanus:mundabitur_dust', 'Пил мундабітур', [
                'Пил таємного кристала + редстоун + порошок іфрита + кістне борошно + мембрана фантома + порох. '
                'Ним «оживляють» структури, наприклад кузню Гефеста.',
            ], deps=['fa_crystal']),
            Q('fa_pedestal', 'forbidden_arcanus:darkstone_pedestal', "П'єдестал з темного каменю", [
                "П'єдестали ставляться навколо кузні Гефеста — на них кладуть інгредієнти ритуалів.",
            ], deps=['fa_crystal', 'fa_deorum'], reward='none'),
            Q('fa_deorum', 'forbidden_arcanus:deorum_ingot', 'Деорум', [
                'Золото + деревне вугілля + пил таємного кристала + пил мундабітур. Метал богів — для кузні Гефеста.',
            ], deps=['fa_crystal', 'fa_mundabitur']),
            Q('fa_forge', 'forbidden_arcanus:hephaestus_forge_tier_1', 'Кузня Гефеста', [
                'Ритуальна кузня: нові рівні відкривають кращі ритуали.',
            ], deps=['fa_deorum']),
            Q('fa_star', 'forbidden_arcanus:dark_nether_star', 'Темна зірка Незеру', [
                'Злиток обсидіанової сталі + зірка Незеру. Проклята зірка — для найсильніших ритуалів.',
            ], deps=['fa_forge', 'v_nether_star', 'fa_obsidiansteel']),
            Q('fa_stella', 'forbidden_arcanus:eternal_stella', 'Вічна стелла', [
                'Робить предмет незламним. &6Один із 21 ключа.&r',
            ], deps=['fa_star'], kind='key'),
            Q('fa_clibano', 'forbidden_arcanus:clibano_core', 'Клібано', [
                'Піч Forbidden & Arcanus. У збірці — на &6сталевому корпусі Mekanism&r (міст).',
            ], deps=['fa_crystal', 'mk_casing'], reward='none'),
            Q('fa_obsidiansteel', 'forbidden_arcanus:obsidiansteel_ingot', 'Обсидіанова сталь', [
                'Обсидіан, спалений у Клібано. Обсидіанові черепи (захист від вогню й лави), інструменти й темна зірка Незеру.',
            ], deps=['fa_clibano']),
            Q('fa_skull', 'forbidden_arcanus:obsidian_skull', 'Обсидіановий череп', [
                'Обсидіанова сталь + череп скелета. Поки в інвентарі — захищає від вогню. Ритуалом у кузні його можна зробити вічним.',
            ], deps=['fa_obsidiansteel'], reward='none'),
        ]),
    ])
