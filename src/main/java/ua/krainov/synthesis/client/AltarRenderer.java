package ua.krainov.synthesis.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import ua.krainov.synthesis.blockentity.AltarBlockEntity;

/**
 * Рендерер вівтарів. Центральний предмет під час ритуалу піднімається вище і крутиться швидше.
 * Для Вівтаря Капіталізму ({@code beams = true}) від кожного п'єдестала до центру б'ють промені.
 */
public class AltarRenderer<T extends AltarBlockEntity<?>> implements BlockEntityRenderer<T> {
    private static final float CENTER_Y = 1.45f;

    private final ItemRenderer itemRenderer;
    private final boolean beams;

    public AltarRenderer(BlockEntityRendererProvider.Context context, boolean beams) {
        this.itemRenderer = context.getItemRenderer();
        this.beams = beams;
    }

    @Override
    public void render(T altar, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        Level level = altar.getLevel();
        if (level == null) return;
        boolean active = altar.isCrafting();
        float time = level.getGameTime() + partialTick;

        if (beams && active && !altar.getActivePedestals().isEmpty()) {
            renderBeams(altar, time, poseStack, buffer);
        }

        ItemStack stack = altar.getItem();
        if (stack.isEmpty()) return;

        float height = active ? CENTER_Y : 1.15f;
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

    /** Промені — по дві схрещені смужки від верху кожного п'єдестала до предмета в центрі. */
    private void renderBeams(T altar, float time, PoseStack poseStack, MultiBufferSource buffer) {
        VertexConsumer vc = buffer.getBuffer(RenderType.lightning());
        Matrix4f matrix = poseStack.last().pose();
        BlockPos center = altar.getBlockPos();
        int index = 0;
        for (BlockPos p : altar.getActivePedestals()) {
            float sx = p.getX() - center.getX() + 0.5f;
            float sy = p.getY() - center.getY() + 1.2f;
            float sz = p.getZ() - center.getZ() + 0.5f;
            float pulse = 0.5f + 0.5f * (float) Math.sin(time * 0.25f + index * 0.9f);
            int alpha = (int) (70 + 110 * pulse);
            // Кольори по черзі: золото / фіолет / бірюза — як у текстурах мода
            int[] rgb = switch (index % 3) {
                case 0 -> new int[]{255, 200, 80};
                case 1 -> new int[]{170, 110, 255};
                default -> new int[]{90, 210, 255};
            };
            beam(vc, matrix, sx, sy, sz, 0.5f, CENTER_Y, 0.5f, 0.035f + 0.015f * pulse, rgb, alpha);
            index++;
        }
    }

    private static void beam(VertexConsumer vc, Matrix4f m, float x1, float y1, float z1,
                             float x2, float y2, float z2, float width, int[] rgb, int alpha) {
        Vector3f dir = new Vector3f(x2 - x1, y2 - y1, z2 - z1);
        Vector3f side1 = new Vector3f(dir).cross(0, 1, 0);
        if (side1.lengthSquared() < 1.0e-6f) side1.set(1, 0, 0);
        side1.normalize(width);
        Vector3f side2 = new Vector3f(dir).cross(side1).normalize(width);
        quad(vc, m, x1, y1, z1, x2, y2, z2, side1, rgb, alpha);
        quad(vc, m, x1, y1, z1, x2, y2, z2, side2, rgb, alpha);
    }

    /** Смужка з двох боків (щоб її було видно з будь-якого ракурсу). */
    private static void quad(VertexConsumer vc, Matrix4f m, float x1, float y1, float z1,
                             float x2, float y2, float z2, Vector3f s, int[] c, int a) {
        vertex(vc, m, x1 + s.x, y1 + s.y, z1 + s.z, c, a);
        vertex(vc, m, x2 + s.x, y2 + s.y, z2 + s.z, c, a);
        vertex(vc, m, x2 - s.x, y2 - s.y, z2 - s.z, c, a);
        vertex(vc, m, x1 - s.x, y1 - s.y, z1 - s.z, c, a);

        vertex(vc, m, x1 - s.x, y1 - s.y, z1 - s.z, c, a);
        vertex(vc, m, x2 - s.x, y2 - s.y, z2 - s.z, c, a);
        vertex(vc, m, x2 + s.x, y2 + s.y, z2 + s.z, c, a);
        vertex(vc, m, x1 + s.x, y1 + s.y, z1 + s.z, c, a);
    }

    private static void vertex(VertexConsumer vc, Matrix4f m, float x, float y, float z, int[] c, int a) {
        vc.addVertex(m, x, y, z).setColor(c[0], c[1], c[2], a);
    }

    /** Промені виходять за межі блока — розширюємо рамку, щоб їх не обрізало. */
    @Override
    public AABB getRenderBoundingBox(T altar) {
        return new AABB(altar.getBlockPos()).inflate(AltarBlockEntity.RADIUS + 1);
    }
}
