"""Генерує текстури Синтезатора, Кінетичного приводу, Ядра синтезу та інтерфейсу синтезатора.
Запуск: python tools/gen_machine_textures.py"""
import random
from PIL import Image, ImageDraw

RES = 'src/main/resources/assets/synthesis/textures/'
D, M, L, H = (46, 46, 54), (66, 66, 78), (88, 88, 102), (112, 112, 128)
GD, G, GL = (156, 107, 30), (217, 164, 65), (246, 210, 122)
TEAL, PURP = (79, 195, 247), (155, 107, 224)
IRON_D, IRON, IRON_L = (96, 100, 108), (140, 146, 156), (186, 192, 200)
AND_D, AND, AND_L = (110, 104, 92), (150, 142, 126), (182, 174, 156)   # «андезит»


def frame(im, c_out, c_in):
    for i in range(16):
        for (x, y) in [(i, 0), (i, 15), (0, i), (15, i)]:
            im.putpixel((x, y), c_out)
    for i in range(1, 15):
        for (x, y) in [(i, 1), (1, i)]:
            im.putpixel((x, y), c_in)


def machine_side():
    rng = random.Random(10)
    im = Image.new('RGBA', (16, 16))
    for y in range(16):
        for x in range(16):
            im.putpixel((x, y), rng.choice([IRON, IRON, IRON_L, IRON_D]))
    frame(im, IRON_D, IRON_L)
    for x in range(3, 13):          # золота смуга
        im.putpixel((x, 7), G)
        im.putpixel((x, 8), GD)
    for (x, y) in [(3, 3), (12, 3), (3, 12), (12, 12)]:  # заклепки
        im.putpixel((x, y), IRON_L)
    return im


def machine_front():
    im = machine_side()
    # віконце з фіолетово-бірюзовим сяйвом
    for y in range(4, 12):
        for x in range(4, 12):
            im.putpixel((x, y), D)
    for y in range(5, 11):
        for x in range(5, 11):
            im.putpixel((x, y), PURP if (x + y) % 3 else TEAL)
    for (x, y) in [(7, 7), (8, 7), (7, 8), (8, 8)]:
        im.putpixel((x, y), GL)
    return im


def machine_top():
    rng = random.Random(11)
    im = Image.new('RGBA', (16, 16))
    for y in range(16):
        for x in range(16):
            im.putpixel((x, y), rng.choice([IRON, IRON_L, IRON]))
    frame(im, IRON_D, IRON_L)
    for i in range(4, 12):
        im.putpixel((i, 4), G); im.putpixel((i, 11), G)
        im.putpixel((4, i), G); im.putpixel((11, i), G)
    for (x, y) in [(7, 7), (8, 7), (7, 8), (8, 8)]:
        im.putpixel((x, y), TEAL)
    return im


def drive_side():
    rng = random.Random(12)
    im = Image.new('RGBA', (16, 16))
    for y in range(16):
        for x in range(16):
            im.putpixel((x, y), rng.choice([AND, AND, AND_L, AND_D]))
    frame(im, AND_D, AND_L)
    for y in range(3, 13):           # руна-стрілка: обертання передається в машину
        im.putpixel((7, y), PURP); im.putpixel((8, y), PURP)
    for (x, y) in [(5, 5), (6, 4), (9, 4), (10, 5), (5, 10), (6, 11), (9, 11), (10, 10)]:
        im.putpixel((x, y), TEAL)
    return im


def drive_end():
    im = drive_side()
    for y in range(5, 11):           # отвір під вал
        for x in range(5, 11):
            im.putpixel((x, y), D)
    for y in range(6, 10):
        for x in range(6, 10):
            im.putpixel((x, y), (120, 90, 60) if (x + y) % 2 else (150, 112, 74))
    return im


def core_item():
    im = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    pts = {}
    for y in range(16):
        for x in range(16):
            dx, dy = x - 7.5, y - 7.5
            d = abs(dx) + abs(dy)
            if d <= 7:
                c = G if d > 5.5 else (PURP if d > 3 else (TEAL if d > 1.2 else GL))
                if d > 6.5:
                    c = GD
                pts[(x, y)] = c
    for (x, y), c in pts.items():
        im.putpixel((x, y), c)
    for (x, y) in [(6, 5), (5, 6)]:
        im.putpixel((x, y), (255, 255, 255))
    return im


