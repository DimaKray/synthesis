"""Вставка нових квестів у доріжку після вказаного квесту: insert_after(key, code), де code — текст Q(...) викликів
(з відступом 12 пробілів і комами в кінці). Потім save()."""
import glob, os, re
from patch_specs import _match

D = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'chapters')
_files = {f: open(f, encoding='utf-8').read() for f in glob.glob(os.path.join(D, '*.py'))}


def insert_after(key, code):
    for f, t in _files.items():
        m = re.search(r"Q\(\s*'" + re.escape(key) + r"'\s*,", t)
        if not m:
            continue
        end = _match(t, m.start() + 1, '(', ')')
        # пропустити кому й перенос
        j = end
        while j < len(t) and t[j] in ',':
            j += 1
        if t[j] == '\n':
            j += 1
        code = code.strip('\n') + '\n'
        _files[f] = t[:j] + code + t[j:]
        return
    raise KeyError(key)


def save():
    for f, t in _files.items():
        open(f, 'w', encoding='utf-8').write(t)
