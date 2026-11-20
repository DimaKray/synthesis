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
import ua.krainov.synthesis.blockentity.FortuneConverterBlockEntity;
import ua.krainov.synthesis.registry.ModBlocks;
import ua.krainov.synthesis.registry.ModMenus;

/** Меню Перетворювача удачі: руда, інструмент, Модуль удачі, 2 апгрейди + інвентар гравця. */
public class FortuneConverterMenu extends AbstractContainerMenu {
    /** Координати слотів (лівий верхній кут предмета) у порядку слотів машини. */
    public static final int[][] SLOT_POS = {{34, 26}, {92, 17}, {110, 17}, {92, 35}, {110, 35}};

    private static final int MACHINE_SLOTS = FortuneConverterBlockEntity.SLOTS;
    private static final int PLAYER_SLOTS = 36;

    private final FortuneConverterBlockEntity machine;
    private final ContainerData data;
    private final ContainerLevelAccess access;

    public FortuneConverterMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf buf) {
        this(containerId, inventory,
                (FortuneConverterBlockEntity) inventory.player.level().getBlockEntity(buf.readBlockPos()),
                new SimpleContainerData(7));
    }

    public FortuneConverterMenu(int containerId, Inventory inventory, FortuneConverterBlockEntity machine, ContainerData data) {
        super(ModMenus.FORTUNE_CONVERTER.get(), containerId);
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

    public FortuneConverterBlockEntity.Status getStatus() {
        int i = data.get(4);
        FortuneConverterBlockEntity.Status[] values = FortuneConverterBlockEntity.Status.values();
        return i >= 0 && i < values.length ? values[i] : FortuneConverterBlockEntity.Status.IDLE;
    }

    public int getFortune() {
        return data.get(5);
    }

    public boolean hasSilk() {
        return (data.get(6) & 1) != 0;
    }

    public boolean hasSmelting() {
        return (data.get(6) & 2) != 0;
    }

    /** Shift+клік: з машини — в інвентар; з інвентаря — у підходящий слот машини. */
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();

        if (index < MACHINE_SLOTS) {
            if (!moveItemStackTo(stack, MACHINE_SLOTS, MACHINE_SLOTS + PLAYER_SLOTS, true)) return ItemStack.EMPTY;
        } else {
            if (!moveItemStackTo(stack, 0, MACHINE_SLOTS, false)) return ItemStack.EMPTY;
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
        return stillValid(access, player, ModBlocks.FORTUNE_CONVERTER.get());
    }
}
