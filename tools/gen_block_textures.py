"""Генерує 16x16 текстури блоків (п'єдестал, Вівтар Злиття) у ванільному піксель-арт стилі.
Запуск: python tools/gen_block_textures.py"""
import random
from PIL import Image

OUT = 'src/main/resources/assets/synthesis/textures/block/'
D, M, L, H = (46, 46, 54), (66, 66, 78), (88, 88, 102), (112, 112, 128)   # камінь
GD, G, GL = (156, 107, 30), (217, 164, 65), (246, 210, 122)                 # золото
TEAL, PURP = (79, 195, 247), (155, 107, 224)                               # сяйво

def stone(rng):
    return rng.choice([M, M, M, L, D])

def side():
    rng = random.Random(1)
    im = Image.new('RGBA', (16, 16))
    for y in range(16):
        for x in range(16):
            c = stone(rng)
            if y in (2,):            c = GL if x % 3 else G   # верх плити — золота кромка
            elif y == 3:             c = rng.choice([L, H, L])
            elif y == 4:             c = GD if x % 2 else G    # низ плити
            elif 5 <= y <= 12:       # колона
                if x in (7, 8):      c = TEAL if y % 3 else PURP  # світний жолоб
                elif x in (6, 9):    c = D
                elif x in (4, 11):   c = L
            elif y == 13:            c = G if x % 4 else GD    # кромка основи
            elif y >= 14:            c = rng.choice([D, M, D])
            im.putpixel((x, y), c)
    return im

def top():
    rng = random.Random(2)
    im = Image.new('RGBA', (16, 16))
    for y in range(16):
        for x in range(16):
            c = rng.choice([L, L, M, H])
            if x in (2, 13) or y in (2, 13):
                if 2 <= x <= 13 and 2 <= y <= 13: c = G
            im.putpixel((x, y), c)
    # руна в центрі: ромб
    for (x, y) in [(7,4),(8,4),(5,6),(10,6),(4,7),(11,7),(4,8),(11,8),(5,9),(10,9),(7,11),(8,11),(6,5),(9,5),(6,10),(9,10)]:
        im.putpixel((x, y), PURP)
    for (x, y) in [(7,7),(8,7),(7,8),(8,8)]:
        im.putpixel((x, y), TEAL)
    for (x, y) in [(2,2),(13,2),(2,13),(13,13)]:
        im.putpixel((x, y), GL)
    return im

def bottom():
    rng = random.Random(3)
    im = Image.new('RGBA', (16, 16))
    for y in range(16):
        for x in range(16):
            im.putpixel((x, y), rng.choice([D, M, D]))
    return im

def altar_side():
    # рядки текстури = висота блока згори вниз: плита y10-13 -> рядки 3-5, тіло 4-10 -> 6-11, основа 0-4 -> 12-15
    rng = random.Random(4)
    im = Image.new('RGBA', (16, 16))
    for y in range(16):
        for x in range(16):
            c = stone(rng)
            if y == 3:               c = GL if x % 3 else G
            elif y == 4:             c = rng.choice([L, H])
            elif y == 5:             c = GD if x % 2 else G
            elif 6 <= y <= 11:       # тіло з трьома світними рунами
                c = rng.choice([D, M, M])
                if x in (3, 4) and 7 <= y <= 10:   c = TEAL if (x + y) % 2 else PURP
                if x in (11, 12) and 7 <= y <= 10: c = TEAL if (x + y) % 2 else PURP
                if x in (7, 8) and 7 <= y <= 10:   c = PURP if y in (7, 10) else TEAL
                if x in (2, 13):                    c = L
            elif y == 12:            c = G if x % 4 else GD
            elif y >= 13:            c = rng.choice([D, M, D])
            im.putpixel((x, y), c)
    return im

def altar_top():
    rng = random.Random(5)
    im = Image.new('RGBA', (16, 16))
    for y in range(16):
        for x in range(16):
            im.putpixel((x, y), rng.choice([L, L, M, H]))
    for i in range(1, 15):   # золота рамка
        for (x, y) in [(i, 1), (i, 14), (1, i), (14, i)]:
            im.putpixel((x, y), G)
    # восьмикутне коло рун
    ring = [(6,3),(7,3),(8,3),(9,3),(4,4),(5,4),(10,4),(11,4),(3,5),(12,5),(3,6),(12,6),(3,7),(12,7),
            (3,8),(12,8),(3,9),(12,9),(3,10),(12,10),(4,11),(5,11),(10,11),(11,11),(6,12),(7,12),(8,12),(9,12)]
    for (x, y) in ring: im.putpixel((x, y), PURP)
    for (x, y) in [(7,6),(8,6),(6,7),(9,7),(6,8),(9,8),(7,9),(8,9)]: im.putpixel((x, y), TEAL)
    for (x, y) in [(7,7),(8,7),(7,8),(8,8)]: im.putpixel((x, y), GL)
    for (x, y) in [(1,1),(14,1),(1,14),(14,14)]: im.putpixel((x, y), GL)
    return im

side().save(OUT + 'pedestal_side.png')
altar_side().save(OUT + 'fusion_altar_side.png')
altar_top().save(OUT + 'fusion_altar_top.png')
top().save(OUT + 'pedestal_top.png')
bottom().save(OUT + 'pedestal_bottom.png')
print('ok')
