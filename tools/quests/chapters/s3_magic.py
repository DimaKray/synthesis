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
        ]),
        Lane('Ars Nouveau', [
            Q('ars_scribe', 'ars_nouveau:scribes_table', 'Стіл писаря', [
                'Тут вчать нові гліфи для заклинань (за самоцвіти джерела й предмети).',
            ], deps=['ars_book']),
            Q('ars_apprentice', 'ars_nouveau:apprentice_spell_book', 'Книга учня', [
                'Більше слотів заклинань і гліфи другого рівня.',
            ], deps=['ars_scribe', 'ars_chamber']),
            Q('ars_core', 'ars_nouveau:arcane_core', 'Таємне ядро', [
                'Центр ритуалів і пізніх рецептів Ars (через зачаровувальний апарат).',
            ], deps=['ars_apparatus']),
            Q('ars_horn', 'ars_nouveau:wilden_horn', 'Вайлдени', [
                'Вайлдени — дикі звірі Ars у лісах і горах. Їхні роги, шипи й крила потрібні для виклику Хімери.',
            ], deps=['ars_book']),
            Q('ars_tribute', 'ars_nouveau:wilden_tribute', 'Данина Вайлденів', [
                'Виклич Вайлден-Хімеру ритуалом і перемож — вона лишить данину. &6Один із 21 ключа.&r',
            ], deps=['ars_horn', 'ars_core'], kind='key'),
            Q('ars_archmage', 'ars_nouveau:archmage_spell_book', 'Книга архімага', [
                'Найкраща книга заклинань Ars.',
            ], deps=['ars_tribute', 'ars_apprentice'], reward='none'),
        ]),
        Lane('Iron\'s Spells', [
            Q('isb_table', 'irons_spellbooks:inscription_table', 'Стіл написання', [
                'Iron\'s Spells \'n Spellbooks: сувої вписують у книги заклинань. У збірці стіл робиться із &6самоцвітом джерела Ars&r (міст).',
            ], deps=['ars_gem']),
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
                'ExtraBotany продовжує Ботанію після Геї: нові квіти, зброя й бос Его.',
            ], deps=['bo_gaia_spirit']),
            Q('eb_catalyst', 'extrabotany:dimension_catalyst', 'Каталізатор вимірів', [
                'У збірці — зі &6злитком порожнечі Таумкрафту&r (міст).',
            ], deps=['eb_quartz', 'tc_void']),
            Q('eb_shadow', 'extrabotany:shadowium_ingot', 'Тіньовий і фотонієвий злитки', [
                'Два протилежні метали ExtraBotany — обидва потрібні для оріхалку.',
            ], deps=['eb_catalyst'], items=['extrabotany:shadowium_ingot', 'extrabotany:photonium_ingot']),
            Q('eb_orichalcos', 'extrabotany:orichalcos_ingot', 'Оріхалк', [
                'Найвищий метал ExtraBotany. &6Один із 21 ключа.&r',
            ], deps=['eb_shadow'], kind='key'),
            Q('mb_infuser', 'masterfulbotany:mana_infuser', 'Мана-інфузор', [
                'Masterful Botany: покращена плита терасталі. У збірці — на &6блоці самоцвітів джерела Ars&r (міст).',
            ], deps=['bo_terrasteel', 'ars_gem']),
            Q('mb_alfsteel', 'masterfulbotany:alfsteel_ingot', 'Альфсталь', [
                'Метал Альфгейму Masterful Botany — для найкращих інструментів, пілонів і рюкзаків.',
            ], deps=['mb_infuser', 'bo_portal']),
            Q('ae_elemental', 'ars_elemental:mark_of_mastery', 'Знак майстерності', [
                'Ars Elemental: стихійні фокуси й школи магії. Знак — вершина мода.',
            ], deps=['ars_core'], reward='none'),
        ]),
    ])
