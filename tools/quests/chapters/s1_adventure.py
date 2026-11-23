from qb import Q, Lane, Chapter

CHAPTER = Chapter(
    'capitalism_s1_adventure', 'Пригоди й боси',
    'Сутінковий ліс, Cataclysm і ванільні боси. Тут два ключі: вогняний злиток і курсіум',
    'twilightforest:fiery_ingot', tier=3, group='side', order=10, lanes=[
        Lane('Ванілла', [
            Q('v_netherite', 'minecraft:netherite_ingot', 'Незерит', [
                'Стародавні уламки глибоко в Незері (рівень Y≈15). Незеритові інструменти — гарна база перед першими босами.',
            ], deps=['welcome']),
            Q('v_dragon', 'minecraft:dragon_egg', 'Дракон Енду', [
                'Очі Енду ведуть до твердині й порталу. Після дракона відкриваються Енд-міста з елітрами,',
                'а Draconic Evolution отримує &6серце дракона&r (при повторних викликах дракона).',
            ], deps=['v_netherite']),
            Q('v_elytra', 'minecraft:elytra', 'Елітри', [
                'Кораблі Енду в містах Енду. Політ змінює все — особливо з феєрверками.',
            ], deps=['v_dragon'], reward='none'),
            Q('v_trial', 'minecraft:heavy_core', 'Палати випробувань', [
                'Підземні палати з випробувальними спавнерами. Зловісні сховища дають важке ядро для булави.',
            ], deps=['welcome'], reward='none'),
        ]),
        Lane('Сутінковий ліс', [
            Q('tf_portal', 'twilightforest:naga_scale', 'Наги', [
                'Портал: басейн води 2×2 в оточенні квітів, кинь діамант. У Сутінковому лісі прогрес іде строго за босами — '
                'ліс сам не пустить тебе далі, доки не переможеш попереднього.',
                'Перший бос — &6Нага&r у кам\'яному дворі. Її луска — для броні.',
            ], deps=['welcome']),
            Q('tf_lich', 'twilightforest:lich_trophy', 'Ліч', [
                'Вежа Ліча. Він ховається за щитами з черепів — розбивай їх стрілами. Після нього відкриваються болота й снігові землі.',
            ], deps=['tf_portal']),
            Q('tf_minoshroom', 'twilightforest:minoshroom_trophy', 'Міношрум', [
                'Лабіринт у болотах. Мапа лабіринту з його скринь допоможе не заблукати.',
            ], deps=['tf_lich']),
            Q('tf_hydra', 'twilightforest:hydra_trophy', 'Гідра', [
                'Вогняне болото. Три голови, кожна вогняна — бери вогнестійкість.',
            ], deps=['tf_minoshroom']),
            Q('tf_fiery', 'twilightforest:fiery_ingot', 'Вогняний злиток', [
                'Кров гідри + залізо. &6Один із 21 ключа.&r',
            ], deps=['tf_hydra'], kind='key'),
            Q('tf_knight', 'twilightforest:knight_phantom_trophy', 'Лицарі-фантоми', [
                'Підземна фортеця під Темним лісом. Лицарський метал з неї — для броні.',
            ], deps=['tf_lich']),
            Q('tf_urghast', 'twilightforest:ur_ghast_trophy', 'Ур-гаст', [
                'Темна вежа. Кармініт з неї — для механізмів вежі.',
            ], deps=['tf_knight']),
            Q('tf_yeti', 'twilightforest:alpha_yeti_trophy', 'Альфа-єті', [
                'Печера в снігах. Після нього — Палац Снігової королеви.',
            ], deps=['tf_lich']),
            Q('tf_queen', 'twilightforest:snow_queen_trophy', 'Снігова королева', [
                'Аврорин палац на вершині льодовика. Найважчий бос лісу на цьому етапі.',
            ], deps=['tf_yeti', 'tf_urghast'], reward='none'),
        ]),
        Lane('Cataclysm', [
            Q('ca_monstrosity', 'cataclysm:monstrous_horn', 'Незеритова потвора', [
                'Велетенський голем у Кузні душ (Незер). Перший великий бос мода.',
            ], deps=['v_netherite']),
            Q('ca_ignis_boss', 'cataclysm:the_incinerator', 'Спопелювач', [
                'Меч Ігніса: незеритовий меч + ігнітієвий злиток + стрижень іфрита. Сам Ігніс живе на Палаючій арені в Незері.',
            ], deps=['ca_monstrosity', 'ca_ignitium']),
            Q('ca_leviathan', 'cataclysm:tidal_claws', 'Левіафан', [
                'Затоплене місто в океані. Бій під водою — бери підводне дихання й магію.',
            ], deps=['ca_monstrosity'], reward='none'),
            Q('ca_harbinger', 'cataclysm:witherite_ingot', 'Харбінджер', [
                'Механічний бос зі Стародавньої фабрики. Візеритові злитки — для зброї Cataclysm.',
            ], deps=['ca_monstrosity']),
            Q('ca_cursium', 'cataclysm:cursium_ingot', 'Малідиктус', [
                "Проклятий лицар з Морозної в'язниці. Курсіум — &6один із 21 ключа.&r",
            ], deps=['ca_harbinger'], kind='key'),
        ]),
    ])
