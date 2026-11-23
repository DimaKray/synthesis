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
# Словник назв «англійська → українська»: work/names_tr.json (основний) + work/names_extra.txt («англ\tукр»,
# доповнення для нових модів). Ключ — сам англійський текст, тож extract.py можна перезапускати.
tr = dict(json.load(open(os.path.join(WORK, 'names_tr.json'), encoding='utf-8')))
extra = os.path.join(WORK, 'names_extra.txt')
if os.path.exists(extra):
    for line in open(extra, encoding='utf-8'):
        line = line.rstrip('\r\n')
        if line:
            en, text = line.split('\t', 1)
            tr[en] = text
problems = 0
for en, text in tr.items():
    if sorted(PH.findall(en)) != sorted(PH.findall(text)):
        print('плейсхолдери не збігаються:', repr(en), '->', repr(text))
        problems += 1
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

# ---- готова українська там, де гра показала б англійську (немає ru_ru, але є uk_ua) ----
import zipfile
uk_fill = {}        # ns -> {key: uk} — будь-які ключі, не лише назви
uk_files = {}       # шлях ru_ru у паку -> байти uk_ua-файлу Patchouli
PB = re.compile(r'assets/([^/]+)/patchouli_books/([^/]+)/([a-z]{2}_[a-z]{2})/(.+)$')
all_lang, locs, uk_bytes = {}, {}, {}
for jar in sorted(glob.glob(r"C:/Users/dimak/curseforge/minecraft/Instances/Capitalism/mods/*.jar")):
    try:
        z = zipfile.ZipFile(jar)
    except Exception:
        continue
    for n in z.namelist():
        m = re.match(r'assets/([^/]+)/lang/(en_us|uk_ua|ru_ru)\.json$', n)
        if m:
            try:
                all_lang.setdefault(m.group(1), {}).setdefault(m.group(2), {}).update(
                    json.loads(z.read(n).decode('utf-8-sig')))
            except Exception:
                pass
            continue
        m = PB.match(n)
        if m and not n.endswith('/'):
            locs.setdefault((m.group(1), m.group(2), m.group(4)), set()).add(m.group(3))
            if m.group(3) == 'uk_ua':
                uk_bytes[(m.group(1), m.group(2), m.group(4))] = z.read(n)
for ns, L in all_lang.items():
    en, uk, ruo = L.get('en_us', {}), L.get('uk_ua', {}), L.get('ru_ru', {})
    for k in en:
        if k not in ruo and isinstance(uk.get(k), str) and uk[k].strip():
            uk_fill.setdefault(ns, {})[k] = uk[k]
for key, ls in locs.items():
    if 'uk_ua' in ls and 'ru_ru' not in ls and key in uk_bytes:
        ns, book, rel = key
        uk_files[f'assets/{ns}/patchouli_books/{book}/ru_ru/{rel}'] = uk_bytes[key]
print('готова uk замість англ.: ключів', sum(len(d) for d in uk_fill.values()),
      '| файлів книжок Patchouli', len(uk_files))

# Окремі ключі, перекладені вручну (не назви й не книжки): work/lang_extra.json = {ns: {key: uk}}
lang_extra = {}
le = os.path.join(WORK, 'lang_extra.json')
if os.path.exists(le):
    lang_extra = json.load(open(le, encoding='utf-8'))

total = 0
for ns in sorted(set(keys) | set(existing) | set(book_lang) | set(uk_fill) | set(lang_extra)):
    new = {k: tr[v] for k, v in keys.get(ns, {}).items() if v in tr}
    new.update(book_lang.get(ns, {}))
    new.update(lang_extra.get(ns, {}))
    ru = dict(uk_fill.get(ns, {}))
    ru.update(existing.get(ns, {}))
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
# Гайди GuideME (ae2guide): переклад у guides/<ns>/uk/*.md → assets/<ns>/ae2guide/_ru_ru/ і _uk_ua/
guide_pages = 0
for d in glob.glob(os.path.join(HERE, 'guides', '*', 'uk')):
    ns = os.path.basename(os.path.dirname(d))
    for f in glob.glob(os.path.join(d, '*.md')):
        for loc in ('ru_ru', 'uk_ua'):
            out = os.path.join(PACK, 'assets', ns, 'ae2guide', '_' + loc, os.path.basename(f))
            os.makedirs(os.path.dirname(out), exist_ok=True)
            shutil.copyfile(f, out)
        guide_pages += 1
print('гайди GuideME: сторінок', guide_pages)
for rel, data in uk_files.items():
    out = os.path.join(PACK, rel)
    os.makedirs(os.path.dirname(out), exist_ok=True)
    open(out, 'wb').write(data)
print('модів (просторів імен):', len(os.listdir(os.path.join(PACK, 'assets'))), '| ключів у ru_ru:', total)
