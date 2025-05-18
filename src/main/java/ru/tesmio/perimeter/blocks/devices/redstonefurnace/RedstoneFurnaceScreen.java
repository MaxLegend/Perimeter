package ru.tesmio.perimeter.blocks.devices.redstonefurnace;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import ru.tesmio.perimeter.Perimeter;

public class RedstoneFurnaceScreen extends AbstractContainerScreen<RedstoneFurnaceMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Perimeter.MODID, "textures/gui/redstone_furnace.png");

    //координаты второй стрелки 200,0 расширение слота входа 176 17
    public RedstoneFurnaceScreen(RedstoneFurnaceMenu menu, net.minecraft.world.entity.player.Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
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
        // отрисовка стрелки
        int progressMain = this.menu.getCookProgress(0);
        //     System.out.println("progressMain " + progressMain);
        gui.blit(TEXTURE, x + 73, y + 21, 176, 0, progressMain + 1, 16);
        // Стрелка второго процесса (если расширение активно)
        if (this.menu.hasExpansion()) {
            gui.blit(TEXTURE, x + 49, y + 51, 176, 17, 72, 18);
            int progressExtra = this.menu.getCookProgress(1);
            gui.blit(TEXTURE, x + 73, y + 51, 200, 0, progressExtra + 1, 16);
        }
    }

    private boolean isHovering(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(gui);
        super.render(gui, mouseX, mouseY, partialTick);
        this.renderTooltip(gui, mouseX, mouseY);
        // Координаты GUI
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        // Подсказка для слота ускорения (палка)
        if (isHovering(mouseX, mouseY, x + 152, y + 10, 16, 16) && menu.getSlot(2).getItem().isEmpty()) {
            gui.renderTooltip(this.font, Component.translatable("redstone_furnace.info.speed_up"), mouseX, mouseY);
        }
        // Подсказка для слота ускорения (палка)
        if (isHovering(mouseX, mouseY, x + 152, y + 34, 16, 16) && menu.getSlot(5).getItem().isEmpty()) {
            gui.renderTooltip(this.font, Component.translatable("redstone_furnace.info.redstone_up"), mouseX, mouseY);
        }
        // Подсказка для слота расширения (яблоко)
        if (isHovering(mouseX, mouseY, x + 152, y + 58, 16, 16) && menu.getSlot(6).getItem().isEmpty()) {
            gui.renderTooltip(this.font, Component.translatable("redstone_furnace.info.space_up"), mouseX, mouseY);
        }
    }


}
