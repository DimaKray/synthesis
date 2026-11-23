"""Програмне редагування описів квестів у chapters/*.py: заміна опису (desc) і залежностей за ключем.
Використання з інших скриптів: patch(key, desc=[...], deps=[...])."""
import glob, os, re

D = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'chapters')
_files = {f: open(f, encoding='utf-8').read() for f in glob.glob(os.path.join(D, '*.py'))}


def _match(text, i, open_c, close_c):
    depth, in_str, j = 0, None, i
    while j < len(text):
        c = text[j]
        if in_str:
            if c == '\\':
                j += 2
                continue
            if c == in_str:
                in_str = None
        elif c in '\'"':
            in_str = c
        elif c == open_c:
            depth += 1
        elif c == close_c:
            depth -= 1
            if depth == 0:
                return j + 1
        j += 1
    raise ValueError('не закрито')


def _lit(s):
    return repr(s)


def patch(key, desc=None, deps=None, title=None):
    for f, t in _files.items():
        m = re.search(r"Q\(\s*'" + re.escape(key) + r"'\s*,", t)
        if not m:
            continue
        end = _match(t, m.start() + 1, '(', ')')
        call = t[m.start():end]
        if desc is not None:
            # опис — перший аргумент-список (або рядок) після трьох рядкових аргументів
            args = re.match(r"Q\(\s*'[^']*'\s*,\s*'[^']*'\s*,\s*((?:'(?:[^'\\]|\\.)*'|\"(?:[^\"\\]|\\.)*\"))\s*,\s*", call)
            if not args:
                raise ValueError('не розібрав аргументи ' + key)
            if title is not None:
                call = call[:args.start(1)] + _lit(title) + call[args.end(1):]
                args = re.match(r"Q\(\s*'[^']*'\s*,\s*'[^']*'\s*,\s*((?:'(?:[^'\\]|\\.)*'|\"(?:[^\"\\]|\\.)*\"))\s*,\s*", call)
            s = args.end()
            e = _match(call, s, '[', ']') if call[s] == '[' else re.match(r"(?:'(?:[^'\\]|\\.)*'|\"(?:[^\"\\]|\\.)*\")", call[s:]).end() + s
            body = '[\n' + ''.join(f'                {_lit(x)},\n' for x in desc) + '            ]'
            call = call[:s] + body + call[e:]
        if deps is not None:
            dm = re.search(r"deps=\[[^\]]*\]", call)
            lst = 'deps=[' + ', '.join(f"'{d}'" for d in deps) + ']'
            call = call[:dm.start()] + lst + call[dm.end():] if dm else call[:-1] + ', ' + lst + ')'
        _files[f] = t[:m.start()] + call + t[end:]
        return
    raise KeyError(key)


def save():
    for f, t in _files.items():
        open(f, 'w', encoding='utf-8').write(t)
