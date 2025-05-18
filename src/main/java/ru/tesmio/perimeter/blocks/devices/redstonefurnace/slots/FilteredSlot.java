package ru.tesmio.perimeter.blocks.devices.redstonefurnace.slots;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class FilteredSlot extends Slot {
    public FilteredSlot(Container container, int index, int x, int y) {
        super(container, index, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return this.container.canPlaceItem(this.getSlotIndex(), stack);
    }
}
