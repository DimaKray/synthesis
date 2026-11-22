"""Переклад назв Lightman's Currency і Jackpot (додано 22.11.2026). Однотипні варіанти (дерева, кольори, монети)
збираються з таблиць, решта — вручну. Дописує в work/names_extra.txt лише рядки, яких там ще немає.
Запуск: python tools/ua_pack/gen_econ_names.py"""
import os

HERE = os.path.dirname(os.path.abspath(__file__))
OUT = os.path.join(HERE, 'work', 'names_extra.txt')

# дерево: (родовий відмінок для «з …», прикметник ч.р.)
WOODS = {
    'Oak': 'дуба', 'Spruce': 'ялини', 'Birch': 'берези', 'Jungle': 'тропічного дерева', 'Acacia': 'акації',
    'Dark Oak': 'темного дуба', 'Mangrove': 'мангрової', 'Cherry': 'вишні', 'Bamboo': 'бамбука',
    'Crimson': 'багряного дерева', 'Warped': 'спотвореного дерева', 'Fir': 'ялиці', 'Redwood': 'секвої',
    'Mahogany': 'червоного дерева', 'Jacaranda': 'жакаранди', 'Palm': 'пальми', 'Willow': 'верби',
    'Dead': 'мертвого дерева', 'Magic': 'магічного дерева', 'Umbran': 'умбрану', 'Hellbark': 'пекельної кори',
}
WOOD_ITEMS = {
    'Auction Stand': 'Аукціонна стійка з {}', 'Bookshelf Trader': 'Торговець-книжкова полиця з {}',
    'Card Display': 'Вітрина карток з {}', 'Double Shelf': 'Подвійна полиця з {}', 'Shelf': 'Полиця з {}',
}
# колір: (ч.р., ж.р., с.р.)
COLORS = {
    'White': ('Білий', 'Біла', 'Біле'), 'Light Gray': ('Світло-сірий', 'Світло-сіра', 'Світло-сіре'),
    'Gray': ('Сірий', 'Сіра', 'Сіре'), 'Black': ('Чорний', 'Чорна', 'Чорне'),
    'Brown': ('Коричневий', 'Коричнева', 'Коричневе'), 'Red': ('Червоний', 'Червона', 'Червоне'),
    'Orange': ('Помаранчевий', 'Помаранчева', 'Помаранчеве'), 'Yellow': ('Жовтий', 'Жовта', 'Жовте'),
    'Lime': ('Лаймовий', 'Лаймова', 'Лаймове'), 'Green': ('Зелений', 'Зелена', 'Зелене'),
    'Cyan': ('Бірюзовий', 'Бірюзова', 'Бірюзове'), 'Light Blue': ('Блакитний', 'Блакитна', 'Блакитне'),
    'Blue': ('Синій', 'Синя', 'Синє'), 'Purple': ('Фіолетовий', 'Фіолетова', 'Фіолетове'),
    'Magenta': ('Пурпуровий', 'Пурпурова', 'Пурпурове'), 'Pink': ('Рожевий', 'Рожева', 'Рожеве'),
}
# предмет кольору: (рід 0/1/2, назва, множина?)
COLOR_ITEMS = {
    'Freezer': (2, 'морозильник'), 'Gacha Machine': (0, 'гача-автомат'), 'Vending Machine': (0, 'торговий автомат'),
    'Casino Bit': (0, 'біт казино'), 'Casino Block': (0, 'блок казино'), 'Casino Bricks': (1, 'цегла казино'),
    'Casino Cabochon Pillar': (1, 'колона казино з кабошоном'), 'Casino Chip': (1, 'фішка казино'),
    'Casino Door': (3, 'двері казино'), 'Casino Slab': (1, 'плита казино'), 'Casino Tiles': (1, 'плитка казино'),
    'Casino Trapdoor': (0, 'люк казино'), 'Casino Wall': (1, 'стіна казино'), 'Dart': (0, 'дротик'),
    'Pool Table': (0, 'більярдний стіл'), 'Slot Machine': (0, 'ігровий автомат'), 'Stool': (0, 'табурет'),
}
# монети: (прикметник ж.р. однина, множина, род. мн. для «блок/купка …»)
COINS = {
    'Copper': ('Мідна', 'Мідні', 'мідних'), 'Iron': ('Залізна', 'Залізні', 'залізних'),
    'Gold': ('Золота', 'Золоті', 'золотих'), 'Emerald': ('Смарагдова', 'Смарагдові', 'смарагдових'),
    'Diamond': ('Алмазна', 'Алмазні', 'алмазних'), 'Netherite': ('Незеритова', 'Незеритові', 'незеритових'),
    'Lapis': ('Лазуритова', 'Лазуритові', 'лазуритових'), 'Ender Pearl': ('Ендерова', 'Ендерові', 'ендерових'),
}
MANUAL = {
    '%ss': '%ss', 'c': 'c', 'd': 'd', 'e': 'e', 'g': 'g', 'i': 'i', 'n': 'n',
    'ATM': 'Банкомат', 'ATM Card': 'Банківська картка', 'Access Portable ATM': 'Відкрити портативний банкомат',
    'Access Portable Terminal': 'Відкрити портативний термінал', 'Armor Display': 'Вітрина броні',
    'Avarice Button': 'Кнопка з дерева жадібності', 'Avarice Door': 'Двері з дерева жадібності',
    'Avarice Fence': 'Паркан з дерева жадібності', 'Avarice Fence Gate': 'Хвіртка з дерева жадібності',
    'Avarice Hanging Sign': 'Підвісна табличка з дерева жадібності', 'Avarice Log': 'Колода дерева жадібності',
    'Avarice Planks': 'Дошки з дерева жадібності', 'Avarice Pressure Plate': 'Натискна плита з дерева жадібності',
    'Avarice Sign': 'Табличка з дерева жадібності', 'Avarice Slab': 'Плита з дерева жадібності',
    'Avarice Stairs': 'Сходи з дерева жадібності', 'Avarice Trapdoor': 'Люк з дерева жадібності',
    'Bank Upgrade': 'Банківське покращення', 'Basic Wallet': 'Простий гаманець', 'Card Deck': 'Колода карт',
    'Card Table': 'Картковий стіл', 'Cash Register': 'Касовий апарат', 'Casino Chip': 'Фішка казино',
    'Coin Exchange Upgrade': 'Покращення обміну монет', 'Coin Jar': 'Банка для монет', 'Coin Magnet': 'Магніт для монет',
    'Coin Minting Machine': 'Монетний двір', 'Coins & Items': 'Монети й предмети', 'Command Trader': 'Торговець командами',
    'Contract Allay': 'Контракт: тихоня', 'Contract Egg': 'Контракт: яйце', 'Contract Horse': 'Контракт: кінь',
    'Coupon': 'Купон', 'Cue Stick': 'Більярдний кий', 'Curse of Misfire': 'Прокляття осічки',
    'Curse of Roulette': 'Прокляття рулетки', 'Custom Item Trade Filter': 'Власний фільтр торгівлі предметами',
    'Dart Board': 'Дартс', 'Dealer': 'Дилер', 'Dealer Key': 'Ключ дилера', 'Dealer Spawn Egg': 'Яйце виклику дилера',
    'Display Case': 'Вітрина', 'Extra Machines': 'Додаткові автомати', 'Gacha Ball': 'Гача-кулька',
    'Golden Master Ticket': 'Золотий майстер-квиток', 'Golden Pass': 'Золотий перепустка',
    'Golden Ticket': 'Золотий квиток', 'Golden Ticket Stub': 'Корінець золотого квитка', 'High Roller': 'Великий гравець',
    'Hopper Upgrade': 'Покращення лійки', 'Interaction Upgrade (Diamond)': 'Покращення взаємодії (алмаз)',
    'Interaction Upgrade (Emerald)': 'Покращення взаємодії (смарагд)',
    'Interaction Upgrade (Netherite)': 'Покращення взаємодії (незерит)',
    'Item Capacity Upgrade (Diamond)': 'Покращення місткості (алмаз)',
    'Item Capacity Upgrade (Gold)': 'Покращення місткості (золото)',
    'Item Capacity Upgrade (Iron)': 'Покращення місткості (залізо)',
    'Item Capacity Upgrade (Netherite)': 'Покращення місткості (незерит)',
    'Item Network Trader T1': 'Мережевий торговець предметами T1', 'Item Network Trader T2': 'Мережевий торговець предметами T2',
    'Item Network Trader T3': 'Мережевий торговець предметами T3', 'Item Network Trader T4': 'Мережевий торговець предметами T4',
    'Item Trader Interface': 'Інтерфейс торговця предметами', 'Jar of Sus': 'Підозріла банка', 'Life Chip': 'Фішка життя',
    'Lucky Ticket': 'Щасливий квиток', 'Machine Upgrades': 'Покращення автоматів', 'Magnet Upgrade (Copper)': 'Покращення-магніт (мідь)',
    'Magnet Upgrade (Emerald)': 'Покращення-магніт (смарагд)', 'Magnet Upgrade (Gold)': 'Покращення-магніт (золото)',
    'Magnet Upgrade (Iron)': 'Покращення-магніт (залізо)', 'Master Ticket': 'Майстер-квиток', 'Misc Machines': 'Інші автомати',
    'Money Bag': 'Мішок грошей', 'Money Chest': 'Скриня для грошей', 'Money Mending': 'Грошове лагодження',
    'Mulligan Ticket': 'Квиток повтору', 'Network Upgrade': 'Мережеве покращення', 'Open Wallet': 'Відкрити гаманець',
    'Pass': 'Перепустка', 'Paygate': 'Платна брама', 'Piggy Bank': 'Скарбничка', 'Plinko': 'Плінко',
    'Prepaid Card': 'Передплачена картка', 'Reroll': 'Переграш', 'Root Of All Evil': 'Корінь усього зла',
    'Security Upgrade': 'Покращення безпеки', 'Slot Machine': 'Ігровий автомат',
    'Speed Upgrade (Diamond)': 'Покращення швидкості (алмаз)', 'Speed Upgrade (Emerald)': 'Покращення швидкості (смарагд)',
    'Speed Upgrade (Gold)': 'Покращення швидкості (золото)', 'Speed Upgrade (Iron)': 'Покращення швидкості (залізо)',
    'Speed Upgrade (Netherite)': 'Покращення швидкості (незерит)', 'Stogie': 'Сигара', 'Stogie Box': 'Коробка сигар',
    'Tax Collector': 'Збирач податків', 'Ticket': 'Квиток', 'Ticket Kiosk': 'Кіоск квитків', 'Ticket Station': 'Станція квитків',
    'Ticket Stub': 'Корінець квитка', 'Top Streak': 'Найкраща серія',
    'Trade Offer Upgrade (Copper)': 'Покращення пропозицій (мідь)', 'Trade Offer Upgrade (Diamond)': 'Покращення пропозицій (алмаз)',
    'Trade Offer Upgrade (Emerald)': 'Покращення пропозицій (смарагд)', 'Trade Offer Upgrade (Gold)': 'Покращення пропозицій (золото)',
    'Trade Offer Upgrade (Iron)': 'Покращення пропозицій (залізо)', 'Trade Offer Upgrade (Netherite)': 'Покращення пропозицій (незерит)',
    'Trading Core': 'Торгове ядро', 'Trading Machines': 'Торгові автомати', 'Trading Terminal': 'Торговий термінал',
    'Transaction Register': 'Реєстр транзакцій', 'Variant Wand': 'Жезл варіантів', 'Void Upgrade': 'Покращення знищення',
    'Wallet (Copper)': 'Гаманець (мідь)', 'Wallet (Diamond)': 'Гаманець (алмаз)', 'Wallet (Emerald)': 'Гаманець (смарагд)',
    'Wallet (Ender Dragon)': 'Гаманець (Дракон Енду)', 'Wallet (Gold)': 'Гаманець (золото)', 'Wallet (Iron)': 'Гаманець (залізо)',
    'Wallet (Nether Star)': 'Гаманець (зірка Незеру)', 'Wallet (Netherite)': 'Гаманець (незерит)',
}
MANUAL['Golden Pass'] = 'Золота перепустка'