# --- інтерфейс 176×166 (у файлі 256×256) ---
BG, WHITE, DARK, BLACK, SLOT = (198, 198, 198), (255, 255, 255), (85, 85, 85), (0, 0, 0), (139, 139, 139)


def panel(dr):
    dr.rectangle([0, 0, 175, 165], fill=BG)
    dr.line([(2, 0), (173, 0)], fill=BLACK); dr.line([(2, 165), (173, 165)], fill=BLACK)
    dr.line([(0, 2), (0, 163)], fill=BLACK); dr.line([(175, 2), (175, 163)], fill=BLACK)
    for (x, y) in [(1, 1), (174, 1), (1, 164), (174, 164)]:
        dr.point((x, y), fill=BLACK)
    dr.line([(1, 2), (1, 163)], fill=WHITE); dr.line([(2, 1), (173, 1)], fill=WHITE)
    dr.line([(2, 2), (2, 162)], fill=WHITE); dr.line([(2, 2), (172, 2)], fill=WHITE)
    dr.line([(174, 2), (174, 163)], fill=DARK); dr.line([(2, 164), (173, 164)], fill=DARK)
    dr.line([(173, 3), (173, 163)], fill=DARK); dr.line([(3, 163), (173, 163)], fill=DARK)


def sunken(dr, x, y, w, h, fill=SLOT):
    """Заглиблена рамка: (x, y) — зовнішній кут, w×h — зовнішній розмір."""
    dr.rectangle([x, y, x + w - 1, y + h - 1], fill=fill)
    dr.line([(x, y), (x + w - 2, y)], fill=(55, 55, 55))
    dr.line([(x, y), (x, y + h - 2)], fill=(55, 55, 55))
    dr.line([(x + 1, y + h - 1), (x + w - 1, y + h - 1)], fill=WHITE)
    dr.line([(x + w - 1, y + 1), (x + w - 1, y + h - 1)], fill=WHITE)


def slot(dr, ix, iy):
    sunken(dr, ix - 1, iy - 1, 18, 18)


def arrow(dr, x, y, color):
    # тіло 0..15 × 6..10, голова 15..22
    dr.rectangle([x, y + 6, x + 15, y + 10], fill=color)
    for i in range(8):
        dr.line([(x + 15 + i, y + 1 + i), (x + 15 + i, y + 15 - i)], fill=color)


def gui():
    im = Image.new('RGBA', (256, 256), (0, 0, 0, 0))
    dr = ImageDraw.Draw(im)
    panel(dr)
    sunken(dr, 9, 17, 14, 54, fill=(40, 20, 20))      # шкала FE (внутрішня 10..22 × 18..70)
    sunken(dr, 27, 17, 18, 54, fill=(30, 30, 40))     # бак (внутрішній 28..44 × 18..70)
    for (ix, iy) in [(56, 17), (56, 35), (56, 53)]:
        slot(dr, ix, iy)
    sunken(dr, 115, 30, 26, 26)                       # великий вихідний слот
    arrow(dr, 82, 35, (139, 139, 139))                # порожня стрілка
    arrow(dr, 176, 0, (255, 255, 255))                # заповнена — для прогресу
    for row in range(3):
        for col in range(9):
            slot(dr, 8 + col * 18, 84 + row * 18)
    for col in range(9):
        slot(dr, 8 + col * 18, 142)
    # поділки на баку
    for yy in range(26, 70, 9):
        dr.line([(39, yy), (43, yy)], fill=(200, 200, 220, 160))
    return im


if __name__ == '__main__':
    machine_side().save(RES + 'block/synthesizer_side.png')
    machine_front().save(RES + 'block/synthesizer_front.png')
    machine_top().save(RES + 'block/synthesizer_top.png')
    drive_side().save(RES + 'block/kinetic_drive_side.png')
    drive_end().save(RES + 'block/kinetic_drive_end.png')
    core_item().save(RES + 'item/synthesis_core.png')
    import os
    os.makedirs(RES + 'gui', exist_ok=True)
    gui().save(RES + 'gui/synthesizer.png')
    print('ok')


# --- Стабілізатори душ: кліть (прозорий центр), колір рамки за рівнем ---
TIER_COLORS = {
    'basic': ((70, 70, 80), (120, 120, 135), (170, 170, 185)),
    'wyvern': ((60, 30, 95), (120, 70, 190), (175, 130, 240)),
    'awakened': ((120, 55, 10), (230, 130, 30), (255, 200, 110)),
    'chaotic': ((70, 10, 10), (170, 25, 25), (240, 90, 70)),
}


