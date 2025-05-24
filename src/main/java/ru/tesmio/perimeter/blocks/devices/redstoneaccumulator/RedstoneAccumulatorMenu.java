package ru.tesmio.perimeter.blocks.devices.redstoneaccumulator;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import ru.tesmio.perimeter.core.registration.RegMenus;

public class RedstoneAccumulatorMenu extends AbstractContainerMenu {

    private final SimpleContainerData data;
    BlockEntity be;

    public RedstoneAccumulatorMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, inv.player.level().getBlockEntity(BlockPos.of(buf.readLong())));
    }

    public RedstoneAccumulatorMenu(int id, Inventory inv, BlockEntity be) {
        super(RegMenus.ACCUMULATOR_MENU.get(), id);
        this.be = be;
        if (be instanceof RedstoneAccumulatorEntity acc) {
            this.data = new SimpleContainerData(2) {
                @Override
                public int get(int index) {
                    return switch (index) {
                        case 0 -> acc.getEnergyStored();
                        default -> 0;
                    };
                }

                @Override
                public void set(int index, int value) {
                }

                @Override
                public int getCount() {
                    return 2;
                }
            };
        } else {
            this.data = new SimpleContainerData(2);
        }

        addDataSlots(data);
    }

    public boolean isPaused() {
        return data.get(1) == 1;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        return ItemStack.EMPTY;
    }

    public int getEnergy() {
        return data.get(0);
    }
}
