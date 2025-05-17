package ru.tesmio.perimeter.network.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import ru.tesmio.perimeter.blocks.devices.areasensor.AreaSensorEntity;

import java.util.function.Supplier;

public class AreaSensorPacket {
    private final BlockPos pos;
    private final int range;

    public AreaSensorPacket(BlockPos pos, int range) {
        this.pos = pos;
        this.range = range;
    }

    public AreaSensorPacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos(), buf.readInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(range);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            Level level = player.level();
            if (level.getBlockEntity(pos) instanceof AreaSensorEntity be) {
                be.setRange(range);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
