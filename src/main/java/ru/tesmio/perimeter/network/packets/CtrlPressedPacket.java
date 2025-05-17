package ru.tesmio.perimeter.network.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import ru.tesmio.perimeter.blocks.devices.redstonecable.RedstoneCableEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CtrlPressedPacket {
    private final BlockPos pos;

    public CtrlPressedPacket(BlockPos pos) {
        this.pos = pos;
    }

    public CtrlPressedPacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    public static void handle(CtrlPressedPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                BlockEntity be = player.level().getBlockEntity(packet.pos);
                if (be instanceof RedstoneCableEntity cable) {
                    List<BlockPos> connections = new ArrayList<>(cable.getConnections());
                    for (BlockPos other : connections) {
                        BlockEntity otherBe = player.level().getBlockEntity(other);
                        if (otherBe instanceof RedstoneCableEntity otherCable) {
                            otherCable.removeConnection(packet.pos);
                            otherCable.setChanged();
                            player.level().sendBlockUpdated(other, otherCable.getBlockState(), otherCable.getBlockState(), 3);
                        }
                    }

                    cable.clearConnections();
                    cable.setChanged();
                    player.level().sendBlockUpdated(packet.pos, cable.getBlockState(), cable.getBlockState(), 3);
                }
            }
        });
        context.setPacketHandled(true);
    }
}