"""Генерує 16x16 текстури п'єдестала синтезу (ванільний піксель-арт).
Запуск: python tools/gen_pedestal_textures.py"""
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

side().save(OUT + 'pedestal_side.png')
top().save(OUT + 'pedestal_top.png')
bottom().save(OUT + 'pedestal_bottom.png')
print('ok')
