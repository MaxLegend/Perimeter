package ru.tesmio.perimeter.blocks.devices.redstonefurnace;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import ru.tesmio.perimeter.blocks.devices.redstonefurnace.slots.ConditionalSlot;
import ru.tesmio.perimeter.blocks.devices.redstonefurnace.slots.FilteredSlot;
import ru.tesmio.perimeter.core.registration.RegMenus;


public class RedstoneFurnaceMenu extends AbstractContainerMenu {
    private final Container container;
    private final RedstoneFurnaceEntity be;

    public RedstoneFurnaceMenu(int id, Inventory playerInventory, RedstoneFurnaceEntity be) {
        super(RegMenus.REDSTONE_FURNACE_MENU.get(), id);
        this.container = be.getInventory();
        this.be = be;
        // input slot
        this.addSlot(new FilteredSlot(container, 0, 50, 22));
        // output slot
        this.addSlot(new FilteredSlot(container, 1, 104, 22));
        // speed upgrade slot (stick)
        this.addSlot(new FilteredSlot(container, 2, 152, 10));

        // secondary input/output if expansion is active
        this.addSlot(new ConditionalSlot(container, 3, 50, 52, be::hasExpansionUpgrade));
        this.addSlot(new ConditionalSlot(container, 4, 104, 52, be::hasExpansionUpgrade));
        // expansion upgrade slot (apple)
        this.addSlot(new ConditionalSlot(container, 6, 152, 34, be::hasExpansionUpgrade));
        this.addSlot(new FilteredSlot(container, 5, 152, 58));

        // player inventory
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // hotbar
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            int blockSlotCount = 7;
            int playerInvStart = blockSlotCount;
            int playerHotbarEnd = blockSlotCount + 36;

            if (index < blockSlotCount) {
                // Перемещение из инвентаря блока -> в инвентарь игрока
                if (!this.moveItemStackTo(stackInSlot, playerInvStart, playerHotbarEnd, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Перемещение из инвентаря игрока -> в инвентарь блока
                if (stackInSlot.is(Items.STICK)) {
                    if (!this.moveItemStackTo(stackInSlot, 2, 3, false)) return ItemStack.EMPTY;
                } else if (stackInSlot.is(Items.APPLE)) {
                    if (!this.moveItemStackTo(stackInSlot, 6, 7, false)) return ItemStack.EMPTY;
                } else if (stackInSlot.is(Items.REDSTONE)) {
                    if (!this.moveItemStackTo(stackInSlot, 5, 7, false)) return ItemStack.EMPTY;
                } else {
                    // Пытаемся поместить во входы
                    if (!this.moveItemStackTo(stackInSlot, 0, 1, false)) {
                        // Если слот расширения доступен, пробуем туда
                        if (!this.moveItemStackTo(stackInSlot, 3, 4, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    public boolean hasExpansion() {
        return be.hasExpansionUpgrade();
    }

    public int getCookProgress(int cookIndex) {
        return be.getCookProgressScaled(cookIndex, 24);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

}