tr = dict(MANUAL)
for w, gen in WOODS.items():
    for item, pat in WOOD_ITEMS.items():
        tr[f'{w} {item}'] = pat.format(gen)
for c, forms in COLORS.items():
    for item, (g, noun) in COLOR_ITEMS.items():
        adj = {0: forms[0], 1: forms[1], 2: forms[0], 3: forms[0][:-2] + 'і' if forms[0].endswith('ий') else forms[0][:-1] + 'і'}[g]
        tr[f'{c} {item}'] = f'{adj} {noun}'
    tr[f'Large {c} Vending Machine'] = f'Великий {forms[0].lower()} торговий автомат'
for c, (f1, plur, gen) in COINS.items():
    for pref, upref in (('', ''), ('Chocolate ', 'Шоколадна ')):
        base = f'{f1.lower()} монета' if upref else f'{f1} монета'
        tr[f'{pref}{c} Coin'] = (upref + base) if upref else base
        tr[f'{pref}{c} Coins'] = ('Шоколадні ' + plur.lower() + ' монети') if upref else f'{plur} монети'
        tr[f'{pref}{c} Coinpile'] = ('Шоколадна купка ' if upref else 'Купка ') + f'{gen} монет'
        tr[f'{pref}{c} Coinpiles'] = ('Шоколадні купки ' if upref else 'Купки ') + f'{gen} монет'
        tr[f'{pref}Block of {c} Coins'] = ('Шоколадний блок ' if upref else 'Блок ') + f'{gen} монет'
        tr[f'{pref}Blocks of {c} Coins'] = ('Шоколадні блоки ' if upref else 'Блоки ') + f'{gen} монет'
    tr[f'Ancient {c} Coin'] = f'Давня {f1.lower()} монета'

have = set()
if os.path.exists(OUT):
    have = {l.split('\t', 1)[0] for l in open(OUT, encoding='utf-8') if l.strip()}
need = os.path.join(HERE, 'work', 'new_names.txt')
wanted = [l.rstrip('\n') for l in open(need, encoding='utf-8')] if os.path.exists(need) else list(tr)
added, miss = 0, []
with open(OUT, 'a', encoding='utf-8') as f:
    for en in wanted:
        if en in have:
            continue
        if en in tr:
            f.write(f'{en}\t{tr[en]}\n')
            added += 1
        else:
            miss.append(en)
print('додано:', added, '| без перекладу:', miss)
