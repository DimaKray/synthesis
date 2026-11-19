package ua.krainov.synthesis.compat.create;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import ua.krainov.synthesis.api.RotationSource;

/**
 * Кінетичний привід. Навантаження (SU) задає сусідній Синтезатор:
 * поки машина стоїть — 0, під час крафту — стільки, скільки вказано в рецепті ("stress").
 * Create рахує навантаження як impact × |RPM|, тож impact = SU / |RPM|.
 */
public class KineticDriveBlockEntity extends KineticBlockEntity implements RotationSource {
    private int requiredStress;

    public KineticDriveBlockEntity(BlockPos pos, BlockState state) {
        super(CreateCompat.KINETIC_DRIVE_BE.get(), pos, state);
    }

    @Override
    public float calculateStressApplied() {
        float speed = Math.abs(getTheoreticalSpeed());
        float impact = (requiredStress <= 0 || speed < 1) ? 0 : requiredStress / speed;
        this.lastStressApplied = impact;
        return impact;
    }

    @Override
    public void onSpeedChanged(float previousSpeed) {
        super.onSpeedChanged(previousSpeed);
        refreshStress();
    }

    @Override
    public int getRpm() {
        return (int) Math.abs(getSpeed()); // getSpeed() = 0, якщо мережа перевантажена
    }

    @Override
    public void setRequiredStress(int stressUnits) {
        if (stressUnits == requiredStress) return;
        requiredStress = stressUnits;
        refreshStress();
    }

    private void refreshStress() {
        if (level == null || level.isClientSide || !hasNetwork()) return;
        getOrCreateNetwork().updateStressFor(this, calculateStressApplied());
    }
}
