package ua.krainov.synthesis.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.RecipeMatcher;
import ua.krainov.synthesis.registry.ModRecipes;

import java.util.List;

/**
 * Рецепт Вівтаря Злиття ({@code "type": "synthesis:fusion"}).
 * Центральний предмет кладеться на вівтар, решта — на п'єдестали (порядок не важливий).
 * mana / source / energy поки лише зберігаються — споживання з'явиться на етапі 4.
 */
public record FusionRecipe(Ingredient center, List<Ingredient> pedestals,
                           int mana, int source, int energy, int time, ItemStack result)
        implements Recipe<FusionRecipeInput>, AltarRecipe {

    public static final MapCodec<FusionRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("center").forGetter(FusionRecipe::center),
            ExtraCodecs.nonEmptyList(Ingredient.CODEC_NONEMPTY.listOf()).fieldOf("pedestals").forGetter(FusionRecipe::pedestals),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("mana", 0).forGetter(FusionRecipe::mana),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("source", 0).forGetter(FusionRecipe::source),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("energy", 0).forGetter(FusionRecipe::energy),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("time", 100).forGetter(FusionRecipe::time),
            ItemStack.STRICT_CODEC.fieldOf("result").forGetter(FusionRecipe::result)
    ).apply(inst, FusionRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, List<Ingredient>> INGREDIENT_LIST =
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list());

    /** Передача рецепта з сервера на клієнт (потрібно для JEI). */
    public static final StreamCodec<RegistryFriendlyByteBuf, FusionRecipe> STREAM_CODEC =
            StreamCodec.of(FusionRecipe::write, FusionRecipe::read);

    private static void write(RegistryFriendlyByteBuf buf, FusionRecipe recipe) {
        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.center);
        INGREDIENT_LIST.encode(buf, recipe.pedestals);
        buf.writeVarInt(recipe.mana);
        buf.writeVarInt(recipe.source);
        buf.writeVarInt(recipe.energy);
        buf.writeVarInt(recipe.time);
        ItemStack.STREAM_CODEC.encode(buf, recipe.result);
    }

    private static FusionRecipe read(RegistryFriendlyByteBuf buf) {
        Ingredient center = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
        List<Ingredient> pedestals = INGREDIENT_LIST.decode(buf);
        int mana = buf.readVarInt();
        int source = buf.readVarInt();
        int energy = buf.readVarInt();
        int time = buf.readVarInt();
        ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
        return new FusionRecipe(center, pedestals, mana, source, energy, time, result);
    }

    @Override
    public boolean matches(FusionRecipeInput input, Level level) {
        if (!center.test(input.center())) return false;
        List<ItemStack> items = input.pedestals();
        if (items.size() != pedestals.size()) return false;
        // Безладне зіставлення: кожен інгредієнт має знайти свій п'єдестал
        return RecipeMatcher.findMatches(items, pedestals) != null;
    }

    @Override
    public ItemStack assemble(FusionRecipeInput input, HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(center);
        list.addAll(pedestals);
        return list;
    }

    /** true — книга рецептів ванілли не показує цей рецепт і не пише попереджень у лог. */
    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.FUSION_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.FUSION_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<FusionRecipe> {
        @Override
        public MapCodec<FusionRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FusionRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
