package ru.tesmio.perimeter.blocks.devices.vibrocable.network;

import net.minecraft.core.BlockPos;

public interface IVibrationNetworkMember {
    void setController(BlockPos controller);

    BlockPos getController();
}
