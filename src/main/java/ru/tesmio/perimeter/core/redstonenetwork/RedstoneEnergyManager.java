package ru.tesmio.perimeter.core.redstonenetwork;

import net.minecraft.server.level.ServerLevel;

public class RedstoneEnergyManager {

    public static boolean tryConsumeEnergy(ServerLevel level, int amount) {
        RedstoneEnergyStorage storage = RedstoneEnergyStorage.get(level);
        return storage.consumeEnergy(amount);
    }

    public static void addEnergy(ServerLevel level, int amount) {
        RedstoneEnergyStorage.get(level).addEnergy(amount);
    }

    public static int getEnergy(ServerLevel level) {
        return RedstoneEnergyStorage.get(level).getEnergy();
    }
}

