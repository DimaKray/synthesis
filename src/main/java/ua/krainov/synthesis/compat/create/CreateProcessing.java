package ua.krainov.synthesis.compat.create;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/** Дроблення Create (crushing, якщо немає — milling) для Промислового процесора. Лише якщо ModCompat.CREATE. */
public final class CreateProcessing {
    /** Чи є рецепт дроблення для предмета. */
    public static boolean canCrush(Level level, ItemStack stack) {
        return find(level, stack) != null;
    }

    /** Результат дроблення ОДНОГО предмета (з урахуванням шансів), або null, якщо рецепта немає. */
    @Nullable
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static List<ItemStack> crushOne(Level level, ItemStack stack) {
        ProcessingRecipe recipe = find(level, stack);
        return recipe == null ? null : (List<ItemStack>) recipe.rollResults(level.getRandom());
    }

    @Nullable
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static ProcessingRecipe find(Level level, ItemStack stack) {
        if (stack.isEmpty()) return null;
        SingleRecipeInput input = new SingleRecipeInput(stack.copyWithCount(1));
        for (AllRecipeTypes type : new AllRecipeTypes[]{AllRecipeTypes.CRUSHING, AllRecipeTypes.MILLING}) {
            RecipeType recipeType = type.getType();
            Optional<RecipeHolder> holder = level.getRecipeManager().getRecipeFor(recipeType, input, level);
            if (holder.isPresent() && holder.get().value() instanceof ProcessingRecipe recipe) return recipe;
        }
        return null;
    }

    private CreateProcessing() {}
}
