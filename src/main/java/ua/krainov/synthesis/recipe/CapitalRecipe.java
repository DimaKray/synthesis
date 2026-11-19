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
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import ua.krainov.synthesis.registry.ModRecipes;

import java.util.List;
import java.util.Optional;

/**
 * Рецепт Вівтаря Капіталізму ({@code "type": "synthesis:capital"}) — як fusion,
 * плюс рідина з бака вівтаря і обертання Create (min_rpm, stress). Рідина витрачається поступово,
 * тому на сам збіг рецепта вона не впливає.
 */
public record CapitalRecipe(Ingredient center, List<Ingredient> pedestals,
                            int mana, int source, int energy, Optional<SizedFluidIngredient> fluid,
                            int minRpm, int stress, int time, ItemStack result)
        implements Recipe<FusionRecipeInput>, AltarRecipe {

    public static final MapCodec<CapitalRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("center").forGetter(CapitalRecipe::center),
            ExtraCodecs.nonEmptyList(Ingredient.CODEC_NONEMPTY.listOf()).fieldOf("pedestals").forGetter(CapitalRecipe::pedestals),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("mana", 0).forGetter(CapitalRecipe::mana),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("source", 0).forGetter(CapitalRecipe::source),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("energy", 0).forGetter(CapitalRecipe::energy),
            SizedFluidIngredient.FLAT_CODEC.optionalFieldOf("fluid").forGetter(CapitalRecipe::fluid),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("min_rpm", 0).forGetter(CapitalRecipe::minRpm),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("stress", 0).forGetter(CapitalRecipe::stress),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("time", 600).forGetter(CapitalRecipe::time),
            ItemStack.STRICT_CODEC.fieldOf("result").forGetter(CapitalRecipe::result)
    ).apply(inst, CapitalRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, List<Ingredient>> INGREDIENT_LIST =
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list());
    private static final StreamCodec<RegistryFriendlyByteBuf, Optional<SizedFluidIngredient>> OPTIONAL_FLUID =
            ByteBufCodecs.optional(SizedFluidIngredient.STREAM_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, CapitalRecipe> STREAM_CODEC =
            StreamCodec.of(CapitalRecipe::write, CapitalRecipe::read);

    private static void write(RegistryFriendlyByteBuf buf, CapitalRecipe r) {
        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, r.center);
        INGREDIENT_LIST.encode(buf, r.pedestals);
        buf.writeVarInt(r.mana);
        buf.writeVarInt(r.source);
        buf.writeVarInt(r.energy);
        OPTIONAL_FLUID.encode(buf, r.fluid);
        buf.writeVarInt(r.minRpm);
        buf.writeVarInt(r.stress);
        buf.writeVarInt(r.time);
        ItemStack.STREAM_CODEC.encode(buf, r.result);
    }

    private static CapitalRecipe read(RegistryFriendlyByteBuf buf) {
        Ingredient center = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
        List<Ingredient> pedestals = INGREDIENT_LIST.decode(buf);
        int mana = buf.readVarInt();
        int source = buf.readVarInt();
        int energy = buf.readVarInt();
        Optional<SizedFluidIngredient> fluid = OPTIONAL_FLUID.decode(buf);
        int minRpm = buf.readVarInt();
        int stress = buf.readVarInt();
        int time = buf.readVarInt();
        ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
        return new CapitalRecipe(center, pedestals, mana, source, energy, fluid, minRpm, stress, time, result);
    }

    @Override
    public boolean matches(FusionRecipeInput input, Level level) {
        if (!center.test(input.center())) return false;
        List<ItemStack> items = input.pedestals();
        if (items.size() != pedestals.size()) return false;
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

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.CAPITAL_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.CAPITAL_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<CapitalRecipe> {
        @Override
        public MapCodec<CapitalRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CapitalRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
