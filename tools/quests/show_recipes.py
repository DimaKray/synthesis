"""Показує інгредієнти рецептів для квестів (щоб писати конкретні описи).
python show_recipes.py [ключ ...]   — без аргументів: квести, де в описі є «JEI» або опис короткий."""
import os, sys
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import recipes  # noqa: E402
from build import load_chapters  # noqa: E402

idx, _ = recipes.load()
qs = {q.key: q for ch in load_chapters() for lane in ch.lanes for q in lane.quests}
keys = sys.argv[1:] or [k for k, q in qs.items() if not q.check and ('JEI' in ' '.join(q.desc) or len(' '.join(q.desc)) < 70)]
for k in keys:
    q = qs[k]
    print(f'== {k} ({q.item}) — {" ".join(q.desc)[:90]}')
    for r in idx.get(q.item, [])[:3]:
        ings = [a[0] if len(a) == 1 else a[0] + f'(+{len(a) - 1})' for a in r['ingredients']]
        cnt = {}
        for i in ings:
            cnt[i] = cnt.get(i, 0) + 1
        print(f'   {r["type"]:32} {", ".join(f"{v}×{c}" if c > 1 else v for v, c in cnt.items())}')
