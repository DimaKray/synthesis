"""Виводить структуру квестів: глава → квести (назва, завдання, залежності, позиція). Для аналізу."""
import glob, os, re, sys
sys.path.insert(0, os.path.dirname(__file__))
from snbt import loads

Q = r"C:/Users/dimak/curseforge/minecraft/Instances/Capitalism/config/ftbquests/quests"
lang = loads(open(Q + '/lang/en_us.snbt', encoding='utf-8').read())
only = sys.argv[1] if len(sys.argv) > 1 else ''
for f in sorted(glob.glob(Q + '/chapters/*.snbt')):
    if only not in f:
        continue
    ch = loads(open(f, encoding='utf-8').read())
    title = lang.get(f"chapter.{ch['id']}.title")
    qs = ch.get('quests', [])
    names = {q['id']: lang.get(f"quest.{q['id']}.title", '?') for q in qs}
    print(f"\n=== {os.path.basename(f)} | {title} | квестів {len(qs)}")
    for q in qs:
        tasks = []
        for t in q.get('tasks', []):
            if t.get('type') == 'item':
                it = t.get('item')
                tasks.append(it.get('id') if isinstance(it, dict) else str(it))
            else:
                tasks.append(t.get('type'))
        deps = [names.get(d, d) for d in q.get('dependencies', [])]
        desc = lang.get(f"quest.{q['id']}.quest_desc", [])
        print(f"  ({float(str(q.get('x','0')).rstrip('d')):5.1f},{float(str(q.get('y','0')).rstrip('d')):5.1f}) "
              f"{names[q['id']]} | {', '.join(tasks)} | після: {', '.join(deps) or '—'} | опис: {len(' '.join(desc))} симв.")
