package ua.krainov.synthesis.block;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

/** Рівні Прискорювача: у скільки разів частіше тікає сусідня машина. */
public enum AcceleratorTier implements StringRepresentable {
    X2("x2", 2, 100_000),
    X4("x4", 4, 400_000),
    X8("x8", 8, 1_600_000),
    X16("x16", 16, 6_400_000);

    public static final Codec<AcceleratorTier> CODEC = StringRepresentable.fromEnum(AcceleratorTier::values);

    private final String name;
    public final int multiplier;
    public final int energyCapacity;

    AcceleratorTier(String name, int multiplier, int energyCapacity) {
        this.name = name;
        this.multiplier = multiplier;
        this.energyCapacity = energyCapacity;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
