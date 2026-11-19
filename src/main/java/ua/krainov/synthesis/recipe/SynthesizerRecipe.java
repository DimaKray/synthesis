package ua.krainov.synthesis.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
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
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import ua.krainov.synthesis.registry.ModRecipes;

import java.util.List;
import java.util.Optional;

/**
 * Рецепт Техно-магічного синтезатора ({@code "type": "synthesis:synthesizing"}).
 * До 3 предметів (порядок не важливий), необов'язкова рідина, FE за весь крафт,
 * мінімальні RPM і навантаження (SU) для Create — лише якщо min_rpm > 0.
 */
public record SynthesizerRecipe(List<Ingredient> ingredients, Optional<SizedFluidIngredient> fluid,
                                int energy, int minRpm, int stress, int time, ItemStack result)
        implements Recipe<SynthesizerRecipeInput> {

    public static final int MAX_INGREDIENTS = 3;

    private static final Codec<List<Ingredient>> INGREDIENTS_CODEC =
            ExtraCodecs.nonEmptyList(Ingredient.CODEC_NONEMPTY.listOf()).validate(list -> list.size() <= MAX_INGREDIENTS
                    ? DataResult.success(list)
                    : DataResult.error(() -> "Synthesizer recipe can have at most " + MAX_INGREDIENTS + " ingredients"));

    public static final MapCodec<SynthesizerRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            INGREDIENTS_CODEC.fieldOf("ingredients").forGetter(SynthesizerRecipe::ingredients),
            SizedFluidIngredient.FLAT_CODEC.optionalFieldOf("fluid").forGetter(SynthesizerRecipe::fluid),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("energy", 0).forGetter(SynthesizerRecipe::energy),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("min_rpm", 0).forGetter(SynthesizerRecipe::minRpm),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("stress", 0).forGetter(SynthesizerRecipe::stress),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("time", 200).forGetter(SynthesizerRecipe::time),
            ItemStack.STRICT_CODEC.fieldOf("result").forGetter(SynthesizerRecipe::result)
    ).apply(inst, SynthesizerRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, List<Ingredient>> INGREDIENT_LIST =
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list());
    private static final StreamCodec<RegistryFriendlyByteBuf, Optional<SizedFluidIngredient>> OPTIONAL_FLUID =
            ByteBufCodecs.optional(SizedFluidIngredient.STREAM_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, SynthesizerRecipe> STREAM_CODEC =
            StreamCodec.of(SynthesizerRecipe::write, SynthesizerRecipe::read);

    private static void write(RegistryFriendlyByteBuf buf, SynthesizerRecipe r) {
        INGREDIENT_LIST.encode(buf, r.ingredients);
        OPTIONAL_FLUID.encode(buf, r.fluid);
        buf.writeVarInt(r.energy);
        buf.writeVarInt(r.minRpm);
        buf.writeVarInt(r.stress);
        buf.writeVarInt(r.time);
        ItemStack.STREAM_CODEC.encode(buf, r.result);
    }

    private static SynthesizerRecipe read(RegistryFriendlyByteBuf buf) {
        List<Ingredient> ingredients = INGREDIENT_LIST.decode(buf);
        Optional<SizedFluidIngredient> fluid = OPTIONAL_FLUID.decode(buf);
        int energy = buf.readVarInt();
        int minRpm = buf.readVarInt();
        int stress = buf.readVarInt();
        int time = buf.readVarInt();
        ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
        return new SynthesizerRecipe(ingredients, fluid, energy, minRpm, stress, time, result);
    }

    @Override
    public boolean matches(SynthesizerRecipeInput input, Level level) {
        if (input.items().size() != ingredients.size()) return false;
        if (fluid.isPresent() && !fluid.get().test(input.fluid())) return false;
        return RecipeMatcher.findMatches(input.items(), ingredients) != null;
    }

    @Override
    public ItemStack assemble(SynthesizerRecipeInput input, HolderLookup.Provider registries) {
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
        list.addAll(ingredients);
        return list;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.SYNTHESIZING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.SYNTHESIZING_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<SynthesizerRecipe> {
        @Override
        public MapCodec<SynthesizerRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SynthesizerRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
