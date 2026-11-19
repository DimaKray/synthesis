package ua.krainov.synthesis.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import ua.krainov.synthesis.SynthesisMod;
import ua.krainov.synthesis.recipe.FusionRecipe;
import ua.krainov.synthesis.recipe.SynthesizerRecipe;

/** Типи рецептів мода та їхні серіалізатори (читання з JSON). */
public final class ModRecipes {
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, SynthesisMod.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, SynthesisMod.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<FusionRecipe>> FUSION_TYPE =
            TYPES.register("fusion", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(SynthesisMod.MODID, "fusion")));

    public static final DeferredHolder<RecipeSerializer<?>, FusionRecipe.Serializer> FUSION_SERIALIZER =
            SERIALIZERS.register("fusion", FusionRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<SynthesizerRecipe>> SYNTHESIZING_TYPE =
            TYPES.register("synthesizing", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(SynthesisMod.MODID, "synthesizing")));

    public static final DeferredHolder<RecipeSerializer<?>, SynthesizerRecipe.Serializer> SYNTHESIZING_SERIALIZER =
            SERIALIZERS.register("synthesizing", SynthesizerRecipe.Serializer::new);

    private ModRecipes() {}
}
