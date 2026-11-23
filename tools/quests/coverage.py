"""Які моди (простори імен з предметами) ще не мають жодного квесту."""
import glob, os, re, sys, zipfile
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from build import load_chapters  # noqa: E402

used = set()
for ch in load_chapters():
    for lane in ch.lanes:
        for q in lane.quests:
            for it in (q.items or [q.item]):
                used.add(it.split(':')[0])
M = r"C:/Users/dimak/curseforge/minecraft/Instances/Capitalism/mods"
out = []
for j in sorted(glob.glob(M + '/*.jar')):
    ns = {}
    for n in zipfile.ZipFile(j).namelist():
        m = re.match(r'assets/([^/]+)/models/item/[^/]+\.json$', n)
        if m:
            ns[m.group(1)] = ns.get(m.group(1), 0) + 1
    for k, v in ns.items():
        if k not in used and k != 'minecraft' and v >= 3:
            out.append((k, v, os.path.basename(j)[:45]))
for k, v, j in sorted(out, key=lambda x: -x[1]):
    print(f'{k:32} {v:5}  {j}')
