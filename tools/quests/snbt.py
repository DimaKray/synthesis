"""Мінімальний парсер/запис SNBT у форматі FTB Quests (для інструментів збірки Capitalism).
Числа з суфіксами (1.0d, 600L, 0b) зберігаються як рядки-«сирі значення» класу Raw, щоб записати їх як було."""
import re


class Raw(str):
    """Сире значення без лапок: числа з суфіксом, true/false тощо."""


TOKEN = re.compile(r'\s*(?:(\{)|(\})|(\[)|(\])|(,)|(:)|"((?:[^"\\]|\\.)*)"|([^\s{}\[\],:"]+))')


def _tokens(text):
    pos = 0
    n = len(text)
    while pos < n:
        m = TOKEN.match(text, pos)
        if not m:
            if text[pos:].strip() == '':
                return
            raise ValueError('SNBT: не розібрано біля ' + repr(text[pos:pos + 40]))
        pos = m.end()
        if m.group(1): yield ('{', None)
        elif m.group(2): yield ('}', None)
        elif m.group(3): yield ('[', None)
        elif m.group(4): yield (']', None)
        elif m.group(5): continue
        elif m.group(6): yield (':', None)
        elif m.group(7) is not None:
            yield ('str', bytes(m.group(7), 'utf-8').decode('unicode_escape').encode('latin-1').decode('utf-8')
                   if '\\' in m.group(7) else m.group(7))
        elif m.group(8):
            yield ('raw', m.group(8))


def loads(text):
    toks = list(_tokens(text))
    i = 0

    def value():
        nonlocal i
        t, v = toks[i]
        if t == '{':
            i += 1
            d = {}
            while toks[i][0] != '}':
                k = toks[i][1]
                i += 1
                assert toks[i][0] == ':', toks[i]
                i += 1
                d[k] = value()
            i += 1
            return d
        if t == '[':
            i += 1
            lst = []
            while toks[i][0] != ']':
                lst.append(value())
            i += 1
            return lst
        i += 1
        return v if t == 'str' else Raw(v)

    return value()


def _q(s):
    return '"' + s.replace('\\', '\\\\').replace('"', '\\"') + '"'


def dumps(o, ind=0):
    t = '\t'
    if isinstance(o, dict):
        if not o:
            return '{ }'
        lines = ['{']
        for k in sorted(o):
            key = k if re.fullmatch(r'[A-Za-z0-9_\-]+', k) else _q(k)  # ключі з крапкою — в лапках, як у FTB
            lines.append(t * (ind + 1) + f'{key}: {dumps(o[k], ind + 1)}')
        lines.append(t * ind + '}')
        return '\n'.join(lines)
    if isinstance(o, list):
        if not o:
            return '[ ]'
        if all(not isinstance(x, (dict, list)) for x in o):
            return '[' + ', '.join(dumps(x, ind) for x in o) + ']'
        lines = ['[']
        for x in o:
            lines.append(t * (ind + 1) + dumps(x, ind + 1))
        lines.append(t * ind + ']')
        return '\n'.join(lines)
    if isinstance(o, Raw):
        return str(o)
    if isinstance(o, bool):
        return 'true' if o else 'false'
    if isinstance(o, (int, float)):
        return repr(o)
    return _q(str(o))
