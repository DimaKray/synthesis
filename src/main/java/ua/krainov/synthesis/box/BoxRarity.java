package ua.krainov.synthesis.box;

import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.LootTable;
import org.joml.Vector3f;
import ua.krainov.synthesis.SynthesisMod;

import java.util.Locale;

/** Рідкості Капітал-боксів: колір назви, колір частинок, таблиця луту. */
public enum BoxRarity {
    COMMON(ChatFormatting.WHITE, 0xEEEEEE),
    RARE(ChatFormatting.BLUE, 0x4F8BFF),
    EPIC(ChatFormatting.LIGHT_PURPLE, 0xB06BFF),
    LEGENDARY(ChatFormatting.GOLD, 0xFFC531);

    public final ChatFormatting color;
    public final int rgb;

    BoxRarity(ChatFormatting color, int rgb) {
        this.color = color;
        this.rgb = rgb;
    }

    public String id() {
        return name().toLowerCase(Locale.ROOT);
    }

    public Vector3f particleColor() {
        return new Vector3f(((rgb >> 16) & 0xFF) / 255f, ((rgb >> 8) & 0xFF) / 255f, (rgb & 0xFF) / 255f);
    }

    /** data/synthesis/loot_table/boxes/&lt;рідкість&gt;.json */
    public ResourceKey<LootTable> lootTable() {
        return ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(SynthesisMod.MODID, "boxes/" + id()));
    }

    public static BoxRarity byId(String id) {
        for (BoxRarity r : values()) {
            if (r.id().equalsIgnoreCase(id)) return r;
        }
        return null;
    }
}
