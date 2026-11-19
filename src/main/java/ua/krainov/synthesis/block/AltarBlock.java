package ua.krainov.synthesis.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import net.neoforged.neoforge.fluids.FluidUtil;
import ua.krainov.synthesis.blockentity.AltarBlockEntity;
import ua.krainov.synthesis.registry.ModItems;

/**
 * Спільний блок вівтарів (Злиття, Капіталізму). ПКМ предметом — покласти центральний предмет,
 * ПКМ ще раз — забрати; ПКМ відром — бак рідини (якщо вівтар його відкриває).
 * ПКМ Скіпетром синтезу — запустити ритуал (обробляє сам Скіпетр).
 */
public abstract class AltarBlock extends BaseEntityBlock {
    protected static final VoxelShape SHAPE = Shapes.or(
            Block.box(0, 0, 0, 16, 4, 16),
            Block.box(2, 4, 2, 14, 10, 14),
            Block.box(1, 10, 1, 15, 13, 15));

    protected AltarBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    /** Тікер для конкретного типу вівтаря (у підкласах). */
    protected static <T extends BlockEntity, E extends AltarBlockEntity<?>> BlockEntityTicker<T> altarTicker(
            Level level, BlockEntityType<T> type, BlockEntityType<E> altarType) {
        return level.isClientSide ? null : createTickerHelper(type, altarType, AltarBlockEntity::serverTick);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        // Скіпетр не кладемо на вівтар — далі спрацює його власна дія
        if (stack.is(ModItems.SYNTHESIS_SCEPTER.get())) {
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }
        // Відра — у бак (працює лише там, де бак відкритий як capability: Вівтар Капіталізму)
        if (FluidUtil.interactWithFluidHandler(player, hand, level, pos, hit.getDirection())) {
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        if (!(level.getBlockEntity(pos) instanceof AltarBlockEntity<?> altar) || !altar.isEmpty()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!level.isClientSide) {
            altar.placeItem(stack);
            stack.consume(1, player);
            level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0f, 0.8f);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof AltarBlockEntity<?> altar) || altar.isEmpty()) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            if (altar.isCrafting()) {
                player.displayClientMessage(Component.translatable("message.synthesis.altar.busy"), true);
            } else {
                player.getInventory().placeItemBackInInventory(altar.takeItem());
                level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0f, 0.8f);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof AltarBlockEntity<?> altar) {
            Containers.dropItemStack(level, pos.getX(), pos.getY() + 1, pos.getZ(), altar.takeItem());
            altar.onRemoved();
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
