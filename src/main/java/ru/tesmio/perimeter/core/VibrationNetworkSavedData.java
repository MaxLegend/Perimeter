package ru.tesmio.perimeter.core;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.*;

public class VibrationNetworkSavedData extends SavedData {
    private final Map<BlockPos, UUID> blockToNetwork = new HashMap<>();
    private final Map<UUID, Set<BlockPos>> networks = new HashMap<>();

    public static VibrationNetworkSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                VibrationNetworkSavedData::load,
                VibrationNetworkSavedData::new,
                "vibration_network"
        );
    }

    public VibrationNetworkSavedData() {
    }

    public static VibrationNetworkSavedData load(CompoundTag tag) {
        VibrationNetworkSavedData data = new VibrationNetworkSavedData();

        ListTag networksTag = tag.getList("Networks", Tag.TAG_COMPOUND);
        for (Tag t : networksTag) {
            CompoundTag netTag = (CompoundTag) t;
            UUID id = netTag.getUUID("Id");

            Set<BlockPos> members = new HashSet<>();
            ListTag posList = netTag.getList("Members", Tag.TAG_LONG);
            for (Tag p : posList) {
                members.add(BlockPos.of(((LongTag) p).getAsLong()));
            }

            data.networks.put(id, members);
            for (BlockPos pos : members) {
                data.blockToNetwork.put(pos, id);
            }
        }

        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag networksTag = new ListTag();
        for (Map.Entry<UUID, Set<BlockPos>> entry : networks.entrySet()) {
            CompoundTag netTag = new CompoundTag();
            netTag.putUUID("Id", entry.getKey());

            ListTag posList = new ListTag();
            for (BlockPos pos : entry.getValue()) {
                posList.add(LongTag.valueOf(pos.asLong()));
            }
            netTag.put("Members", posList);

            networksTag.add(netTag);
        }

        tag.put("Networks", networksTag);
        return tag;
    }

    public Map<BlockPos, UUID> getBlockToNetwork() {
        return blockToNetwork;
    }

    public Map<UUID, Set<BlockPos>> getNetworks() {
        return networks;
    }
}
