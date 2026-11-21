package ua.krainov.synthesis.compat.botania;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import org.joml.Matrix4f;

/** Рівень мани в Великому басейні — анімована текстура мани Ботанії, як у звичайному басейні. Лише клієнт. */
public class GreatManaPoolRenderer implements BlockEntityRenderer<GreatManaPoolBlockEntity> {
    private static final ResourceLocation MANA = ResourceLocation.fromNamespaceAndPath("botania", "block/mana_water");

    public GreatManaPoolRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(GreatManaPoolBlockEntity pool, float partialTick, PoseStack poseStack, MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {
        float fill = pool.getClientFill();
        if (fill <= 0) return;
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(MANA);
        // Усередині басейну: від дна (2/16) до верху стінок (8/16)
        float y = (2.0f + 5.8f * Math.min(1f, fill)) / 16f;
        float min = 2f / 16f, max = 14f / 16f;
        VertexConsumer vc = buffer.getBuffer(RenderType.translucent());
        Matrix4f m = poseStack.last().pose();
        PoseStack.Pose pose = poseStack.last();
        float u0 = sprite.getU(0.125f), u1 = sprite.getU(0.875f), v0 = sprite.getV(0.125f), v1 = sprite.getV(0.875f);
        vertex(vc, m, pose, min, y, min, u0, v0, packedLight);
        vertex(vc, m, pose, min, y, max, u0, v1, packedLight);
        vertex(vc, m, pose, max, y, max, u1, v1, packedLight);
        vertex(vc, m, pose, max, y, min, u1, v0, packedLight);
    }

    private static void vertex(VertexConsumer vc, Matrix4f m, PoseStack.Pose pose, float x, float y, float z,
                               float u, float v, int light) {
        vc.addVertex(m, x, y, z).setColor(255, 255, 255, 230).setUv(u, v).setLight(light).setNormal(pose, 0, 1, 0);
    }
}
