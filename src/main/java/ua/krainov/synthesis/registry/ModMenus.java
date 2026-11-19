package ua.krainov.synthesis.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import ua.krainov.synthesis.SynthesisMod;
import ua.krainov.synthesis.menu.SynthesizerMenu;

/** Типи меню (інтерфейсів) мода. */
public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, SynthesisMod.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<SynthesizerMenu>> SYNTHESIZER =
            MENUS.register("synthesizer", () -> IMenuTypeExtension.create(SynthesizerMenu::new));

    private ModMenus() {}
}
