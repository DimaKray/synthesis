"""Збирає квести FTB збірки Capitalism з описів у tools/quests/chapters/*.py.
- перевіряє, що всі предмети існують у модах і всі залежності відомі;
- розкладає квести автоматично (колонки за глибиною, доріжки одна під одною, без накладань);
- повторно використовує старі ID квестів із тим самим предметом (щоб не губився прогрес);
- пише глави, групи глав і тексти (lang/en_us.snbt — українською, бо гра в ru_ru бере en_us як запасну).
Запуск: python tools/quests/build.py [--dry]"""
import glob, hashlib, importlib.util, json, os, random, re, shutil, sys, time

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
from snbt import loads, dumps, Raw  # noqa: E402
import registry  # noqa: E402

Q_DIR = r"C:/Users/dimak/curseforge/minecraft/Instances/Capitalism/config/ftbquests/quests"
BACKUP = r"C:/Users/dimak/curseforge/minecraft/Instances/Capitalism/backups"
DRY = '--dry' in sys.argv

# ---------- нагороди ----------
COIN = {0: ('coin_iron', 5), 1: ('coin_gold', 1), 2: ('coin_gold', 2), 3: ('coin_emerald', 1), 4: ('coin_emerald', 2), 5: ('coin_diamond', 1)}
XP = {0: 1, 1: 2, 2: 3, 3: 5, 4: 8, 5: 10}
BOX = {0: 'common', 1: 'common', 2: 'rare', 3: 'epic', 4: 'legendary', 5: 'legendary'}


def load_chapters():
    chs = []
    for f in sorted(glob.glob(os.path.join(HERE, 'chapters', '*.py'))):
        spec = importlib.util.spec_from_file_location(os.path.basename(f)[:-3], f)
        m = importlib.util.module_from_spec(spec)
        spec.loader.exec_module(m)
        chs.append(m.CHAPTER)
    return chs


def stable_id(*parts):
    """Детермінований ID ≤ 7FFFFFFFFFFFFFFF з рядка (щоб повторні збірки давали ті самі ID)."""
    h = int(hashlib.sha1('|'.join(parts).encode()).hexdigest()[:16], 16) & 0x7FFFFFFFFFFFFFFF
    return '%016X' % max(h, 0x1000000000000000)


def main():
    chapters = load_chapters()
    reg = registry.load()
    allq = {}
    errors = []
    for ch in chapters:
        for lane in ch.lanes:
            for q in lane.quests:
                if q.key in allq:
                    errors.append(f'дубль ключа {q.key}')
                allq[q.key] = (ch, q)
    for key, (ch, q) in allq.items():
        for d in q.deps:
            if d not in allq:
                errors.append(f'{ch.file}/{key}: невідома залежність {d}')
        for it in (q.items or [q.item]):
            if not registry.is_vanilla(it) and it not in reg:
                errors.append(f'{ch.file}/{key}: предмета {it} немає в модах')
    if errors:
        print('ПОМИЛКИ:\n  ' + '\n  '.join(errors))
        sys.exit(1)

    # ---------- старі ID: предмет -> id (якщо предмет зустрічався рівно в одному квесті) ----------
    old_by_item = {}
    for f in glob.glob(Q_DIR + '/chapters/*.snbt'):
        for oq in loads(open(f, encoding='utf-8').read()).get('quests', []):
            for t in oq.get('tasks', []):
                it = t.get('item')
                iid = it.get('id') if isinstance(it, dict) else None
                if iid:
                    old_by_item.setdefault(iid, []).append(oq['id'])
    old_by_item = {k: v[0] for k, v in old_by_item.items() if len(v) == 1}
    used_old = set()

    qid = {}
    for key, (ch, q) in allq.items():
        old = old_by_item.get(q.item)
        if old and old not in used_old and not q.check:
            qid[key] = old
            used_old.add(old)
        else:
            qid[key] = stable_id('quest', key)

    lang_out = {}
    files = {}
    groups = {'main': stable_id('group', 'main'), 'side': stable_id('group', 'side')}
    for ch in chapters:
        cid = stable_id('chapter', ch.file)
        lang_out[f'chapter.{cid}.title'] = ch.title
        lang_out[f'chapter.{cid}.chapter_subtitle'] = ch.subtitle
        in_ch = {q.key for lane in ch.lanes for q in lane.quests}
        # глибина в межах глави
        depth = {}

        def dep_depth(k, stack=()):
            if k in depth:
                return depth[k]
            q = allq[k][1]
            ds = [d for d in q.deps if d in in_ch and d not in stack]
            depth[k] = 0 if not ds else 1 + max(dep_depth(d, stack + (k,)) for d in ds)
            return depth[k]

        for k in in_ch:
            dep_depth(k)
        quests = []
        y0 = 0.0
        for lane in ch.lanes:
            cols = {}
            for q in lane.quests:
                cols.setdefault(depth[q.key], []).append(q)
            if len(cols) == 1 and len(lane.quests) > 3:
                # незалежні квести однієї глибини — у рядок, а не стовпчиком
                d0 = next(iter(cols))
                for i, q in enumerate(lane.quests):
                    quests.append(build_quest(ch, q, qid, allq, (d0 + i) * 2.0, y0, lang_out))
                y0 += 3.0
                continue
            rows = max(len(v) for v in cols.values())
            for col, qs in cols.items():
                for i, q in enumerate(qs):
                    off = (rows - len(qs)) / 2.0
                    x = col * 2.0
                    y = y0 + (off + i) * 1.5
                    quests.append(build_quest(ch, q, qid, allq, x, y, lang_out))
            y0 += rows * 1.5 + 1.5
        files[ch.file] = {
            'default_hide_dependency_lines': Raw('false'), 'default_quest_shape': '', 'filename': ch.file,
            'group': groups[ch.group], 'icon': {'id': ch.icon}, 'id': cid, 'images': [],
            'order_index': Raw(str(ch.order)), 'quest_links': [], 'quests': quests,
        }
    lang_out[f'chapter_group.{groups["main"]}.title'] = 'Основний шлях'
    lang_out[f'chapter_group.{groups["side"]}.title'] = 'Побічні шляхи (ключі)'

    total = sum(len(f['quests']) for f in files.values())
    print(f'глав: {len(files)} | квестів: {total} | старих ID збережено: {len(used_old)}')
    if DRY:
        return

    # бекап робимо лише раз — перед першою перебудовою (далі квести й так генеруються з описів)
    if not glob.glob(os.path.join(BACKUP, 'ftbquests_before_rebuild_*')):
        bdir = os.path.join(BACKUP, 'ftbquests_before_rebuild_' + time.strftime('%Y-%m-%d_%H%M%S'))
        shutil.copytree(Q_DIR, bdir)
        print('бекап:', bdir)
    for f in glob.glob(Q_DIR + '/chapters/*.snbt'):
        os.remove(f)
    for name, data in files.items():
        open(f'{Q_DIR}/chapters/{name}.snbt', 'w', encoding='utf-8', newline='\r\n').write(dumps(data) + '\n')
    groups_data = {'chapter_groups': [{'id': groups['main']}, {'id': groups['side']}]}
    open(f'{Q_DIR}/chapter_groups.snbt', 'w', encoding='utf-8', newline='\r\n').write(dumps(groups_data) + '\n')
    old_lang = loads(open(Q_DIR + '/lang/en_us.snbt', encoding='utf-8').read())
    keep = {k: v for k, v in old_lang.items() if not re.match(r'^(chapter|quest|task|reward|chapter_group)\.', k)}
    keep.update(lang_out)
    open(Q_DIR + '/lang/en_us.snbt', 'w', encoding='utf-8', newline='\r\n').write(dumps(keep) + '\n')
    print('записано')


