package ua.krainov.synthesis.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import ua.krainov.synthesis.SynthesisMod;
import ua.krainov.synthesis.blockentity.SynthesizerBlockEntity;
import ua.krainov.synthesis.menu.SynthesizerMenu;

import java.util.List;

/** Інтерфейс синтезатора: шкала FE, бак рідини, 3 входи, стрілка прогресу, вихід. */
public class SynthesizerScreen extends AbstractContainerScreen<SynthesizerMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SynthesisMod.MODID, "textures/gui/synthesizer.png");

    // Внутрішні області шкал (у координатах інтерфейсу) — збігаються з текстурою
    private static final int ENERGY_X = 10, ENERGY_Y = 18, ENERGY_W = 12, ENERGY_H = 52;
    private static final int FLUID_X = 28, FLUID_Y = 18, FLUID_W = 16, FLUID_H = 52;
    private static final int ARROW_X = 82, ARROW_Y = 35, ARROW_W = 24, ARROW_H = 17;

    public SynthesizerScreen(SynthesizerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        int x = leftPos, y = topPos;
        g.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // Стрілка прогресу (заповнена копія лежить у текстурі праворуч, u=176)
        int max = menu.getMaxProgress();
        if (max > 0 && menu.getProgress() > 0) {
            int w = Math.min(ARROW_W, menu.getProgress() * ARROW_W / max);
            g.blit(TEXTURE, x + ARROW_X, y + ARROW_Y, 176, 0, w, ARROW_H);
        }

        // Енергія — знизу вгору
        int energy = menu.getEnergy();
        int eh = (int) ((long) ENERGY_H * energy / SynthesizerBlockEntity.ENERGY_CAPACITY);
        if (eh > 0) {
            g.fillGradient(x + ENERGY_X, y + ENERGY_Y + ENERGY_H - eh, x + ENERGY_X + ENERGY_W,
                    y + ENERGY_Y + ENERGY_H, 0xFFFF5A3C, 0xFFB0201A);
        }

        renderFluid(g, x + FLUID_X, y + FLUID_Y);
    }

    private void renderFluid(GuiGraphics g, int x, int y) {
        FluidStack fluid = menu.getMachine().getTank().getFluid();
        if (fluid.isEmpty()) return;
        int h = (int) ((long) FLUID_H * fluid.getAmount() / SynthesizerBlockEntity.TANK_CAPACITY);
        if (h <= 0) h = 1;

        IClientFluidTypeExtensions ext = IClientFluidTypeExtensions.of(fluid.getFluid());
        TextureAtlasSprite sprite = minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(ext.getStillTexture(fluid));
        int tint = ext.getTintColor(fluid);
        float a = ((tint >> 24) & 0xFF) / 255f;
        float r = ((tint >> 16) & 0xFF) / 255f;
        float gr = ((tint >> 8) & 0xFF) / 255f;
        float b = (tint & 0xFF) / 255f;

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(r, gr, b, a == 0 ? 1f : a);
        // Малюємо шматками по 16 пікселів знизу вгору
        int bottom = y + FLUID_H;
        for (int drawn = 0; drawn < h; drawn += 16) {
            int seg = Math.min(16, h - drawn);
            g.blit(x, bottom - drawn - seg, 0, FLUID_W, seg, sprite);
        }
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
        super.renderLabels(g, mouseX, mouseY);
        SynthesizerBlockEntity.Status status = menu.getStatus();
        int color = switch (status) {
            case WORKING -> 0x2E7D32;
            case IDLE -> 0x555555;
            default -> 0xB71C1C;
        };
        g.drawString(font, Component.translatable("gui.synthesis.status." + status.name().toLowerCase(java.util.Locale.ROOT)),
                78, 20, color, false);
        if (menu.getMinRpm() > 0) {
            int rpmColor = menu.getRpm() >= menu.getMinRpm() ? 0x2E7D32 : 0xB71C1C;
            g.drawString(font, Component.translatable("gui.synthesis.rpm", menu.getRpm(), menu.getMinRpm()),
                    78, 58, rpmColor, false);
        }
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        int rx = mouseX - leftPos, ry = mouseY - topPos;

        if (inside(rx, ry, ENERGY_X, ENERGY_Y, ENERGY_W, ENERGY_H)) {
            g.renderTooltip(font, Component.translatable("gui.synthesis.energy",
                    menu.getEnergy(), SynthesizerBlockEntity.ENERGY_CAPACITY), mouseX, mouseY);
        } else if (inside(rx, ry, FLUID_X, FLUID_Y, FLUID_W, FLUID_H)) {
            FluidStack fluid = menu.getMachine().getTank().getFluid();
            Component text = fluid.isEmpty()
                    ? Component.translatable("gui.synthesis.tank_empty", SynthesizerBlockEntity.TANK_CAPACITY)
                    : Component.translatable("gui.synthesis.tank", fluid.getHoverName(),
                    fluid.getAmount(), SynthesizerBlockEntity.TANK_CAPACITY);
            g.renderTooltip(font, text, mouseX, mouseY);
        } else if (inside(rx, ry, ARROW_X, ARROW_Y, ARROW_W, ARROW_H) && menu.getMaxProgress() > 0) {
            int percent = menu.getProgress() * 100 / menu.getMaxProgress();
            g.renderComponentTooltip(font, List.of(
                    Component.translatable("gui.synthesis.progress", percent),
                    Component.translatable("gui.synthesis.status." + menu.getStatus().name().toLowerCase(java.util.Locale.ROOT))
                            .withStyle(ChatFormatting.GRAY)), mouseX, mouseY);
        }
        renderTooltip(g, mouseX, mouseY);
    }

    private static boolean inside(int x, int y, int left, int top, int w, int h) {
        return x >= left && x < left + w && y >= top && y < top + h;
    }
}
