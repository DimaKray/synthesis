// ============================================================
//  Capitalism — фінальні крафти ендгейму
//  Infinity Catalyst вимагає ключові ресурси з УСІХ модів і робиться
//  лише на Вівтарі Капіталізму (мод «Синтез»). Список ключів нижче — для довідки
//  (той самий список у рецепті вівтаря).
//  Після змін: у грі виконай /reload
// ============================================================

// Ключовий ресурс кожного мода (по одному, шлях до кожного — окрема пригода)
const CAPITALISM_KEYS = [
  'draconicevolution:chaotic_core',      // Draconic Evolution — після Хаос-дракона
  'botania:gaia_ingot',                  // Botania — злиток Гаї (бос Gaia Guardian)
  'tc4modern:primordial_pearl',          // Thaumcraft 4 — Первісна перлина
  'ttinkerer:ichorium',                  // Thaumic Tinkerer — іхоріум
  'mekanism:pellet_antimatter',          // Mekanism — антиматерія
  'witchery:infernal_animus',            // Witchery — пекельна анімуса
  'cataclysm:cursium_ingot',             // L_Ender's Cataclysm — курсіум (бос Maledictus)
  'ic2r_addons:quantum_solar_panel',     // IC2R + Advanced Solar Panels — квантова сонячна панель
  'twilightforest:fiery_ingot',          // Twilight Forest — вогняний злиток (Гідра)
  'ars_nouveau:wilden_tribute',          // Ars Nouveau — данина Вайлден-Хімери
  'irons_spellbooks:legendary_ink',      // Iron's Spells — легендарне чорнило
  'create:precision_mechanism',          // Create — прецизійний механізм
  'ae2:quantum_entangled_singularity',   // Applied Energistics 2 — квантова сингулярність
  // --- партія модів 20.11.2026 ---
  'powah:crystal_nitro',                 // Powah — нітро-кристал
  'oritech:unholy_intelligence',         // Oritech — нечестивий інтелект
  'neovitae:blood_orb_transcendent',     // Neo Vitae — трансцендентна сфера крові
  'occultism:book_of_binding_bound_marid', // Occultism — зв'язаний марид
  'forbidden_arcanus:eternal_stella',    // Forbidden & Arcanus — вічна стелла
  'extrabotany:orichalcos_ingot',        // Extra Botany — оріхалк
  'mysticalagradditions:insanium_ingot', // Mystical Agriculture — інсаніум
  'divinerpg:mortum_heart'               // DivineRPG — серце Мортуму
]

// Оригінальні інгредієнти Re:Avaritia
const AVARITIA_BASE = [
  'minecraft:bedrock',
  'avaritia:crystal_matrix_ingot',
  'avaritia:neutron_ingot',
  'avaritia:cosmic_meatballs',
  'avaritia:ultimate_stew',
  'avaritia:endest_pearl',
  'avaritia:record_fragment'
]

const toIngredients = (ids) => ids.map(id => ({ item: id }))

ServerEvents.recipes(event => {
  // Infinity Catalyst тепер робиться ЛИШЕ на Вівтарі Капіталізму (мод «Синтез», рецепт synthesis:capital/infinity_catalyst):
  // Сплав єдності III у центрі + 21 ключ на п'єдесталах + мана, джерело, FE, лава й обертання Create.
  // Тому прибираємо всі рецепти через Avaritia — і стандартні, і наші старі capitalism:*.
  event.remove({ id: 'avaritia:infinity_catalyst' })
  event.remove({ id: 'avaritia:infinity_catalyst_eternal' })
  event.remove({ type: 'avaritia:infinity_catalyst' })

  console.info(`[Capitalism] Infinity Catalyst: лише Вівтар Капіталізму (${CAPITALISM_KEYS.length} ключів)`)
})
