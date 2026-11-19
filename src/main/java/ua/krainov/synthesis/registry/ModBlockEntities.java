package ua.krainov.synthesis.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import ua.krainov.synthesis.SynthesisMod;
import ua.krainov.synthesis.blockentity.FusionAltarBlockEntity;
import ua.krainov.synthesis.blockentity.PedestalBlockEntity;
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

    private ModBlockEntities() {}
}
