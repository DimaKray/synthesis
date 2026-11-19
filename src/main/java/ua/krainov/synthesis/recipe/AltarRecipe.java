package ua.krainov.synthesis.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.List;
import java.util.Optional;

/**
 * Спільне для рецептів вівтарів (Злиття і Капіталізму): центр, п'єдестали, ресурси.
 * Рідина й обертання є лише у Вівтаря Капіталізму — для Злиття повертаються «порожні» значення.
 */
public interface AltarRecipe {
    Ingredient center();

    List<Ingredient> pedestals();

    int mana();

    int source();

    int energy();

    int time();

    ItemStack result();

    default Optional<SizedFluidIngredient> fluid() {
        return Optional.empty();
    }

    default int minRpm() {
        return 0;
    }

    default int stress() {
        return 0;
    }
}
