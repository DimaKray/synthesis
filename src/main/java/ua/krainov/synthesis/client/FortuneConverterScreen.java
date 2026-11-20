package ua.krainov.synthesis.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import ua.krainov.synthesis.SynthesisMod;
import ua.krainov.synthesis.blockentity.FortuneConverterBlockEntity;
import ua.krainov.synthesis.menu.FortuneConverterMenu;

import java.util.Locale;

/** Інтерфейс Перетворювача удачі. */
public class FortuneConverterScreen extends AbstractContainerScreen<FortuneConverterMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SynthesisMod.MODID, "textures/gui/fortune_converter.png");
    private static final int ENERGY_X = 10, ENERGY_Y = 18, ENERGY_W = 12, ENERGY_H = 52;
    private static final int ARROW_X = 56, ARROW_Y = 26, ARROW_W = 24, ARROW_H = 17;

    public FortuneConverterScreen(FortuneConverterMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        int x = leftPos, y = topPos;
        g.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        int max = menu.getMaxProgress();
        if (max > 0 && menu.getProgress() > 0) {
            g.blit(TEXTURE, x + ARROW_X, y + ARROW_Y, 176, 0, Math.min(ARROW_W, menu.getProgress() * ARROW_W / max), ARROW_H);
        }
        int eh = (int) ((long) ENERGY_H * menu.getEnergy() / FortuneConverterBlockEntity.ENERGY_CAPACITY);
        if (eh > 0) {
            g.fillGradient(x + ENERGY_X, y + ENERGY_Y + ENERGY_H - eh, x + ENERGY_X + ENERGY_W,
                    y + ENERGY_Y + ENERGY_H, 0xFFFF5A3C, 0xFFB0201A);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
        super.renderLabels(g, mouseX, mouseY);
        g.drawString(font, Component.translatable("gui.synthesis.fortune", menu.getFortune()), 130, 21, 0x404040, false);
        if (menu.hasSmelting()) g.drawString(font, Component.translatable("gui.synthesis.smelting"), 130, 31, 0xB4581E, false);
        if (menu.hasSilk()) g.drawString(font, Component.translatable("gui.synthesis.silk"), 130, 41, 0x3A6FB0, false);

        FortuneConverterBlockEntity.Status status = menu.getStatus();
        int color = switch (status) {
            case WORKING -> 0x2E7D32;
            case IDLE -> 0x555555;
            default -> 0xB71C1C;
        };
        g.drawString(font, Component.translatable("gui.synthesis.converter.status." + status.name().toLowerCase(Locale.ROOT)),
                30, 60, color, false);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        int rx = mouseX - leftPos, ry = mouseY - topPos;
        if (rx >= ENERGY_X && rx < ENERGY_X + ENERGY_W && ry >= ENERGY_Y && ry < ENERGY_Y + ENERGY_H) {
            g.renderTooltip(font, Component.translatable("gui.synthesis.energy",
                    menu.getEnergy(), FortuneConverterBlockEntity.ENERGY_CAPACITY), mouseX, mouseY);
        }
        renderTooltip(g, mouseX, mouseY);
    }
}
