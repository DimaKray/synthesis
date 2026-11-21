"""Моделі блоків на ОРИГІНАЛЬНИХ текстурах (ванілла / Ботанія / Create) + наші прозорі оверлеї з деталями.
Чужі текстури не копіюються — моделі лише посилаються на них за назвою ресурсу.
Запуск: python tools/gen_models.py"""
import json
import os
from PIL import Image

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
A = os.path.join(ROOT, 'src/main/resources/assets/synthesis')
TEX = os.path.join(A, 'textures/block/overlay')
os.makedirs(TEX, exist_ok=True)
FACES = ['down', 'up', 'north', 'south', 'west', 'east']


def write(path, obj):
    full = os.path.join(A, path)
    os.makedirs(os.path.dirname(full), exist_ok=True)
    with open(full, 'w', encoding='utf-8', newline='\n') as f:
        f.write(json.dumps(obj, ensure_ascii=False, indent=2) + '\n')


def box(frm, to, faces, cull=True, rotation=None):
    el = {"from": frm, "to": to, "faces": {}}
    for face, tex in faces.items():
        f = {"texture": tex}
        if cull and ((face == 'down' and frm[1] == 0) or (face == 'up' and to[1] == 16)
                     or (face == 'north' and frm[2] == 0) or (face == 'south' and to[2] == 16)
                     or (face == 'west' and frm[0] == 0) or (face == 'east' and to[0] == 16)):
            f["cullface"] = face
        el["faces"][face] = f
    if rotation:
        el["rotation"] = rotation
    return el


def overlay(tex, faces=FACES, grow=0.01):
    """Прозорий шар поверх куба (трохи більший, щоб не мерехтів)."""
    return box([-grow, -grow, -grow], [16 + grow, 16 + grow, 16 + grow], {f: tex for f in faces}, cull=False)


def model(name, textures, elements, cutout=False, parent="minecraft:block/block"):
    m = {"parent": parent, "textures": textures, "elements": elements}
    if cutout:
        m["render_type"] = "minecraft:cutout"
    write(f'models/block/{name}.json', m)


def item_from_block(name):
    write(f'models/item/{name}.json', {"parent": f"synthesis:block/{name}"})


def cube_faces(down, up, north, south, west, east):
    return {"down": down, "up": up, "north": north, "south": south, "west": west, "east": east}


# ---------------- наші прозорі оверлеї ----------------
def png(name, pixels):
    im = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    for (x, y), c in pixels.items():
        im.putpixel((x, y), c)
    im.save(os.path.join(TEX, name + '.png'))


def frame_pixels(color, inset=1, corners_only=False):
    px = {}
    lo, hi = inset, 15 - inset
    for i in range(lo, hi + 1):
        for (x, y) in [(i, lo), (i, hi), (lo, i), (hi, i)]:
            if corners_only and lo + 3 < i < hi - 3:
                continue
            px[(x, y)] = color
    return px


TIER4 = {'basic': (150, 150, 165, 255), 'wyvern': (150, 80, 230, 255), 'awakened': (255, 150, 40, 255), 'chaotic': (230, 40, 40, 255)}
for t, c in TIER4.items():
    px = frame_pixels(c, 0, corners_only=True)
    px.update({(7, 7): c, (8, 8): c, (7, 8): c, (8, 7): c})
    png(f'stabilizer_{t}', px)

ACCEL = {'x2': (90, 220, 90, 255), 'x4': (79, 195, 247, 255), 'x8': (180, 100, 250, 255), 'x16': (250, 60, 60, 255)}
for t, c in ACCEL.items():
    png(f'accelerator_{t}', frame_pixels(c, 1))

SOLAR = {'hybrid': (170, 170, 185, 255), 'ultimate': (230, 180, 60, 255), 'quantum': (79, 195, 247, 255), 'infinite': (255, 255, 255, 255)}
for t, c in SOLAR.items():
    png(f'solar_{t}', frame_pixels(c, 0))

window = {}
for y in range(5, 11):
    for x in range(5, 11):
        window[(x, y)] = (40, 25, 60, 255) if x in (5, 10) or y in (5, 10) else ((155, 107, 224, 255) if (x + y) % 3 else (79, 195, 247, 255))
for p in [(7, 7), (8, 7), (7, 8), (8, 8)]:
    window[p] = (246, 210, 122, 255)
png('synthesizer_window', window)

pick = {}
for (x, y) in [(4, 5), (5, 4), (6, 4), (7, 4), (8, 4), (9, 4), (10, 4), (11, 5)]:
    pick[(x, y)] = (60, 110, 230, 255)
