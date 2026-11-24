"""Переклад назв Steam 'n' Rails, Create Deco і Mekanism Extras за шаблонами (кольори/рівні з узгодженням роду),
плюс ручний словник ic2_names.tsv (IC2R, IC2R-Addons, решта). Дописує в work/names_extra.txt лише відсутні рядки.
Запуск: python tools/ua_pack/gen_newmods_names.py"""
import json
import os
import re

HERE = os.path.dirname(os.path.abspath(__file__))
WORK = os.path.join(HERE, 'work')

# прикметник: (основа, м'яка?) -> форми m/f/n/p/fg (жін. родовий)
ADJ = {
    'White': ('біл', 0), 'Orange': ('помаранчев', 0), 'Magenta': ('пурпуров', 0), 'Light Blue': ('блакитн', 0),
    'Yellow': ('жовт', 0), 'Lime': ('лаймов', 0), 'Pink': ('рожев', 0), 'Gray': ('сір', 0), 'Grey': ('сір', 0),
    'Light Gray': ('світло-сір', 0), 'Light Grey': ('світло-сір', 0), 'Cyan': ('бірюзов', 0), 'Purple': ('фіолетов', 0),
    'Blue': ('син', 1), 'Brown': ('коричнев', 0), 'Green': ('зелен', 0), 'Red': ('червон', 0), 'Black': ('чорн', 0),
    'Chartreuse': ('шартрезов', 0), 'Maroon': ('бордов', 0), 'Ochrum': ('охрян', 0), 'Olive Green': ('оливков', 0),
    'Pine Green': ('хвойно-зелен', 0), 'Royal Blue': ('королівсько-син', 1), 'Scorchia': ('обпален', 0),
    'Sea Green': ('морсько-зелен', 0), 'Turquoise': ('бірюзово-зелен', 0), 'Vermilion': ('кіноварн', 0),
    'Diorite': ('діоритов', 0), 'Dripstone': ('натічн', 0), 'Granite': ('гранітн', 0), 'Limestone': ('вапняков', 0),
    'Tuff': ('туфов', 0), 'Locometal': ('локометалев', 0),
    'Dean': ('пісочн', 0), 'Dusk': ('сутінков', 0), 'Pearl': ('перламутров', 0), 'Scarlet': ('багрян', 0),
    'Umber': ('умбров', 0), 'Verdant': ('смарагдов', 0),
    # рівні Mekanism Extras
    'Absolute': ('абсолютн', 0), 'Supreme': ('верховн', 0), 'Cosmic': ('космічн', 0), 'Infinite': ('нескінченн', 0),
}
END = {0: {'m': 'ий', 'f': 'а', 'n': 'е', 'p': 'і', 'fg': 'ої'}, 1: {'m': 'ій', 'f': 'я', 'n': 'є', 'p': 'і', 'fg': 'ьої'}}


def adj(name, g):
    stem, soft = ADJ[name]
    return stem + END[soft][g]


def cap(s):
    return s[:1].upper() + s[1:]


COLORS = sorted(ADJ, key=len, reverse=True)
COLOR_RE = '(' + '|'.join(re.escape(c) for c in COLORS) + ')'

# ---------- Steam 'n' Rails ----------
RAIL = [  # (англ. хвіст, рід, укр. хвіст)
    ('Brass Wrapped Locometal Boiler', 'm', 'котел у латунній обгортці'),
    ('Copper Wrapped Locometal Boiler', 'm', 'котел у мідній обгортці'),
    ('Iron Wrapped Locometal Boiler', 'm', 'котел у залізній обгортці'),
    ('Brass Wrapped Locometal Smokebox', 'f', 'димова коробка в латунній обгортці'),
    ('Copper Wrapped Locometal Smokebox', 'f', 'димова коробка в мідній обгортці'),
    ('Iron Wrapped Locometal Smokebox', 'f', 'димова коробка в залізній обгортці'),
    ('Brass Wrapped Locometal', 'm', 'локометал у латунній обгортці'),
    ('Copper Wrapped Locometal', 'm', 'локометал у мідній обгортці'),
    ('Iron Wrapped Locometal', 'm', 'локометал у залізній обгортці'),
    ('Folding Locometal Door', 'p', 'складні локометалеві двері'),
    ('Hinged Locometal Door', 'p', 'навісні локометалеві двері'),
    ('Sliding Locometal Door', 'p', 'розсувні локометалеві двері'),
    ('Four Pane Locometal Window', 'n', 'локометалеве вікно на 4 шибки'),
    ('Two Pane Locometal Window', 'n', 'локометалеве вікно на 2 шибки'),
    ('Single Pane Locometal Window', 'n', 'локометалеве вікно з однією шибкою'),
    ('Round Pane Locometal Window', 'n', 'локометалеве вікно з круглою шибкою'),
    ('Locometal End Ladder', 'f', 'торцева локометалева драбина'),
    ('Locometal Rung Ladder', 'f', 'локометалева драбина зі щаблями'),
    ('Locometal Flywheel', 'm', 'локометалевий маховик'),
    ('Locometal Pillar', 'f', 'локометалева колона'),
    ('Locometal Trapdoor', 'm', 'локометалевий люк'),
    ('Locometal Vent', 'f', 'локометалева вентиляційна решітка'),
    ('Locometal Boiler', 'm', 'локометалевий котел'),
    ('Locometal Smokebox', 'f', 'локометалева димова коробка'),
    ('Riveted Locometal', 'm', 'клепаний локометал'),
    ('Slashed Locometal', 'm', 'смугастий локометал'),
    ('Paint Pitcher', 'm', 'глечик фарби'),
    ('Paint', 'f', 'фарба'),
]
SMOKE_MAT = {'Brass': 'латунна', 'Copper': 'мідна', 'Steel': 'сталева'}
SMOKE_KIND = {'Coalburner': 'вугільна', 'Double': 'подвійна', 'Oilburner': 'нафтова', 'Streamlined': 'обтічна',
              'Woodburner': 'дров\'яна'}


