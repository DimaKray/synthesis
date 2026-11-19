package ua.krainov.synthesis.compat.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import ua.krainov.synthesis.SynthesisMod;
import ua.krainov.synthesis.recipe.FusionRecipe;
import ua.krainov.synthesis.registry.ModItems;

import java.util.List;

/** Сторінка JEI для рецептів Вівтаря Злиття: центр, п'єдестали по колу, результат праворуч. */
public class FusionRecipeCategory implements IRecipeCategory<FusionRecipe> {
    public static final RecipeType<FusionRecipe> TYPE =
            RecipeType.create(SynthesisMod.MODID, "fusion", FusionRecipe.class);

    private static final int WIDTH = 160;
    private static final int HEIGHT = 104;
    private static final int CENTER_X = 48; // центр кола п'єдесталів
    private static final int CENTER_Y = 52;
    private static final int RING_RADIUS = 34;

    private final IDrawable icon;
    private final IDrawable arrow;

    public FusionRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModItems.FUSION_ALTAR.get()));
        this.arrow = guiHelper.getRecipeArrow();
    }

    @Override
    public RecipeType<FusionRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.synthesis.fusion");
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
    public void setRecipe(IRecipeLayoutBuilder builder, FusionRecipe recipe, IFocusGroup focuses) {
        // Центральний предмет (слот 16×16, тому зсув на 8)
        builder.addSlot(RecipeIngredientRole.INPUT, CENTER_X - 8, CENTER_Y - 8)
                .setStandardSlotBackground()
                .addIngredients(recipe.center());

        // П'єдестали — рівномірно по колу, перший зверху
        List<Ingredient> pedestals = recipe.pedestals();
        int n = pedestals.size();
        for (int i = 0; i < n; i++) {
            double angle = -Math.PI / 2 + 2 * Math.PI * i / n;
            int x = CENTER_X + (int) Math.round(Math.cos(angle) * RING_RADIUS) - 8;
            int y = CENTER_Y + (int) Math.round(Math.sin(angle) * RING_RADIUS) - 8;
            builder.addSlot(RecipeIngredientRole.INPUT, x, y)
                    .setStandardSlotBackground()
                    .addIngredients(pedestals.get(i));
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 130, CENTER_Y - 8)
                .setOutputSlotBackground()
                .addItemStack(recipe.result());
    }

    @Override
    public void draw(FusionRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics,
                     double mouseX, double mouseY) {
        arrow.draw(guiGraphics, 96, CENTER_Y - 8);

        Font font = Minecraft.getInstance().font;
        int y = CENTER_Y + 14;
        String seconds = String.format("%.1f", recipe.time() / 20.0);
        guiGraphics.drawString(font, Component.translatable("jei.synthesis.time", seconds), 92, y, 0xFF555555, false);
        // Ресурси (споживатимуться з етапу 4) — показуємо, якщо задані
        if (recipe.mana() > 0) {
            y += 10;
            guiGraphics.drawString(font, Component.translatable("jei.synthesis.mana", recipe.mana()), 92, y, 0xFF2E7DBF, false);
        }
        if (recipe.source() > 0) {
            y += 10;
            guiGraphics.drawString(font, Component.translatable("jei.synthesis.source", recipe.source()), 92, y, 0xFF8E44AD, false);
        }
        if (recipe.energy() > 0) {
            y += 10;
            guiGraphics.drawString(font, Component.translatable("jei.synthesis.energy", recipe.energy()), 92, y, 0xFFC0392B, false);
        }
    }
}
