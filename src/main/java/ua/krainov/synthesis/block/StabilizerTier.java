package ua.krainov.synthesis.block;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

/** Рівні Стабілізатора душ (прив'язка до ядер Draconic Evolution). */
public enum StabilizerTier implements StringRepresentable {
    BASIC("basic", 400, 0, 1, 500_000),
    WYVERN("wyvern", 200, 1, 1, 1_000_000),
    AWAKENED("awakened", 100, 2, 2, 4_000_000),
    CHAOTIC("chaotic", 40, 3, 4, 16_000_000);

    public static final Codec<StabilizerTier> CODEC = StringRepresentable.fromEnum(StabilizerTier::values);

    private final String name;
    /** Тривалість циклу в тіках (20 тіків = 1 с). */
    public final int cycleTicks;
    /** Рівень «Добичі» (Looting) віртуальної зброї. */
    public final int looting;
    /** Скільки мобів «вбивається» за цикл. */
    public final int mobsPerCycle;
    /** Місткість буфера FE. */
    public final int energyCapacity;

    StabilizerTier(String name, int cycleTicks, int looting, int mobsPerCycle, int energyCapacity) {
        this.name = name;
        this.cycleTicks = cycleTicks;
        this.looting = looting;
        this.mobsPerCycle = mobsPerCycle;
        this.energyCapacity = energyCapacity;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
