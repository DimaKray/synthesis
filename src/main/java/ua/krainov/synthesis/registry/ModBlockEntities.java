package ua.krainov.synthesis.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import ua.krainov.synthesis.SynthesisMod;
import ua.krainov.synthesis.blockentity.AcceleratorBlockEntity;
import ua.krainov.synthesis.blockentity.CapitalAltarBlockEntity;
import ua.krainov.synthesis.blockentity.FortuneConverterBlockEntity;
import ua.krainov.synthesis.blockentity.FusionAltarBlockEntity;
import ua.krainov.synthesis.blockentity.IndustrialProcessorBlockEntity;
import ua.krainov.synthesis.blockentity.PedestalBlockEntity;
import ua.krainov.synthesis.blockentity.QuantumAssemblerBlockEntity;
import ua.krainov.synthesis.blockentity.SolarPanelBlockEntity;
import ua.krainov.synthesis.blockentity.SoulStabilizerBlockEntity;
import ua.krainov.synthesis.blockentity.SynthesizerBlockEntity;

/** Типи BlockEntity (блоків, що зберігають дані). */
public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SynthesisMod.MODID);

    @SuppressWarnings("DataFlowIssue") // build(null) — стандартний спосіб, тип даних не потрібен
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PedestalBlockEntity>> PEDESTAL =
            BLOCK_ENTITIES.register("pedestal",
                    () -> BlockEntityType.Builder.of(PedestalBlockEntity::new, ModBlocks.PEDESTAL.get()).build(null));

    @SuppressWarnings("DataFlowIssue")
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FusionAltarBlockEntity>> FUSION_ALTAR =
            BLOCK_ENTITIES.register("fusion_altar",
                    () -> BlockEntityType.Builder.of(FusionAltarBlockEntity::new, ModBlocks.FUSION_ALTAR.get()).build(null));

    @SuppressWarnings("DataFlowIssue")
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SynthesizerBlockEntity>> SYNTHESIZER =
            BLOCK_ENTITIES.register("synthesizer",
                    () -> BlockEntityType.Builder.of(SynthesizerBlockEntity::new, ModBlocks.SYNTHESIZER.get()).build(null));

    @SuppressWarnings("DataFlowIssue")
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CapitalAltarBlockEntity>> CAPITAL_ALTAR =
            BLOCK_ENTITIES.register("capital_altar",
                    () -> BlockEntityType.Builder.of(CapitalAltarBlockEntity::new, ModBlocks.CAPITAL_ALTAR.get()).build(null));

    @SuppressWarnings("DataFlowIssue")
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SoulStabilizerBlockEntity>> SOUL_STABILIZER =
            BLOCK_ENTITIES.register("soul_stabilizer",
                    () -> BlockEntityType.Builder.of(SoulStabilizerBlockEntity::new,
                            ModBlocks.SOUL_STABILIZER_BASIC.get(), ModBlocks.SOUL_STABILIZER_WYVERN.get(),
                            ModBlocks.SOUL_STABILIZER_AWAKENED.get(), ModBlocks.SOUL_STABILIZER_CHAOTIC.get()).build(null));

    @SuppressWarnings("DataFlowIssue")
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FortuneConverterBlockEntity>> FORTUNE_CONVERTER =
            BLOCK_ENTITIES.register("fortune_converter",
                    () -> BlockEntityType.Builder.of(FortuneConverterBlockEntity::new, ModBlocks.FORTUNE_CONVERTER.get()).build(null));

    @SuppressWarnings("DataFlowIssue")
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AcceleratorBlockEntity>> ACCELERATOR =
            BLOCK_ENTITIES.register("accelerator",
                    () -> BlockEntityType.Builder.of(AcceleratorBlockEntity::new,
                            ModBlocks.ACCELERATOR_X2.get(), ModBlocks.ACCELERATOR_X4.get(),
                            ModBlocks.ACCELERATOR_X8.get(), ModBlocks.ACCELERATOR_X16.get()).build(null));

    @SuppressWarnings("DataFlowIssue")
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SolarPanelBlockEntity>> SOLAR_PANEL =
            BLOCK_ENTITIES.register("solar_panel",
                    () -> BlockEntityType.Builder.of(SolarPanelBlockEntity::new,
                            ModBlocks.SOLAR_HYBRID.get(), ModBlocks.SOLAR_ULTIMATE.get(),
                            ModBlocks.SOLAR_QUANTUM.get(), ModBlocks.SOLAR_INFINITE.get()).build(null));

    @SuppressWarnings("DataFlowIssue")
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<IndustrialProcessorBlockEntity>> INDUSTRIAL_PROCESSOR =
            BLOCK_ENTITIES.register("industrial_processor",
                    () -> BlockEntityType.Builder.of(IndustrialProcessorBlockEntity::new, ModBlocks.INDUSTRIAL_PROCESSOR.get()).build(null));

    @SuppressWarnings("DataFlowIssue")
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<QuantumAssemblerBlockEntity>> QUANTUM_ASSEMBLER =
            BLOCK_ENTITIES.register("quantum_assembler",
                    () -> BlockEntityType.Builder.of(QuantumAssemblerBlockEntity::new, ModBlocks.QUANTUM_ASSEMBLER.get()).build(null));

    private ModBlockEntities() {}
}
