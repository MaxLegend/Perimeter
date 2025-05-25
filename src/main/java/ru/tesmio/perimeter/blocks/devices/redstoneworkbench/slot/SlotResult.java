package ru.tesmio.perimeter.blocks.devices.redstoneworkbench.slot;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import ru.tesmio.perimeter.blocks.devices.redstoneworkbench.RedstoneWorkbenchEntity;

public class SlotResult extends SlotItemHandler {

    private final RedstoneWorkbenchEntity blockEntity;

    public SlotResult(RedstoneWorkbenchEntity blockEntity, int index, int x, int y) {
        super(blockEntity.getItemHandler(), index, x, y);
        this.blockEntity = blockEntity;
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        checkTakeAchievements(stack);
        blockEntity.onTakeResult(player);
        super.onTake(player, stack);

        //   blockEntity.consumeIngredients();
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack safeInsert(ItemStack stack, int amount) {
        return stack; // Запрещаем вставку
    }

    @Override
    public ItemStack safeTake(int amount, int count, Player player) {
        ItemStack taken = super.safeTake(amount, count, player);
        if (!taken.isEmpty()) {
            blockEntity.consumeIngredients();
        }
        return taken;
    }
}
