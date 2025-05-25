package ru.tesmio.perimeter.blocks.devices.redstoneworkbench;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class RedstoneWorkbenchContainer implements CraftingContainer {
    private final NonNullList<ItemStack> items;
    private final int width;
    private final int height;

    public RedstoneWorkbenchContainer(NonNullList<ItemStack> items) {
        if (items.size() != 9) {
            throw new IllegalArgumentException("RedstoneWorkbenchContainer requires exactly 9 items (3x3 grid)");
        }
        this.items = items;
        this.width = 3;
        this.height = 3;
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : items) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int index) {
        return items.get(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack stack = items.get(index);
        if (stack.isEmpty()) return ItemStack.EMPTY;
        ItemStack result = stack.split(count);
        if (stack.isEmpty()) items.set(index, ItemStack.EMPTY);
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack stack = items.get(index);
        items.set(index, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        items.set(index, stack);
    }

    @Override
    public void setChanged() {
        // Можно оставить пустым, если не требуется реакция на изменение
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        items.clear();
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }


    @Override
    public List<ItemStack> getItems() {
        return items;
    }

    @Override
    public void fillStackedContents(StackedContents contents) {
        for (ItemStack stack : items) {
            contents.accountSimpleStack(stack);
        }
    }
}
