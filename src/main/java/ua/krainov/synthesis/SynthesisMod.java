package ua.krainov.synthesis;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
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
        ModItems.ITEMS.register(modBus);
        ModCreativeTabs.TABS.register(modBus);
        LOGGER.info("Capitalism: Синтез завантажується");
    }
}
