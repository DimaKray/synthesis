package ua.krainov.synthesis.box;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Лічильник активної гри гравця (data attachment, зберігається з гравцем і переживає смерть).
 * activeSeconds — скільки секунд активної гри з останнього кидка; idleSeconds — скільки секунд гравець нічого не робить.
 */
public class BoxTimer {
    public static final Codec<BoxTimer> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.optionalFieldOf("active", 0).forGetter(t -> t.activeSeconds),
            Codec.INT.optionalFieldOf("idle", 0).forGetter(t -> t.idleSeconds)
    ).apply(inst, BoxTimer::new));

    public int activeSeconds;
    public int idleSeconds;

    // Не зберігаються: останнє положення/поворот голови та «щось зробив» між перевірками
    boolean hasSnapshot;
    double lastX, lastY, lastZ;
    float lastYaw, lastPitch;
    boolean acted;

    public BoxTimer() {
        this(0, 0);
    }

    public BoxTimer(int activeSeconds, int idleSeconds) {
        this.activeSeconds = activeSeconds;
        this.idleSeconds = idleSeconds;
    }

    /** Гравець щось зробив (ламав, бив, клацав, писав у чат). */
    public void markActive() {
        acted = true;
    }
}
