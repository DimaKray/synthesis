package ua.krainov.synthesis.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import ua.krainov.synthesis.blockentity.PedestalBlockEntity;

/** Малює предмет над п'єдесталом: повільно обертається і левітує. */
public class PedestalRenderer implements BlockEntityRenderer<PedestalBlockEntity> {
    private final ItemRenderer itemRenderer;

    public PedestalRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(PedestalBlockEntity pedestal, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        ItemStack stack = pedestal.getItem();
        Level level = pedestal.getLevel();
        if (stack.isEmpty() || level == null) return;

        float time = level.getGameTime() + partialTick;
        float bob = (float) Math.sin(time / 10.0) * 0.05f;

        poseStack.pushPose();
        poseStack.translate(0.5, 1.2 + bob, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees((time * 2.0f) % 360.0f));
        poseStack.scale(0.6f, 0.6f, 0.6f);

        // Світло беремо з блоку над п'єдесталом, інакше предмет буде темним
        int light = LevelRenderer.getLightColor(level, pedestal.getBlockPos().above());
        itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, light, OverlayTexture.NO_OVERLAY,
                poseStack, buffer, level, 0);

        poseStack.popPose();
    }
}
