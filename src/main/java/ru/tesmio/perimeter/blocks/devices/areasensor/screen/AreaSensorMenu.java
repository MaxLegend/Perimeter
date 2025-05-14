package ru.tesmio.perimeter.blocks.devices.areasensor.screen;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import ru.tesmio.perimeter.blocks.devices.areasensor.AreaSensorEntity;
import ru.tesmio.perimeter.core.RegMenus;

public class AreaSensorMenu extends AbstractContainerMenu {
    private final BlockPos blockPos;
    private final ContainerData data;

    public AreaSensorMenu(int id, Inventory inventory, FriendlyByteBuf buf) {
        this(id, inventory, inventory.player.level().getBlockEntity(BlockPos.of(buf.readLong())));
    }

    public AreaSensorMenu(int id, Inventory inventory, BlockEntity be) {
        super(RegMenus.AREA_SENSOR_MENU.get(), id);
        this.blockPos = be.getBlockPos();

        if (be instanceof AreaSensorEntity sensor) {
            this.data = sensor.getContainerData(); // ← теперь всё связано
        } else {
            this.data = new SimpleContainerData(1);
        }
        // Важно: клиент ожидает dataSlot с хотя бы одним значением, если сервер его добавляет
        addDataSlots(data);
    }

    @Override
    public boolean stillValid(Player player) {
        return true; // или использовать ContainerLevelAccess и проверку дальности
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public int getRange() {
        return data.get(0);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY; // слоты не используются
    }

}
