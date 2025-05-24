package ru.tesmio.perimeter.blocks.devices.redstoneaccumulator;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;
import ru.tesmio.perimeter.core.NetworkHandler;
import ru.tesmio.perimeter.core.redstonenetwork.IRedstoneEnergy;
import ru.tesmio.perimeter.core.registration.RegBlockEntitys;
import ru.tesmio.perimeter.network.packets.EnergySyncClientPacket;


public class RedstoneAccumulatorEntity extends BlockEntity implements MenuProvider, IRedstoneEnergy {
    private int tickCounter = 0;
    private int energy = 0;
    private int clientEnergy = 0;
    private static final int ENERGY_COST = 25;
    private static final int MAX_ENERGY = 10000;


    public RedstoneAccumulatorEntity(BlockPos pos, BlockState state) {
        super(RegBlockEntitys.REDSTONE_ACCUMULATOR_ENTITY.get(), pos, state);
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }


    public int getEnergy() {
        return energy;
    }

    public void tick() {
        if (level instanceof ServerLevel) {
            boolean powered = level.hasNeighborSignal(worldPosition);
            if (powered) {
                tickCounter++;
                if (tickCounter >= 20) {
                    tickCounter = 0;
                    if (addEnergy(getEnergyCost())) {
                        NetworkHandler.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)),
                                new EnergySyncClientPacket(worldPosition, energy));
                    }
                }
            }
        }
    }

    public boolean addEnergy(int amount) {
        if (energy < MAX_ENERGY) {
            energy = Math.min(MAX_ENERGY, energy + amount);
            setChanged();
            return true;
        }
        return false;
    }

    public boolean consumeEnergy(int amount) {
        if (energy >= amount) {
            energy -= amount;
            setChanged();
            return true;
        }
        return false;
    }

    public int getEnergyStored() {
        return energy;
    }

    public int getMaxEnergyStored() {
        return MAX_ENERGY;
    }

    @Override
    public boolean tryUseEnergy(ServerLevel level) {
        return addEnergy(getEnergyCost());
    }

    @Override
    public int getEnergyCost() {
        return ENERGY_COST;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Energy", energy);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        energy = tag.getInt("Energy");
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Redstone Accumulator");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new RedstoneAccumulatorMenu(id, inv, this);
    }
}
