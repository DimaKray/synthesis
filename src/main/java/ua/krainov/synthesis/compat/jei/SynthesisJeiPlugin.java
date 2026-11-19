package ua.krainov.synthesis.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import ua.krainov.synthesis.SynthesisMod;
import ua.krainov.synthesis.recipe.FusionRecipe;
import ua.krainov.synthesis.registry.ModItems;
import ua.krainov.synthesis.registry.ModRecipes;

import java.util.List;

/** Плагін JEI. Завантажується лише самим JEI, тож без JEI мод не падає. */
@JeiPlugin
public class SynthesisJeiPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(SynthesisMod.MODID, "jei");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new FusionRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;
        List<FusionRecipe> recipes = level.getRecipeManager().getAllRecipesFor(ModRecipes.FUSION_TYPE.get())
                .stream().map(RecipeHolder::value).toList();
        registration.addRecipes(FusionRecipeCategory.TYPE, recipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModItems.FUSION_ALTAR.get()), FusionRecipeCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModItems.PEDESTAL.get()), FusionRecipeCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModItems.SYNTHESIS_SCEPTER.get()), FusionRecipeCategory.TYPE);
    }
}
