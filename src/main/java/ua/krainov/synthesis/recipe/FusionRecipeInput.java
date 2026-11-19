package ua.krainov.synthesis.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

/** Що лежить на вівтарі: центральний предмет і предмети з п'єдесталів (лише непорожні). */
public record FusionRecipeInput(ItemStack center, List<ItemStack> pedestals) implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        return index == 0 ? center : pedestals.get(index - 1);
    }

    @Override
    public int size() {
        return 1 + pedestals.size();
    }
}