def stabilizer(tier):
    dark, mid, light = TIER_COLORS[tier]
    im = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    for i in range(16):
        for (x, y) in [(i, 0), (i, 15), (0, i), (15, i)]:
            im.putpixel((x, y), dark)
        for (x, y) in [(i, 1), (1, i)]:
            if 1 <= i <= 14:
                im.putpixel((x, y), light)
        for (x, y) in [(i, 14), (14, i)]:
            if 1 <= i <= 14:
                im.putpixel((x, y), mid)
    for i in range(2, 14):   # ґрати: хрест посередині
        im.putpixel((7, i), mid)
        im.putpixel((8, i), dark)
        im.putpixel((i, 7), mid)
        im.putpixel((i, 8), dark)
    for (x, y) in [(7, 7), (8, 7), (7, 8), (8, 8)]:
        im.putpixel((x, y), (79, 195, 247))   # бірюзове «око» душі
    return im


if __name__ == '__main__':
    for t in TIER_COLORS:
        stabilizer(t).save(RES + 'block/soul_stabilizer_' + t + '.png')
    print('stabilizers ok')


# --- Капітал-бокси: подарункова коробка, колір за рідкістю ---
BOX_COLORS = {
    'common': ((150, 150, 150), (205, 205, 205), (245, 245, 245)),
    'rare': ((30, 60, 150), (60, 110, 220), (130, 175, 255)),
    'epic': ((80, 30, 130), (140, 70, 210), (200, 140, 255)),
    'legendary': ((150, 95, 10), (225, 160, 30), (255, 220, 110)),
}


def capital_box(rarity):
    dark, mid, light = BOX_COLORS[rarity]
    ribbon_d, ribbon = ((150, 25, 35), (220, 50, 60)) if rarity != 'legendary' else ((120, 40, 160), (170, 90, 230))
    im = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    for y in range(5, 15):            # корпус
        for x in range(2, 14):
            c = mid
            if x == 2 or y == 14: c = dark
            if x == 13: c = dark
            if y == 5: c = light
            im.putpixel((x, y), c)
    for y in range(3, 6):             # кришка
        for x in range(1, 15):
            im.putpixel((x, y), light if y == 3 else mid)
        im.putpixel((1, y), dark); im.putpixel((14, y), dark)
    for y in range(3, 15):            # стрічка вертикальна
        im.putpixel((7, y), ribbon); im.putpixel((8, y), ribbon_d)
    for x in range(1, 15):            # стрічка на кришці
        im.putpixel((x, 4), ribbon if x not in (7, 8) else ribbon_d)
    for (x, y) in [(5, 1), (6, 2), (5, 2), (10, 1), (9, 2), (10, 2), (7, 2), (8, 2)]:  # бантик
        im.putpixel((x, y), ribbon)
    for (x, y) in [(4, 7), (11, 9), (5, 12)]:   # блиск
        im.putpixel((x, y), (255, 255, 255))
    return im


if __name__ == '__main__':
    for r in BOX_COLORS:
        capital_box(r).save(RES + 'item/capital_box_' + r + '.png')
    print('boxes ok')


# --- Перетворювач удачі ---
def converter_front():
    im = machine_side()
    for y in range(3, 13):            # «кирка» з лазуриту на передній панелі
        for x in range(3, 13):
            im.putpixel((x, y), D)
    pick = [(4, 5), (5, 4), (6, 4), (7, 4), (8, 4), (9, 4), (10, 4), (11, 5)]
    for p in pick:
        im.putpixel(p, (40, 80, 200))
    for i in range(5, 12):
        im.putpixel((7, i), (140, 100, 60))
        im.putpixel((8, i), (110, 75, 45))
    for (x, y) in [(5, 10), (10, 9), (11, 11), (4, 8)]:
        im.putpixel((x, y), (60, 220, 120))   # «самоцвіти»
    return im


def converter_top():
    im = machine_top()
    for y in range(5, 11):            # воронка для руди
        for x in range(5, 11):
            im.putpixel((x, y), D if 6 <= x <= 9 and 6 <= y <= 9 else IRON_D)
    return im


