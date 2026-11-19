package ua.krainov.synthesis.registry;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import ua.krainov.synthesis.SynthesisMod;
import ua.krainov.synthesis.block.PedestalBlock;

/** Усі блоки мода. */
public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SynthesisMod.MODID);

    /** П'єдестал синтезу — тримає один предмет і показує його над собою. */
    public static final DeferredBlock<PedestalBlock> PEDESTAL = BLOCKS.register("pedestal",
            () -> new PedestalBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DEEPSLATE)
                    .strength(2.5f, 6.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.POLISHED_DEEPSLATE)
                    .noOcclusion()));

    private ModBlocks() {}
}
