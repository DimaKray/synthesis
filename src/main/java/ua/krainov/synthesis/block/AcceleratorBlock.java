package ua.krainov.synthesis.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import ua.krainov.synthesis.blockentity.AcceleratorBlockEntity;
import ua.krainov.synthesis.registry.ModBlockEntities;

/** Прискорювач ×2/×4/×8/×16. Ставиться впритул до машин; ПКМ — стан; редстоун — вимкнути. */
public class AcceleratorBlock extends BaseEntityBlock {
    public static final MapCodec<AcceleratorBlock> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            AcceleratorTier.CODEC.fieldOf("tier").forGetter(AcceleratorBlock::getTier),
            propertiesCodec()
    ).apply(inst, AcceleratorBlock::new));

    private final AcceleratorTier tier;

    public AcceleratorBlock(AcceleratorTier tier, Properties properties) {
        super(properties);
        this.tier = tier;
    }

    public AcceleratorTier getTier() {
        return tier;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AcceleratorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null
                : createTickerHelper(type, ModBlockEntities.ACCELERATOR.get(), AcceleratorBlockEntity::serverTick);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof AcceleratorBlockEntity accelerator) {
            accelerator.describe(player);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
