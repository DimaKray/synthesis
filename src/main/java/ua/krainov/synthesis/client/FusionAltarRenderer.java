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
import ua.krainov.synthesis.blockentity.FusionAltarBlockEntity;

/** Центральний предмет вівтаря. Під час ритуалу піднімається вище і крутиться швидше. */
public class FusionAltarRenderer implements BlockEntityRenderer<FusionAltarBlockEntity> {
    private final ItemRenderer itemRenderer;

    public FusionAltarRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(FusionAltarBlockEntity altar, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        ItemStack stack = altar.getItem();
        Level level = altar.getLevel();
        if (stack.isEmpty() || level == null) return;

        boolean active = altar.isCrafting();
        float time = level.getGameTime() + partialTick;
        float height = active ? 1.45f : 1.15f;
        float bob = (float) Math.sin(time / (active ? 5.0 : 10.0)) * (active ? 0.08f : 0.05f);
        float speed = active ? 10.0f : 2.0f;

        poseStack.pushPose();
        poseStack.translate(0.5, height + bob, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees((time * speed) % 360.0f));
        poseStack.scale(0.7f, 0.7f, 0.7f);

        int light = LevelRenderer.getLightColor(level, altar.getBlockPos().above());
        itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, light, OverlayTexture.NO_OVERLAY,
                poseStack, buffer, level, 0);

        poseStack.popPose();
    }
}
