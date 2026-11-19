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
import ua.krainov.synthesis.blockentity.CapitalAltarBlockEntity;
import ua.krainov.synthesis.registry.ModBlockEntities;

/** Вівтар Капіталізму — фінал (рецепти synthesis:capital). */
public class CapitalAltarBlock extends AltarBlock {
    public static final MapCodec<CapitalAltarBlock> CODEC = simpleCodec(CapitalAltarBlock::new);

    public CapitalAltarBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CapitalAltarBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return altarTicker(level, type, ModBlockEntities.CAPITAL_ALTAR.get());
    }
}