for i in range(5, 12):
    pick[(7, i)] = (140, 100, 60, 255)
    pick[(8, i)] = (100, 70, 40, 255)
for (x, y) in [(4, 10), (11, 9), (10, 12), (5, 12)]:
    pick[(x, y)] = (70, 230, 130, 255)
png('converter_pickaxe', pick)

core = {}
for y in range(5, 11):
    for x in range(5, 11):
        core[(x, y)] = (255, 140, 40, 255) if (x + y) % 2 else (255, 200, 80, 255)
core.update({(7, 7): (255, 255, 220, 255), (8, 8): (255, 255, 220, 255)})
core.update(frame_pixels((120, 40, 160, 255), 4))
png('draconic_core', core)

# ---------------- моделі ----------------
V = 'minecraft:block/'

# П'єдестал: глибинний сланець + золота плита
model('pedestal', {"particle": V + "polished_deepslate"}, [
    box([1, 0, 1], [15, 3, 15], {f: V + "polished_deepslate" for f in FACES}),
    box([4, 3, 4], [12, 11, 12], {f: V + "chiseled_deepslate" for f in ['north', 'south', 'west', 'east']}),
    box([2, 11, 2], [14, 14, 14], {"down": V + "polished_deepslate", "up": V + "deepslate_tiles",
                                   "north": V + "gold_block", "south": V + "gold_block", "west": V + "gold_block", "east": V + "gold_block"}),
])
item_from_block('pedestal')

# Вівтар Злиття: сланець + золото + верх столу зачарувань
model('fusion_altar', {"particle": V + "polished_deepslate"}, [
    box([0, 0, 0], [16, 4, 16], {"down": V + "polished_deepslate", "up": V + "deepslate_tiles",
                                 **{f: V + "polished_deepslate" for f in ['north', 'south', 'west', 'east']}}),
    box([2, 4, 2], [14, 10, 14], {f: V + "chiseled_deepslate" for f in ['north', 'south', 'west', 'east']}),
    box([1, 10, 1], [15, 13, 15], {"down": V + "polished_deepslate", "up": V + "enchanting_table_top",
                                   **{f: V + "gold_block" for f in ['north', 'south', 'west', 'east']}}),
])
item_from_block('fusion_altar')

# Вівтар Капіталізму: позолочений чорний камінь + золото + маяк
model('capital_altar', {"particle": V + "gold_block"}, [
    box([0, 0, 0], [16, 4, 16], {"down": V + "polished_blackstone", "up": V + "polished_blackstone",
                                 **{f: V + "gilded_blackstone" for f in ['north', 'south', 'west', 'east']}}),
    box([2, 4, 2], [14, 10, 14], {f: V + "chiseled_polished_blackstone" for f in ['north', 'south', 'west', 'east']}),
    box([1, 10, 1], [15, 13, 15], {"down": V + "polished_blackstone", "up": V + "beacon",
                                   **{f: V + "gold_block" for f in ['north', 'south', 'west', 'east']}}),
])
item_from_block('capital_altar')

# Стабілізатори душ: ванільний спавнер + кольорові кути рівня
for t in TIER4:
    n = f'soul_stabilizer_{t}'
    model(n, {"particle": V + "spawner"}, [
        box([0, 0, 0], [16, 16, 16], {f: V + "spawner" for f in FACES}, cull=False),
        overlay(f"synthesis:block/overlay/stabilizer_{t}"),
    ], cutout=True)
    item_from_block(n)


def orientable(name, top, bottom, side, front, front_overlay=None):
    els = [box([0, 0, 0], [16, 16, 16], cube_faces(bottom, top, front, side, side, side))]
    if front_overlay:
        els.append(box([0, 0, -0.01], [16, 16, 0], {"north": front_overlay}, cull=False))
    model(name, {"particle": side}, els, cutout=bool(front_overlay))
    item_from_block(name)


orientable('synthesizer', V + "smithing_table_top", V + "smithing_table_bottom", V + "smithing_table_side",
           V + "smithing_table_front", "synthesis:block/overlay/synthesizer_window")
orientable('fortune_converter', V + "lodestone_top", V + "lodestone_top", V + "lodestone_side",
           V + "lodestone_side", "synthesis:block/overlay/converter_pickaxe")
orientable('industrial_processor', V + "blast_furnace_top", V + "blast_furnace_top", V + "blast_furnace_side",
           V + "blast_furnace_front_on")

