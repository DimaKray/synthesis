"""Збирає назви (предмети, блоки, моби, ефекти…) з модів збірки, для яких немає українського перекладу.
Результат: tools/ua_pack/work/todo.json (унікальні англійські рядки) і work/keys.json (де кожен рядок ужито).
Запуск: python tools/ua_pack/extract.py"""
import glob
import json
import os
import re
import zipfile

MODS = r"C:/Users/dimak/curseforge/minecraft/Instances/Capitalism/mods"
HERE = os.path.dirname(os.path.abspath(__file__))
WORK = os.path.join(HERE, 'work')
os.makedirs(WORK, exist_ok=True)

NAME_KEY = re.compile(r'^(item|block|entity|effect|enchantment|fluid|fluid_type|itemGroup|biome|container|key)\.')
SKIP_NS = {'synthesis'}  # наш мод уже перекладений


def is_name(key, value):
    if not NAME_KEY.match(key) or key.count('.') > 3:
        return False
    low = key.lower()
    if any(w in low for w in ('desc', 'tooltip', 'info', 'lore', 'hint', 'jei', 'guide', 'flavor')):
        return False
    return isinstance(value, str) and re.search(r'[A-Za-z]', value) is not None and len(value) <= 80


existing_uk = {}   # ns -> {key: uk}
todo_keys = {}     # ns -> {key: en}
for jar in sorted(glob.glob(MODS + '/*.jar')):
    try:
        z = zipfile.ZipFile(jar)
    except Exception:
        continue
    per = {}
    for n in z.namelist():
        m = re.match(r'assets/([^/]+)/lang/(en_us|uk_ua)\.json$', n)
        if m and m.group(1) not in SKIP_NS:
            try:
                per.setdefault(m.group(1), {})[m.group(2)] = json.loads(z.read(n).decode('utf-8-sig'))
            except Exception:
                pass
    for ns, langs in per.items():
        en, uk = langs.get('en_us', {}), langs.get('uk_ua', {})
        for k, v in en.items():
            if not is_name(k, v):
                continue
            if k in uk and isinstance(uk[k], str) and uk[k].strip():
                existing_uk.setdefault(ns, {})[k] = uk[k]
            else:
                todo_keys.setdefault(ns, {})[k] = v

unique = sorted({v for d in todo_keys.values() for v in d.values()})
json.dump(unique, open(os.path.join(WORK, 'todo.json'), 'w', encoding='utf-8'), ensure_ascii=False, indent=0)
json.dump(todo_keys, open(os.path.join(WORK, 'keys.json'), 'w', encoding='utf-8'), ensure_ascii=False)
json.dump(existing_uk, open(os.path.join(WORK, 'existing_uk.json'), 'w', encoding='utf-8'), ensure_ascii=False)
print('ключів без перекладу:', sum(len(d) for d in todo_keys.values()), '| унікальних рядків:', len(unique),
      '| вже є українською:', sum(len(d) for d in existing_uk.values()))
