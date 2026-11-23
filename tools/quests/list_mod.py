"""Список предметів мода з англійськими назвами (для вибору віх квестів).
python list_mod.py <namespace> [фільтр-регекс]"""
import glob, json, os, re, sys, zipfile

M = r"C:/Users/dimak/curseforge/minecraft/Instances/Capitalism/mods"
ns = sys.argv[1]
flt = re.compile(sys.argv[2]) if len(sys.argv) > 2 else None
names, items = {}, set()
for j in glob.glob(M + '/*.jar'):
    z = zipfile.ZipFile(j)
    for n in z.namelist():
        if n == f'assets/{ns}/lang/en_us.json':
            d = json.loads(z.read(n).decode('utf-8-sig'))
            for k, v in d.items():
                m = re.match(rf'^(?:item|block)\.{ns}\.([a-z0-9_/.]+)$', k)
                if m:
                    names[m.group(1)] = v
        m = re.match(rf'assets/{ns}/models/item/(.+)\.json$', n)
        if m:
            items.add(m.group(1))
for i in sorted(items | set(names)):
    if flt and not flt.search(i):
        continue
    print(f'{ns}:{i:45} {names.get(i, "")}')
