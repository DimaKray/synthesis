from qb import Q, Lane, Chapter

CHAPTER = Chapter(
    'capitalism_s5_farms', 'Ферми й світи',
    'Mystical Agriculture і виміри DivineRPG. Ключі: інсаніум і серце Мортуму',
    'mysticalagriculture:inferium_essence', tier=2, group='side', order=14, lanes=[
        Lane('Mystical Agriculture', [
            Q('ma_inferium', 'mysticalagriculture:inferium_essence', 'Есенція інферіуму', [
                'Насіння інферіуму росте з руди інферіуму; врожай — есенція, головна валюта мода. '
                'З есенцій вирощують ресурси: залізо, алмази, мана, навіть руди інших модів.',
            ], deps=['welcome']),
            Q('ma_altar', 'mysticalagriculture:infusion_altar', 'Вівтар інфузії', [
                'Крафт насіння: центр + 8 п\'єдесталів. У збірці — з &6манасталі й мана-перлини Ботанії&r (міст).',
            ], deps=['ma_inferium', 'bo_pearl']),
            Q('ma_iron', 'mysticalagriculture:iron_seeds', 'Залізне насіння', [
                'Перше ресурсне насіння: залізо, що росте само. Зроби таке ж для потрібних тобі ресурсів.',
            ], deps=['ma_altar']),
            Q('ma_prudentium', 'mysticalagriculture:prudentium_essence', 'Покращення есенцій', [
                'Інферіум → пруденціум → терціум → імперіум → супреміум. Кожен рівень відкриває цінніше насіння.',
            ], deps=['ma_altar'], items=['mysticalagriculture:prudentium_essence', 'mysticalagriculture:tertium_essence']),
            Q('ma_supremium', 'mysticalagriculture:supremium_essence', 'Супреміум', ['Найвища звичайна есенція.'], deps=['ma_prudentium']),
            Q('ma_awakening', 'mysticalagriculture:awakening_altar', 'Вівтар пробудження', [
                'У збірці — з &6драконієм і драконієвим ядром&r (міст). Пробуджує супреміум.',
            ], deps=['ma_supremium', 'de_core']),
            Q('ma_insanium', 'mysticalagradditions:insanium_ingot', 'Інсаніум', [
                'Злиток процвітання + 2 есенції інсаніуму. Mystical Agradditions — есенція понад супреміум. &6Один із 21 ключа.&r',
            ], deps=['ma_awakening'], kind='key'),
        ]),
        Lane('Mystical Agriculture Plus', [
            Q('map_seeds', 'mysticalagricultureplus:andesite_alloy_seeds', 'Насіння модів', [
                'Mystical Agriculture Plus додає насіння для ресурсів інших модів: андезитовий сплав, есенції Ars, таємний кристал і т.д. '
                'Робиться так само — на вівтарі наповнення з основи насіння й ресурсу.',
            ], deps=['ma_altar', 'cr_alloy', 'ma_prudentium']),
            Q('map_antimatter', 'mysticalagricultureplus:antimatter_seeds', 'Насіння антиматерії', [
                'Наповнення: основа насіння процвітання + &6пелети антиматерії&r + блоки інсаніуму. Вирощуй антиматерію замість SPS.',
            ], deps=['map_seeds', 'ma_insanium', 'mk_antimatter'], reward='none'),
        ]),
        Lane('DivineRPG', [
            Q('dv_arlemite', 'divinerpg:arlemite_ingot', 'Арлеміт', [
                'DivineRPG додає купу вимірів з босами. Арлеміт і рілміт — перші руди в звичайному світі.',
            ], deps=['welcome']),
            Q('dv_shards', 'divinerpg:divine_shards', 'Божественні уламки', [
                'Потрібні для порталів у виміри Едему й далі.',
            ], deps=['dv_arlemite']),
            Q('dv_eden', 'divinerpg:eden_gem', 'Едем', [
                'Перший «небесний» вимір. Самоцвіти кожного виміру відкривають портал у наступний:',
                'Едем → Дикий ліс → Апалачія → Скайтерн → Мортум.',
            ], deps=['dv_shards']),
            Q('dv_skythern', 'divinerpg:skythern_gem', 'Скайтерн', [
                'Самоцвіти крафтяться з фрагментів, що падають з мобів відповідного виміру. Скайтерн — передостанній небесний вимір.',
            ], deps=['dv_eden'],
              items=['divinerpg:wildwood_gem', 'divinerpg:apalachia_gem', 'divinerpg:skythern_gem']),
            Q('dv_mortum', 'divinerpg:mortum_heart', 'Серце Мортуму', [
                'Найтемніший вимір DivineRPG. &6Один із 21 ключа.&r',
            ], deps=['dv_skythern'], kind='key'),
        ]),
    ])
