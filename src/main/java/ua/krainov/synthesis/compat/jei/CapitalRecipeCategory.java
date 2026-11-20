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
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import ua.krainov.synthesis.SynthesisMod;
import ua.krainov.synthesis.recipe.CapitalRecipe;
import ua.krainov.synthesis.registry.ModItems;

import java.util.Arrays;
import java.util.List;

/** Сторінка JEI для Вівтаря Капіталізму: велике коло п'єдесталів, рідина, вимоги знизу. */
public class CapitalRecipeCategory implements IRecipeCategory<CapitalRecipe> {
    public static final RecipeType<CapitalRecipe> TYPE =
            RecipeType.create(SynthesisMod.MODID, "capital", CapitalRecipe.class);

    private static final int WIDTH = 196;
    private static final int HEIGHT = 176;
    private static final int CENTER_X = 70;
    private static final int CENTER_Y = 70;
    /** Як у світі: перші 8 інгредієнтів — внутрішнє кільце, решта — зовнішнє. */
    private static final int INNER_COUNT = 8;
    private static final int INNER_RADIUS = 30;
    private static final int OUTER_RADIUS = 60;

    private final IDrawable icon;
    private final IDrawable arrow;

    public CapitalRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModItems.CAPITAL_ALTAR.get()));
        this.arrow = guiHelper.getRecipeArrow();
    }

    @Override
    public RecipeType<CapitalRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.synthesis.capital");
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
    public void setRecipe(IRecipeLayoutBuilder builder, CapitalRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, CENTER_X - 8, CENTER_Y - 8)
                .setStandardSlotBackground()
                .addIngredients(recipe.center());

        List<Ingredient> pedestals = recipe.pedestals();
        int n = pedestals.size();
        int inner = Math.min(INNER_COUNT, n);
        int outer = n - inner;
        for (int i = 0; i < n; i++) {
            boolean isInner = i < inner;
            int count = isInner ? inner : outer;
            int index = isInner ? i : i - inner;
            int radius = isInner || outer == 0 ? INNER_RADIUS : OUTER_RADIUS;
            if (n <= INNER_COUNT) radius = OUTER_RADIUS / 2 + 12;
            double angle = -Math.PI / 2 + 2 * Math.PI * index / Math.max(1, count);
            int x = CENTER_X + (int) Math.round(Math.cos(angle) * radius) - 8;
            int y = CENTER_Y + (int) Math.round(Math.sin(angle) * radius) - 8;
            builder.addSlot(RecipeIngredientRole.INPUT, x, y)
                    .setStandardSlotBackground()
                    .addIngredients(pedestals.get(i));
        }

        if (recipe.fluid().isPresent()) {
            SizedFluidIngredient fluid = recipe.fluid().get();
            builder.addSlot(RecipeIngredientRole.INPUT, WIDTH - 20, 2)
                    .setStandardSlotBackground()
                    .setFluidRenderer(fluid.amount(), false, 16, 16)
                    .addIngredients(NeoForgeTypes.FLUID_STACK, Arrays.asList(fluid.getFluids()));
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, WIDTH - 22, CENTER_Y - 8)
                .setOutputSlotBackground()
                .addItemStack(recipe.result());
    }

    @Override
    public void draw(CapitalRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics,
                     double mouseX, double mouseY) {
        arrow.draw(guiGraphics, WIDTH - 50, CENTER_Y - 8);
        Font font = Minecraft.getInstance().font;
        int y = CENTER_Y + OUTER_RADIUS + 14;
        String seconds = String.format("%.1f", recipe.time() / 20.0);
        guiGraphics.drawString(font, Component.translatable("jei.synthesis.time", seconds), 2, y, 0xFF555555, false);
        if (recipe.energy() > 0) {
            guiGraphics.drawString(font, Component.translatable("jei.synthesis.energy", recipe.energy()), 80, y, 0xFFC0392B, false);
        }
        y += 10;
        if (recipe.mana() > 0) {
            guiGraphics.drawString(font, Component.translatable("jei.synthesis.mana", recipe.mana()), 2, y, 0xFF2E7DBF, false);
        }
        if (recipe.source() > 0) {
            guiGraphics.drawString(font, Component.translatable("jei.synthesis.source", recipe.source()), 92, y, 0xFF8E44AD, false);
        }
        y += 10;
        if (recipe.minRpm() > 0) {
            guiGraphics.drawString(font, Component.translatable("jei.synthesis.rotation", recipe.minRpm(), recipe.stress()),
                    2, y, 0xFF8D6E63, false);
        }
    }
}
