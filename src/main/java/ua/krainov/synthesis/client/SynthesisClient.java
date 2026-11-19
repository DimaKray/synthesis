package ua.krainov.synthesis.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import ua.krainov.synthesis.SynthesisMod;
import ua.krainov.synthesis.registry.ModBlockEntities;

/** Реєстрації, потрібні лише клієнту (рендерери). На сервері цей клас не завантажується. */
@EventBusSubscriber(modid = SynthesisMod.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class SynthesisClient {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.PEDESTAL.get(), PedestalRenderer::new);
    }

    private SynthesisClient() {}
}
