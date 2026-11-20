package ua.krainov.synthesis.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import ua.krainov.synthesis.SynthesisMod;
import ua.krainov.synthesis.blockentity.IndustrialProcessorBlockEntity;
import ua.krainov.synthesis.menu.IndustrialProcessorMenu;

import java.util.Locale;

/** Інтерфейс Промислового процесора: кнопка режиму, шкала FE, прогрес, 6 виходів. */
public class IndustrialProcessorScreen extends AbstractContainerScreen<IndustrialProcessorMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SynthesisMod.MODID, "textures/gui/industrial_processor.png");
    private static final int ENERGY_X = 10, ENERGY_Y = 18, ENERGY_W = 12, ENERGY_H = 52;
    private static final int ARROW_X = 58, ARROW_Y = 35, ARROW_W = 24, ARROW_H = 17;

    private Button modeButton;

    public IndustrialProcessorScreen(IndustrialProcessorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        modeButton = addRenderableWidget(Button.builder(modeText(), b -> {
                    if (minecraft != null && minecraft.gameMode != null) {
                        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, IndustrialProcessorMenu.BUTTON_MODE);
                    }
                })
                .bounds(leftPos + 112, topPos + 4, 58, 14)
                .build());
    }

    private Component modeText() {
        return Component.translatable("gui.synthesis.processor.mode." + menu.getMode().name().toLowerCase(Locale.ROOT));
    }

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        int x = leftPos, y = topPos;
        g.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        int max = menu.getMaxProgress();
        if (max > 0 && menu.getProgress() > 0) {
            g.blit(TEXTURE, x + ARROW_X, y + ARROW_Y, 176, 0, Math.min(ARROW_W, menu.getProgress() * ARROW_W / max), ARROW_H);
        }
        int eh = (int) ((long) ENERGY_H * menu.getEnergy() / IndustrialProcessorBlockEntity.ENERGY_CAPACITY);
        if (eh > 0) {
            g.fillGradient(x + ENERGY_X, y + ENERGY_Y + ENERGY_H - eh, x + ENERGY_X + ENERGY_W,
                    y + ENERGY_Y + ENERGY_H, 0xFFFF5A3C, 0xFFB0201A);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
        super.renderLabels(g, mouseX, mouseY);
        IndustrialProcessorBlockEntity.Status status = menu.getStatus();
        int color = switch (status) {
            case WORKING -> 0x2E7D32;
            case IDLE -> 0x555555;
            default -> 0xB71C1C;
        };
        g.drawString(font, Component.translatable("gui.synthesis.processor.status." + status.name().toLowerCase(Locale.ROOT)),
                56, 64, color, false);
        g.drawString(font, Component.translatable("gui.synthesis.processor.batch", menu.getBatch()), 60, 25, 0x404040, false);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        if (modeButton != null) modeButton.setMessage(modeText());
        super.render(g, mouseX, mouseY, partialTick);
        int rx = mouseX - leftPos, ry = mouseY - topPos;
        if (rx >= ENERGY_X && rx < ENERGY_X + ENERGY_W && ry >= ENERGY_Y && ry < ENERGY_Y + ENERGY_H) {
            g.renderTooltip(font, Component.translatable("gui.synthesis.energy",
                    menu.getEnergy(), IndustrialProcessorBlockEntity.ENERGY_CAPACITY), mouseX, mouseY);
        }
        renderTooltip(g, mouseX, mouseY);
    }
}
