package ua.krainov.synthesis.compat.botania;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Великий басейн мани, рівні 1–3. ПКМ — запас; компаратор — заповненість; редстоун — не віддавати. */
public class GreatManaPoolBlock extends BaseEntityBlock {
    public static final MapCodec<GreatManaPoolBlock> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.intRange(1, 3).fieldOf("tier").forGetter(GreatManaPoolBlock::getTier),
            propertiesCodec()
    ).apply(inst, GreatManaPoolBlock::new));

    private static final int[] CAPACITIES = {100_000_000, 500_000_000, 2_000_000_000};

    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 8, 16);

    private final int tier;

    public GreatManaPoolBlock(int tier, Properties properties) {
        super(properties);
        this.tier = tier;
    }

    public int getTier() {
        return tier;
    }

    public int capacity() {
        return CAPACITIES[Math.max(1, Math.min(3, tier)) - 1];
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    /** Колір рівня (як у басейнів Ботанії): I — без відтінку, II — зелений, III — рожевий. */
    public int tintColor() {
        return switch (tier) {
            case 2 -> 0xFFA6F5B4;
            case 3 -> 0xFFF7A8E6;
            default -> 0xFFFFFFFF;
        };
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GreatManaPoolBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null
                : createTickerHelper(type, BotaniaMachines.GREAT_POOL_BE.get(), GreatManaPoolBlockEntity::serverTick);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof GreatManaPoolBlockEntity pool) pool.describe(player);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof GreatManaPoolBlockEntity pool ? pool.comparatorSignal() : 0;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.synthesis.great_pool", String.format("%,d", capacity()))
                .withStyle(ChatFormatting.GRAY));
    }
}
