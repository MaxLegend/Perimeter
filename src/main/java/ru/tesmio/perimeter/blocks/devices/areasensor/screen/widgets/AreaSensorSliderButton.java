package ru.tesmio.perimeter.blocks.devices.areasensor.screen.widgets;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class AreaSensorSliderButton extends AbstractSliderButton {

    private final Consumer<Integer> onValueChanged;

    public AreaSensorSliderButton(int x, int y, int width, int height, int value, Consumer<Integer> onValueChanged) {
        super(x, y, width, height, Component.literal(""), (value - 1) / 7.0D);
        this.onValueChanged = onValueChanged;
        updateMessage();
    }

    @Override
    protected void updateMessage() {
        int val = 1 + (int)(value * 7);
        setMessage(Component.literal("Range: " + val));
    }

    @Override
    protected void applyValue() {
        int val = 1 + (int)(value * 7);
        onValueChanged.accept(val);
        updateMessage();
    }
}