# Квантовий збирач: ванільний крафтер (автокрафт)
model('quantum_assembler', {"particle": V + "crafter_north"}, [
    box([0, 0, 0], [16, 16, 16], cube_faces(V + "crafter_bottom", V + "crafter_top", V + "crafter_north",
                                            V + "crafter_south", V + "crafter_west", V + "crafter_east")),
])
item_from_block('quantum_assembler')

# Прискорювачі: золотий блок + годинник + рамка рівня
for t in ACCEL:
    n = f'accelerator_{t}'
    model(n, {"particle": V + "gold_block"}, [
        box([0, 0, 0], [16, 16, 16], {f: V + "gold_block" for f in FACES}),
        overlay("minecraft:item/clock_00", grow=0.01),
        overlay(f"synthesis:block/overlay/accelerator_{t}", grow=0.02),
    ], cutout=True)
    item_from_block(n)

# Капіталі-панелі: датчик денного світла + рамка рівня зверху
for t in SOLAR:
    n = f'solar_{t}'
    top = V + ("daylight_detector_inverted_top" if t == 'infinite' else "daylight_detector_top")
    side_faces = {}
    els = [{"from": [0, 0, 0], "to": [16, 6, 16], "faces": {
        "down": {"uv": [0, 0, 16, 16], "texture": V + "daylight_detector_side", "cullface": "down"},
        "up": {"uv": [0, 0, 16, 16], "texture": top},
        **{f: {"uv": [0, 10, 16, 16], "texture": V + "daylight_detector_side", "cullface": f} for f in ['north', 'south', 'west', 'east']}}},
        {"from": [0, 6.01, 0], "to": [16, 6.01, 16], "faces": {"up": {"texture": f"synthesis:block/overlay/solar_{t}"}}}]
    model(n, {"particle": top}, els, cutout=True)
    item_from_block(n)

# Кінетичний привід (лише з Create): андезитовий корпус, торці — коробка передач
for_drive = {"particle": "create:block/andesite_casing"}
model('kinetic_drive', for_drive, [
    box([0, 0, 0], [16, 16, 16], cube_faces("create:block/gearbox_top", "create:block/gearbox_top",
                                            "create:block/andesite_casing", "create:block/andesite_casing",
                                            "create:block/andesite_casing", "create:block/andesite_casing")),
])
item_from_block('kinetic_drive')

# Великі басейни мани (лише з Ботанією): форма й текстури басейну Ботанії, колір рівня — tint у коді
for t in (1, 2, 3):
    n = f'great_mana_pool_{t}'
    write(f'models/block/{n}.json', {"parent": "botania:block/shapes/mana_pool", "textures": {
        "bottom": "botania:block/mana_pool_bottom", "inside": "botania:block/mana_pool_inside",
        "side": "botania:block/mana_pool_side", "top": "botania:block/mana_pool_top"}})
    item_from_block(n)

# Драконієвий розповсюджувач: дошки мрійного дерева + драконієве ядро
model('draconic_spreader', {"particle": "botania:block/dreamwood_planks"}, [
    box([0, 0, 0], [16, 16, 16], {f: "botania:block/dreamwood_planks" for f in FACES}),
    overlay("synthesis:block/overlay/draconic_core"),
], cutout=True)
item_from_block('draconic_spreader')

# Мультитеплиця: основа з живого каменю, квіти всередині, скляна оболонка
plant = lambda tex, angle: {"from": [3.5, 3, 8], "to": [12.5, 12, 8], "rotation": {"origin": [8, 8, 8], "axis": "y", "angle": angle},
                            "shade": False, "faces": {"north": {"texture": tex}, "south": {"texture": tex}}}
model('multi_greenhouse', {"particle": V + "glass"}, [
    box([0, 0, 0], [16, 3, 16], {f: "botania:block/livingrock" for f in FACES}),
    plant(V + "poppy", 45), plant(V + "blue_orchid", -45),
    box([0, 0, 0], [16, 16, 16], {f: V + "glass" for f in FACES}),
], cutout=True)
item_from_block('multi_greenhouse')

# Предмети на ванільних текстурах (колір рівня — tint у коді)
for n in ['unity_alloy_1', 'unity_alloy_2', 'unity_alloy_3']:
    write(f'models/item/{n}.json', {"parent": "minecraft:item/generated", "textures": {"layer0": "minecraft:item/iron_ingot"}})
write('models/item/synthesis_core.json', {"parent": "minecraft:item/generated", "textures": {"layer0": "minecraft:item/nether_star"}})
print('models ok')