def rail(en):
    m = re.fullmatch(COLOR_RE + r' on ' + COLOR_RE + r' (Chevron|Hazard Stripes)', en)
    if m:
        what = 'Шеврон' if m.group(3) == 'Chevron' else 'Смуги небезпеки'
        return f'{what} ({adj(m.group(1), "m")} на {adj(m.group(2), "m")})'
    m = re.fullmatch(r'Iron Capped (Brass|Copper|Steel) (\w+) Smokestack', en)
    if m:
        return f'{cap(SMOKE_MAT[m.group(1)])} {SMOKE_KIND[m.group(2)]} димова труба із залізною шапкою'
    m = re.fullmatch(r'(Flat |Plated )?(?:' + COLOR_RE + r' )?(.+)', en)
    pre, col, tail = m.group(1), m.group(2), m.group(3)
    if pre == 'Plated ' and tail == 'Locometal':
        return cap(f'{adj(col, "m")} обшитий локометал') if col else None
    for t, g, uk in RAIL:
        if tail == t:
            if pre == 'Flat ':
                uk = 'плаский ' + uk
            return cap(f'{adj(col, g)} {uk}' if col else uk)
    return None


# ---------- Create Deco ----------
BRICK_MOD = {'Corner': 'кутов', 'Cracked': 'потріскан', 'Long': 'довг', 'Mossy': 'замшіл', 'Short': 'коротк',
             'Tiled': 'плитков'}
METAL = {'Andesite': 'андезиту', 'Brass': 'латуні', 'Copper': 'міді', 'Iron': 'заліза', 'Industrial Iron': 'промислового заліза',
         'Zinc': 'цинку', 'Gold': 'золота', 'Netherite': 'незериту'}
METAL_ITEM = {
    'Bars': 'Ґрати з {m}', 'Bars Overlay': 'Накладка ґрат з {m}', 'Catwalk': 'Місток з {m}',
    'Catwalk Railing': 'Поруччя містка з {m}', 'Catwalk Stairs': 'Сходи містка з {m}', 'Door': 'Двері з {m}',
    'Mesh Fence': 'Сітчастий паркан з {m}', 'Sheet Metal': 'Листовий метал з {m}', 'Support': 'Опора з {m}',
    'Support Wedge': 'Клин опори з {m}', 'Train Hull': 'Корпус поїзда з {m}', 'Trapdoor': 'Люк з {m}',
    'Window': 'Вікно з {m}', 'Window Pane': 'Віконна панель з {m}', 'Coin': 'Монета з {m}',
    'Coin Stack': 'Стос монет з {m}', 'Ladder': 'Драбина з {m}', 'Sheet': 'Лист з {m}', 'Ingot': 'Злиток {m}',
    'Nugget': 'Самородок {m}', 'Alloy Sheet': 'Лист андезитового сплаву',
}


