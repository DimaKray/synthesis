package ua.krainov.synthesis.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

/** Анімація відкриття Капітал-боксу: поки затиснута ПКМ, бокс виїжджає до центру й крутиться дедалі швидше. */
public class CapitalBoxClientExtension implements IClientItemExtensions {
    @Override
    public boolean applyForgeHandTransform(PoseStack poseStack, LocalPlayer player, HumanoidArm arm,
                                           ItemStack itemInHand, float partialTick, float equipProcess,
                                           float swingProcess) {
        if (!player.isUsingItem() || player.getUseItem() != itemInHand) return false;

        int side = arm == HumanoidArm.RIGHT ? 1 : -1;
        int total = itemInHand.getUseDuration(player);
        float used = total - (player.getUseItemRemainingTicks() - partialTick + 1.0f);
        float t = Math.max(0f, Math.min(1f, used / total));

        // Звичайне положення предмета в руці, плавно зсуваємо до центру екрана
        poseStack.translate(side * 0.56f * (1f - 0.6f * t), -0.52f + 0.18f * t, -0.72f);
        poseStack.translate(0, (float) Math.sin(used * 0.6f) * 0.02f * t, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(used * used * 0.8f)); // прискорене обертання
        return true;
    }
}
