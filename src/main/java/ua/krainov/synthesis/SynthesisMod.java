package ua.krainov.synthesis;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import ua.krainov.synthesis.compat.ModCompat;
import ua.krainov.synthesis.config.SynthesisConfig;
import net.neoforged.fml.config.ModConfig;
import ua.krainov.synthesis.compat.botania.BotaniaCompat;
import ua.krainov.synthesis.compat.create.CreateCompat;
import ua.krainov.synthesis.registry.ModAttachments;
import ua.krainov.synthesis.registry.ModBlockEntities;
import ua.krainov.synthesis.registry.ModBlocks;
import ua.krainov.synthesis.registry.ModCreativeTabs;
import ua.krainov.synthesis.registry.ModItems;
import ua.krainov.synthesis.registry.ModMenus;
import ua.krainov.synthesis.registry.ModRecipes;

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
        ModRecipes.TYPES.register(modBus);
        ModRecipes.SERIALIZERS.register(modBus);
        ModMenus.MENUS.register(modBus);
        ModAttachments.ATTACHMENTS.register(modBus);
        ModCreativeTabs.TABS.register(modBus);
        // Кінетичний привід існує лише разом із Create
        if (ModCompat.CREATE) {
            CreateCompat.register(modBus);
        }
        modBus.addListener(SynthesisMod::registerCapabilities);
        container.registerConfig(ModConfig.Type.COMMON, SynthesisConfig.COMMON_SPEC, "synthesis-common.toml");
        container.registerConfig(ModConfig.Type.SERVER, SynthesisConfig.SERVER_SPEC, "synthesis-server.toml");
        LOGGER.info("Capitalism: Синтез завантажується");
    }

    /** Відкриваємо інвентарі п'єдестала й вівтаря для хоперів, труб і жолобів Create. */
    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.PEDESTAL.get(),
                (pedestal, side) -> pedestal.getInventory());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.FUSION_ALTAR.get(),
                (altar, side) -> altar.getInventory());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.SYNTHESIZER.get(),
                (machine, side) -> machine.getAutomationItems());
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.SYNTHESIZER.get(),
                (machine, side) -> machine.getTank());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.SYNTHESIZER.get(),
                (machine, side) -> machine.getEnergy());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.CAPITAL_ALTAR.get(),
                (altar, side) -> altar.getInventory());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.CAPITAL_ALTAR.get(),
                (altar, side) -> altar.getEnergy());
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.CAPITAL_ALTAR.get(),
                (altar, side) -> altar.getTank());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.SOUL_STABILIZER.get(),
                (machine, side) -> machine.getAutomationSlot());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.SOUL_STABILIZER.get(),
                (machine, side) -> machine.getEnergy());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.FORTUNE_CONVERTER.get(),
                (machine, side) -> machine.getAutomation());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.FORTUNE_CONVERTER.get(),
                (machine, side) -> machine.getEnergy());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.ACCELERATOR.get(),
                (accelerator, side) -> accelerator.getEnergy());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.SOLAR_PANEL.get(),
                (panel, side) -> panel.getEnergy());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.INDUSTRIAL_PROCESSOR.get(),
                (machine, side) -> machine.getAutomation());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.INDUSTRIAL_PROCESSOR.get(),
                (machine, side) -> machine.getEnergy());
        // FE — від кабелів будь-якого мода
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.FUSION_ALTAR.get(),
                (altar, side) -> altar.getEnergy());
        // Мана — лише якщо встановлена Botania
        if (ModCompat.BOTANIA) {
            BotaniaCompat.registerCapabilities(event);
        }
    }
}
