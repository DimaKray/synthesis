package ua.krainov.synthesis.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import ua.krainov.synthesis.recipe.FusionRecipe;
import ua.krainov.synthesis.registry.ModBlockEntities;
import ua.krainov.synthesis.registry.ModRecipes;

/** Вівтар Злиття — рецепти {@code synthesis:fusion}. Уся логіка — в {@link AltarBlockEntity}. */
public class FusionAltarBlockEntity extends AltarBlockEntity<FusionRecipe> {
    public FusionAltarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FUSION_ALTAR.get(), pos, state);
    }

    @Override
    protected RecipeType<FusionRecipe> recipeType() {
        return ModRecipes.FUSION_TYPE.get();
    }
}
