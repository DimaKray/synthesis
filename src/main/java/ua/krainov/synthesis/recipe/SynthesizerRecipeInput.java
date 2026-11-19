package ua.krainov.synthesis.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

/** Вміст синтезатора: непорожні вхідні слоти + рідина в баку. */
public record SynthesizerRecipeInput(List<ItemStack> items, FluidStack fluid) implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        return items.get(index);
    }

    @Override
    public int size() {
        return items.size();
    }
}
