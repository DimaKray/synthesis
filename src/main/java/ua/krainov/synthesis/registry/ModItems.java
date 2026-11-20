package ua.krainov.synthesis.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import ua.krainov.synthesis.SynthesisMod;
import ua.krainov.synthesis.item.SynthesisScepterItem;
import ua.krainov.synthesis.item.CapitalBoxItem;
import ua.krainov.synthesis.box.BoxRarity;

/** Усі предмети мода. */
public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SynthesisMod.MODID);

    /** Сплав єдності I — манасталь + латунь + тауміум + осмій. */
    public static final DeferredItem<Item> UNITY_ALLOY_1 = ITEMS.registerSimpleItem("unity_alloy_1",
            new Item.Properties().rarity(Rarity.UNCOMMON));

    /** Сплав єдності II — терасталь + сталь + злиток порожнечі + самоцвіт джерела. */
    public static final DeferredItem<Item> UNITY_ALLOY_2 = ITEMS.registerSimpleItem("unity_alloy_2",
            new Item.Properties().rarity(Rarity.RARE));

    /** Сплав єдності III — злиток Гаї + атомний сплав + іхоріум + ігнітій. */
    public static final DeferredItem<Item> UNITY_ALLOY_3 = ITEMS.registerSimpleItem("unity_alloy_3",
            new Item.Properties().rarity(Rarity.EPIC));

    /** Скіпетр синтезу — запускає ритуали Вівтаря Злиття. */
    public static final DeferredItem<SynthesisScepterItem> SYNTHESIS_SCEPTER = ITEMS.registerItem("synthesis_scepter",
            SynthesisScepterItem::new, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));

    /** Предмет-блок п'єдестала. */
    public static final DeferredItem<BlockItem> PEDESTAL = ITEMS.registerSimpleBlockItem(ModBlocks.PEDESTAL);

    public static final DeferredItem<BlockItem> FUSION_ALTAR = ITEMS.registerSimpleBlockItem(ModBlocks.FUSION_ALTAR);

    public static final DeferredItem<BlockItem> SYNTHESIZER = ITEMS.registerSimpleBlockItem(ModBlocks.SYNTHESIZER);

    /** Ядро синтезу — компонент машин, робиться в Синтезаторі. */
    public static final DeferredItem<Item> SYNTHESIS_CORE = ITEMS.registerSimpleItem("synthesis_core",
            new Item.Properties().rarity(Rarity.RARE));

    public static final DeferredItem<BlockItem> CAPITAL_ALTAR = ITEMS.registerItem("capital_altar",
            props -> new BlockItem(ModBlocks.CAPITAL_ALTAR.get(), props), new Item.Properties().rarity(Rarity.EPIC));

    public static final DeferredItem<BlockItem> SOUL_STABILIZER_BASIC = ITEMS.registerSimpleBlockItem(ModBlocks.SOUL_STABILIZER_BASIC);
    public static final DeferredItem<BlockItem> SOUL_STABILIZER_WYVERN = ITEMS.registerItem("soul_stabilizer_wyvern",
            props -> new BlockItem(ModBlocks.SOUL_STABILIZER_WYVERN.get(), props), new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final DeferredItem<BlockItem> SOUL_STABILIZER_AWAKENED = ITEMS.registerItem("soul_stabilizer_awakened",
            props -> new BlockItem(ModBlocks.SOUL_STABILIZER_AWAKENED.get(), props), new Item.Properties().rarity(Rarity.RARE));
    public static final DeferredItem<BlockItem> SOUL_STABILIZER_CHAOTIC = ITEMS.registerItem("soul_stabilizer_chaotic",
            props -> new BlockItem(ModBlocks.SOUL_STABILIZER_CHAOTIC.get(), props), new Item.Properties().rarity(Rarity.EPIC));

    /** Капітал-бокси — падають гравцям за активну гру. */
    public static final DeferredItem<CapitalBoxItem> CAPITAL_BOX_COMMON = box(BoxRarity.COMMON, Rarity.COMMON);
    public static final DeferredItem<CapitalBoxItem> CAPITAL_BOX_RARE = box(BoxRarity.RARE, Rarity.RARE);
    public static final DeferredItem<CapitalBoxItem> CAPITAL_BOX_EPIC = box(BoxRarity.EPIC, Rarity.EPIC);
    public static final DeferredItem<CapitalBoxItem> CAPITAL_BOX_LEGENDARY = box(BoxRarity.LEGENDARY, Rarity.EPIC);

    private static DeferredItem<CapitalBoxItem> box(BoxRarity rarity, Rarity itemRarity) {
        return ITEMS.registerItem("capital_box_" + rarity.id(), props -> new CapitalBoxItem(rarity, props),
                new Item.Properties().stacksTo(16).rarity(itemRarity));
    }

    private ModItems() {}
}
