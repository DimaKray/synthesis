package ua.krainov.synthesis.compat.botania;

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

/** Меню Мультитеплиці: 32 слоти квітів (8×4), слот палива, кнопка «Стак». */
public class GreenhouseMenu extends AbstractContainerMenu {
    public static final int BUTTON_STACK = 0;
    public static final int FUEL_X = 152, FUEL_Y = 18;
    public static final int PLAYER_Y = 108;

    private static final int MACHINE_SLOTS = GreenhouseBlockEntity.FLOWER_SLOTS + 1;
    private static final int PLAYER_SLOTS = 36;

    private final GreenhouseBlockEntity machine;
    private final ContainerData data;
    private final ContainerLevelAccess access;

    public GreenhouseMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf buf) {
        this(containerId, inventory,
                (GreenhouseBlockEntity) inventory.player.level().getBlockEntity(buf.readBlockPos()),
                new SimpleContainerData(6));
    }

    public GreenhouseMenu(int containerId, Inventory inventory, GreenhouseBlockEntity machine, ContainerData data) {
        super(BotaniaMachines.GREENHOUSE_MENU.get(), containerId);
        this.machine = machine;
        this.data = data;
        this.access = ContainerLevelAccess.create(machine.getLevel(), machine.getBlockPos());

        for (int i = 0; i < GreenhouseBlockEntity.FLOWER_SLOTS; i++) {
            addSlot(new SlotItemHandler(machine.getItems(), i, 8 + (i % 8) * 18, 18 + (i / 8) * 18));
        }
        addSlot(new SlotItemHandler(machine.getItems(), GreenhouseBlockEntity.SLOT_FUEL, FUEL_X, FUEL_Y));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, PLAYER_Y + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inventory, col, 8 + col * 18, PLAYER_Y + 58));
        }
        addDataSlots(data);
    }

    public int getMana() {
        return (data.get(0) & 0xFFFF) | ((data.get(1) & 0xFFFF) << 16);
    }

    public int getManaPerSecond() {
        return (data.get(2) & 0xFFFF) | ((data.get(3) & 0xFFFF) << 16);
    }

    public boolean isStackMode() {
        return data.get(4) != 0;
    }

    public int getBurnPercent() {
        return data.get(5);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == BUTTON_STACK) {
            machine.toggleStackMode();
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
        return stillValid(access, player, BotaniaMachines.GREENHOUSE.get());
    }
}
