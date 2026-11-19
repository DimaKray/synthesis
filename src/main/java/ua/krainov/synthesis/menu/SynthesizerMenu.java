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
import ua.krainov.synthesis.blockentity.SynthesizerBlockEntity;
import ua.krainov.synthesis.registry.ModBlocks;
import ua.krainov.synthesis.registry.ModMenus;

/** Меню синтезатора: 3 входи, вихід, інвентар гравця + числа для шкал. */
public class SynthesizerMenu extends AbstractContainerMenu {
    // Координати слотів (лівий верхній кут предмета) — мають збігатися з текстурою інтерфейсу
    public static final int[][] INPUT_POS = {{56, 17}, {56, 35}, {56, 53}};
    public static final int OUTPUT_X = 120;
    public static final int OUTPUT_Y = 35;

    private static final int MACHINE_SLOTS = 4;
    private static final int PLAYER_SLOTS = 36;

    private final SynthesizerBlockEntity machine;
    private final ContainerData data;
    private final ContainerLevelAccess access;

    /** Клієнт: позицію машини присилає сервер. */
    public SynthesizerMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf buf) {
        this(containerId, inventory,
                (SynthesizerBlockEntity) inventory.player.level().getBlockEntity(buf.readBlockPos()),
                new SimpleContainerData(7));
    }

    /** Сервер. */
    public SynthesizerMenu(int containerId, Inventory inventory, SynthesizerBlockEntity machine, ContainerData data) {
        super(ModMenus.SYNTHESIZER.get(), containerId);
        this.machine = machine;
        this.data = data;
        this.access = ContainerLevelAccess.create(machine.getLevel(), machine.getBlockPos());

        for (int i = 0; i < SynthesizerBlockEntity.INPUT_SLOTS; i++) {
            addSlot(new SlotItemHandler(machine.getItems(), i, INPUT_POS[i][0], INPUT_POS[i][1]));
        }
        addSlot(new SlotItemHandler(machine.getItems(), SynthesizerBlockEntity.OUTPUT_SLOT, OUTPUT_X, OUTPUT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

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

    public SynthesizerBlockEntity getMachine() {
        return machine;
    }

    public int getProgress() {
        return data.get(0);
    }

    public int getMaxProgress() {
        return data.get(1);
    }

    public int getRpm() {
        return data.get(2);
    }

    public int getMinRpm() {
        return data.get(3);
    }

    public SynthesizerBlockEntity.Status getStatus() {
        int i = data.get(4);
        SynthesizerBlockEntity.Status[] values = SynthesizerBlockEntity.Status.values();
        return i >= 0 && i < values.length ? values[i] : SynthesizerBlockEntity.Status.IDLE;
    }

    public int getEnergy() {
        return (data.get(5) & 0xFFFF) | ((data.get(6) & 0xFFFF) << 16);
    }

    /** Shift+клік: з машини — в інвентар, з інвентаря — у вхідні слоти. */
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();

        if (index < MACHINE_SLOTS) {
            if (!moveItemStackTo(stack, MACHINE_SLOTS, MACHINE_SLOTS + PLAYER_SLOTS, true)) return ItemStack.EMPTY;
        } else {
            if (!moveItemStackTo(stack, 0, SynthesizerBlockEntity.INPUT_SLOTS, false)) return ItemStack.EMPTY;
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
        return stillValid(access, player, ModBlocks.SYNTHESIZER.get());
    }
}
