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
            Q('ma_seeds', 'mysticalagriculture:inferium_seeds', 'Насіння інферіуму', [
                'Есенція інферіуму + насіння пшениці. Посади на &6інферіумній ріллі&r (рілля + есенція) — дає більше есенції.',
            ], deps=['ma_inferium']),
            Q('ma_accel', 'mysticalagriculture:inferium_growth_accelerator', 'Прискорювач росту', [
                'Ставиться під грядку (до кількох у стовпчик) — рослини над ним ростуть швидше.',
            ], deps=['ma_seeds'], reward='none'),
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
            Q('ma_harvester', 'mysticalagriculture:harvester', 'Комбайн', [
                'Рамка машини + соуліумовий злиток + алмазна коса. Сам збирає врожай і пересаджує. Соуліум — з есенції душ (екстрактор душ).',
            ], deps=['ma_supremium']),
            Q('ma_armor', 'mysticalagriculture:supremium_chestplate', 'Броня супреміуму', [
                'Інструменти й броня з есенцій покращуються рівнями: інферіум → … → супреміум. Супреміумова броня з аугментами — одна з найкращих.',
            ], deps=['ma_supremium'], reward='none'),
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
        Lane("Farmer's Delight", [
            Q('fd_knife', 'farmersdelight:flint_knife', 'Ніж', [
                "Farmer's Delight додає справжню кухню. Ніж (кремінь + палиця) ріже на обробній дошці й збирає з трави солому, а з мобів — більше м'яса.",
                'Нові культури — капуста, помідори, цибуля, рис — ростуть дикими: шукай їх на узбережжях і в теплих біомах.',
            ], deps=['welcome']),
            Q('fd_board', 'farmersdelight:cutting_board', 'Обробна дошка', [
                'Дошки + палиця. Поклади продукт на дошку й ріж ножем (ПКМ): фарш, шматки, нарізка. Сокирою — кора з колод, киркою — подрібнення.',
            ], deps=['fd_knife']),
            Q('fd_pot', 'farmersdelight:cooking_pot', 'Каструля', [
                "Цегла + залізо + дерев'яна лопата + вода. Постав на &6плиту&r або вогнище — і вари страви з кількох інгредієнтів (рецепти в JEI).",
                'Страви ситніші за ванільну їжу й дають корисні ефекти: «Ситість» і «Затишок».',
            ], deps=['fd_board']),
            Q('fd_stove', 'farmersdelight:stove', 'Плита', [
                'Цегла + вогнище + залізо. Гріє каструлю й сковороду, а зверху на ній можна смажити шість шматків одразу.',
            ], deps=['fd_pot'], reward='none'),
            Q('fd_soil', 'farmersdelight:rich_soil', 'Родючий ґрунт', [
                "Органічний компост (земля + солома + кістне борошно + гниле м'ясо чи кора) сам перетворюється на родючий ґрунт. "
                'На ньому рослини ростуть швидше, а з грибів виростають великі гриби.',
            ], deps=['fd_knife']),
            Q('fd_feast', 'farmersdelight:roast_chicken_block', 'Святковий стіл', [
                'Великі страви-«бенкети» ставляться блоком, і з них беруть порції мискою. Запечена курка — курятина, картопля, морква, цибуля, яйце й хліб.',
            ], deps=['fd_pot'], reward='none'),
            Q('sad_slicer', 'sliceanddice:slicer', 'Різальна машина', [
                'Create Slice & Dice: автоматична кухня на Create. Різальна машина (андезитовий корпус + поворотний стіл + шестерня) '
                'ріже продукти ножем замість тебе — на депо чи конвеєрі.',
                'Каструлю на плиті наповнюють механічні руки, а &6розбризкувач&r поливає грядки водою чи рідким добривом.',
            ], deps=['fd_pot', 'cr_casing']),
        ]),
        Lane('DivineRPG', [
            Q('dv_arlemite', 'divinerpg:arlemite_ingot', 'Арлеміт', [
                'DivineRPG додає купу вимірів з босами. Арлеміт і рілміт — перші руди в звичайному світі.',
            ], deps=['welcome']),
            Q('dv_realmite', 'divinerpg:realmite_ingot', 'Реаліміт і рупі', [
                'Руди DivineRPG у звичайному світі: реаліміт, арлеміт, рупі. Броня з них сильніша за алмазну.',
            ], deps=['welcome'], reward='none'),
            Q('dv_shadow', 'divinerpg:shadow_bar', 'Тіньовий злиток', [
                'Переплавлений тіньовий пил (з мобів). Потрібен для порталів і зброї мода.',
            ], deps=['dv_arlemite']),
            Q('dv_shards', 'divinerpg:divine_shards', 'Божественні уламки', [
                'Потрібні для порталів у виміри Едему й далі.',
            ], deps=['dv_arlemite']),
            Q('dv_eden', 'divinerpg:eden_gem', 'Едем', [
                'Перший «небесний» вимір. Самоцвіти кожного виміру відкривають портал у наступний:',
                'Едем → Дикий ліс → Апалачія → Скайтерн → Мортум.',
            ], deps=['dv_shards']),
            Q('dv_wildwood', 'divinerpg:wildwood_gem', 'Дикий ліс', [
                'Кожен небесний вимір DivineRPG дає самоцвіт із фрагментів мобів: Едем → Дикий ліс → Апалачія → Скайтерн → Мортум. '
                'Портали — з блоків самоцвіту попереднього виміру (див. JEI).',
            ], deps=['dv_eden']),
            Q('dv_apalachia', 'divinerpg:apalachia_gem', 'Апалачія', [
                'Третій небесний вимір. Самоцвіт — з фрагментів Апалачії.',
            ], deps=['dv_wildwood']),
            Q('dv_skythern', 'divinerpg:skythern_gem', 'Скайтерн', [
                'Самоцвіти крафтяться з фрагментів, що падають з мобів відповідного виміру. Скайтерн — передостанній небесний вимір.',
            ], deps=['dv_eden'],
              items=['divinerpg:wildwood_gem', 'divinerpg:apalachia_gem', 'divinerpg:skythern_gem']),
            Q('dv_mortum', 'divinerpg:mortum_heart', 'Серце Мортуму', [
                'Найтемніший вимір DivineRPG. &6Один із 21 ключа.&r',
            ], deps=['dv_skythern'], kind='key'),
        ]),
    ])
