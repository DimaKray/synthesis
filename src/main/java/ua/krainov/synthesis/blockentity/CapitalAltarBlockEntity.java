package ua.krainov.synthesis.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import ua.krainov.synthesis.recipe.CapitalRecipe;

import java.util.List;
import ua.krainov.synthesis.registry.ModBlockEntities;
import ua.krainov.synthesis.registry.ModRecipes;

/**
 * Вівтар Капіталізму — фінальний вівтар, рецепти {@code synthesis:capital}.
 * Мультиблок 9×9: 21 п'єдестал на фіксованих позиціях ({@link #LAYOUT}). Окрім мани/джерела/FE має бак
 * рідини й вимагає обертання Create. Промені від п'єдесталів малює рендерер.
 */
public class CapitalAltarBlockEntity extends AltarBlockEntity<CapitalRecipe> {
    public CapitalAltarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CAPITAL_ALTAR.get(), pos, state);
    }

    /**
     * Розмітка 21 п'єдестала (зсуви від вівтаря, той самий рівень):
     * внутрішнє кільце (радіус 2) — 8, зовнішнє (радіус 4) — 13, з півдня (+Z) прохід у 3 клітинки.
     */
    public static final List<BlockPos> LAYOUT = List.of(
            // внутрішнє кільце
            new BlockPos(-2, 0, -2), new BlockPos(0, 0, -2), new BlockPos(2, 0, -2),
            new BlockPos(-2, 0, 0), new BlockPos(2, 0, 0),
            new BlockPos(-2, 0, 2), new BlockPos(0, 0, 2), new BlockPos(2, 0, 2),
            // зовнішнє кільце: північ
            new BlockPos(-4, 0, -4), new BlockPos(-2, 0, -4), new BlockPos(0, 0, -4), new BlockPos(2, 0, -4), new BlockPos(4, 0, -4),
            // захід і схід
            new BlockPos(-4, 0, -2), new BlockPos(-4, 0, 0), new BlockPos(-4, 0, 2),
            new BlockPos(4, 0, -2), new BlockPos(4, 0, 0), new BlockPos(4, 0, 2),
            // південь — лише кути (посередині прохід)
            new BlockPos(-4, 0, 4), new BlockPos(4, 0, 4));

    @Override
    protected List<BlockPos> pedestalOffsets() {
        return LAYOUT;
    }

    @Override
    protected boolean fixedLayout() {
        return true;
    }

    @Override
    public int structureRadius() {
        return 4;
    }

    @Override
    protected RecipeType<CapitalRecipe> recipeType() {
        return ModRecipes.CAPITAL_TYPE.get();
    }

    /** Фінал заслуговує на салют. */
    @Override
    protected void onRitualFinished(ServerLevel level) {
        BlockPos pos = getBlockPos();
        level.sendParticles(ParticleTypes.FLASH, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, 1, 0, 0, 0, 0);
        level.sendParticles(ParticleTypes.FIREWORK, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5,
                80, 0.5, 0.5, 0.5, 0.25);
        level.playSound(null, pos, SoundEvents.END_PORTAL_SPAWN, SoundSource.BLOCKS, 0.6f, 1.2f);
    }
}
