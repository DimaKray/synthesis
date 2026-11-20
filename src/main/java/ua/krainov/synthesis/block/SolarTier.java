package ua.krainov.synthesis.block;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import ua.krainov.synthesis.config.SynthesisConfig;

/** Рівні Капітал-панелей. Генерація — у synthesis-common.toml. */
public enum SolarTier implements StringRepresentable {
    HYBRID("hybrid", false),
    ULTIMATE("ultimate", false),
    QUANTUM("quantum", false),
    INFINITE("infinite", true);

    public static final Codec<SolarTier> CODEC = StringRepresentable.fromEnum(SolarTier::values);

    private final String name;
    /** Не зважає на дощ. */
    public final boolean ignoresRain;

    SolarTier(String name, boolean ignoresRain) {
        this.name = name;
        this.ignoresRain = ignoresRain;
    }

    public int dayOutput() {
        return SynthesisConfig.SOLAR_DAY[ordinal()].get();
    }

    public int nightOutput() {
        return SynthesisConfig.SOLAR_NIGHT[ordinal()].get();
    }

    /** Буфер — на 10 секунд денної генерації (мінімум 10 000 FE). */
    public int bufferCapacity() {
        return (int) Math.min(Integer.MAX_VALUE, Math.max(10_000L, dayOutput() * 200L));
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
