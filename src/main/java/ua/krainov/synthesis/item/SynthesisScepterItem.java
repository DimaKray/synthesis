package ua.krainov.synthesis.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import ua.krainov.synthesis.blockentity.AltarBlockEntity;

/**
 * Скіпетр синтезу. ПКМ по вівтарю — запустити ритуал; Shift+ПКМ — показати стан і чого бракує.
 */
public class SynthesisScepterItem extends Item {
    public SynthesisScepterItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        // Прив'язка Драконієвого розповсюджувача (лише з Ботанією)
        if (ua.krainov.synthesis.compat.ModCompat.BOTANIA) {
            InteractionResult bind = ua.krainov.synthesis.compat.botania.BotaniaMachines.scepterUse(context);
            if (bind != null) return bind;
        }
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (player == null || !(level.getBlockEntity(context.getClickedPos()) instanceof AltarBlockEntity<?> altar)) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            if (player.isShiftKeyDown()) {
                altar.describe(player);
            } else {
                altar.tryStart(player);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
