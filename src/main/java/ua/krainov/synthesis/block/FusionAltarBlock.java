package ua.krainov.synthesis.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import ua.krainov.synthesis.blockentity.FusionAltarBlockEntity;
import ua.krainov.synthesis.registry.ModBlockEntities;

/** Вівтар Злиття (рецепти synthesis:fusion). */
public class FusionAltarBlock extends AltarBlock {
    public static final MapCodec<FusionAltarBlock> CODEC = simpleCodec(FusionAltarBlock::new);

    public FusionAltarBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FusionAltarBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return altarTicker(level, type, ModBlockEntities.FUSION_ALTAR.get());
    }
}
