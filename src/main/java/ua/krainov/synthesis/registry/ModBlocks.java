package ua.krainov.synthesis.registry;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import ua.krainov.synthesis.SynthesisMod;
import ua.krainov.synthesis.block.CapitalAltarBlock;
import ua.krainov.synthesis.block.FusionAltarBlock;
import ua.krainov.synthesis.block.PedestalBlock;
import ua.krainov.synthesis.block.SynthesizerBlock;

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

    /** Вівтар Злиття — центр ритуалу, п'єдестали ставляться навколо. */
    public static final DeferredBlock<FusionAltarBlock> FUSION_ALTAR = BLOCKS.register("fusion_altar",
            () -> new FusionAltarBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DEEPSLATE)
                    .strength(3.5f, 9.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.POLISHED_DEEPSLATE)
                    .lightLevel(state -> 5)
                    .noOcclusion()));

    /** Техно-магічний синтезатор — машина з інтерфейсом, рідиною, FE і (за потреби) обертанням Create. */
    public static final DeferredBlock<SynthesizerBlock> SYNTHESIZER = BLOCKS.register("synthesizer",
            () -> new SynthesizerBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(3.5f, 6.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)));

    /** Вівтар Капіталізму — фінальний ритуал (Infinity Catalyst). */
    public static final DeferredBlock<CapitalAltarBlock> CAPITAL_ALTAR = BLOCKS.register("capital_altar",
            () -> new CapitalAltarBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.GOLD)
                    .strength(5.0f, 1200.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.NETHERITE_BLOCK)
                    .lightLevel(state -> 10)
                    .noOcclusion()));

    private ModBlocks() {}
}
