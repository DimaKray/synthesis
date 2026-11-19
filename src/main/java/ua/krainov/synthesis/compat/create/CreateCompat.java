package ua.krainov.synthesis.compat.create;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import ua.krainov.synthesis.SynthesisMod;

/**
 * Інтеграція з Create: Кінетичний привід — блок з валом, який бере обертання й навантаження
 * з мережі Create і передає RPM сусідньому Синтезатору.
 * Усе тут реєструється й викликається ЛИШЕ якщо ModCompat.CREATE.
 */
public final class CreateCompat {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SynthesisMod.MODID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SynthesisMod.MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SynthesisMod.MODID);

    public static final DeferredBlock<KineticDriveBlock> KINETIC_DRIVE = BLOCKS.register("kinetic_drive",
            () -> new KineticDriveBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(2.0f, 6.0f)
                    .sound(SoundType.WOOD)));

    public static final DeferredItem<BlockItem> KINETIC_DRIVE_ITEM = ITEMS.registerSimpleBlockItem(KINETIC_DRIVE);

    @SuppressWarnings("DataFlowIssue")
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KineticDriveBlockEntity>> KINETIC_DRIVE_BE =
            BLOCK_ENTITIES.register("kinetic_drive",
                    () -> BlockEntityType.Builder.of(KineticDriveBlockEntity::new, KINETIC_DRIVE.get()).build(null));

    public static void register(IEventBus modBus) {
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        BLOCK_ENTITIES.register(modBus);
    }

    public static Item driveItem() {
        return KINETIC_DRIVE_ITEM.get();
    }

    private CreateCompat() {}
}
