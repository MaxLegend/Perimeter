package ru.tesmio.perimeter.items;

import net.minecraft.world.item.Item;
import ru.tesmio.perimeter.blocks.devices.redstonecircuit.CircuitComponents;

public class CircuitComponent extends Item {
    private final CircuitComponents type;

    public CircuitComponent(CircuitComponents type) {
        super(new Properties());
        this.type = type;
    }

    public CircuitComponents getType() {
        return type;
    }
}
