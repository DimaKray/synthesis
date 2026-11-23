"""Конструктор квестів FTB для збірки Capitalism.
Глави описуються в tools/quests/chapters/*.py (див. Q, Lane, Chapter), а build.py пише їх у config/ftbquests.

Розкладка автоматична: у кожній «доріжці» (мод/тема) квести стоять у колонках за глибиною залежностей,
доріжки — одна під одною. Ворота (Сплави єдності тощо) — великі шестикутники в кінці глави."""


class Q:
    def __init__(self, key, item, title, desc, deps=(), count=1, reward=None, kind='normal', items=None, sub=None,
                 check=False):
        self.key = key          # унікальний ключ у межах усіх глав
        self.item = item        # головний предмет (іконка й завдання)
        self.items = items      # кілька предметів-завдань (усі потрібні)
        self.title = title
        self.desc = desc if isinstance(desc, list) else [desc]
        self.deps = list(deps)
        self.count = count
        self.reward = reward    # None = за рівнем глави
        self.kind = kind        # normal | gate | info | key
        self.sub = sub          # короткий підзаголовок
        self.check = check      # завдання-галочка замість предмета


class Lane:
    def __init__(self, name, quests):
        self.name = name
        self.quests = quests


class Chapter:
    def __init__(self, file, title, subtitle, icon, lanes, tier, group='main', order=0):
        self.file = file
        self.title = title
        self.subtitle = subtitle if isinstance(subtitle, list) else [subtitle]
        self.icon = icon
        self.lanes = lanes
        self.tier = tier        # 0..5 — від нього залежать нагороди
        self.group = group
        self.order = order
