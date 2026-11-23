from qb import Q, Lane, Chapter

CHAPTER = Chapter(
    'capitalism_s6_astral', 'Astral Sorcery',
    'Магія зірок і сузір\'їв (альфа-порт — можливі недоробки)',
    'astralsorcery:tome', tier=2, group='side', order=15, lanes=[
        Lane('Зоряне світло', [
            Q('as_crystal', 'astralsorcery:rock_crystal', 'Гірський кришталь', [
                'Руда гірського кришталю — глибоко під землею. Для довідника знадобиться ще &6аквамарин&r (у піску на берегах).',
                'Мод — альфа-порт на 1.21.1: якщо щось не працює, це не твоя помилка.',
            ], deps=['welcome']),
            Q('as_tome', 'astralsorcery:tome', 'Астральний том', [
                'Довідник мода (книга + аквамарин, див. JEI). Розділи відкриваються поступово — читай по черзі, він веде крок за кроком.',
            ], deps=['as_crystal']),
            Q('as_wand', 'astralsorcery:wand', 'Резонуючий жезл', [
                'Показує, де зоряного світла найбільше, і запускає крафти на вівтарях.',
            ], deps=['as_tome']),
            Q('as_altar1', 'astralsorcery:altar_luminance', 'Світний крафтовий вівтар', [
                'Перший вівтар. Крафтить уночі під відкритим небом, живиться зоряним світлом.',
            ], deps=['as_wand']),
            Q('as_starmetal', 'astralsorcery:starmetal_ingot', 'Зоряний метал', [
                'Залізна руда перетворюється на руду зоряного металу під променем зоряного світла (як саме — у томі). Основа подальших вівтарів.',
            ], deps=['as_altar1']),
            Q('as_altar2', 'astralsorcery:altar_illumination', 'Освітлений вівтар', [
                'Другий вівтар — більші рецепти й лінзи.',
            ], deps=['as_starmetal']),
            Q('as_attune', 'astralsorcery:attunement_altar', 'Вівтар налаштування', [
                'Тут налаштовуються на сузір\'я — і ти, і кристали. Налаштування гравця відкриває дерево перків.',
            ], deps=['as_altar2']),
            Q('as_altar3', 'astralsorcery:altar_radiance', 'Сяйний вівтар', [
                'Найвищий вівтар Astral Sorcery.',
            ], deps=['as_attune'], reward='none'),
        ]),
        Lane('Applied AstralSorcery', [
            Q('aas_cell', 'appliedas:lumen_storage_cell_1k', 'Сховище люмену в ME', [
                'Applied AstralSorcery зберігає люмен у мережі AE2 і автоматизує вівтарі. Довідник — у посібнику AE2 (перекладений).',
            ], deps=['as_altar1', 'ae_drive']),
            Q('aas_altar', 'appliedas:altar_automation_interface', 'Автоматизація вівтаря', [
                'Постачальник шаблонів AE2 сам кладе інгредієнти й запускає вівтар.',
            ], deps=['aas_cell', 'ae_autocraft'], reward='none'),
        ]),
    ])
