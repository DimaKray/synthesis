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
**Етап 1 (скелет) — код готовий, ще не запушений.** Є: вкладка «Синтез», Сплави єдності I–III, Скіпетр синтезу (поки без функцій), текстури, lang (en_us, uk_ua, ru_ru = українською — гра Дмитра російською локаллю).

### ПЕРШЕ, що треба зробити
1. Створити `.github/workflows/build.yml` (текст нижче) — попередня сесія не могла записати папку `.github`.
2. `git init -b main`, закомітити все, `git remote add origin https://github.com/DimaKray/synthesis.git`, запушити.
3. Дочекатися GitHub Actions; jar з'явиться в Releases (`build-N`).
4. Скопіювати jar у mods профілю Capitalism (шлях нижче) і попросити Дмитра перевірити вкладку «Синтез» у креативі.

```yaml
name: Build

on:
  push:
    branches: [ main ]
  workflow_dispatch:

permissions:
  contents: write

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Java 21
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 21

      - name: Gradle
        uses: gradle/actions/setup-gradle@v4

      - name: Build
        run: |
          chmod +x ./gradlew
          ./gradlew build --no-daemon

      - name: Upload jar
        uses: actions/upload-artifact@v4
        with:
          name: synthesis-jar
          path: build/libs/*.jar

      - name: Release
        uses: softprops/action-gh-release@v2
        with:
          tag_name: build-${{ github.run_number }}
          name: Build ${{ github.run_number }}
          body: "Автоматична збірка з коміту ${{ github.sha }}"
          files: build/libs/*.jar
          make_latest: true
```

Якщо на ПК Дмитра встановлено JDK 21 — можна збирати й локально (`gradlew build`), це швидше за Actions. Якщо ні — збираємо через Actions (Дмитро не хоче нічого встановлювати без потреби).

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
