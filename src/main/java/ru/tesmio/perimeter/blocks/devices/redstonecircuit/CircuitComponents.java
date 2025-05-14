package ru.tesmio.perimeter.blocks.devices.redstonecircuit;

import net.minecraft.util.StringRepresentable;

public enum CircuitComponents implements StringRepresentable {
    RESISTOR("resistor"),
    LAMP("lamp"),
    TRANSISTOR("transistor"),
    CONDENSER("condenser");

    private final String name;

    CircuitComponents(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public CircuitComponents getComponents() {
        return this;
    }
}
