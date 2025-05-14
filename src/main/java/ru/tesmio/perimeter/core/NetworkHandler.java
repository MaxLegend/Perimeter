package ru.tesmio.perimeter.core;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import ru.tesmio.perimeter.Perimeter;
import ru.tesmio.perimeter.network.packets.AreaSensorPacket;
import ru.tesmio.perimeter.network.packets.AreaSensorPacketClient;


public class NetworkHandler {
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Perimeter.MODID, "main"),
            () -> "1.0",
            s -> true,
            s -> true
    );

    private static int id = 0;

    public static void register() {
        INSTANCE.registerMessage(id++,
                AreaSensorPacket.class,
                AreaSensorPacket::encode,
                AreaSensorPacket::new,
                AreaSensorPacket::handle);
        INSTANCE.registerMessage(id++,
                AreaSensorPacketClient.class,
                AreaSensorPacketClient::encode,
                AreaSensorPacketClient::new,
                AreaSensorPacketClient::handle);


    }
}
