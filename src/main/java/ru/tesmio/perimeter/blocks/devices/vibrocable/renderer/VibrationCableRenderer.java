package ru.tesmio.perimeter.blocks.devices.vibrocable.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import ru.tesmio.perimeter.blocks.devices.vibrocable.VibrationCableEntity;

public class VibrationCableRenderer implements BlockEntityRenderer<VibrationCableEntity> {

    public VibrationCableRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(VibrationCableEntity blockEntity, float partialTicks, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState mimic = blockEntity.getMimickedState();
        Level level = blockEntity.getLevel();
        if (level == null) return;
        if (mimic == null) return;

        BlockPos pos = blockEntity.getBlockPos();
        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();

        dispatcher.renderBatched(
                mimic, pos, level,
                poseStack, bufferSource.getBuffer(RenderType.cutoutMipped()),
                false, level.getRandom()
        );


    }
}