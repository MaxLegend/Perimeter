package ru.tesmio.perimeter.network.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import ru.tesmio.perimeter.blocks.devices.areasensor.AreaSensorEntity;

import java.util.function.Supplier;

public class AreaSensorPacketClient {
    private final BlockPos pos;
    private final int range;

    public AreaSensorPacketClient(BlockPos pos, int range) {
        this.pos = pos;
        this.range = range;
    }

    public AreaSensorPacketClient(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.range = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(range);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            BlockEntity be = Minecraft.getInstance().level.getBlockEntity(pos);
            if (be instanceof AreaSensorEntity sensor) {
                sensor.setRange(range); // Клиентское обновление
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
