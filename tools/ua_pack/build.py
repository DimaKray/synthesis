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

# ---- книжки (extract_books.py → work/books_todo.json, переклад у work/bout_*.txt) ----
book_lang = {}   # ns -> {key: uk}
book_files = {}  # file -> [(path, uk)]
bpath = os.path.join(WORK, 'books_todo.json')
if os.path.exists(bpath):
    units = {u['id']: u for u in json.load(open(bpath, encoding='utf-8'))}
    BPH = re.compile(r'\[#\]\([^)]*\)|%%|\\\\n|\$\(t:|\$\([^)]*\)|%(\d+\$)?[sd]|\{\d+\}|§.|entry://\S+?\)')

    def dec(s):
        out, i = [], 0
        while i < len(s):
            if s[i] == '\\' and i + 1 < len(s):
                out.append({'n': '\n', 't': '\t', '\\': '\\'}.get(s[i + 1], '\\' + s[i + 1]))
                i += 2
            else:
                out.append(s[i])
                i += 1
        return ''.join(out)

    done = 0
    for f in sorted(glob.glob(os.path.join(WORK, 'bout_*.txt'))):
        for line in open(f, encoding='utf-8'):
            line = line.rstrip('\r\n')
            if not line:
                continue
            idx, text = line.split('\t', 1)
            u, text = units[int(idx)], dec(text)
            a = sorted(m.group(0) for m in BPH.finditer(u['en']))
            b = sorted(m.group(0) for m in BPH.finditer(text))
            if a != b:
                print('книжка: розмітка не збігається', idx, set(a) ^ set(b))
                problems += 1
            if u['kind'] == 'lang':
                book_lang.setdefault(u['ns'], {})[u['key']] = text
            else:
                book_files.setdefault(u['file'], []).append((u['path'], text))
            done += 1
    print('книжки: перекладено фрагментів', done, 'з', len(units))

total = 0
for ns in sorted(set(keys) | set(existing) | set(book_lang)):
    new = {k: tr[v] for k, v in keys.get(ns, {}).items() if v in tr}
    new.update(book_lang.get(ns, {}))
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
if book_files:
    import zipfile
    wanted = set(book_files)
    for jar in sorted(glob.glob(r"C:/Users/dimak/curseforge/minecraft/Instances/Capitalism/mods/*.jar")):
        try:
            z = zipfile.ZipFile(jar)
        except Exception:
            continue
        for n in set(z.namelist()) & wanted:
            data = json.loads(z.read(n).decode('utf-8-sig'))
            for path, text in book_files[n]:
                o = data
                for p in path[:-1]:
                    o = o[p]
                o[path[-1]] = text
            for loc in ('ru_ru', 'uk_ua'):
                out = os.path.join(PACK, n.replace('/en_us/', f'/{loc}/', 1))
                os.makedirs(os.path.dirname(out), exist_ok=True)
                json.dump(data, open(out, 'w', encoding='utf-8'), ensure_ascii=False, indent=1)
            wanted.discard(n)
    print('книжки: сторінок-файлів записано', len(book_files) - len(wanted), '| не знайдено', len(wanted))
print('модів (просторів імен):', len(os.listdir(os.path.join(PACK, 'assets'))), '| ключів у ru_ru:', total)
