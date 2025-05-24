package ru.tesmio.perimeter.core.redstonenetwork;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class RedstoneEnergyStorage extends SavedData {

    public static final String ID = "redstone_energy_storage";

    private int energy = 0;
    private static final int MAX_ENERGY = 100000;

    public void addEnergy(int amount) {
        energy = Math.min(MAX_ENERGY, energy + amount);
        setDirty();
    }

    public boolean consumeEnergy(int amount) {
        if (energy >= amount) {
            energy -= amount;
            setDirty();
            return true;
        }
        return false;
    }

    public int getEnergy() {
        return energy;
    }

    public static RedstoneEnergyStorage get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                tag -> {
                    RedstoneEnergyStorage storage = new RedstoneEnergyStorage();
                    storage.energy = tag.getInt("Energy");
                    return storage;
                },
                () -> {
                    RedstoneEnergyStorage storage = new RedstoneEnergyStorage();
                    return storage;
                },
                ID
        );
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putInt("Energy", energy);
        return tag;
    }
}
