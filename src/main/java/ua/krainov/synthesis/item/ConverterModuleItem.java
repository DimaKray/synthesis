package ua.krainov.synthesis.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/** Модулі Перетворювача удачі: удача I–V, плавка, шовковий дотик. */
public class ConverterModuleItem extends Item {
    public enum Kind { FORTUNE, SMELTING, SILK_TOUCH }

    private final Kind kind;
    private final int level;

    public ConverterModuleItem(Kind kind, int level, Properties properties) {
        super(properties);
        this.kind = kind;
        this.level = level;
    }

    public Kind getKind() {
        return kind;
    }

    /** Рівень удачі (лише для FORTUNE). */
    public int getLevel() {
        return level;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        String key = switch (kind) {
            case FORTUNE -> "tooltip.synthesis.module.fortune";
            case SMELTING -> "tooltip.synthesis.module.smelting";
            case SILK_TOUCH -> "tooltip.synthesis.module.silk_touch";
        };
        tooltip.add(Component.translatable(key, level).withStyle(ChatFormatting.GRAY));
    }
}
