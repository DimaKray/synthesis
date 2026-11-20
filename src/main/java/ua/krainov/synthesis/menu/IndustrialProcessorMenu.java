package ua.krainov.synthesis.menu;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;
import ua.krainov.synthesis.blockentity.IndustrialProcessorBlockEntity;
import ua.krainov.synthesis.registry.ModBlocks;
import ua.krainov.synthesis.registry.ModMenus;

/** Меню Промислового процесора. Кнопка режиму працює через ванільний clickMenuButton (без своїх пакетів). */
public class IndustrialProcessorMenu extends AbstractContainerMenu {
    public static final int BUTTON_MODE = 0;
    /** Позиції слотів: вхід, модуль, 6 виходів (3×2). */
    public static final int[][] SLOT_POS = {{34, 17}, {34, 53},
            {88, 26}, {106, 26}, {124, 26}, {88, 44}, {106, 44}, {124, 44}};

    private static final int MACHINE_SLOTS = IndustrialProcessorBlockEntity.SLOTS;
    private static final int PLAYER_SLOTS = 36;

    private final IndustrialProcessorBlockEntity machine;
    private final ContainerData data;
    private final ContainerLevelAccess access;

    public IndustrialProcessorMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf buf) {
        this(containerId, inventory,
                (IndustrialProcessorBlockEntity) inventory.player.level().getBlockEntity(buf.readBlockPos()),
                new SimpleContainerData(7));
    }

    public IndustrialProcessorMenu(int containerId, Inventory inventory, IndustrialProcessorBlockEntity machine, ContainerData data) {
        super(ModMenus.INDUSTRIAL_PROCESSOR.get(), containerId);
        this.machine = machine;
        this.data = data;
        this.access = ContainerLevelAccess.create(machine.getLevel(), machine.getBlockPos());

        for (int i = 0; i < MACHINE_SLOTS; i++) {
            addSlot(new SlotItemHandler(machine.getItems(), i, SLOT_POS[i][0], SLOT_POS[i][1]));
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inventory, col, 8 + col * 18, 142));
        }
        addDataSlots(data);
    }

    public int getProgress() {
        return data.get(0);
    }

    public int getMaxProgress() {
        return data.get(1);
    }

    public int getEnergy() {
        return (data.get(2) & 0xFFFF) | ((data.get(3) & 0xFFFF) << 16);
    }

    public IndustrialProcessorBlockEntity.Status getStatus() {
        int i = data.get(4);
        var values = IndustrialProcessorBlockEntity.Status.values();
        return i >= 0 && i < values.length ? values[i] : IndustrialProcessorBlockEntity.Status.IDLE;
    }

    public IndustrialProcessorBlockEntity.Mode getMode() {
        int i = data.get(5);
        var values = IndustrialProcessorBlockEntity.Mode.values();
        return i >= 0 && i < values.length ? values[i] : IndustrialProcessorBlockEntity.Mode.SMELTING;
    }

    public int getBatch() {
        return data.get(6);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == BUTTON_MODE) {
            machine.cycleMode();
            return true;
        }
        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();

        if (index < MACHINE_SLOTS) {
            if (!moveItemStackTo(stack, MACHINE_SLOTS, MACHINE_SLOTS + PLAYER_SLOTS, true)) return ItemStack.EMPTY;
        } else {
            // у вхід або в слот модуля (виходи самі не приймають)
            if (!moveItemStackTo(stack, 0, 2, false)) return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        if (stack.getCount() == copy.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, stack);
        return copy;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModBlocks.INDUSTRIAL_PROCESSOR.get());
    }
}
