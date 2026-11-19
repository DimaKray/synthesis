package ua.krainov.synthesis;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import ua.krainov.synthesis.registry.ModBlockEntities;
import ua.krainov.synthesis.registry.ModBlocks;
import ua.krainov.synthesis.registry.ModCreativeTabs;
import ua.krainov.synthesis.registry.ModItems;

/**
 * Capitalism: Синтез — точка входу мода.
 * Тут лише реєстрації; логіка живе в окремих пакетах.
 */
@Mod(SynthesisMod.MODID)
public class SynthesisMod {
    public static final String MODID = "synthesis";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SynthesisMod(IEventBus modBus, ModContainer container) {
        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modBus);
        ModCreativeTabs.TABS.register(modBus);
        modBus.addListener(SynthesisMod::registerCapabilities);
        LOGGER.info("Capitalism: Синтез завантажується");
    }

    /** Відкриваємо інвентар п'єдестала для хоперів, труб і жолобів Create. */
    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.PEDESTAL.get(),
                (pedestal, side) -> pedestal.getInventory());
    }
}
