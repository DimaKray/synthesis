# CLAUDE.md — Capitalism: Синтез

Контекст для Claude Code. Відповідай Дмитру **українською**, простою мовою (англійська в нього слабка). Дмитро — веб-розробник (HTML/CSS/JS, вчить React/Node), Java знає слабо: код пишеш ти, пояснюєш коротко.

## Що це
Власний NeoForge-мод Дмитра для його збірки **Capitalism** (Minecraft 1.21.1). Машини й вівтарі, що **споживають «валюти» різних модів одночасно** (мана Ботанії, джерело Ars Nouveau, енергія FE, обертання Create, рідини) і роблять компоненти, яких більше ніде не отримати. Повний дизайн: `docs/DESIGN.md`.

- mod id `synthesis`, пакет `ua.krainov.synthesis`, назва «Capitalism: Синтез» (англ. «Capitalism: Synthesis»)
- NeoForge **21.1.267**, Minecraft 1.21.1, Java 21, ModDevGradle 2.0.141, Gradle 8.14.3 (wrapper у репо)
- Репозиторій: **github.com/DimaKray/synthesis** (публічний, гілка `main`)
- Ліцензія MIT

## Прийняті рішення
- Текстури 16×16 піксель-арт у ванільному стилі (генеруємо скриптом/вручну)
- Ритуали запускає **Скіпетр синтезу** (без редстоуну)
- Конвертера валют НЕ робимо; ману з гравця (Iron's Spells) НЕ беремо
- Кожна інтеграція опційна (`ModList.isLoaded`), мод не падає без конкретного мода
- Логіку без Minecraft тримати в `core/` (для майбутнього переносу на 26.x), інтеграції в `compat/`

## Стан
**Етап 1 (скелет) — готовий. Етап 2 (П'єдестал) — код запушено (0.2.0), чекає перевірки в грі.** Є: П'єдестал синтезу (`block/PedestalBlock`, `blockentity/PedestalBlockEntity`, рендерер `client/PedestalRenderer`, інвентар відкритий для хоперів/Create через capability; текстури генерує `tools/gen_pedestal_textures.py`), вкладка «Синтез», Сплави єдності I–III, Скіпетр синтезу (поки без функцій), текстури, lang (en_us, uk_ua, ru_ru = українською — гра Дмитра російською локаллю).

CI: `.github/workflows/build.yml` — кожен пуш у `main` збирає jar і викладає в Releases (`build-N`). `gh` на ПК немає: статус перевіряти через `curl https://api.github.com/repos/DimaKray/synthesis/actions/runs?per_page=1`, jar брати з `releases/latest`. На ПК стоїть Java 23 (не 21); локально не збираємо.

## Етапи
| # | Етап | Результат |
|---|---|---|
| 1 | Скелет + CI | мод запускається, предмети, вкладка |
| 2 | П'єдестал | ставиться, тримає й показує предмет (BlockEntity + рендерер) |
| 3 | Вівтар Злиття (предмети) | мультиблок, Скіпетр, рецепт `synthesis:fusion` з JSON, частинки, JEI |
| 4 | Живлення вівтаря | мана Ботанії, джерело Ars, FE |
| 5 | Синтезатор | GUI, рідина, FE, обертання Create |
| 6 | Вівтар Капіталізму | мультиблок 7×7, 13 п'єдесталів, крафт Infinity Catalyst |
| 7 | Інтеграція у збірку | рецепти, мости KubeJS, розділ квестів «Синтез» |
| 8 | Перенос на 26.x | — |

## API інтеграцій (перевірено в jar-файлах збірки)
- **Botania Revived** (неофіційний порт!): пакет `velrondevs.botania.api` (НЕ `vazkii`), `velrondevs.botania.api.mana.ManaReceiver`, `velrondevs.botania.api.BotaniaCapabilities`. Підключати як compileOnly з локального jar (`libs/`), бо в Maven його немає.
- **Ars Nouveau**: `com.hollingsworth.arsnouveau.api.util.SourceUtil`, `api.source.ISourceTile`, `SourceManager`
- **Create 6.0.10**: `com.simibubi.create.content.kinetics.base.KineticBlockEntity`
- **JEI** 19.x (`mezz.jei.api.IModPlugin`)
- Таумкрафт (неофіційний порт `tc4modern`) — API невідомий, лише його предмети як інгредієнти

## Збірка Capitalism (середовище тестування)
- Профіль CurseForge: `C:\Users\dimak\curseforge\minecraft\Instances\Capitalism` (mods, logs, crash-reports, kubejs, config)
- ~70 модів: Create, Mekanism, AE2, Industrial Upgrade, Draconic Evolution + Retro Draconic, Botania Revived, Thaumcraft 4 port (`tc4modern`) + Thaumic Tinkerer port (`ttinkerer`), Just Another Witchery Remake, Ars Nouveau, Iron's Spells, Re:Avaritia, Twilight Forest, L_Ender's Cataclysm, Apotheosis, FTB Quests/Teams/Chunks, KubeJS
- ⚠ **Curios закріплено на 9.5.1** — 9.6.0 ламає Witchery. Не оновлювати.
- KubeJS-скрипти збірки: `kubejs/server_scripts/capitalism_endgame.js` (Infinity Catalyst вимагає 13 ключів), `capitalism_creative.js`, `capitalism_bridges.js` (мости між модами)
- Квести FTB: `config/ftbquests/quests/` (293 квести, генеровані). ID квестів ≤ 7FFFFFFFFFFFFFFF, тексти в `lang/en_us.snbt`
- ID предметів Industrial Upgrade мають вигляд `industrialupgrade:simplemachine/macerator_iu` — модель у jar не гарантує, що предмет зареєстрований (`industrialupgrade:infinitycore` НЕ існує)
- При краші: читати `crash-reports/` і `logs/latest.log`, `logs/kubejs/server.log`

## Стиль роботи з Дмитром
- Діяти, а не розпитувати по дрібницях; питати лише про справжні розвилки
- Після змін — коротко що зроблено і що йому перевірити в грі
- Коміти з поясненням українською
