package ru.tesmio.perimeter.blocks.devices.redstoneaccumulator;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import ru.tesmio.perimeter.Perimeter;

public class RedstoneAccumulatorScreen extends AbstractContainerScreen<RedstoneAccumulatorMenu> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Perimeter.MODID, "textures/gui/redstone_accumulator.png");
    private boolean modeActive = true;

    public RedstoneAccumulatorScreen(RedstoneAccumulatorMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 54;
    }


    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(gui); // фон
        this.renderBg(gui, partialTick, mouseX, mouseY);
        super.render(gui, mouseX, mouseY, partialTick); // обязательно!
        this.renderTooltip(gui, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        gui.blit(TEXTURE, leftPos, topPos - 10, 0, 0, imageWidth, imageHeight, 256, 54);

        int maxBarHeight = 54;
        int energy = menu.getEnergy();
        int barHeight = (int) ((energy / 10000f) * maxBarHeight);
        if (barHeight > 0) {
            int barX = leftPos + 244 - 165;
            int barY = topPos - 19 + (maxBarHeight - barHeight);
            int textureX = 176;
            int textureY = 36 + (maxBarHeight - barHeight);

            gui.blit(TEXTURE, barX, barY, textureX, textureY, 18, barHeight, 256, 54);
        }


    }

}