def build_quest(ch, q, qid, allq, x, y, lang):
    i = qid[q.key]
    lang[f'quest.{i}.title'] = q.title
    lang[f'quest.{i}.quest_desc'] = q.desc
    if q.sub:
        lang[f'quest.{i}.quest_subtitle'] = q.sub
    elif q.kind == 'key':
        lang[f'quest.{i}.quest_subtitle'] = 'Ключ до Infinity Catalyst'
    tasks = []
    if q.check:
        tasks.append({'id': stable_id('task', q.key), 'type': 'checkmark'})
    else:
        for n, it in enumerate(q.items or [q.item]):
            t = {'id': stable_id('task', q.key, str(n)), 'type': 'item', 'item': {'count': Raw('1'), 'id': it}}
            if q.count > 1 and not q.items:
                t['count'] = Raw(f'{q.count}L')
            tasks.append(t)
    rewards = []
    tier = ch.tier
    if q.reward != 'none' and not q.check:
        coin, cnt = COIN[tier]
        if q.kind in ('gate', 'key'):
            cnt *= 2
        rewards.append({'id': stable_id('rw', q.key, 'xp'), 'type': 'xp_levels', 'xp_levels': Raw(str(XP[tier] * (2 if q.kind == 'gate' else 1)))})
        rewards.append({'id': stable_id('rw', q.key, 'coin'), 'type': 'item', 'count': Raw(str(cnt)),
                        'item': {'count': Raw('1'), 'id': f'lightmanscurrency:{coin}'}})
        if q.kind in ('gate', 'key'):
            box = BOX[tier] if q.kind == 'gate' else 'rare'
            rewards.append({'id': stable_id('rw', q.key, 'box'), 'type': 'item',
                            'item': {'count': Raw('1'), 'id': f'synthesis:capital_box_{box}'}})
    quest = {'id': i, 'x': Raw(f'{x}d'), 'y': Raw(f'{y}d'), 'tasks': tasks, 'rewards': rewards}
    if q.deps:
        quest['dependencies'] = [qid[d] for d in q.deps]
    if q.kind == 'gate':
        quest['shape'] = 'hexagon'
        quest['size'] = Raw('2.0d')
    elif q.kind == 'info':
        quest['shape'] = 'gear'
    elif q.kind == 'key':
        quest['shape'] = 'diamond'
        quest['size'] = Raw('1.5d')
    return quest


if __name__ == '__main__':
    main()
