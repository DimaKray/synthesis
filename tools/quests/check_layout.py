"""Перевіряє розкладку квестів у збірці: накладання й розмір глав."""
import glob, os, sys
sys.path.insert(0, os.path.dirname(__file__))
from snbt import loads

Q = r"C:/Users/dimak/curseforge/minecraft/Instances/Capitalism/config/ftbquests/quests"
for f in sorted(glob.glob(Q + '/chapters/*.snbt')):
    d = loads(open(f, encoding='utf-8').read())
    pos = [(float(q['x'].rstrip('d')), float(q['y'].rstrip('d'))) for q in d['quests']]
    clash = sum(1 for i, a in enumerate(pos) for b in pos[i + 1:] if abs(a[0] - b[0]) < 1.2 and abs(a[1] - b[1]) < 1.2)
    print(f'{os.path.basename(f):38} квестів {len(pos):3} накладань {clash} ширина {max(p[0] for p in pos):5} висота {max(p[1] for p in pos):5}')
