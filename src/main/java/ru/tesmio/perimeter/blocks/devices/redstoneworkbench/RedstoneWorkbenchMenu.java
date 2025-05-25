package ru.tesmio.perimeter.blocks.devices.redstoneworkbench;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import ru.tesmio.perimeter.blocks.devices.redstoneworkbench.slot.SlotResult;
import ru.tesmio.perimeter.core.PerimeterItems;
import ru.tesmio.perimeter.core.registration.RegMenus;

//надо доделывать слоты шаблонов, верстак работает, также визуал
public class RedstoneWorkbenchMenu extends AbstractContainerMenu {
    private final RedstoneWorkbenchEntity blockEntity;

    public RedstoneWorkbenchMenu(int containerId, Inventory playerInventory, RedstoneWorkbenchEntity blockEntity) {
        super(RegMenus.WORKBENCH_MENU.get(), containerId);
        this.blockEntity = blockEntity;

        // Слоты 3x3
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), col + row * 3, 30 + col * 18, 17 + row * 18));
            }
        }

        // Слот выхода (последний слот, индекс 9)
        this.addSlot(new SlotResult(blockEntity, 9, 124, 35));
        // Слот входного шаблона (10) - x7 y16
        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 10, 8, 17) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(PerimeterItems.CRAFTING_TEMPLATE.get());
            }
        });

        // Слот сохранения шаблона (11) - x92 y60
        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 11, 93, 61) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(PerimeterItems.CRAFTING_TEMPLATE.get());
            }
        });
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory,
                        col + row * 9 + 9,
                        8 + col * 18,
                        84 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true; // Упрощенная проверка
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index == 9) { // Слот результата
                // Вызываем onTake для обработки расходования ингредиентов
                slot.onTake(player, itemstack1);

                if (!this.moveItemStackTo(itemstack1, 10, 46, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < 9) { // Слоты крафта
                if (!this.moveItemStackTo(itemstack1, 10, 46, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 9, false)) { // Из инвентаря в сетку
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }
}
