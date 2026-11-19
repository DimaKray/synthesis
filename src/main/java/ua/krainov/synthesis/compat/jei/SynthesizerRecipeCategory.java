package ua.krainov.synthesis.compat.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import ua.krainov.synthesis.SynthesisMod;
import ua.krainov.synthesis.recipe.SynthesizerRecipe;
import ua.krainov.synthesis.registry.ModItems;

import java.util.Arrays;
import java.util.List;

/** Сторінка JEI для Синтезатора: бак рідини, до 3 предметів, результат, вимоги знизу. */
public class SynthesizerRecipeCategory implements IRecipeCategory<SynthesizerRecipe> {
    public static final RecipeType<SynthesizerRecipe> TYPE =
            RecipeType.create(SynthesisMod.MODID, "synthesizing", SynthesizerRecipe.class);

    private static final int WIDTH = 160;
    private static final int HEIGHT = 80;
    private static final int TANK_CAPACITY_SHOWN = 4_000;

    private final IDrawable icon;
    private final IDrawable arrow;

    public SynthesizerRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModItems.SYNTHESIZER.get()));
        this.arrow = guiHelper.getRecipeArrow();
    }

    @Override
    public RecipeType<SynthesizerRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.synthesis.synthesizing");
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SynthesizerRecipe recipe, IFocusGroup focuses) {
        if (recipe.fluid().isPresent()) {
            SizedFluidIngredient fluid = recipe.fluid().get();
            List<FluidStack> stacks = Arrays.asList(fluid.getFluids());
            builder.addSlot(RecipeIngredientRole.INPUT, 2, 2)
                    .setFluidRenderer(Math.max(TANK_CAPACITY_SHOWN, fluid.amount()), false, 16, 52)
                    .addIngredients(NeoForgeTypes.FLUID_STACK, stacks);
        }
        for (int i = 0; i < recipe.ingredients().size(); i++) {
            builder.addSlot(RecipeIngredientRole.INPUT, 26, 2 + i * 18)
                    .setStandardSlotBackground()
                    .addIngredients(recipe.ingredients().get(i));
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 90, 20)
                .setOutputSlotBackground()
                .addItemStack(recipe.result());
    }

    @Override
    public void draw(SynthesizerRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics,
                     double mouseX, double mouseY) {
        arrow.draw(guiGraphics, 54, 20);
        Font font = Minecraft.getInstance().font;
        String seconds = String.format("%.1f", recipe.time() / 20.0);
        guiGraphics.drawString(font, Component.translatable("jei.synthesis.time", seconds), 2, 59, 0xFF555555, false);
        if (recipe.energy() > 0) {
            guiGraphics.drawString(font, Component.translatable("jei.synthesis.energy", recipe.energy()), 72, 59, 0xFFC0392B, false);
        }
        if (recipe.minRpm() > 0) {
            guiGraphics.drawString(font, Component.translatable("jei.synthesis.rotation", recipe.minRpm(), recipe.stress()),
                    2, 70, 0xFF8D6E63, false);
        }
    }
}
