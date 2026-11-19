package ua.krainov.synthesis.compat.create;

import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/** Кінетичний привід: вал проходить крізь блок уздовж осі (як у вала Create). */
public class KineticDriveBlock extends RotatedPillarKineticBlock implements IBE<KineticDriveBlockEntity> {
    public KineticDriveBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(AXIS);
    }

    @Override
    public boolean hasShaftTowards(LevelReader level, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == state.getValue(AXIS);
    }

    @Override
    public Class<KineticDriveBlockEntity> getBlockEntityClass() {
        return KineticDriveBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends KineticDriveBlockEntity> getBlockEntityType() {
        return CreateCompat.KINETIC_DRIVE_BE.get();
    }
}