def deco(en):
    m = re.fullmatch(r'(?:(Corner|Cracked|Long|Mossy|Short|Tiled) )?' + COLOR_RE + r' (Bricks|Brick Slab|Brick Stairs|Brick Wall)', en)
    if m:
        mod, col, kind = m.groups()
        if kind == 'Bricks':
            return cap(adj(col, 'f') + (' ' + BRICK_MOD[mod] + 'а' if mod else '') + ' цегла')
        gen = adj(col, 'fg') + (' ' + BRICK_MOD[mod] + 'ої' if mod else '')
        return {'Brick Slab': 'Плита', 'Brick Stairs': 'Сходи', 'Brick Wall': 'Стіна'}[kind] + f' з {gen} цегли'
    m = re.fullmatch(COLOR_RE + r' (Andesite|Brass|Copper|Industrial Iron|Iron|Zinc) Cage Lamp', en)
    if m:
        return cap(f'{adj(m.group(1), "f")} лампа в клітці з {METAL[m.group(2)]}')
    m = re.fullmatch(COLOR_RE + r' (Placard|Shipping Container)', en)
    if m:
        return cap(adj(m.group(1), 'm') + (' плакат' if m.group(2) == 'Placard' else ' вантажний контейнер'))
    m = re.fullmatch(r'(Locked )?(Andesite|Brass|Copper|Industrial Iron|Iron|Zinc|Gold|Netherite) ?(.+)', en)
    if m:
        locked, met, item = m.groups()
        if item == 'Coin Stack Block' or item.endswith('Coin Stack Block'):
            return f'Блок стосу монет з {METAL[met]}'
        if item in METAL_ITEM:
            s = METAL_ITEM[item].format(m=METAL[met])
            return ('Замкнені ' + s[0].lower() + s[1:]) if locked else s
    return None


# ---------- Mekanism Extras ----------
MEK = {
    'Bin': ('m', 'бункер'), 'Chemical Tank': ('m', 'хімічний бак'), 'Control Circuit': ('f', 'схема керування'),
    'Energy Cube': ('m', 'енергетичний куб'), 'Fluid Tank': ('m', 'рідинний бак'),
    'Induction Cell': ('f', 'індукційна комірка'), 'Induction Provider': ('m', 'індукційний постачальник'),
    'Logistical Transporter': ('m', 'логістичний транспортер'), 'Mechanical Pipe': ('f', 'механічна труба'),
    'Pressurized Tube': ('f', 'трубка під тиском'), 'Radioactive Waste Barrel': ('f', 'бочка радіоактивних відходів'),
    'Thermodynamic Conductor': ('m', 'термодинамічний провідник'), 'Tier Installer': ('m', 'установник рівня'),
    'Universal Cable': ('m', 'універсальний кабель'),
}
FACT = {'Centrifuging': 'центрифугування', 'Combining': 'об\'єднання', 'Compressing': 'стиснення',
        'Crushing': 'дроблення', 'Crystallizing': 'кристалізації', 'Dissolving': 'розчинення',
        'Enriching': 'збагачення', 'Infusing': 'насичення', 'Injecting': 'вприскування', 'Lathe': 'токарної обробки',
        'Liquifying': 'розрідження', 'Oxidizing': 'окиснення', 'Painting': 'фарбування',
        'Pigment Extracting': 'видобування пігменту', 'Planting': 'вирощування', 'Pressing': 'пресування',
        'Pressurised Reacting': 'реакцій під тиском', 'Purifying': 'очищення', 'Recycling': 'переробки',
        'Replicating': 'реплікації', 'Rolling Mill': 'прокатки', 'Sawing': 'розпилювання', 'Smelting': 'плавки',
        'Stamping': 'штампування', 'Washing': 'промивання'}


def mek(en):
    m = re.fullmatch(r'(Absolute|Supreme|Cosmic|Infinite) (.+)', en)
    if not m:
        return None
    tier, rest = m.groups()
    if rest.endswith(' Factory') and rest[:-8] in FACT:
        return cap(f'{adj(tier, "f")} фабрика {FACT[rest[:-8]]}')
    if rest in MEK:
        g, uk = MEK[rest]
        return cap(f'{adj(tier, g)} {uk}')
    return None


def main():
    keys = json.load(open(os.path.join(WORK, 'keys.json'), encoding='utf-8'))
    tr = json.load(open(os.path.join(WORK, 'names_tr.json'), encoding='utf-8'))
    extra_path = os.path.join(WORK, 'names_extra.txt')
    have = set(tr)
    for line in open(extra_path, encoding='utf-8'):
        if '\t' in line:
            have.add(line.split('\t', 1)[0])
    manual = {}
    for line in open(os.path.join(HERE, 'ic2_names.tsv'), encoding='utf-8'):
        if '\t' in line:
            a, b = line.rstrip('\n').split('\t', 1)
            manual[a] = b
    gen = {'railways': rail, 'createdeco': deco, 'mekanism_extras': mek}
    out, left = {}, []
    for ns in ('railways', 'createdeco', 'mekanism_extras', 'ic2', 'ic2r_addons'):
        for en in sorted(set(keys.get(ns, {}).values())):
            if en in have or en in out:
                continue
            uk = manual.get(en) or (gen[ns](en) if ns in gen else None)
            if uk:
                out[en] = uk
            else:
                left.append(f'{ns}\t{en}')
    with open(extra_path, 'a', encoding='utf-8') as f:
        for en, uk in out.items():
            f.write(f'{en}\t{uk}\n')
    print('додано:', len(out), '| без перекладу:', len(left))
    for x in left:
        print('  ', x)


if __name__ == '__main__':
    main()
