package ru.tesmio.perimeter.cache;

import ru.tesmio.perimeter.blocks.devices.linearsensor.LinearTransmitterEntity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ClientTransmitterCache {
    private static final Set<LinearTransmitterEntity> TRANSMITTERS = new HashSet<>();

    public static void add(LinearTransmitterEntity be) {
        TRANSMITTERS.add(be);
    }

    public static void remove(LinearTransmitterEntity be) {
        TRANSMITTERS.remove(be);
    }

    public static Collection<LinearTransmitterEntity> getAll() {
        return TRANSMITTERS;
    }

    public static void clear() {
        TRANSMITTERS.clear();
    }
}
