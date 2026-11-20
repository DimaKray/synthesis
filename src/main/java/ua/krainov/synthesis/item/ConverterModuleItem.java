package ua.krainov.synthesis.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/** Модулі машин: удача I–V, плавка, шовковий дотик (Перетворювач удачі); стековий I–III (Промисловий процесор). */
public class ConverterModuleItem extends Item {
    public enum Kind { FORTUNE, SMELTING, SILK_TOUCH, STACK }

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

    /** Скільки предметів за операцію Промислового процесора: без модуля 8, I — 16, II — 32, III — 64. */
    public static int stackCapacity(int level) {
        return 8 << Math.max(0, Math.min(3, level));
    }

    /** Рівень удачі (FORTUNE) або рівень стекового модуля (STACK). */
    public int getLevel() {
        return level;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        String key = switch (kind) {
            case FORTUNE -> "tooltip.synthesis.module.fortune";
            case SMELTING -> "tooltip.synthesis.module.smelting";
            case SILK_TOUCH -> "tooltip.synthesis.module.silk_touch";
            case STACK -> "tooltip.synthesis.module.stack";
        };
        Object arg = kind == Kind.STACK ? stackCapacity(level) : level;
        tooltip.add(Component.translatable(key, arg).withStyle(ChatFormatting.GRAY));
    }
}
