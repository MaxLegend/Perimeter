package ru.tesmio.perimeter.core.blocknetwork;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.*;

public class BlockNetworkSystem {


    private static final Map<Level, BlockNetworkSystem> INSTANCES = new WeakHashMap<>();
    private final BlockNetworkSavedData data;

    private final Map<BlockPos, UUID> blockToNetwork = new HashMap<>();
    private final Map<UUID, Set<BlockPos>> networks = new HashMap<>();

    public static BlockNetworkSystem get(Level level) {
        return INSTANCES.computeIfAbsent(level, l -> new BlockNetworkSystem(level));
    }

    private BlockNetworkSystem(Level level) {
        this.data = BlockNetworkSavedData.get((ServerLevel) level);
    }

    public void onBlockAdded(Level level, BlockPos pos) {
        Set<UUID> neighborNets = findNeighborNetworks(pos);
        UUID newNetworkId = UUID.randomUUID();
        Set<BlockPos> newNetworkMembers = new HashSet<>();
        newNetworkMembers.add(pos);

        for (UUID neighborNet : neighborNets) {
            Set<BlockPos> members = data.getNetworks().remove(neighborNet);
            if (members != null) {
                newNetworkMembers.addAll(members);
            }
        }

        data.getNetworks().put(newNetworkId, newNetworkMembers);
        for (BlockPos p : newNetworkMembers) {
            data.getBlockToNetwork().put(p, newNetworkId);
        }

        updateControllers(level, newNetworkId);
        data.setDirty();
    }

    public void onBlockRemoved(Level level, BlockPos pos) {
        UUID networkId = data.getBlockToNetwork().remove(pos);
        if (networkId == null) return;

        Set<BlockPos> oldMembers = data.getNetworks().remove(networkId);
        if (oldMembers == null) return;

        oldMembers.remove(pos);

        Set<BlockPos> unvisited = new HashSet<>(oldMembers);
        while (!unvisited.isEmpty()) {
            BlockPos start = unvisited.iterator().next();
            Set<BlockPos> component = new HashSet<>();
            Queue<BlockPos> queue = new ArrayDeque<>();
            queue.add(start);
            unvisited.remove(start);

            while (!queue.isEmpty()) {
                BlockPos current = queue.poll();
                component.add(current);
                for (Direction dir : Direction.values()) {
                    BlockPos neighbor = current.relative(dir);
                    if (unvisited.contains(neighbor)) {
                        unvisited.remove(neighbor);
                        queue.add(neighbor);
                    }
                }
            }

            UUID newId = UUID.randomUUID();
            data.getNetworks().put(newId, component);
            for (BlockPos p : component) {
                data.getBlockToNetwork().put(p, newId);
            }
            updateControllers(level, newId);
        }

        data.setDirty();
    }

    private Set<UUID> findNeighborNetworks(BlockPos pos) {
        Set<UUID> result = new HashSet<>();
        for (Direction dir : Direction.values()) {
            BlockPos neighbor = pos.relative(dir);
            UUID net = data.getBlockToNetwork().get(neighbor);
            if (net != null) result.add(net);
        }
        return result;
    }

    private void updateControllers(Level level, UUID networkId) {
        Set<BlockPos> members = data.getNetworks().get(networkId);
        if (members == null) return;

        BlockPos controller = members.stream().min(Comparator.comparing(BlockPos::asLong)).orElseThrow();

        for (BlockPos pos : members) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof IBlockNetworkMember v) {
                v.setBlockInNetwork(controller);
            }
        }
    }

    public Set<BlockPos> getMembers(BlockPos controller) {
        UUID networkId = data.getBlockToNetwork().get(controller);
        if (networkId == null) {
            return Set.of();
        }

        Set<BlockPos> members = data.getNetworks().get(networkId);
        if (members == null) {
            return Set.of();
        }

        return members;
    }

    public void transmitSignal(Level level, BlockPos controller) {
        UUID id = data.getBlockToNetwork().get(controller);
        if (id == null) {
            return;
        }

        for (BlockPos pos : data.getNetworks().getOrDefault(id, Set.of())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof IBlockNetworkMember cable) {
                cable.pulse();
            }
        }
    }
}