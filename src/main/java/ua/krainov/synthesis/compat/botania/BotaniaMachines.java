package ua.krainov.synthesis.compat.botania;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;
import velrondevs.botania.api.BotaniaCapabilities;
import ua.krainov.synthesis.SynthesisMod;
import ua.krainov.synthesis.config.SynthesisConfig;

/**
 * Машини мани (етап 8): Великий басейн мани I–III, Драконієвий розповсюджувач, Мультитеплиця.
 * Реєструються ЛИШЕ якщо встановлена Ботанія (ModCompat.BOTANIA).
 */
public final class BotaniaMachines {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SynthesisMod.MODID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SynthesisMod.MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SynthesisMod.MODID);
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, SynthesisMod.MODID);

    private static final String BIND_KEY = "synthesis_spreader_bind";

    public static final DeferredBlock<GreatManaPoolBlock> GREAT_POOL_1 = pool(1);
    public static final DeferredBlock<GreatManaPoolBlock> GREAT_POOL_2 = pool(2);
    public static final DeferredBlock<GreatManaPoolBlock> GREAT_POOL_3 = pool(3);
    public static final DeferredBlock<DraconicSpreaderBlock> DRACONIC_SPREADER = BLOCKS.register("draconic_spreader",
            () -> new DraconicSpreaderBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE)
                    .strength(3.0f, 6.0f).sound(SoundType.WOOD).noOcclusion()));
    public static final DeferredBlock<GreenhouseBlock> GREENHOUSE = BLOCKS.register("multi_greenhouse",
            () -> new GreenhouseBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT)
                    .strength(2.5f, 6.0f).sound(SoundType.GLASS).noOcclusion()));

    public static final DeferredItem<BlockItem> GREAT_POOL_1_ITEM = ITEMS.registerItem("great_mana_pool_1",
            p -> new BlockItem(GREAT_POOL_1.get(), p), new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final DeferredItem<BlockItem> GREAT_POOL_2_ITEM = ITEMS.registerItem("great_mana_pool_2",
            p -> new BlockItem(GREAT_POOL_2.get(), p), new Item.Properties().rarity(Rarity.RARE));
    public static final DeferredItem<BlockItem> GREAT_POOL_3_ITEM = ITEMS.registerItem("great_mana_pool_3",
            p -> new BlockItem(GREAT_POOL_3.get(), p), new Item.Properties().rarity(Rarity.EPIC));
    public static final DeferredItem<BlockItem> DRACONIC_SPREADER_ITEM = ITEMS.registerItem("draconic_spreader",
            p -> new BlockItem(DRACONIC_SPREADER.get(), p), new Item.Properties().rarity(Rarity.RARE));
    public static final DeferredItem<BlockItem> GREENHOUSE_ITEM = ITEMS.registerSimpleBlockItem(GREENHOUSE);

    @SuppressWarnings("DataFlowIssue")
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GreatManaPoolBlockEntity>> GREAT_POOL_BE =
            BLOCK_ENTITIES.register("great_mana_pool", () -> BlockEntityType.Builder.of(GreatManaPoolBlockEntity::new,
                    GREAT_POOL_1.get(), GREAT_POOL_2.get(), GREAT_POOL_3.get()).build(null));
    @SuppressWarnings("DataFlowIssue")
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DraconicSpreaderBlockEntity>> DRACONIC_SPREADER_BE =
            BLOCK_ENTITIES.register("draconic_spreader", () -> BlockEntityType.Builder.of(DraconicSpreaderBlockEntity::new,
                    DRACONIC_SPREADER.get()).build(null));
    @SuppressWarnings("DataFlowIssue")
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GreenhouseBlockEntity>> GREENHOUSE_BE =
            BLOCK_ENTITIES.register("multi_greenhouse", () -> BlockEntityType.Builder.of(GreenhouseBlockEntity::new,
                    GREENHOUSE.get()).build(null));

    public static final DeferredHolder<MenuType<?>, MenuType<GreenhouseMenu>> GREENHOUSE_MENU =
            MENUS.register("multi_greenhouse", () -> IMenuTypeExtension.create(GreenhouseMenu::new));

    private static DeferredBlock<GreatManaPoolBlock> pool(int tier) {
        return BLOCKS.register("great_mana_pool_" + tier,
                () -> new GreatManaPoolBlock(tier, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE)
                        .strength(3.0f, 12.0f).sound(SoundType.STONE).lightLevel(s -> 8)));
    }

    public static void register(IEventBus modBus) {
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        BLOCK_ENTITIES.register(modBus);
        MENUS.register(modBus);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(BotaniaCapabilities.MANA_RECEIVER, GREAT_POOL_BE.get(), (be, side) -> be);
        event.registerBlockEntity(BotaniaCapabilities.MANA_RECEIVER, DRACONIC_SPREADER_BE.get(), (be, side) -> be);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, GREENHOUSE_BE.get(), (be, side) -> be.getAutomation());
    }

    public static void addToTab(CreativeModeTab.Output output) {
        output.accept(GREAT_POOL_1_ITEM.get());
        output.accept(GREAT_POOL_2_ITEM.get());
        output.accept(GREAT_POOL_3_ITEM.get());
        output.accept(DRACONIC_SPREADER_ITEM.get());
        output.accept(GREENHOUSE_ITEM.get());
    }

    /**
     * Скіпетр і Драконієвий розповсюджувач: ПКМ по розповсюджувачу запам'ятовує його,
     * ПКМ по будь-якому приймачу мани прив'язує. Повертає null, якщо це не наш випадок.
     */
    @Nullable
    public static InteractionResult scepterUse(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        ItemStack scepter = context.getItemInHand();
        if (player == null) return null;

        if (level.getBlockEntity(pos) instanceof DraconicSpreaderBlockEntity) {
            if (!level.isClientSide) {
                CustomData.update(DataComponents.CUSTOM_DATA, scepter, tag -> tag.putLong(BIND_KEY, pos.asLong()));
                player.displayClientMessage(Component.translatable("message.synthesis.spreader.bind_start")
                        .withStyle(ChatFormatting.LIGHT_PURPLE), true);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        CompoundTag tag = scepter.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!tag.contains(BIND_KEY)) return null;
        if (level.isClientSide) return InteractionResult.SUCCESS;

        BlockPos spreaderPos = BlockPos.of(tag.getLong(BIND_KEY));
        CustomData.update(DataComponents.CUSTOM_DATA, scepter, t -> t.remove(BIND_KEY));
        if (!(level.getBlockEntity(spreaderPos) instanceof DraconicSpreaderBlockEntity spreader)) {
            player.displayClientMessage(Component.translatable("message.synthesis.spreader.bind_lost"), true);
            return InteractionResult.SUCCESS;
        }
        if (!spreaderPos.closerThan(pos, SynthesisConfig.SPREADER_RANGE.get())) {
            player.displayClientMessage(Component.translatable("message.synthesis.spreader.bind_far",
                    SynthesisConfig.SPREADER_RANGE.get()), true);
            return InteractionResult.SUCCESS;
        }
        if (ManaTransfer.find(level, pos, null) == null) {
            player.displayClientMessage(Component.translatable("message.synthesis.spreader.bind_invalid"), true);
            return InteractionResult.SUCCESS;
        }
        spreader.setTarget(pos);
        player.displayClientMessage(Component.translatable("message.synthesis.spreader.bound",
                pos.getX(), pos.getY(), pos.getZ()).withStyle(ChatFormatting.GREEN), true);
        return InteractionResult.SUCCESS;
    }

    private BotaniaMachines() {}
}
