package ua.krainov.synthesis.block;

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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import ua.krainov.synthesis.blockentity.SolarPanelBlockEntity;
import ua.krainov.synthesis.registry.ModBlockEntities;

import java.util.List;

/** Капітал-панель — плаский блок (як датчик денного світла). ПКМ — стан. */
public class SolarPanelBlock extends BaseEntityBlock {
    public static final MapCodec<SolarPanelBlock> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            SolarTier.CODEC.fieldOf("tier").forGetter(SolarPanelBlock::getTier),
            propertiesCodec()
    ).apply(inst, SolarPanelBlock::new));

    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 6, 16);

    private final SolarTier tier;

    public SolarPanelBlock(SolarTier tier, Properties properties) {
        super(properties);
        this.tier = tier;
    }

    public SolarTier getTier() {
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

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SolarPanelBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null
                : createTickerHelper(type, ModBlockEntities.SOLAR_PANEL.get(), SolarPanelBlockEntity::serverTick);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof SolarPanelBlockEntity panel) {
            panel.describe(player);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.synthesis.solar", tier.dayOutput(), tier.nightOutput())
                .withStyle(ChatFormatting.GRAY));
        if (tier.ignoresRain) {
            tooltip.add(Component.translatable("tooltip.synthesis.solar.no_rain").withStyle(ChatFormatting.GOLD));
        }
    }
}
