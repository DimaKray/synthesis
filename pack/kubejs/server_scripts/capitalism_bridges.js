// ============================================================
//  Capitalism — мости між модами + баланс Infinity Ingot
//  Кожен ключовий верстат/компонент одного мода вимагає предмет з іншого.
//  Згенеровано з оригінальних рецептів модів (лише заміна інгредієнтів).
//  Після змін: /reload
// ============================================================

ServerEvents.recipes(event => {

  // ===== Ранній =====
  // Металургійний інфузор Mekanism стоїть на корпусі Create
  event.remove({ id: 'mekanism:metallurgic_infuser' })
  event.custom({"type": "minecraft:crafting_shaped", "category": "misc", "key": {"#": {"item": "create:andesite_casing"}, "I": {"tag": "c:ingots/iron"}, "O": {"tag": "c:ingots/osmium"}, "R": {"tag": "c:dusts/redstone"}}, "pattern": ["I#I", "ROR", "I#I"], "result": {"count": 1, "id": "mekanism:metallurgic_infuser"}}).id('capitalism:bridge/metallurgic_infuser')
  // Висікач AE2 тисне механічним пресом Create
  event.remove({ id: 'ae2:network/blocks/inscribers' })
  event.custom({"type": "minecraft:crafting_shaped", "category": "misc", "key": {"a": {"tag": "c:ingots/iron"}, "b": {"item": "create:mechanical_press"}, "c": {"tag": "c:ingots/copper"}}, "pattern": ["aba", "c a", "aba"], "result": {"count": 1, "id": "ae2:inscriber"}}).id('capitalism:bridge/network_blocks_inscribers')
  // Камера наповнення Ars Nouveau — з манасталі Ботанії
  event.remove({ id: 'ars_nouveau:imbuement_chamber' })
  event.custom({"type": "minecraft:crafting_shaped", "category": "misc", "key": {"x": {"item": "ars_nouveau:archwood_planks"}, "y": {"item": "botania:manasteel_ingot"}}, "pattern": ["xyx", "x x", "xyx"], "result": {"count": 1, "id": "ars_nouveau:imbuement_chamber"}}).id('capitalism:bridge/imbuement_chamber')
  // Стіл написання Iron's Spells — з самоцвітом джерела Ars
  event.remove({ id: 'irons_spellbooks:inscription_table' })
  event.custom({"type": "minecraft:crafting_shaped", "pattern": ["  B", "TTT", "L L"], "key": {"B": {"item": "ars_nouveau:source_gem"}, "T": {"tag": "minecraft:wooden_slabs"}, "L": {"tag": "minecraft:wooden_fences"}}, "result": {"id": "irons_spellbooks:inscription_table", "count": 1}}).id('capitalism:bridge/inscription_table')
  // Дистилятор Witchery — з латуні Create
  event.remove({ id: 'witchery:distillery' })
  event.custom({"type": "minecraft:crafting_shaped", "category": "misc", "key": {"A": {"item": "witchery:attuned_stone"}, "G": {"item": "minecraft:gold_ingot"}, "I": {"item": "create:brass_ingot"}, "J": {"item": "witchery:jar"}}, "pattern": ["JIJ", "III", "GAG"], "result": {"count": 1, "id": "witchery:distillery"}}).id('capitalism:bridge/distillery')
  // Прядка Witchery крутиться на шестернях Create
  event.remove({ id: 'witchery:spinning_wheel' })
  event.custom({"type": "minecraft:crafting_shaped", "category": "misc", "key": {"A": {"item": "witchery:attuned_stone"}, "H": {"item": "witchery:hawthorn_log"}, "I": {"item": "create:cogwheel"}, "P": {"item": "witchery:hawthorn_planks"}, "W": {"tag": "minecraft:wool"}}, "pattern": ["IIW", "IIH", "PAH"], "result": {"count": 1, "id": "witchery:spinning_wheel"}}).id('capitalism:bridge/spinning_wheel')

  // ===== Середній =====
  // Сталевий корпус Mekanism — з кварцового скла AE2 і Сплаву єдності I («Синтез»)
  event.remove({ id: 'mekanism:steel_casing' })
  event.custom({"type": "minecraft:crafting_shaped", "category": "misc", "key": {"G": {"item": "ae2:quartz_glass"}, "O": {"item": "synthesis:unity_alloy_1"}, "S": {"tag": "c:ingots/steel"}}, "pattern": ["SGS", "GOG", "SGS"], "result": {"count": 1, "id": "mekanism:steel_casing"}}).id('capitalism:bridge/steel_casing')
  // Контролер AE2 — на покращених схемах Mekanism і Сплаві єдності I («Синтез»)
  event.remove({ id: 'ae2:network/blocks/controller' })
  event.custom({"type": "minecraft:crafting_shaped", "category": "misc", "key": {"a": {"item": "ae2:smooth_sky_stone_block"}, "b": {"item": "mekanism:advanced_control_circuit"}, "c": {"item": "ae2:engineering_processor"}, "u": {"item": "synthesis:unity_alloy_1"}}, "pattern": ["aba", "bcb", "aua"], "result": {"count": 1, "id": "ae2:controller"}}).id('capitalism:bridge/network_blocks_controller')
  // Плита Terrasteel Ботанії — на блоці тауміуму
  event.remove({ id: 'botania:terra_plate' })
  event.custom({"type": "minecraft:crafting_shaped", "category": "misc", "key": {"0": {"item": "botania:rune_water"}, "1": {"item": "botania:rune_fire"}, "2": {"item": "botania:rune_earth"}, "3": {"item": "botania:rune_air"}, "8": {"item": "botania:rune_mana"}, "L": {"item": "tc4modern:thaumium_block"}, "M": {"tag": "botania:manasteel_blocks"}}, "pattern": ["LLL", "0M1", "283"], "result": {"count": 1, "id": "botania:terra_plate"}}).id('capitalism:bridge/terra_plate')
  // Пілон Гаї Ботанії — з духом Іншого світу (Witchery)
  event.remove({ id: 'botania:gaia_pylon' })
  event.custom({"type": "minecraft:crafting_shaped", "category": "misc", "key": {"D": {"item": "witchery:spirit_of_otherwhere"}, "E": {"tag": "botania:elementium_ingots"}, "P": {"item": "botania:mana_pylon"}}, "pattern": [" D ", "EPE", " D "], "result": {"count": 1, "id": "botania:gaia_pylon"}}).id('capitalism:bridge/gaia_pylon')
  // Драконієве ядро — з мана-діамантом і тауміумом
  event.remove({ id: 'draconicevolution:components/draconium_core' })
  event.custom({"type": "minecraft:crafting_shaped", "category": "misc", "key": {"A": {"tag": "c:ingots/draconium"}, "B": {"item": "tc4modern:thaumium_ingot"}, "C": {"item": "botania:mana_diamond"}}, "pattern": ["ABA", "BCB", "ABA"], "result": {"count": 1, "id": "draconicevolution:draconium_core"}}).id('capitalism:bridge/components_draconium_core')
  // Зачаровувальний апарат Ars — з урівноваженим уламком Таумкрафту
  event.remove({ id: 'ars_nouveau:enchanting_apparatus' })
  event.custom({"type": "minecraft:crafting_shaped", "category": "misc", "key": {"d": {"item": "tc4modern:balanced_shard"}, "g": {"tag": "c:ingots/gold"}, "n": {"tag": "c:nuggets/gold"}, "s": {"item": "ars_nouveau:sourcestone"}}, "pattern": ["nsn", "gdg", "nsn"], "result": {"count": 1, "id": "ars_nouveau:enchanting_apparatus"}}).id('capitalism:bridge/enchanting_apparatus')
  // Таємне ковадло Iron's Spells — з мана-діамантом Ботанії
  event.remove({ id: 'irons_spellbooks:arcane_anvil' })
  event.custom({"type": "minecraft:crafting_shaped", "pattern": ["AAA", " D ", "SVS"], "key": {"D": {"item": "botania:mana_diamond"}, "V": {"item": "minecraft:anvil"}, "S": {"item": "minecraft:polished_deepslate"}, "A": {"item": "minecraft:amethyst_block"}}, "result": {"id": "irons_spellbooks:arcane_anvil", "count": 1}}).id('capitalism:bridge/arcane_anvil')

  // ===== Пізній =====
  // Ядро Віверни — злиток зірки Незеру з Industrial Upgrade і Сплав єдності II («Синтез»)
  event.remove({ id: 'draconicevolution:components/wyvern_core' })
  event.custom({"type": "minecraft:crafting_shaped", "category": "misc", "key": {"A": {"tag": "c:ingots/draconium"}, "B": {"item": "draconicevolution:draconium_core"}, "C": {"item": "ic2:iridium"}, "U": {"item": "synthesis:unity_alloy_2"}}, "pattern": ["ABA", "BCB", "AUA"], "result": {"count": 1, "id": "draconicevolution:wyvern_core"}}).id('capitalism:bridge/components_wyvern_core')
  // Компонент 256k AE2 — з атомним сплавом Mekanism
  event.remove({ id: 'ae2:network/cells/item_storage_components_cell_256k_part' })
  event.custom({"type": "minecraft:crafting_shaped", "category": "misc", "key": {"a": {"item": "mekanism:alloy_atomic"}, "b": {"item": "ae2:calculation_processor"}, "c": {"item": "ae2:cell_component_64k"}, "d": {"item": "ae2:quartz_glass"}}, "pattern": ["aba", "cdc", "aca"], "result": {"count": 1, "id": "ae2:cell_component_256k"}}).id('capitalism:bridge/network_cells_item_storage_components_cell_256k_part')
  // Порт SPS (антиматерія) — з ядром Віверни Draconic і Сплавом єдності II («Синтез»)
  event.remove({ id: 'mekanism:sps_port' })
  event.custom({"type": "minecraft:crafting_shaped", "category": "misc", "key": {"#": {"item": "mekanism:sps_casing"}, "C": {"item": "draconicevolution:wyvern_core"}, "U": {"item": "synthesis:unity_alloy_2"}}, "pattern": ["U#U", "#C#", " # "], "result": {"count": 1, "id": "mekanism:sps_port"}}).id('capitalism:bridge/sps_port')

  // ===== Фінал =====
  // Нейтронний збирач — на елітних схемах Mekanism, флюїкс-блоках AE2 і Сплаві єдності III («Синтез») в центрі
  event.remove({ id: 'avaritia:neutron_collector' })
  event.custom({"type": "avaritia:shaped_table", "key": {"C": {"item": "avaritia:crystal_matrix_ingot"}, "I": {"item": "minecraft:iron_block"}, "Q": {"item": "ae2:fluix_block"}, "R": {"item": "mekanism:elite_control_circuit"}, "U": {"item": "synthesis:unity_alloy_3"}}, "pattern": ["IIQQQQQII", "I QQQQQ I", "I  RRR  I", "C RRRRR C", "I RRURR I", "C RRRRR C", "I  RRR  I", "I       I", "IIICICIII"], "result": {"count": 1, "id": "avaritia:neutron_collector"}, "tier": 4}).id('capitalism:bridge/neutron_collector')

  // ===== Пізній =====
  // Пробуджене ядро — +злиток порожнечі Таумкрафту і Сплав єдності II («Синтез»)
  event.remove({ id: 'draconicevolution:components/awakened_core' })
  event.custom({"type": "draconicevolution:fusion_crafting", "catalyst": {"tag": "c:nether_stars"}, "ingredients": [{"consume": true, "ingredient": {"item": "draconicevolution:wyvern_core"}}, {"consume": true, "ingredient": {"item": "draconicevolution:wyvern_core"}}, {"consume": true, "ingredient": {"tag": "c:ingots/draconium_awakened"}}, {"consume": true, "ingredient": {"tag": "c:ingots/draconium_awakened"}}, {"consume": true, "ingredient": {"tag": "c:ingots/draconium_awakened"}}, {"consume": true, "ingredient": {"tag": "c:ingots/draconium_awakened"}}, {"consume": true, "ingredient": {"item": "draconicevolution:wyvern_core"}}, {"consume": true, "ingredient": {"item": "draconicevolution:wyvern_core"}}, {"consume": true, "ingredient": {"item": "tc4modern:void_ingot"}}, {"consume": true, "ingredient": {"item": "synthesis:unity_alloy_2"}}], "result": {"count": 1, "id": "draconicevolution:awakened_core"}, "techLevel": "wyvern", "totalEnergy": 1000000}).id('capitalism:bridge/components_awakened_core')
  // Хаотичне ядро — +2 іхоріуми Таумтінкера, ігнітієвий злиток Cataclysm і Сплав єдності III («Синтез»)
  event.remove({ id: 'draconicevolution:components/chaotic_core' })
  event.custom({"type": "draconicevolution:fusion_crafting", "catalyst": {"item": "draconicevolution:large_chaos_frag"}, "ingredients": [{"consume": true, "ingredient": {"tag": "c:ingots/draconium_awakened"}}, {"consume": true, "ingredient": {"tag": "c:ingots/draconium_awakened"}}, {"consume": true, "ingredient": {"item": "draconicevolution:awakened_core"}}, {"consume": true, "ingredient": {"item": "draconicevolution:awakened_core"}}, {"consume": true, "ingredient": {"item": "draconicevolution:large_chaos_frag"}}, {"consume": true, "ingredient": {"item": "draconicevolution:large_chaos_frag"}}, {"consume": true, "ingredient": {"tag": "c:ingots/draconium_awakened"}}, {"consume": true, "ingredient": {"item": "draconicevolution:awakened_core"}}, {"consume": true, "ingredient": {"item": "draconicevolution:awakened_core"}}, {"consume": true, "ingredient": {"item": "draconicevolution:large_chaos_frag"}}, {"consume": true, "ingredient": {"item": "draconicevolution:large_chaos_frag"}}, {"consume": true, "ingredient": {"tag": "c:ingots/draconium_awakened"}}, {"consume": true, "ingredient": {"item": "ttinkerer:ichorium"}}, {"consume": true, "ingredient": {"item": "ttinkerer:ichorium"}}, {"consume": true, "ingredient": {"item": "cataclysm:ignitium_ingot"}}, {"consume": true, "ingredient": {"item": "synthesis:unity_alloy_3"}}], "result": {"count": 1, "id": "draconicevolution:chaotic_core"}, "techLevel": "draconic", "totalEnergy": 100000000}).id('capitalism:bridge/components_chaotic_core')

  // ===== Баланс =====
  // Злиток нескінченності — 1 каталізатор замість 6
  event.remove({ id: 'avaritia:infinity_ingot' })
  event.custom({"type": "avaritia:shaped_table", "key": {"C": {"item": "avaritia:crystal_matrix_ingot"}, "N": {"item": "avaritia:neutron_ingot"}, "X": {"item": "avaritia:infinity_catalyst"}}, "pattern": ["NNNNNNNNN", "NCCCCCCCN", "NCCCXCCCN", "NCCCCCCCN", "NNNNNNNNN"], "result": {"count": 1, "id": "avaritia:infinity_ingot"}, "tier": 4}).id('capitalism:bridge/infinity_ingot')

  console.info('[Capitalism] Мости між модами: 20 рецептів (7 із них — зі Сплавами єдності «Синтезу»)')
})
