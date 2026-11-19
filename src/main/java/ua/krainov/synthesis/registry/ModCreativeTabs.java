package ua.krainov.synthesis.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import ua.krainov.synthesis.SynthesisMod;

/** Власна вкладка «Синтез» у творчому режимі. */
public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SynthesisMod.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.synthesis"))
                    .icon(() -> new ItemStack(ModItems.UNITY_ALLOY_1.get()))
                    .displayItems((params, output) -> {
                        output.accept(ModItems.SYNTHESIS_SCEPTER.get());
                        output.accept(ModItems.UNITY_ALLOY_1.get());
                        output.accept(ModItems.UNITY_ALLOY_2.get());
                        output.accept(ModItems.UNITY_ALLOY_3.get());
                    })
                    .build());

    private ModCreativeTabs() {}
}
