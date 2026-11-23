"""Звіряє порядок квестів з реальними рецептами.
Для кожного квесту знаходить, які ІНШІ квестові предмети потрібні для крафту його предмета
(з урахуванням проміжних деталей, до 5 кроків углиб; обирається найдешевший рецепт),
і перевіряє, що ці квести стоять раніше (серед предків). Виводить пропущені залежності.
Запуск: python tools/quests/verify.py [--refresh]"""
import os, sys
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import recipes  # noqa: E402
from build import load_chapters  # noqa: E402

BASIC = {'minecraft:'}  # ванільні проміжні вважаємо доступними, але квестові ванільні предмети — ні


def main():
    index, _ = recipes.load('--refresh' in sys.argv)
    chapters = load_chapters()
    allq, item2key = {}, {}
    for ch in chapters:
        for lane in ch.lanes:
            for q in lane.quests:
                allq[q.key] = q
                if not q.check:
                    for it in (q.items or [q.item]):
                        item2key.setdefault(it, q.key)

    anc = {}

    def ancestors(k):
        if k in anc:
            return anc[k]
        anc[k] = set()
        s = set()
        for d in allq[k].deps:
            s.add(d)
            s |= ancestors(d)
        anc[k] = s
        return s

    def cyclic(item, r):
        """Рецепт-«розпакування» (злиток з блока тощо): інгредієнт сам крафтиться з цього предмета."""
        for alt in r['ingredients']:
            for y in alt:
                for r2 in index.get(y, []):
                    if any(item in a for a in r2['ingredients']):
                        return True
        return False

    memo = {}

    def need(item, depth=0, stack=()):
        """Мінімальна множина квестових предметів, потрібних для крафту item (None — рецепта немає)."""
        if depth > 0 and item in item2key:
            return {item}, []
        if item in memo:
            return memo[item]
        if depth > 5 or item in stack:
            return set(), []
        if depth > 0 and item.startswith('minecraft:'):
            # ванільних рецептів в індексі немає — лише чужі «покращення»/розбирання (Apotheosis тощо), вони вводять в оману
            return set(), []
        rs = [r for r in index.get(item, []) if not cyclic(item, r)]
        if not rs:
            return (None, []) if depth == 0 else (set(), [])
        best = None
        for r in rs:
            total, ok = set(), True
            for alt in r['ingredients']:
                opts = [need(o, depth + 1, stack + (item,))[0] for o in alt]
                opts = [o if o is not None else set() for o in opts]
                total |= min(opts, key=len) if opts else set()
            if best is None or len(total) < len(best[0]):
                best = (total, [r])
        if depth > 0:
            memo[item] = best
        return best

    problems = 0
    unknown = []
    for key, q in allq.items():
        if q.check:
            continue
        a = ancestors(key) | {key}
        for it in (q.items or [q.item]):
            req, rs = need(it)
            if req is None:
                unknown.append(f'{key} ({it})')
                continue
            missing = sorted({item2key[r] for r in req if item2key.get(r) not in a})
            if missing:
                problems += 1
                src = rs[0]['source'] if rs else '?'
                print(f'  {key:18} <- бракує залежностей: {", ".join(missing)}   [рецепт: {rs[0]["type"]} з {src}]')
    print(f'\nпроблем: {problems}; без рецепта в даних (крафт у коді мода / здобич / світ): {len(unknown)}')
    if '--unknown' in sys.argv:
        print('  ' + '\n  '.join(unknown))


if __name__ == '__main__':
    main()
