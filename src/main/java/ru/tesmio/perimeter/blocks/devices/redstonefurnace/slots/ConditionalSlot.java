package ru.tesmio.perimeter.blocks.devices.redstonefurnace.slots;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class ConditionalSlot extends Slot {
    private final Supplier<Boolean> isEnabled;

    public ConditionalSlot(Container container, int index, int x, int y, Supplier<Boolean> isEnabled) {
        super(container, index, x, y);
        this.isEnabled = isEnabled;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return isEnabled.get() && this.container.canPlaceItem(this.getSlotIndex(), stack);
    }

    @Override
    public boolean isActive() {
        return isEnabled.get();
    }
}