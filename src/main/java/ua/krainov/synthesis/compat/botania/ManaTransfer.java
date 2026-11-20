package ua.krainov.synthesis.compat.botania;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import velrondevs.botania.api.BotaniaCapabilities;
import velrondevs.botania.api.mana.ManaCollector;
import velrondevs.botania.api.mana.ManaPool;
import velrondevs.botania.api.mana.ManaReceiver;

/** Передача мани між блоками через API Ботанії без втрат (не переливаємо через край). */
public final class ManaTransfer {
    /** Скільки мани приймач може ще взяти. Для невідомих приймачів — невеликими порціями. */
    public static int spaceOf(ManaReceiver receiver) {
        if (receiver.isFull()) return 0;
        if (receiver instanceof ManaSpace own) return Math.max(0, own.manaSpace());
        if (receiver instanceof ManaPool pool) return Math.max(0, pool.getMaxMana() - pool.getCurrentMana());
        if (receiver instanceof ManaCollector collector) return Math.max(0, collector.getMaxMana() - collector.getCurrentMana());
        return 1000;
    }

    @Nullable
    public static ManaReceiver find(Level level, BlockPos pos, @Nullable Direction side) {
        return level.getCapability(BotaniaCapabilities.MANA_RECEIVER, pos, side);
    }

    /**
     * Віддає приймачу до {@code max} мани. Повертає, скільки реально передано.
     */
    public static int give(ManaReceiver receiver, int max) {
        int amount = Math.min(max, spaceOf(receiver));
        if (amount <= 0) return 0;
        receiver.receiveMana(amount);
        return amount;
    }

    /**
     * Роздає ману в усі 6 сусідніх приймачів (крім тих, що відкидає {@code skip}).
     * Повертає скільки всього віддано.
     */
    public static int pushToNeighbours(Level level, BlockPos pos, int available, int ratePerTarget,
                                       java.util.function.Predicate<ManaReceiver> skip) {
        int sent = 0;
        for (Direction dir : Direction.values()) {
            if (available - sent <= 0) break;
            ManaReceiver receiver = find(level, pos.relative(dir), dir.getOpposite());
            if (receiver == null || skip.test(receiver)) continue;
            sent += give(receiver, Math.min(ratePerTarget, available - sent));
        }
        return sent;
    }

    private ManaTransfer() {}
}
