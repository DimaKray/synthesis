package ua.krainov.synthesis.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import ua.krainov.synthesis.registry.ModBlockEntities;

/**
 * Дані п'єдестала: один слот на один предмет.
 * Інвентар відкритий для хоперів і труб Create через capability (див. SynthesisMod).
 */
public class PedestalBlockEntity extends BlockEntity {
    private final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide) {
                // Повідомляємо клієнтів, щоб рендерер показав новий предмет
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
            }
        }
    };

    public PedestalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PEDESTAL.get(), pos, state);
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public ItemStack getItem() {
        return inventory.getStackInSlot(0);
    }

    public boolean isEmpty() {
        return getItem().isEmpty();
    }

    /** Кладе копію одного предмета зі стеку. Повертає true, якщо вдалося. */
    public boolean placeItem(ItemStack stack) {
        if (!isEmpty() || stack.isEmpty()) return false;
        inventory.setStackInSlot(0, stack.copyWithCount(1));
        return true;
    }

    /** Забирає предмет з п'єдестала. */
    public ItemStack takeItem() {
        ItemStack taken = getItem().copy();
        inventory.setStackInSlot(0, ItemStack.EMPTY);
        return taken;
    }

    // --- збереження у світ ---

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("inventory")) {
            inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        }
    }

    // --- синхронізація з клієнтом ---

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
