package ua.krainov.synthesis.box;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import ua.krainov.synthesis.config.SynthesisConfig;
import ua.krainov.synthesis.core.BoxMath;
import ua.krainov.synthesis.registry.ModAttachments;
import ua.krainov.synthesis.registry.ModItems;

import java.time.DayOfWeek;
import java.time.LocalDate;

/** Видача Капітал-боксів: лічильник активної гри, кидок шансу, вибір рідкості. */
public final class BoxService {
    /** Викликається раз на секунду для кожного гравця на сервері. */
    public static void tickSecond(ServerPlayer player) {
        BoxTimer timer = player.getData(ModAttachments.BOX_TIMER);

        boolean moved = !timer.hasSnapshot
                || player.getX() != timer.lastX || player.getY() != timer.lastY || player.getZ() != timer.lastZ
                || player.getYRot() != timer.lastYaw || player.getXRot() != timer.lastPitch;
        boolean active = (timer.hasSnapshot && moved) || timer.acted;
        timer.hasSnapshot = true;
        timer.lastX = player.getX();
        timer.lastY = player.getY();
        timer.lastZ = player.getZ();
        timer.lastYaw = player.getYRot();
        timer.lastPitch = player.getXRot();
        timer.acted = false;

        timer.idleSeconds = active ? 0 : timer.idleSeconds + 1;
        if (timer.idleSeconds >= SynthesisConfig.BOX_AFK_MINUTES.get() * 60) return; // AFK — лічильник стоїть

        timer.activeSeconds++;
        if (timer.activeSeconds >= SynthesisConfig.BOX_INTERVAL_MINUTES.get() * 60) {
            timer.activeSeconds = 0;
            roll(player);
        }
    }

    public static boolean isWeekend() {
        DayOfWeek day = LocalDate.now().getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    public static double currentChance() {
        return BoxMath.effectiveChance(SynthesisConfig.BOX_CHANCE.get(), isWeekend(),
                SynthesisConfig.BOX_WEEKEND_MULTIPLIER.get(), SynthesisConfig.BOX_WEEKEND_MAX_CHANCE.get());
    }

    /** Кидок шансу; при успіху — бокс випадкової рідкості. */
    public static void roll(ServerPlayer player) {
        if (player.getRandom().nextDouble() >= currentChance()) return;
        int[] weights = {
                SynthesisConfig.BOX_WEIGHT_COMMON.get(), SynthesisConfig.BOX_WEIGHT_RARE.get(),
                SynthesisConfig.BOX_WEIGHT_EPIC.get(), SynthesisConfig.BOX_WEIGHT_LEGENDARY.get()};
        BoxRarity rarity = BoxRarity.values()[BoxMath.pickWeighted(weights, player.getRandom().nextDouble())];
        give(player, rarity);
    }

    /** Кладе бокс в інвентар (повний — падає під ноги) і повідомляє. */
    public static void give(ServerPlayer player, BoxRarity rarity) {
        ItemStack box = new ItemStack(boxItem(rarity));
        Component boxName = box.getHoverName();
        ItemHandlerHelper.giveItemToPlayer(player, box);
        player.sendSystemMessage(Component.translatable("message.synthesis.box.received", boxName)
                .withStyle(ChatFormatting.GREEN));
        if (rarity == BoxRarity.LEGENDARY && SynthesisConfig.BOX_ANNOUNCE_LEGENDARY.get() && player.getServer() != null) {
            player.getServer().getPlayerList().broadcastSystemMessage(
                    Component.translatable("message.synthesis.box.announce", player.getDisplayName(), boxName)
                            .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD), false);
        }
    }

    public static Item boxItem(BoxRarity rarity) {
        return switch (rarity) {
            case COMMON -> ModItems.CAPITAL_BOX_COMMON.get();
            case RARE -> ModItems.CAPITAL_BOX_RARE.get();
            case EPIC -> ModItems.CAPITAL_BOX_EPIC.get();
            case LEGENDARY -> ModItems.CAPITAL_BOX_LEGENDARY.get();
        };
    }

    private BoxService() {}
}
