package ru.tesmio.perimeter.blocks.concretechest;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import ru.tesmio.perimeter.Perimeter;

public class ConcreteChestScreen extends AbstractContainerScreen<ConcreteChestMenu> {
    // Текстура GUI (замените на свою)
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(Perimeter.MODID, "textures/gui/concrete_chest.png");

    public ConcreteChestScreen(ConcreteChestMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
        this.imageWidth = 176; // Ширина текстуры GUI
        this.imageHeight = 222; // Высота текстуры GUI
        this.inventoryLabelY = this.imageHeight - 94; // Позиция текста инвентаря игрока
    }

    @Override
    protected void init() {
        super.init();
        // Здесь можно добавить кнопки или другие элементы GUI
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        // Рисуем фон
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        gui.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(gui);
        super.render(gui, mouseX, mouseY, partialTick);
        this.renderTooltip(gui, mouseX, mouseY); // Рендер тултипов
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        // Рендер заголовка и инвентаря игрока
        gui.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        gui.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);
    }
}
