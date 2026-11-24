from qb import Q, Lane, Chapter

CHAPTER = Chapter(
    'capitalism_s3_magic', 'Глибша магія',
    'Вершини Таумкрафту, Ars Nouveau, Iron\'s Spells і доповнень Ботанії. Ключі: первісна перлина, данина Вайлденів, легендарне чорнило, оріхалк',
    'tc4modern:primordial_pearl', tier=3, group='side', order=12, lanes=[
        Lane('Таумкрафт', [
            Q('tc_pedestal', 'tc4modern:wand_pedestal', 'П\'єдестал жезла', [
                'Заряджає жезл віс із вузлів аури поруч. Без нього жезл доведеться заряджати вручну.',
            ], deps=['tc_greatwood']),
            Q('tc_nitor', 'tc4modern:nitor', 'Нітор', [
                'Вічне магічне світло. Також інгредієнт багатьох рецептів Таумкрафту.',
            ], deps=['tc_crucible']),
            Q('tc_charm', 'tc4modern:primal_charm', 'Первісний оберіг', [
                'Зменшує витрату віс. Потрібен для серйозної інфузії.',
            ], deps=['tc_void']),
            Q('tc_eye', 'tc4modern:eldritch_eye', 'Жахливе око', [
                'Ключ до жахливого виміру Таумкрафту — там лежать матеріали для первісної перлини.',
            ], deps=['tc_void']),
            Q('tc_pearl', 'tc4modern:primordial_pearl', 'Первісна перлина', [
                'Найрідший предмет Таумкрафту — з глибин жахливого виміру. &6Один із 21 ключа.&r',
            ], deps=['tc_eye', 'tc_charm'], kind='key'),
            Q('tt_pick', 'ttinkerer:ichor_pick', 'Іхорові інструменти', [
                'Найкращі інструменти Таумкрафту з іхоріуму.',
            ], deps=['tt_ichorium'], reward='none'),
            Q('tt_quartz', 'ttinkerer:dark_quartz', 'Темний кварц', [
                'Кварц + вугілля. Декоративний блок і інгредієнт пристроїв Thaumic Tinkerer.',
            ], deps=['tc_book'], reward='none'),
            Q('tt_enchanter', 'ttinkerer:enchanter', 'Освячувач', [
                'Зачаровує предмети есенціями, без досвіду й без випадковості — обираєш чари сам (див. Таумономікон).',
            ], deps=['tc_workbench'], reward='none'),
            Q('tt_armor', 'ttinkerer:ichorcloth_chest', 'Іхорова броня', [
                'Броня з іхорової тканини: найкращий захист Таумкрафту й особливі ефекти кожної частини.',
            ], deps=['tt_ichor'], reward='none'),
        ]),
        Lane('Ars Nouveau', [
            Q('ars_scribe', 'ars_nouveau:scribes_table', 'Стіл писаря', [
                'Тут вчать нові гліфи для заклинань (за самоцвіти джерела й предмети).',
            ], deps=['ars_book']),
            Q('ars_apprentice', 'ars_nouveau:apprentice_spell_book', 'Книга учня', [
                'Більше слотів заклинань і гліфи другого рівня.',
            ], deps=['ars_scribe', 'ars_chamber']),
            Q('ars_core', 'ars_nouveau:arcane_core', 'Таємне ядро', [
                'Звичайний верстак: джерельний камінь + самоцвіт джерела. Центр ритуалів і пізніх рецептів Ars.',
            ], deps=['ars_apparatus']),
            Q('ars_horn', 'ars_nouveau:wilden_horn', 'Вайлдени', [
                'Вайлдени — дикі звірі Ars у лісах і горах. Їхні роги, шипи й крила потрібні для виклику Хімери.',
            ], deps=['ars_book']),
            Q('ars_tribute', 'ars_nouveau:wilden_tribute', 'Данина Вайлденів', [
                'Виклич Вайлден-Хімеру ритуалом і перемож — вона лишить данину. &6Один із 21 ключа.&r',
            ], deps=['ars_horn', 'ars_core'], kind='key'),
            Q('ars_archmage', 'ars_nouveau:archmage_spell_book', 'Книга архімага', [
                'Покращення книги учня: тотем безсмертя + зірка Незеру + &6данина Вайлденів&r. Найкраща книга заклинань Ars.',
            ], deps=['ars_tribute', 'ars_apprentice', 'v_nether_star'], reward='none'),
        ]),
        Lane('Iron\'s Spells', [
            Q('isb_table', 'irons_spellbooks:inscription_table', 'Стіл написання', [
                'Iron\'s Spells \'n Spellbooks: сувої вписують у книги заклинань. У збірці стіл робиться із &6самоцвітом джерела Ars&r (міст).',
            ], deps=['ars_gem']),
            Q('isb_books', 'irons_spellbooks:iron_spell_book', 'Книга заклинань', [
                'Мідна → залізна → золота книга: що краща, то більше слотів для заклинань. Сувої заклинань «записуються» в книгу на столі написання.',
            ], deps=['isb_table']),
            Q('isb_forge', 'irons_spellbooks:scroll_forge', 'Кузня сувоїв', [
                'Полірований глибосланець + плакучий обсидіан. Тут роблять нові сувої: чорнило + папір + фокус школи магії.',
            ], deps=['isb_table']),
            Q('isb_cauldron', 'irons_spellbooks:alchemist_cauldron', 'Казан алхіміка', [
                'Варить зілля мани, розбирає сувої на чорнило й есенції. Таємна есенція + есенція вогню + казан.',
            ], deps=['isb_essence']),
            Q('isb_armor', 'irons_spellbooks:pyromancer_chestplate', 'Мантія мага', [
                'Мантія мага + руна школи + таємна есенція = броня школи (піромант, кріомант, некромант…). Більше мани й сили заклинань своєї школи.',
            ], deps=['isb_essence'], reward='none'),
            Q('isb_orb', 'irons_spellbooks:upgrade_orb', 'Сфера покращення', [
                'Таємний злиток + есенція + брухт. На таємному ковадлі покращує броню мага (наприклад, +мана, +шкода вогнем).',
            ], deps=['isb_anvil'], reward='none'),
            Q('isb_essence', 'irons_spellbooks:arcane_essence', 'Таємна есенція', [
                'Головний матеріал мода: з магічних споруд, мобів-магів і розбирання сувоїв.',
            ], deps=['isb_table']),
            Q('isb_anvil', 'irons_spellbooks:arcane_anvil', 'Таємне ковадло', [
                'Покращує сувої й книги. У збірці — з &6мана-діамантом Ботанії&r (міст).',
            ], deps=['isb_essence', 'bo_diamond']),
            Q('isb_epic', 'irons_spellbooks:epic_ink', 'Епічне чорнило', [
                'Чорнило визначає рідкісність заклинання. Звичайне → рідкісне → епічне → легендарне.',
            ], deps=['isb_essence']),
            Q('isb_ink', 'irons_spellbooks:legendary_ink', 'Легендарне чорнило', [
                'Найрідкісніше чорнило — для найсильніших заклинань. &6Один із 21 ключа.&r',
            ], deps=['isb_epic'], kind='key'),
            Q('isb_book', 'irons_spellbooks:legendary_spell_book', 'Легендарна книга', ['Книга на найбільше заклинань.'], deps=['isb_ink', 'isb_anvil'], reward='none'),
        ]),
        Lane('Доповнення Ботанії', [
            Q('eb_quartz', 'extrabotany:gaia_quartz', 'Кварц Геї', [
                'Кварц + самородок терасталі. ExtraBotany продовжує Ботанію: нові квіти, зброя й бос Его.',
            ], deps=['bo_terrasteel']),
            Q('eb_fuel', 'extrabotany:nightmare_fuel', 'Паливо кошмарів', [
                'Вугілля, наповнене маною в басейні. З паливом кошмарів і уламками духу роблять тіньовий і фотонієвий злитки.',
            ], deps=['bo_pool']),
            Q('eb_buffer', 'extrabotany:manabuffer', 'Буфер мани', [
                'Величезне сховище мани, що приймає й віддає ману розповсюджувачам. Квантовий буфер — ще більший.',
            ], deps=['eb_quartz', 'bo_gaia_ingot'], reward='none'),
            Q('eb_catalyst', 'extrabotany:dimension_catalyst', 'Каталізатор вимірів', [
                'У збірці — зі &6злитком порожнечі Таумкрафту&r (міст).',
            ], deps=['eb_shadow', 'tc_void']),
            Q('eb_shadow', 'extrabotany:shadowium_ingot', 'Тіньовий і фотонієвий злитки', [
                'Обидва — на рунічному вівтарі з елементіуму й товченої позолоченої картоплі:',
                'тіньовий — з паливом кошмарів, фотонієвий — з уламками духу.',
            ], deps=['eb_quartz', 'bo_elementium', 'eb_fuel'], items=['extrabotany:shadowium_ingot', 'extrabotany:photonium_ingot']),
            Q('eb_orichalcos', 'extrabotany:orichalcos_ingot', 'Оріхалк', [
                'Рунічний вівтар: &6медаль героя&r + 2 злитки Геї + 4 духи Геї + товчена позолочена картопля.',
                'Медаль героя — зі скриньки Пандори, яку лишає бос &6Его&r (виклик — сферою природи на арені ритуалу Геї, див. Лексикон).',
                '&6Один із 21 ключа.&r',
            ], deps=['eb_shadow', 'bo_gaia_ingot'], kind='key'),
            Q('mb_infuser', 'masterfulbotany:mana_infuser', 'Мана-інфузор', [
                'Masterful Botany: покращена плита терасталі. У збірці — на &6блоці самоцвітів джерела Ars&r (міст).',
            ], deps=['bo_terrasteel', 'ars_gem', 'bo_dragonstone', 'bo_elementium']),
            Q('mb_alfsteel', 'masterfulbotany:alfsteel_ingot', 'Альфсталь', [
                'Метал Альфгейму Masterful Botany — для найкращих інструментів, пілонів і рюкзаків.',
            ], deps=['mb_infuser', 'bo_portal']),
            Q('mb_sword', 'masterfulbotany:alfsteel_sword', 'Альфсталевий меч', [
                'Терасталевий меч + альфсталь + шаблон альфсталі на ковальському столі. Альфсталь покращує всю зброю Ботанії.',
            ], deps=['mb_alfsteel'], reward='none'),
            Q('mb_pylon', 'masterfulbotany:alfsteel_pylon', 'Альфсталевий пілон', [
                'Природний пілон + самородки альфсталі + сльоза гаста. Потужніша заміна пілонам для порталу в Альфгейм і ритуалів.',
            ], deps=['mb_alfsteel'], reward='none'),
            Q('ae_elemental', 'ars_elemental:mark_of_mastery', 'Знак майстерності', [
                'Наповнення: &6данина Вайлденів&r + есенції всіх шкіл Ars. Вершина Ars Elemental.',
            ], deps=['ars_core', 'ars_tribute', 'v_wither_skull'], reward='none'),
            Q('ae_focus', 'ars_elemental:fire_focus', 'Стихійні фокуси', [
                'Знак майстерності + менший фокус школи в зачаровувальному апараті. Фокус підсилює заклинання своєї стихії й дає імунітети.',
            ], deps=['ae_elemental'], reward='none'),
            Q('ae_siren', 'ars_elemental:siren_charm', 'Оберіг сирени', [
                'Самоцвіти джерела + уламки сирени + риба-левиця. Дозволяє довго бути під водою. Сирени живуть в океанах.',
            ], deps=['ars_apparatus'], reward='none'),
        ]),
    ])
