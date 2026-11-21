"""Збирає тексти внутрішньоігрових книжок (Patchouli, Modonomicon), які НЕ мають ні українського, ні російського перекладу.
Два види:
  lang  — книжка посилається на ключі мовного файла; беремо ключі, яких нема ні в uk_ua, ні в ru_ru цього мода;
  file  — Patchouli-сторінка з текстом прямо у JSON (en_us/...json), коли для книжки нема папки uk_ua/ru_ru з цим файлом.
Результат: work/books_todo.json — список {id, ns, kind, key | file+path, en}.
Запуск: python tools/ua_pack/extract_books.py"""
import glob
import json
import os
import re
import zipfile

MODS = r"C:/Users/dimak/curseforge/minecraft/Instances/Capitalism/mods"
HERE = os.path.dirname(os.path.abspath(__file__))
WORK = os.path.join(HERE, 'work')
SKIP_NS = {'synthesis'}
TEXT_FIELDS = {'text', 'title', 'name', 'description', 'subtitle', 'landing_text', 'header', 'entry_text'}
PATCHOULI = re.compile(r'assets/([^/]+)/patchouli_books/([^/]+)/([a-z]{2}_[a-z]{2})/(.+\.json)$')
MODONOMICON = re.compile(r'data/([^/]+)/modonomicon/books/.+\.json$')
PATCHOULI_DATA = re.compile(r'data/([^/]+)/patchouli_books/.+\.json$')


def load(z, n):
    try:
        return json.loads(z.read(n).decode('utf-8-sig'))
    except Exception:
        return None


def walk(o, path=()):
    """Повертає (шлях, рядок) для всіх рядків у JSON."""
    if isinstance(o, dict):
        for k, v in o.items():
            yield from walk(v, path + (k,))
    elif isinstance(o, list):
        for i, v in enumerate(o):
            yield from walk(v, path + (i,))
    elif isinstance(o, str):
        yield path, o


langs = {}          # ns -> lang -> dict (зливаємо всі jar)
book_files = []     # (jar, name, ns, kind)
patchouli_locales = {}  # (ns, book, rel) -> set(locale)
zips = {}
for jar in sorted(glob.glob(MODS + '/*.jar')):
    try:
        z = zipfile.ZipFile(jar)
    except Exception:
        continue
    zips[jar] = z
    for n in z.namelist():
        m = re.match(r'assets/([^/]+)/lang/(en_us|uk_ua|ru_ru)\.json$', n)
        if m:
            d = load(z, n)
            if isinstance(d, dict):
                langs.setdefault(m.group(1), {}).setdefault(m.group(2), {}).update(d)
            continue
        m = PATCHOULI.match(n)
        if m:
            patchouli_locales.setdefault((m.group(1), m.group(2), m.group(4)), set()).add(m.group(3))
            if m.group(3) == 'en_us':
                book_files.append((jar, n, m.group(1), 'patchouli'))
            continue
        m = MODONOMICON.match(n) or PATCHOULI_DATA.match(n)
        if m:
            book_files.append((jar, n, m.group(1), 'data'))

all_en = {}
for ns, L in langs.items():
    for k, v in L.get('en_us', {}).items():
        all_en.setdefault(k, (ns, v))


def has_tr(ns, key):
    L = langs.get(ns, {})
    return any(isinstance(L.get(l, {}).get(key), str) and L[l][key].strip() for l in ('uk_ua', 'ru_ru'))


units, seen_keys = [], set()
for jar, n, ns, kind in book_files:
    d = load(zips[jar], n)
    if d is None:
        continue
    file_unit = None
    for path, s in walk(d):
        if not path or path[-1] not in TEXT_FIELDS:
            continue
        if s in all_en:                          # посилання на мовний ключ
            kns, en = all_en[s]
            if kns in SKIP_NS or s in seen_keys or has_tr(kns, s):
                continue
            seen_keys.add(s)
            if re.search(r'[A-Za-z]{2}', en):
                units.append({'ns': kns, 'kind': 'lang', 'key': s, 'en': en})
        elif kind == 'patchouli' and re.search(r'[A-Za-z]{3,}', s) and ' ' in s.strip() or (
                kind == 'patchouli' and path[-1] in ('name', 'title') and re.search(r'[A-Za-z]{3,}', s)):
            m = PATCHOULI.match(n)
            locs = patchouli_locales.get((m.group(1), m.group(2), m.group(4)), set())
            if locs & {'uk_ua', 'ru_ru'}:
                continue
            units.append({'ns': ns, 'kind': 'file', 'file': n, 'path': list(path), 'en': s})

for i, u in enumerate(units):
    u['id'] = i
json.dump(units, open(os.path.join(WORK, 'books_todo.json'), 'w', encoding='utf-8'), ensure_ascii=False, indent=0)
by = {}
for u in units:
    by.setdefault(u['ns'], [0, 0])
    by[u['ns']][0] += 1
    by[u['ns']][1] += len(u['en'].split())
for ns, (c, w) in sorted(by.items(), key=lambda x: -x[1][1]):
    print(f'{w:>7} слів  {c:>5} рядків  {ns}')
print('РАЗОМ слів:', sum(w for c, w in by.values()), 'рядків:', len(units))
