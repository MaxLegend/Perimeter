package ru.tesmio.perimeter.blocks.devices.redstonefurnace;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import ru.tesmio.perimeter.core.registration.RegMenus;


public class RedstoneFurnaceMenu extends AbstractContainerMenu {
    private final Container container;
    // сделать чтоб слоты включались и выключались динамически (можно сделать тчобы они были, но были неактивными пока
    // не положен апгрейд расширения. Кроме того, система дублирования работает не паралллельно, а вместо первой - поправить
    public RedstoneFurnaceMenu(int id, Inventory playerInventory, RedstoneFurnaceEntity be) {
        super(RegMenus.REDSTONE_FURNACE_MENU.get(), id);
        this.container = be.getInventory();

        // input slot
        this.addSlot(new Slot(container, 0, 56, 17));
        // output slot
        this.addSlot(new Slot(container, 1, 116, 35));
        // speed upgrade slot (stick)
        this.addSlot(new Slot(container, 2, 150, 17));

        // secondary input/output if expansion is active
        this.addSlot(new Slot(container, 3, 56, 35));
        this.addSlot(new Slot(container, 4, 116, 53));
        // expansion upgrade slot (apple)
        this.addSlot(new Slot(container, 5, 150, 53));

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
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

}
