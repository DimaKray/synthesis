"""Назви Astral Sorcery і аддонів (додано 23.11.2026). Дописує в work/names_extra.txt рядки, яких там ще немає."""
import os
import re

HERE = os.path.dirname(os.path.abspath(__file__))
OUT = os.path.join(HERE, 'work', 'names_extra.txt')
T = {
    '%s Artifact': 'Артефакт: %s', '%s Artifact Shard': 'Уламок артефакту: %s', '%s Lumen Crystal': 'Кристал люмену: %s',
    '%s Lumen Crystal Cluster': 'Скупчення кристалів люмену: %s',
    'Aberrant Circuit Board': 'Аберантна друкована плата', 'Aberrant Crystal': 'Аберантний кристал',
    'Aberrant Crystal Block': 'Блок аберантного кристала', 'Aberrant Crystal Cluster': 'Скупчення аберантних кристалів',
    'Aberrant Inscriber Press': 'Аберантний прес висікача', 'Aberrant Processor': 'Аберантний процесор',
    'Akashic Singularity': 'Акашична сингулярність', 'Altar Automation Interface': 'Інтерфейс автоматизації вівтаря',
    'An empty and sparkly scroll': 'Порожній мерехтливий сувій', 'Angle: %s': 'Кут: %s',
    'Applied Astralsorcery': 'Applied AstralSorcery', 'Aquamarine': 'Аквамарин', 'Aquamarine Shale': 'Аквамариновий сланець',
    'Artifact': 'Артефакт', 'Artifact Shard': 'Уламок артефакту', 'Astral Sorcery': 'Astral Sorcery',
    'Astral Sorcery - Artifacts': 'Astral Sorcery — артефакти', 'Astral Sorcery - Attuned Crystals': 'Astral Sorcery — налаштовані кристали',
    'Astral Sorcery - Constellation Papers': 'Astral Sorcery — папери сузір\'їв', 'Astral Sorcery - Lumen': 'Astral Sorcery — люмен',
    'Astrolabe': 'Астролябія', 'Attuned Celestial Crystal': 'Налаштований небесний кристал',
    'Attuned Rock Crystal': 'Налаштований гірський кришталь', 'Attunement Altar': 'Вівтар налаштування',
    'Automatic Starmetal Chisel': 'Автоматичне зубило з зоряного металу', 'Bucket of Liquid Starlight': 'Відро рідкого зоряного світла',
    'Cave Illuminator': 'Освітлювач печер', 'Celestial Crystal': 'Небесний кристал', 'Celestial Crystal Cluster': 'Скупчення небесних кристалів',
    'Chiseled Marble': 'Різьблений мармур', 'Constellation Core': 'Ядро сузір\'я', 'Constellation Paper': 'Папір сузір\'я',
    'Constellation Relay': 'Реле сузір\'я', 'Containment Chalice': 'Чаша утримання',
    'Contains a vast amount of knowledge': 'Містить безмежну кількість знань', 'Contains knowledge from %s': 'Містить знання від %s',
    'Conversion Wand': 'Жезл перетворення', 'Crushed Raw Starmetal': 'Подрібнений необроблений зоряний метал',
    'Empty Flask': 'Порожня колба', 'Engraved Infused Wood': 'Гравірована наповнена деревина', 'Engraved Marble': 'Гравірований мармур',
    'Enriched Infused Wood': 'Збагачена наповнена деревина', 'Fengarum Dynamism Gem': 'Самоцвіт динамізму Фенгарум',
    'Flare': 'Спалах', 'Flare Light': 'Світло спалаху', 'Fluid Spark': 'Рідинна іскра', 'Focus Relay': 'Реле фокуса',
    'Formation Wand': 'Жезл формування', 'Gem Crystal Cluster': 'Скупчення кристалів-самоцвітів', 'Glass Lens': 'Скляна лінза',
    'Glimmer Amaranth': 'Мерехтливий амарант', 'Grappling Hook': 'Гак-кішка', 'Hyacinth': 'Гіацинт',
    'Ilium Dynamism Gem': 'Самоцвіт динамізму Іліум', 'Illuminated Crafting Table': 'Освітлений верстак',
    'Illumination Powder': 'Порошок освітлення', 'Illumination Spark': 'Іскра освітлення', 'Illumination Wand': 'Жезл освітлення',
    'Impulsion Wand': 'Жезл поштовху', 'Infused Wood': 'Наповнена деревина', 'Infused Wood Arch': 'Арка з наповненої деревини',
    'Infused Wood Column': 'Колона з наповненої деревини', 'Infused Wood Planks': 'Дошки з наповненої деревини',
    'Infused Wood Slab': 'Плита з наповненої деревини', 'Infused Wood Stairs': 'Сходи з наповненої деревини',
    'Iridescent Crafting Altar': 'Райдужний крафтовий вівтар', 'Iridescent Shifting Star': 'Райдужна мінлива зоря', 'Iris': 'Ірис',
    'Item Spark': 'Предметна іскра', 'L': 'L', 'Lens': 'Лінза', 'Lightwell': 'Світлокриниця', 'Linking Tool': 'Інструмент зв\'язування',
    'Liquid Starlight': 'Рідке зоряне світло', 'Lumen Alchemy Array': 'Алхімічний масив люмену', 'Lumen Array': 'Масив люмену',
    'Lumen Cell Housing': 'Корпус комірки люмену', 'Lumen Circuit Board': 'Друкована плата люмену', 'Lumen Crystal': 'Кристал люмену',
    'Lumen Crystal Block': 'Блок кристала люмену', 'Lumen Crystal Cluster': 'Скупчення кристалів люмену',
    'Lumen Crystallizer': 'Кристалізатор люмену', 'Lumen Filament': 'Нитка люмену', 'Lumen Inscriber Press': 'Прес висікача люмену',
    'Lumen Processor': 'Процесор люмену', 'Luminous Crafting Altar': 'Світний крафтовий вівтар',
    'ME Celestial Gateway': 'ME небесна брама', 'ME Chalice': 'ME чаша', 'ME Lumen Alchemy Array': 'ME алхімічний масив люмену',
    'ME Lumen Array': 'ME масив люмену', 'ME Lumen Crystal Collection Plane': 'ME площина збору кристалів люмену',
    'ME Lumen Crystallizer': 'ME кристалізатор люмену', 'ME Lumen Filament': 'ME нитка люмену', 'ME Resonating Wand': 'ME резонуючий жезл',
    'ME Starlight Infuser': 'ME наповнювач зоряного світла', 'ME Tree Beacon': 'ME маяк дерев', 'Marble': 'Мармур',
    'Marble Arch': 'Мармурова арка', 'Marble Bricks': 'Мармурова цегла', 'Marble Pillar': 'Мармурова колона',
    'Marble Slab': 'Мармурова плита', 'Marble Stairs': 'Мармурові сходи', 'Nocturnal Powder': 'Нічний порошок',
    'Nocturnal Spark': 'Нічна іскра', 'Non-Empty Annihilation Plane': 'Непорожня площина знищення', 'Orchid': 'Орхідея',
    'Ourium Dynamism Gem': 'Самоцвіт динамізму Оуріум', 'P2P Tunnel - Starlight': 'P2P-тунель — зоряне світло',
    'Parchment': 'Пергамент', 'Perk Nullifier': 'Анулятор перків', 'Perk Seal': 'Печатка перків', 'Phoenix Blessing': 'Благословення фенікса',
    'Prism': 'Призма', 'Protea': 'Протея', 'Rampage': 'Шаленство', 'Raw Starmetal': 'Необроблений зоряний метал',
    'Raw Starmetal Block': 'Блок необробленого зоряного металу', 'Resonating Crafting Table': 'Резонуючий верстак',
    'Resonating Gem': 'Резонуючий самоцвіт', 'Resonating Wand': 'Резонуючий жезл', 'Resplendent Prism': 'Сяйна призма',
    'Rock Crystal': 'Гірський кришталь', 'Rock Crystal Ore': 'Руда гірського кришталю', 'Runed Marble': 'Рунний мармур',
    'Scroll of Written Expertise': 'Сувій записаної майстерності', 'Shifting Star': 'Мінлива зоря', 'Shooting Star': 'Падуча зоря',
    'Sooty Chiseled Marble': 'Закопчений різьблений мармур', 'Sooty Engraved Marble': 'Закопчений гравірований мармур',
    'Sooty Marble': 'Закопчений мармур', 'Sooty Marble Arch': 'Закопчена мармурова арка', 'Sooty Marble Bricks': 'Закопчена мармурова цегла',
    'Sooty Marble Pillar': 'Закопчена мармурова колона', 'Sooty Marble Slab': 'Закопчена мармурова плита',
    'Sooty Marble Stairs': 'Закопчені мармурові сходи', 'Sooty Runed Marble': 'Закопчений рунний мармур',
    'Spatial Return Portal': 'Просторовий портал повернення', 'Stardew': 'Зоряна роса', 'Stardust': 'Зоряний пил',
    'Starlight Focus Crystal': 'Кристал фокуса зоряного світла', 'Starlight Infuser': 'Наповнювач зоряного світла',
    'Starlight Mysterious Cube': 'Таємничий куб зоряного світла', 'Starlight Transmutation Chamber': 'Камера трансмутації зоряного світла',
    'Starmetal Casing': 'Корпус із зоряного металу', 'Starmetal Chisel': 'Зубило із зоряного металу',
    'Starmetal Encased Cogwheel': 'Шестерня в корпусі із зоряного металу',
    'Starmetal Encased Large Cogwheel': 'Велика шестерня в корпусі із зоряного металу',
    'Starmetal Encased Shaft': 'Вал у корпусі із зоряного металу', 'Starmetal Ingot': 'Злиток зоряного металу',
    'Starmetal Nugget': 'Самородок зоряного металу', 'Starmetal Ore': 'Руда зоряного металу', 'Stellar Filament': 'Зоряна нитка',
    'Structure Marker': 'Маркер споруди', 'Thistle': 'Будяк', 'Tome': 'Том', 'Traversal Wand': 'Жезл переходу',
    'Tree Beacon': 'Маяк дерев', 'Vibrant Infused Wood': 'Яскрава наповнена деревина', 'Vivid Powder': 'Яскравий порошок',
    'Vivid Spark': 'Яскрава іскра', 'Water Flask': 'Колба з водою',
}
for size in ('1k', '4k', '16k', '64k', '256k'):
    T[f'{size} Lumen Storage Component'] = f'Компонент сховища люмену {size}'
    T[f'{size} ME Lumen Storage Cell'] = f'ME комірка сховища люмену {size}'

have = {l.split('\t', 1)[0] for l in open(OUT, encoding='utf-8') if l.strip()} if os.path.exists(OUT) else set()
roman = re.compile(r'^[IVXLC]+$')
need = os.path.join(HERE, 'work', 'new_names.txt')
wanted = [l.rstrip('\n') for l in open(need, encoding='utf-8') if l.strip()]
added, miss = 0, []
with open(OUT, 'a', encoding='utf-8') as f:
    for en in wanted:
        if en in have:
            continue
        uk = T.get(en, en if roman.match(en) else None)
        if uk is None:
            miss.append(en)
            continue
        f.write(f'{en}\t{uk}\n')
        added += 1
print('додано:', added, '| без перекладу:', miss)
