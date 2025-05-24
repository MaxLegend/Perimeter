package ru.tesmio.perimeter.core.redstonenetwork;

import net.minecraft.server.level.ServerLevel;

public interface IRedstoneEnergy {
    boolean tryUseEnergy(ServerLevel level);

    int getEnergyCost();
}
