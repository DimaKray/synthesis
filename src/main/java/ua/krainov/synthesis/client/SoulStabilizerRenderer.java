package ua.krainov.synthesis.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import ua.krainov.synthesis.blockentity.SoulStabilizerBlockEntity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** Мініатюра моба всередині Стабілізатора (як у спавнера), повільно обертається. */
public class SoulStabilizerRenderer implements BlockEntityRenderer<SoulStabilizerBlockEntity> {
    /** Моби для показу — по одному на тип, лише на клієнті, у світ не додаються. */
    private static final Map<ResourceLocation, Entity> DISPLAY = new HashMap<>();
    /** Моби, яких не вдалося намалювати, — більше не пробуємо. */
    private static final Set<ResourceLocation> FAILED = new HashSet<>();

    public SoulStabilizerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(SoulStabilizerBlockEntity machine, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        Level level = machine.getLevel();
        ResourceLocation id = SoulStabilizerBlockEntity.readSoulId(machine.getSoul());
        if (level == null || id == null || FAILED.contains(id)) return;

        Entity entity = DISPLAY.computeIfAbsent(id, key -> {
            EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(key).orElse(null);
            return type == null ? null : type.create(level);
        });
        if (entity == null) return;
        if (entity.level() != level) {
            DISPLAY.remove(id); // світ змінився — створимо заново наступного кадру
            return;
        }

        float size = Math.max(entity.getBbWidth(), entity.getBbHeight());
        float scale = 0.5f / Math.max(1.0f, size);
        float time = level.getGameTime() + partialTick;

        poseStack.pushPose();
        poseStack.translate(0.5, 0.15, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees((time * 1.5f) % 360));
        poseStack.scale(scale, scale, scale);
        try {
            Minecraft.getInstance().getEntityRenderDispatcher()
                    .render(entity, 0, 0, 0, 0, partialTick, poseStack, buffer, packedLight);
        } catch (RuntimeException e) {
            FAILED.add(id); // деякі моби не вміють малюватися без світу — просто не показуємо
        }
        poseStack.popPose();
    }
}
