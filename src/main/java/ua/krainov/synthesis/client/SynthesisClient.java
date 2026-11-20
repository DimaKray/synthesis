package ua.krainov.synthesis.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import ua.krainov.synthesis.registry.ModItems;
import ua.krainov.synthesis.registry.ModMenus;
import ua.krainov.synthesis.SynthesisMod;
import ua.krainov.synthesis.blockentity.CapitalAltarBlockEntity;
import ua.krainov.synthesis.blockentity.FusionAltarBlockEntity;
import ua.krainov.synthesis.registry.ModBlockEntities;

/** Реєстрації, потрібні лише клієнту (рендерери). На сервері цей клас не завантажується. */
@EventBusSubscriber(modid = SynthesisMod.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class SynthesisClient {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.PEDESTAL.get(), PedestalRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.FUSION_ALTAR.get(), ctx -> new AltarRenderer<FusionAltarBlockEntity>(ctx, false));
        event.registerBlockEntityRenderer(ModBlockEntities.CAPITAL_ALTAR.get(), ctx -> new AltarRenderer<CapitalAltarBlockEntity>(ctx, true));
        event.registerBlockEntityRenderer(ModBlockEntities.SOUL_STABILIZER.get(), SoulStabilizerRenderer::new);
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.SYNTHESIZER.get(), SynthesizerScreen::new);
        event.register(ModMenus.FORTUNE_CONVERTER.get(), FortuneConverterScreen::new);
        event.register(ModMenus.INDUSTRIAL_PROCESSOR.get(), IndustrialProcessorScreen::new);
        if (ua.krainov.synthesis.compat.ModCompat.BOTANIA) {
            event.register(ua.krainov.synthesis.compat.botania.BotaniaMachines.GREENHOUSE_MENU.get(),
                    ua.krainov.synthesis.compat.botania.GreenhouseScreen::new);
        }
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new CapitalBoxClientExtension(), ModItems.CAPITAL_BOX_COMMON.get(), ModItems.CAPITAL_BOX_RARE.get(),
                ModItems.CAPITAL_BOX_EPIC.get(), ModItems.CAPITAL_BOX_LEGENDARY.get());
    }

    private SynthesisClient() {}
}
