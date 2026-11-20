package ua.krainov.synthesis.compat.botania;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import velrondevs.botania.api.BotaniaCapabilities;
import velrondevs.botania.api.mana.ManaReceiver;
import ua.krainov.synthesis.blockentity.AltarBlockEntity;
import ua.krainov.synthesis.registry.ModBlockEntities;

/**
 * Botania Revived: вівтар приймає ману від мана-розповсюджувачів.
 * Викликати лише якщо ModCompat.BOTANIA.
 */
public final class BotaniaCompat {
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(BotaniaCapabilities.MANA_RECEIVER, ModBlockEntities.FUSION_ALTAR.get(),
                (altar, side) -> new AltarManaReceiver(altar));
        event.registerBlockEntity(BotaniaCapabilities.MANA_RECEIVER, ModBlockEntities.CAPITAL_ALTAR.get(),
                (altar, side) -> new AltarManaReceiver(altar));
        BotaniaMachines.registerCapabilities(event);
    }

    /** Обгортка: Botania бачить вівтар як приймач мани. */
    private record AltarManaReceiver(AltarBlockEntity<?> altar) implements ManaReceiver, ManaSpace {
        @Override
        public int manaSpace() {
            return AltarBlockEntity.MANA_CAPACITY - altar.getMana();
        }

        @Override
        public Level getManaReceiverLevel() {
            return altar.getLevel();
        }

        @Override
        public BlockPos getManaReceiverPos() {
            return altar.getBlockPos();
        }

        @Override
        public int getCurrentMana() {
            return altar.getMana();
        }

        @Override
        public boolean isFull() {
            return altar.isManaFull();
        }

        @Override
        public void receiveMana(int mana) {
            altar.receiveMana(mana);
        }

        @Override
        public boolean canReceiveManaFromBursts() {
            return true;
        }
    }

    private BotaniaCompat() {}
}
