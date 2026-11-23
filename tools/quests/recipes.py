"""Індекс рецептів і тегів предметів збірки Capitalism (з jar модів + перевизначення KubeJS).
recipes_for(item) -> список рецептів [{id, type, ingredients: [set(item ids)], source}] — ті, що дають цей предмет.
Кешується в .recipes_cache.json."""
import glob, json, os, re, zipfile

MODS = r"C:/Users/dimak/curseforge/minecraft/Instances/Capitalism/mods"
KJS = r"C:/Users/dimak/curseforge/minecraft/Instances/Capitalism/kubejs/server_scripts"
CACHE = os.path.join(os.path.dirname(__file__), '.recipes_cache.json')
RESULT_KEYS = {'result', 'results', 'output', 'outputs', 'result_item', 'resultItem', 'product', 'products'}


def _ingredients(node, out, in_result=False):
    """Збирає «інгредієнти»: кожен вузол {item}/{tag}/{id} поза результатом — окрема альтернатива."""
    if isinstance(node, dict):
        if not in_result:
            if 'item' in node and isinstance(node['item'], str):
                out.append(('item', node['item']))
                return
            if 'tag' in node and isinstance(node['tag'], str):
                out.append(('tag', node['tag']))
                return
        for k, v in node.items():
            if k in ('neoforge:conditions', 'conditions', 'type', 'category', 'group'):
                continue
            _ingredients(v, out, in_result or k in RESULT_KEYS)
    elif isinstance(node, list):
        # список альтернатив [{item},{item}] у ключі shaped — теж окремо
        for v in node:
            _ingredients(v, out, in_result)


def _results(node):
    res = []

    def walk(n, inside):
        if isinstance(n, dict):
            if inside:
                for key in ('id', 'item'):
                    if isinstance(n.get(key), str) and ':' in n[key]:
                        res.append(n[key])
            for k, v in n.items():
                walk(v, inside or k in RESULT_KEYS)
        elif isinstance(n, list):
            for v in n:
                walk(v, inside)
        elif isinstance(n, str) and inside and re.fullmatch(r'[a-z0-9_.\-]+:[a-z0-9_./\-]+', n):
            res.append(n)
    walk(node, False)
    return res


def _js_objects(text, call):
    """Витягує JSON-аргументи з викликів event.custom({...}) / event.remove({...}) у скриптах KubeJS."""
    out = []
    for m in re.finditer(re.escape(call) + r'\(', text):
        i = m.end()
        depth, j = 0, i
        while j < len(text):
            c = text[j]
            if c in '{[':
                depth += 1
            elif c in '}]':
                depth -= 1
                if depth == 0:
                    j += 1
                    break
            j += 1
        raw = text[i:j]
        try:
            out.append(json.loads(raw))
        except Exception:
            try:  # { id: 'x' } — JS-стиль
                out.append(json.loads(re.sub(r"(\w+)\s*:", r'"\1":', raw.replace("'", '"'))))
            except Exception:
                pass
    return out


def build():
    recipes, tags = {}, {}
    for jar in glob.glob(MODS + '/*.jar'):
        try:
            z = zipfile.ZipFile(jar)
        except Exception:
            continue
        for n in z.namelist():
            m = re.match(r'data/([^/]+)/recipes?/(.+)\.json$', n)
            if m:
                try:
                    recipes[f'{m.group(1)}:{m.group(2)}'] = (json.loads(z.read(n).decode('utf-8-sig')), os.path.basename(jar))
                except Exception:
                    pass
                continue
            m = re.match(r'data/([^/]+)/tags/items?/(.+)\.json$', n)
            if m:
                try:
                    d = json.loads(z.read(n).decode('utf-8-sig'))
                except Exception:
                    continue
                vals = []
                for v in d.get('values', []):
                    vals.append(v['id'] if isinstance(v, dict) else v)
                tags.setdefault(f'{m.group(1)}:{m.group(2)}', []).extend(vals)
    # KubeJS: видалення й нові рецепти
    for f in glob.glob(KJS + '/*.js'):
        t = open(f, encoding='utf-8').read()
        for rid in re.findall(r"event\.remove\(\{\s*id:\s*['\"]([^'\"]+)['\"]", t):
            recipes.pop(rid, None)
        for rtype in re.findall(r"event\.remove\(\{\s*type:\s*['\"]([^'\"]+)['\"]", t):
            for k in [k for k, (r, _) in recipes.items() if isinstance(r, dict) and r.get('type') == rtype]:
                recipes.pop(k)
        for out_id in re.findall(r"event\.remove\(\{\s*output:\s*['\"]([^'\"]+)['\"]", t):
            for k in [k for k, (r, _) in recipes.items() if isinstance(r, dict) and out_id in _results(r)]:
                recipes.pop(k)
        for n, o in enumerate(_js_objects(t, 'event.custom')):
            recipes[f'kubejs:{os.path.basename(f)}#{n}'] = (o, 'KubeJS ' + os.path.basename(f))
    # розкриваємо вкладені теги
    def expand(tag, seen=()):
        out = set()
        for v in tags.get(tag, []):
            if v.startswith('#'):
                if v[1:] not in seen:
                    out |= expand(v[1:], seen + (tag,))
            else:
                out.add(v)
        return out
    flat = {t: sorted(expand(t)) for t in tags}
    index = {}
    for rid, (r, src) in recipes.items():
        if not isinstance(r, dict):
            continue
        ings = []
        _ingredients(r, ings)
        alts = []
        for kind, v in ings:
            alts.append(sorted(flat.get(v, [])) if kind == 'tag' else [v])
        alts = [a for a in alts if a]
        for res in set(_results(r)):
            index.setdefault(res, []).append({'id': rid, 'type': r.get('type', '?'), 'ingredients': alts, 'source': src})
    json.dump({'index': index, 'tags': flat}, open(CACHE, 'w', encoding='utf-8'))
    return index, flat


def load(refresh=False):
    if os.path.exists(CACHE) and not refresh:
        d = json.load(open(CACHE, encoding='utf-8'))
        return d['index'], d['tags']
    return build()
