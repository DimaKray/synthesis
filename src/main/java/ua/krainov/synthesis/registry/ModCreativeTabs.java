package ua.krainov.synthesis.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import ua.krainov.synthesis.SynthesisMod;
import ua.krainov.synthesis.compat.ModCompat;
import ua.krainov.synthesis.compat.create.CreateCompat;

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
                        output.accept(ModItems.FUSION_ALTAR.get());
                        output.accept(ModItems.CAPITAL_ALTAR.get());
                        output.accept(ModItems.PEDESTAL.get());
                        output.accept(ModItems.SYNTHESIZER.get());
                        output.accept(ModItems.SOLAR_HYBRID.get());
                        output.accept(ModItems.SOLAR_ULTIMATE.get());
                        output.accept(ModItems.SOLAR_QUANTUM.get());
                        output.accept(ModItems.SOLAR_INFINITE.get());
                        output.accept(ModItems.ACCELERATOR_X2.get());
                        output.accept(ModItems.ACCELERATOR_X4.get());
                        output.accept(ModItems.ACCELERATOR_X8.get());
                        output.accept(ModItems.ACCELERATOR_X16.get());
                        output.accept(ModItems.INDUSTRIAL_PROCESSOR.get());
                        output.accept(ModItems.STACK_MODULE_1.get());
                        output.accept(ModItems.STACK_MODULE_2.get());
                        output.accept(ModItems.STACK_MODULE_3.get());
                        output.accept(ModItems.FORTUNE_CONVERTER.get());
                        output.accept(ModItems.FORTUNE_MODULE_1.get());
                        output.accept(ModItems.FORTUNE_MODULE_2.get());
                        output.accept(ModItems.FORTUNE_MODULE_3.get());
                        output.accept(ModItems.FORTUNE_MODULE_4.get());
                        output.accept(ModItems.FORTUNE_MODULE_5.get());
                        output.accept(ModItems.SMELTING_MODULE.get());
                        output.accept(ModItems.SILK_TOUCH_MODULE.get());
                        output.accept(ModItems.SOUL_STABILIZER_BASIC.get());
                        output.accept(ModItems.SOUL_STABILIZER_WYVERN.get());
                        output.accept(ModItems.SOUL_STABILIZER_AWAKENED.get());
                        output.accept(ModItems.SOUL_STABILIZER_CHAOTIC.get());
                        if (ModCompat.CREATE) output.accept(CreateCompat.driveItem());
                        output.accept(ModItems.SYNTHESIS_CORE.get());
                        output.accept(ModItems.CAPITAL_BOX_COMMON.get());
                        output.accept(ModItems.CAPITAL_BOX_RARE.get());
                        output.accept(ModItems.CAPITAL_BOX_EPIC.get());
                        output.accept(ModItems.CAPITAL_BOX_LEGENDARY.get());
                    })
                    .build());

    private ModCreativeTabs() {}
}
