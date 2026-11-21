"""Ділить work/books_todo.json на пакети work/book_NN.txt (~1800 слів), рядок «id\\tтекст», переноси як \\n.
Переклад кладеться у work/bout_NN.txt у тому ж форматі."""
import json
import os

HERE = os.path.dirname(os.path.abspath(__file__))
WORK = os.path.join(HERE, 'work')
ORDER = ['industrialforegoing', 'botania', 'extrabotany', 'masterfulbotany_lexicon', 'witchery',
         'irons_spellbooks', 'apotheosis', 'ars_n_spells', 'neovitae']
units = json.load(open(os.path.join(WORK, 'books_todo.json'), encoding='utf-8'))
units.sort(key=lambda u: (ORDER.index(u['ns']) if u['ns'] in ORDER else 99, u['id']))


def enc(s):
    return s.replace('\\', '\\\\').replace('\n', '\\n').replace('\t', '\\t')


batches, cur, words = [], [], 0
for u in units:
    w = len(u['en'].split())
    if cur and (words + w > 1800 or cur[-1]['ns'] != u['ns'] and words > 900):
        batches.append(cur)
        cur, words = [], 0
    cur.append(u)
    words += w
batches.append(cur)
for i, b in enumerate(batches):
    with open(os.path.join(WORK, f'book_{i:02d}.txt'), 'w', encoding='utf-8') as f:
        for u in b:
            f.write(f"{u['id']}\t{enc(u['en'])}\n")
    print(i, b[0]['ns'], len(b), sum(len(u['en'].split()) for u in b))
