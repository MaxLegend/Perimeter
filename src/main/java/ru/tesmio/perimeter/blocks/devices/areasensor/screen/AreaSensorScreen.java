package ru.tesmio.perimeter.blocks.devices.areasensor.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import ru.tesmio.perimeter.core.NetworkHandler;
import ru.tesmio.perimeter.network.packets.AreaSensorPacket;
import ru.tesmio.perimeter.blocks.devices.areasensor.screen.widgets.AreaSensorSliderButton;


public class  AreaSensorScreen extends AbstractContainerScreen<AreaSensorMenu> {

    public AreaSensorScreen(AreaSensorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        int current = menu.getRange();
        addRenderableWidget(new AreaSensorSliderButton(leftPos + 10, topPos + 20, 150, 20, current, value -> {
            NetworkHandler.INSTANCE.sendToServer(new AreaSensorPacket(menu.getBlockPos(), value));
        }));

    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        renderBackground(guiGraphics);
    }


}