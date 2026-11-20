package ua.krainov.synthesis.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import ua.krainov.synthesis.blockentity.SoulStabilizerBlockEntity;
import ua.krainov.synthesis.registry.ModBlockEntities;

/**
 * Стабілізатор душ. ПКМ душею Draconic — вставити; ПКМ порожньою рукою — забрати;
 * Shift+ПКМ порожньою рукою — стан у чат. Лут іде в сусідній інвентар (скриня, труба).
 */
public class SoulStabilizerBlock extends BaseEntityBlock {
    public static final MapCodec<SoulStabilizerBlock> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            StabilizerTier.CODEC.fieldOf("tier").forGetter(SoulStabilizerBlock::getTier),
            propertiesCodec()
    ).apply(inst, SoulStabilizerBlock::new));

    private final StabilizerTier tier;

    public SoulStabilizerBlock(StabilizerTier tier, Properties properties) {
        super(properties);
        this.tier = tier;
    }

    public StabilizerTier getTier() {
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
        return new SoulStabilizerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null
                : createTickerHelper(type, ModBlockEntities.SOUL_STABILIZER.get(), SoulStabilizerBlockEntity::serverTick);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof SoulStabilizerBlockEntity machine)
                || SoulStabilizerBlockEntity.readSoulId(stack) == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!machine.getSoul().isEmpty()) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("message.synthesis.stabilizer.occupied"), true);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        if (!level.isClientSide) {
            machine.getSoulSlot().setStackInSlot(0, stack.copyWithCount(1));
            stack.consume(1, player);
            level.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 0.8f, 0.6f);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof SoulStabilizerBlockEntity machine)) return InteractionResult.PASS;
        if (!level.isClientSide) {
            if (player.isShiftKeyDown() || machine.getSoul().isEmpty()) {
                machine.describe(player);
            } else {
                ItemStack taken = machine.getSoul().copy();
                machine.getSoulSlot().setStackInSlot(0, ItemStack.EMPTY);
                player.getInventory().placeItemBackInInventory(taken);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof SoulStabilizerBlockEntity machine) {
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), machine.getSoul().copy());
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
