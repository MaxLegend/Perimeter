package ru.tesmio.perimeter.blocks.concretechest;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import ru.tesmio.perimeter.core.RegMenus;

public class ConcreteChestMenu extends AbstractContainerMenu {
    private final BlockEntity blockEntity;
    private final Player player;
    private final IItemHandler playerInventory;

    // Основной конструктор
    public ConcreteChestMenu(int windowId, Inventory playerInventory, BlockEntity blockEntity) {
        super(RegMenus.CONCRETE_CHEST_MENU.get(), windowId);
        this.blockEntity = blockEntity;
        this.player = playerInventory.player;
        this.playerInventory = new InvWrapper(playerInventory);

        if (blockEntity instanceof ConcreteChestEntity chest) {
            addChestSlots(chest);
        }

        addPlayerSlots(playerInventory);
    }

    // Добавление слотов сундука
    private void addChestSlots(ConcreteChestEntity chest) {
        IItemHandler chestInventory = chest.getItemHandler();

        // Слоты сундука 9x6 (54 слота)
        for (int row = 0; row < 6; ++row) {
            for (int col = 0; col < 9; ++col) {
                int slotIndex = col + row * 9;
                int xPos = 8 + col * 18;
                int yPos = 18 + row * 18;
                this.addSlot(new SlotItemHandler(chestInventory, slotIndex, xPos, yPos));
            }
        }
    }

    // Добавление слотов игрока
    private void addPlayerSlots(Inventory playerInventory) {
        final int PLAYER_INVENTORY_Y_OFFSET = 140; // Смещение для инвентаря игрока
        final int HOTBAR_Y_OFFSET = 198; // Смещение для хотбара
        // Основные слоты (27 слотов)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                int slotIndex = col + row * 9 + 9;
                int xPos = 8 + col * 18;
                int yPos = PLAYER_INVENTORY_Y_OFFSET + row * 18;
                this.addSlot(new SlotItemHandler(this.playerInventory, slotIndex, xPos, yPos));
            }
        }

        // Хотбар (9 слотов)
        for (int col = 0; col < 9; ++col) {
            int xPos = 8 + col * 18;
            int yPos = HOTBAR_Y_OFFSET;
            this.addSlot(new SlotItemHandler(this.playerInventory, col, xPos, yPos));
        }
    }

    // Перенос предметов по Shift+Клик
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();

            // Если клик по сундуку
            if (index < 27) {
                if (!this.moveItemStackTo(stack, 27, 63, true)) {
                    return ItemStack.EMPTY;
                }
            }
            // Если клик по инвентарю игрока
            else if (!this.moveItemStackTo(stack, 0, 27, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        if (blockEntity instanceof ConcreteChestEntity chest) {
            return chest.stillValid(player);
        }
        return false;
    }

    public BlockEntity getBlockEntity() {
        return blockEntity;
    }
}
