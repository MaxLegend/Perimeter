package ru.tesmio.perimeter.blocks.devices.areasensor.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import ru.tesmio.perimeter.Perimeter;
import ru.tesmio.perimeter.blocks.devices.areasensor.screen.widgets.AreaSensorSliderButton;
import ru.tesmio.perimeter.core.NetworkHandler;
import ru.tesmio.perimeter.network.packets.AreaSensorPacket;


public class AreaSensorScreen extends AbstractContainerScreen<AreaSensorMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Perimeter.MODID, "textures/gui/area_sensor_gui.png");

    public AreaSensorScreen(AreaSensorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 54;
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }

    @Override
    protected void init() {
        super.init();
        int current = menu.getRange();
        int sliderWidth = 150;
        int sliderHeight = 20;
        int sliderX = leftPos + (imageWidth - sliderWidth) / 2;
        int sliderY = topPos + 16;

        addRenderableWidget(new AreaSensorSliderButton(sliderX, sliderY, sliderWidth, sliderHeight, current, value -> {
            NetworkHandler.INSTANCE.sendToServer(new AreaSensorPacket(menu.getBlockPos(), value));
        }));

    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        renderBackground(guiGraphics);
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }


}