package ru.tesmio.perimeter.network.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class EnergySyncClientPacket {
    private final BlockPos pos;
    private final int energy;

    public EnergySyncClientPacket(BlockPos pos, int energy) {
        this.pos = pos;
        this.energy = energy;
    }

    public static void encode(EnergySyncClientPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeInt(msg.energy);
    }

    public static EnergySyncClientPacket decode(FriendlyByteBuf buf) {
        return new EnergySyncClientPacket(buf.readBlockPos(), buf.readInt());
    }

    public static void handle(EnergySyncClientPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (Minecraft.getInstance().level != null) {
                var be = Minecraft.getInstance().level.getBlockEntity(msg.pos);
                if (be instanceof ru.tesmio.perimeter.blocks.devices.redstoneaccumulator.RedstoneAccumulatorEntity acc) {
                    acc.setEnergy(msg.energy);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
