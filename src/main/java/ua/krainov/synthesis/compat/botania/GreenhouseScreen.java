package ua.krainov.synthesis.compat.botania;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import ua.krainov.synthesis.SynthesisMod;

/** Інтерфейс Мультитеплиці. Лише клієнт. */
public class GreenhouseScreen extends AbstractContainerScreen<GreenhouseMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SynthesisMod.MODID, "textures/gui/multi_greenhouse.png");
    private static final int MANA_X = 154, MANA_Y = 40, MANA_W = 12, MANA_H = 52;

    private Button stackButton;

    public GreenhouseScreen(GreenhouseMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 190;
        this.inventoryLabelY = GreenhouseMenu.PLAYER_Y - 11;
    }

    @Override
    protected void init() {
        super.init();
        stackButton = addRenderableWidget(Button.builder(stackText(), b -> {
                    if (minecraft != null && minecraft.gameMode != null) {
                        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, GreenhouseMenu.BUTTON_STACK);
                    }
                })
                .bounds(leftPos + 104, topPos + 3, 66, 13)
                .build());
    }

    private Component stackText() {
        return Component.translatable(menu.isStackMode() ? "gui.synthesis.greenhouse.stack_on" : "gui.synthesis.greenhouse.stack_off");
    }

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        int x = leftPos, y = topPos;
        g.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);
        int h = (int) ((long) MANA_H * menu.getMana() / GreenhouseBlockEntity.CAPACITY);
        if (h > 0) {
            g.fillGradient(x + MANA_X, y + MANA_Y + MANA_H - h, x + MANA_X + MANA_W, y + MANA_Y + MANA_H,
                    0xFF6FD3FF, 0xFF1E6FD0);
        }
        int burn = menu.getBurnPercent();
        if (burn > 0) { // полум'я під слотом палива
            int fh = Math.max(1, 4 * burn / 100);
            g.fill(x + 152, y + 36 + 4 - fh, x + 168, y + 40, 0xFFF08A24);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
        super.renderLabels(g, mouseX, mouseY);
        g.drawString(font, Component.translatable("gui.synthesis.greenhouse.rate", menu.getManaPerSecond()),
                80, inventoryLabelY, 0x2E6FB0, false);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        if (stackButton != null) stackButton.setMessage(stackText());
        super.render(g, mouseX, mouseY, partialTick);
        int rx = mouseX - leftPos, ry = mouseY - topPos;
        if (rx >= MANA_X && rx < MANA_X + MANA_W && ry >= MANA_Y && ry < MANA_Y + MANA_H) {
            g.renderTooltip(font, Component.translatable("gui.synthesis.greenhouse.mana",
                    String.format("%,d", menu.getMana()), String.format("%,d", GreenhouseBlockEntity.CAPACITY)), mouseX, mouseY);
        }
        renderTooltip(g, mouseX, mouseY);
    }
}
