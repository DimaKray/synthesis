"""Набір ID предметів/блоків, що існують у модах збірки (за мовними ключами й моделями предметів у jar).
Кешується в tools/quests/.registry_cache.json."""
import glob, json, os, re, zipfile

MODS = r"C:/Users/dimak/curseforge/minecraft/Instances/Capitalism/mods"
CACHE = os.path.join(os.path.dirname(__file__), '.registry_cache.json')


GAME_DUMP = r"C:/Users/dimak/curseforge/minecraft/Instances/Capitalism/kubejs/exported/capitalism_items.txt"


def load(refresh=False):
    """Якщо є вивантаження з гри (kubejs/server_scripts/capitalism_dump.js) — беремо його: це точний список."""
    if os.path.exists(GAME_DUMP):
        newest_jar = max(os.path.getmtime(j) for j in glob.glob(MODS + '/*.jar'))
        if os.path.getmtime(GAME_DUMP) >= newest_jar:
            return set(l.strip() for l in open(GAME_DUMP, encoding='utf-8') if l.strip())
        print('УВАГА: вивантаження предметів з гри старіше за моди — запусти світ, щоб оновити; поки перевіряю за jar')
    if os.path.exists(CACHE) and not refresh:
        return set(json.load(open(CACHE, encoding='utf-8')))
    ids = set()
    vanilla_lang = None
    for jar in glob.glob(MODS + '/*.jar'):
        try:
            z = zipfile.ZipFile(jar)
        except Exception:
            continue
        for n in z.namelist():
            m = re.match(r'assets/([^/]+)/models/item/(.+)\.json$', n)
            if m:
                ids.add(f'{m.group(1)}:{m.group(2)}')
                continue
            m = re.match(r'assets/([^/]+)/lang/en_us\.json$', n)
            if m:
                try:
                    d = json.loads(z.read(n).decode('utf-8-sig'))
                except Exception:
                    continue
                for k in d:
                    mm = re.match(r'^(item|block)\.([a-z0-9_]+)\.([a-z0-9_.\/]+)$', k)
                    if mm and mm.group(2) == m.group(1):
                        ids.add(f'{mm.group(2)}:{mm.group(3)}')
                        ids.add(f'{mm.group(2)}:{mm.group(3).replace(".", "/")}')
    json.dump(sorted(ids), open(CACHE, 'w', encoding='utf-8'))
    return ids


def is_vanilla(i):
    return i.startswith('minecraft:')
