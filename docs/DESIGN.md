# Capitalism: Синтез — дизайн-документ мода

Власний мод Дмитра для збірки Capitalism. Машини й вівтарі, які **споживають «валюти» різних модів одночасно** — ману Ботанії, джерело Ars Nouveau, енергію FE, обертання Create, рідини — і роблять компоненти, яких не отримати ніде більше. Мета: зробити з набору модів одну систему.

- **Назва:** Capitalism: Синтез
- **mod id:** `synthesis`
- **Пакет:** `ua.krainov.synthesis`
- **Платформа:** Minecraft 1.21.1, NeoForge 21.1.267, Java 21 (потім перенос на 26.x)
- **Ліцензія:** MIT

## Прийняті рішення
- Текстури генеруємо в стилі збірки (16×16 піксель-арт, ванільна палітра)
- Ритуали запускаються **Скіпетром синтезу** (без редстоуну)
- **Конвертера валют не робимо** — щоб не ламати баланс модів
- **Ману з гравця (Iron's Spells) не беремо**

---

## 1. «Валюти» модів

| Валюта | Джерело в збірці | Як підключаємо | Статус |
|---|---|---|---|
| Предмети | усі моди | стандартні інгредієнти | ✅ легко |
| Рідини | Create, Mekanism, IU | Fluid capability NeoForge | ✅ легко |
| Енергія FE | Mekanism, Draconic, IU | Energy capability NeoForge | ✅ легко |
| Мана | Botania Revived | `velrondevs.botania.api.mana.ManaReceiver`, `BotaniaCapabilities` | ✅ API є в jar |
| Джерело (Source) | Ars Nouveau | `com.hollingsworth.arsnouveau.api.util.SourceUtil`, `ISourceTile` | ✅ API є |
| Обертання | Create | `com.simibubi.create.content.kinetics.base.KineticBlockEntity` | ✅ є, середня складність |
| Таумкрафт | Thaumcraft PORT | API невідомий | ⚠ лише предмети Таумкрафту як інгредієнти |

**Принцип:** кожна інтеграція опційна. Якщо мод відсутній, машина не вимагає цієї валюти (перевірка через `ModList.isLoaded`).

---

## 2. Блоки й машини

### 2.1 П'єдестал синтезу (`synthesis:pedestal`)
- Тримає один предмет, показує його над собою (обертається, левітує).
- ПКМ кладе або забирає предмет. Сумісний з хопером і трубами Create.

### 2.2 Вівтар Злиття (`synthesis:fusion_altar`)
**Мультиблок:** вівтар у центрі та 4–8 п'єдесталів у радіусі 3 блоків.
1. У вівтар кладеш центральний предмет, на п'єдестали інгредієнти.
2. ПКМ **Скіпетром синтезу** запускає ритуал (Shift+ПКМ — показати, чого бракує).
3. Вівтар тягне ресурси: **ману** з мана-розповсюджувачів (вівтар є `ManaReceiver`), **джерело** з банок Ars поряд (`SourceUtil`), **FE** через кабель.
4. Частинки летять від п'єдесталів до центру, звук наростає.
5. Готово: предмети з п'єдесталів зникають, у центрі результат.
6. Якщо ресурси закінчилися — прогрес зупиняється, але не губиться.

### 2.3 Техно-магічний синтезатор (`synthesis:synthesizer`)
- Інтерфейс: 3 вхідні слоти, 1 вихід, бак рідини, буфер FE.
- Блок кінетичний (вал Create збоку): мінімальні RPM і навантаження (stress).
- Швидкість крафту залежить від RPM.

### 2.4 Вівтар Капіталізму (`synthesis:capital_altar`) — фінал
**Мультиблок 7×7:** центр, 13 п'єдесталів для 13 ключів фіналу, порти Мана / Джерело / Енергія / Обертання / Рідина.
**Крафтить Infinity Catalyst** (рецепт Avaritia прибираємо через KubeJS). Промені від 13 п'єдесталів сходяться в центр.

13 ключів (вже вимагаються в `capitalism_endgame.js`): draconicevolution:chaotic_core, botania:gaia_ingot, tc4modern:primordial_pearl, ttinkerer:ichorium, mekanism:pellet_antimatter, witchery:infernal_animus, cataclysm:cursium_ingot, industrialupgrade:neutroniumingot, twilightforest:fiery_ingot, ars_nouveau:wilden_tribute, irons_spellbooks:legendary_ink, create:precision_mechanism, ae2:quantum_entangled_singularity.

### 2.5 Предмети
| Предмет | Опис | Де робиться |
|---|---|---|
| Скіпетр синтезу | запускає ритуали, показує стан мультиблока | верстак |
| Сплав єдності I | манасталь + латунь + тауміум + осмій | Вівтар Злиття, мана |
| Сплав єдності II | терасталь + сталь Mekanism + злиток порожнечі + самоцвіт джерела | Вівтар Злиття, мана + джерело |
| Сплав єдності III | злиток Гаї + атомний сплав + іхоріум + ігнітій | Вівтар Злиття, мана + джерело + FE |
| Ядро синтезу | компонент машин | Синтезатор |
| Серце Капіталізму | трофей фіналу | Вівтар Капіталізму |

---

## 3. Рецепти (JSON у датапаках)
`data/synthesis/recipe/*.json`, правляться через KubeJS без перекомпіляції.

```json
{
  "type": "synthesis:fusion",
  "center": { "item": "create:brass_ingot" },
  "pedestals": [
    { "item": "botania:manasteel_ingot" },
    { "item": "tc4modern:thaumium_ingot" },
    { "item": "mekanism:ingot_osmium" }
  ],
  "mana": 50000, "source": 0, "energy": 0, "time": 200,
  "result": { "id": "synthesis:unity_alloy_1", "count": 1 }
}
```
```json
{
  "type": "synthesis:synthesizing",
  "ingredients": [ { "item": "synthesis:unity_alloy_1" }, { "item": "ae2:fluix_crystal" } ],
  "fluid": { "fluid": "minecraft:lava", "amount": 1000 },
  "energy": 200000, "min_rpm": 64, "stress": 512, "time": 300,
  "result": { "id": "synthesis:synthesis_core", "count": 1 }
}
```
`synthesis:capital` — як fusion + `rotation_rpm`, `fluid`. JEI: окрема категорія для кожного типу.

---

## 4. Місце в прогресії Capitalism
- **Сплав єдності I**: замінює частину мостів (сталевий корпус Mekanism, контролер AE2).
- **Сплав єдності II**: Ядро Віверни, Пробуджене ядро, порт SPS.
- **Сплав єдності III**: Хаотичне ядро, нейтронний збирач.
- **Вівтар Капіталізму**: єдиний спосіб зробити Infinity Catalyst.
- Квести: новий розділ «7. Синтез».

---

## 5. Технічна архітектура

**Середовище:** NeoForge 21.1.267, ModDevGradle 2.0.141, Java 21, Gradle 8.14.3.

**Залежності (compileOnly):** Botania Revived (API `velrondevs.botania.api`, локальний jar у `libs/` — скопіювати з mods профілю Capitalism), Ars Nouveau, Create, JEI API.

**Пакети:**
```
ua.krainov.synthesis
 ├─ SynthesisMod            (точка входу)
 ├─ registry/               (блоки, предмети, BlockEntity, рецепти, меню, вкладка)
 ├─ block/ + blockentity/
 ├─ recipe/
 ├─ multiblock/
 ├─ compat/                 (botania/, ars/, create/, jei/ — тільки якщо мод завантажений)
 ├─ core/                   (ЛОГІКА БЕЗ MINECRAFT — для легкого переносу)
 └─ client/                 (рендерери, частинки, інтерфейси)
```

**Робочий процес:** Claude Code на ПК Дмитра пише код і пушить у `DimaKray/synthesis`; GitHub Actions збирає jar і публікує в Releases (або локально `gradlew build`, якщо є JDK 21); jar копіюється в mods профілю Capitalism; Дмитро тестує в грі.

---

## 6. Етапи

| # | Етап | Результат | Складність |
|---|---|---|---|
| 1 | Скелет + CI | мод запускається; Сплави єдності, Скіпетр; вкладка в креативі | ⭐ |
| 2 | П'єдестал | ставиться, тримає й показує предмет | ⭐⭐ |
| 3 | Вівтар Злиття (тільки предмети) | мультиблок, Скіпетр, рецепт з JSON, частинки, JEI | ⭐⭐⭐ |
| 4 | Живлення вівтаря | мана Ботанії, джерело Ars, FE | ⭐⭐⭐ |
| 5 | Синтезатор | інтерфейс, рідина, FE, обертання Create | ⭐⭐⭐⭐ |
| 6 | Вівтар Капіталізму | великий мультиблок, Infinity Catalyst, ефекти | ⭐⭐⭐⭐ |
| 7 | Інтеграція у збірку | рецепти, мости, розділ квестів «Синтез» | ⭐⭐ |
| 8 | Перенос на 26.x | — | ⭐⭐⭐ |
