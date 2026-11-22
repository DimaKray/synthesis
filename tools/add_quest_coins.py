"""Додає нагороду-монети Lightman's Currency до кожного квесту FTB збірки Capitalism (22.11.2026).
Монета й кількість залежать від глави. Повторний запуск нічого не дублює (квести, де вже є монета, пропускає).
Запуск: python tools/add_quest_coins.py"""
import glob
import os
import random
import re

Q = r"C:/Users/dimak/curseforge/minecraft/Instances/Capitalism/config/ftbquests/quests"
PLAN = {  # глава -> (монета, кількість)
    'capitalism_01_start': ('coin_iron', 5),
    'capitalism_02_magic': ('coin_gold', 1),
    'capitalism_03_tech': ('coin_gold', 1),
    'capitalism_04_adventure': ('coin_gold', 2),
    'capitalism_05_draconic': ('coin_emerald', 1),
    'capitalism_06_infinity': ('coin_emerald', 2),
    'capitalism_07_industry': ('coin_gold', 1),
    'capitalism_08_occult': ('coin_gold', 1),
    'capitalism_09_farms_worlds': ('coin_gold', 1),
    'capitalism_10_synthesis': ('coin_emerald', 1),
}

used = set()
for f in glob.glob(Q + '/**/*.snbt', recursive=True):
    used |= set(re.findall(r'\b([0-9A-F]{16})\b', open(f, encoding='utf-8').read()))


def new_id():
    while True:
        i = '%016X' % random.randint(0x1000000000000000, 0x7FFFFFFFFFFFFFFF)
        if i not in used:
            used.add(i)
            return i


total = 0
for name, (coin, count) in PLAN.items():
    p = os.path.join(Q, 'chapters', name + '.snbt')
    lines = open(p, encoding='utf-8').read().split('\n')
    # компактну форму «rewards: [{ … }]» (одна нагорода) розгортаємо у звичайну «rewards: [ { … } ]»
    k = 0
    while k < len(lines):
        m = re.match(r'^(\t+)rewards: \[\{$', lines[k])
        if m:
            ind = m.group(1)
            j = k + 1
            while lines[j] != ind + '}]':
                j += 1
            body = ['\t' + x for x in lines[k + 1:j]]
            lines[k:j + 1] = [ind + 'rewards: [', ind + '\t{'] + body + [ind + '\t}', ind + ']']
        k += 1
    out, i, added = [], 0, 0
    while i < len(lines):
        line = lines[i]
        out.append(line)
        m = re.match(r'^(\t+)rewards: \[$', line)
        if m:
            # кінець блоку rewards — перший рядок з тим самим відступом і «]»
            j = i + 1
            while j < len(lines) and lines[j] != m.group(1) + ']':
                j += 1
            block = '\n'.join(lines[i + 1:j])
            if 'lightmanscurrency:' not in block:
                t = m.group(1) + '\t'
                out += [t + '{', t + f'\tcount: {count}', t + f'\tid: "{new_id()}"', t + '\titem: {',
                        t + '\t\tcount: 1', t + f'\t\tid: "lightmanscurrency:{coin}"', t + '\t}',
                        t + '\ttype: "item"', t + '}']
                added += 1
        i += 1
    open(p, 'w', encoding='utf-8', newline='\r\n').write('\n'.join(out))  # файли збірки — з CRLF
    print(name, '+', added)
    total += added
print('разом нагород:', total)
