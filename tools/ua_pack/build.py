"""Збирає ресурспак «Capitalism UA» з українськими назвами для всіх модів збірки.
Вхід: work/todo.json, work/keys.json, work/existing_uk.json (робить extract.py) і work/out_*.txt (переклад «індекс\\tтекст»).
Вихід: <збірка>/resourcepacks/Capitalism_UA/ з ru_ru.json (гра Дмитра в ru_ru) та uk_ua.json.
Запуск: python tools/ua_pack/build.py"""
import glob
import json
import os
import re
import shutil

PACK = r"C:/Users/dimak/curseforge/minecraft/Instances/Capitalism/resourcepacks/Capitalism_UA"
HERE = os.path.dirname(os.path.abspath(__file__))
WORK = os.path.join(HERE, 'work')

todo = json.load(open(os.path.join(WORK, 'todo.json'), encoding='utf-8'))
keys = json.load(open(os.path.join(WORK, 'keys.json'), encoding='utf-8'))
existing = json.load(open(os.path.join(WORK, 'existing_uk.json'), encoding='utf-8'))

PH = re.compile(r'%(\d+\$)?[sd%]|§.')
tr = {}
problems = 0
for f in sorted(glob.glob(os.path.join(WORK, 'out_*.txt'))):
    for line in open(f, encoding='utf-8'):
        line = line.rstrip('\r\n')
        if not line:
            continue
        idx, text = line.split('\t', 1)
        en = todo[int(idx)]
        if sorted(PH.findall(en)) != sorted(PH.findall(text)):
            print('плейсхолдери не збігаються:', idx, repr(en), '->', repr(text))
            problems += 1
        tr[en] = text
missing = [e for e in todo if e not in tr]
print('перекладено:', len(tr), '| бракує:', len(missing), '| проблем:', problems)

if os.path.isdir(PACK):
    shutil.rmtree(PACK)
os.makedirs(PACK)
json.dump({'pack': {'pack_format': 34, 'supported_formats': [34, 48],
                    'description': '§6Capitalism UA§r — українські назви для всіх модів'}},
          open(os.path.join(PACK, 'pack.mcmeta'), 'w', encoding='utf-8'), ensure_ascii=False, indent=2)

total = 0
for ns in sorted(set(keys) | set(existing)):
    new = {k: tr[v] for k, v in keys.get(ns, {}).items() if v in tr}
    ru = dict(existing.get(ns, {}))
    ru.update(new)
    if not ru:
        continue
    d = os.path.join(PACK, 'assets', ns, 'lang')
    os.makedirs(d, exist_ok=True)
    json.dump(dict(sorted(ru.items())), open(os.path.join(d, 'ru_ru.json'), 'w', encoding='utf-8'),
              ensure_ascii=False, indent=1)
    if new:
        json.dump(dict(sorted(new.items())), open(os.path.join(d, 'uk_ua.json'), 'w', encoding='utf-8'),
                  ensure_ascii=False, indent=1)
    total += len(ru)
print('модів (просторів імен):', len(os.listdir(os.path.join(PACK, 'assets'))), '| ключів у ru_ru:', total)