def converter_gui():
    im = Image.new('RGBA', (256, 256), (0, 0, 0, 0))
    dr = ImageDraw.Draw(im)
    panel(dr)
    sunken(dr, 9, 17, 14, 54, fill=(40, 20, 20))      # FE
    slot(dr, 34, 26)                                  # руда
    arrow(dr, 56, 26, (139, 139, 139))
    arrow(dr, 176, 0, (255, 255, 255))
    for (ix, iy) in [(92, 17), (110, 17), (92, 35), (110, 35)]:
        slot(dr, ix, iy)
    for row in range(3):
        for col in range(9):
            slot(dr, 8 + col * 18, 84 + row * 18)
    for col in range(9):
        slot(dr, 8 + col * 18, 142)
    return im


MODULE_COLORS = {1: (60, 90, 200), 2: (60, 170, 90), 3: (200, 170, 40), 4: (170, 70, 220), 5: (230, 60, 60)}


def module(color, mark):
    im = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    for y in range(3, 13):            # плата
        for x in range(2, 14):
            c = (40, 110, 60) if (x + y) % 5 else (60, 140, 80)
            if x in (2, 13) or y in (3, 12):
                c = (25, 70, 40)
            im.putpixel((x, y), c)
    for x in (4, 6, 9, 11):           # контакти
        im.putpixel((x, 13), G)
        im.putpixel((x, 14), GD)
    for y in range(5, 11):            # чип
        for x in range(5, 11):
            im.putpixel((x, y), color)
    for (x, y) in mark:
        im.putpixel((x, y), (255, 255, 255))
    return im


ROMAN = {1: [(7, 6), (7, 7), (7, 8), (7, 9)],
         2: [(6, 6), (6, 7), (6, 8), (6, 9), (8, 6), (8, 7), (8, 8), (8, 9)],
         3: [(5, 6), (5, 7), (5, 8), (5, 9), (7, 6), (7, 7), (7, 8), (7, 9), (9, 6), (9, 7), (9, 8), (9, 9)],
         4: [(5, 6), (5, 7), (5, 8), (5, 9), (7, 6), (8, 8), (9, 6), (8, 9), (7, 7)],
         5: [(6, 6), (7, 8), (8, 9), (9, 8), (10, 6), (7, 7), (9, 7)]}


if __name__ == '__main__':
    converter_front().save(RES + 'block/fortune_converter_front.png')
    converter_top().save(RES + 'block/fortune_converter_top.png')
    converter_gui().save(RES + 'gui/fortune_converter.png')
    for lvl, col in MODULE_COLORS.items():
        module(col, ROMAN[lvl]).save(RES + f'item/fortune_module_{lvl}.png')
    module((230, 110, 30), [(7, 6), (8, 7), (7, 8), (8, 9)]).save(RES + 'item/smelting_module.png')
    module((235, 235, 245), [(6, 7), (7, 7), (8, 8), (9, 8)]).save(RES + 'item/silk_touch_module.png')
    print('converter ok')


# --- Прискорювачі: золотий корпус з циферблатом, колір стрілок/рамки за рівнем ---
ACCEL_COLORS = {'x2': (90, 200, 90), 'x4': (79, 195, 247), 'x8': (170, 100, 240), 'x16': (240, 70, 70)}


def accelerator(tier):
    accent = ACCEL_COLORS[tier]
    rng = random.Random(20)
    im = Image.new('RGBA', (16, 16))
    for y in range(16):
        for x in range(16):
            im.putpixel((x, y), rng.choice([G, G, GD, GL]))
    frame(im, GD, GL)
    for i in range(16):                 # кольорова рамка рівня
        for (x, y) in [(i, 2), (i, 13), (2, i), (13, i)]:
            if 2 <= i <= 13:
                im.putpixel((x, y), accent)
    for y in range(4, 12):              # циферблат
        for x in range(4, 12):
            if (x - 7.5) ** 2 + (y - 7.5) ** 2 <= 14:
                im.putpixel((x, y), (235, 230, 210))
    for (x, y) in [(7, 5), (7, 6), (7, 7), (8, 7), (9, 7), (10, 7)]:   # стрілки
        im.putpixel((x, y), (40, 40, 50))
    for (x, y) in [(7, 4), (11, 7), (7, 11), (4, 7)]:                 # поділки
        im.putpixel((x, y), accent)
    return im


if __name__ == '__main__':
    for t in ACCEL_COLORS:
        accelerator(t).save(RES + 'block/accelerator_' + t + '.png')
    print('accelerators ok')
