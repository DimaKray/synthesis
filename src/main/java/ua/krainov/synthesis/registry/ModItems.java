package ua.krainov.synthesis.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import ua.krainov.synthesis.SynthesisMod;

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

    /** Скіпетр синтезу — запускатиме ритуали (функція з'явиться на етапі 3). */
    public static final DeferredItem<Item> SYNTHESIS_SCEPTER = ITEMS.registerSimpleItem("synthesis_scepter",
            new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));

    /** Предмет-блок п'єдестала. */
    public static final DeferredItem<BlockItem> PEDESTAL = ITEMS.registerSimpleBlockItem(ModBlocks.PEDESTAL);

    private ModItems() {}
}
