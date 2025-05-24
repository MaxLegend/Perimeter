package ru.tesmio.perimeter.blocks.devices.redstoneaccumulator.button;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class DisableButton extends AbstractWidget {
    private final OnPress onPress;
    private final ResourceLocation texture;
    private final int textureXDefault;
    private final int textureXHover;
    private final int textureXPressed;
    private final int textureY;
    private final int width;
    private final int height;
    private boolean isPressedState = false;

    public interface OnPress {
        void onPress(DisableButton button);
    }

    public DisableButton(int x, int y, int width, int height, ResourceLocation texture, int textureXDefault, int textureXHover, int textureXPressed, int textureY, OnPress onPress) {
        super(x, y, width, height, Component.empty());
        this.onPress = onPress;
        this.texture = texture;
        this.textureXDefault = textureXDefault;
        this.textureXHover = textureXHover;
        this.textureXPressed = textureXPressed;
        this.textureY = textureY;
        this.width = width;
        this.height = height;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        isPressedState = !isPressedState;
        this.onPress.onPress(this);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput p_259858_) {

    }

    @Override
    protected void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShaderTexture(0, texture);
        int xTex = textureXDefault;

        if (isPressedState) {
            xTex = textureXPressed;
        } else if (this.isHovered) {

            xTex = textureXHover;
        }
        if (this.isHovered && isMouseDown()) {
            xTex = textureXPressed;
        }

        gui.blit(texture, getX(), getY(), xTex, textureY, width, height, 256, 54);
    }

    private boolean isMouseDown() {

        return this.isHovered && this.isFocused(); // улучшено, можно адаптировать под MouseHelper
    }
}
