package ru.tesmio.perimeter.blocks.devices.spotlight;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import ru.tesmio.perimeter.util.PrebakedGeometry;
import ru.tesmio.perimeter.util.RenderUtils;

public class SpotlightRenderer implements BlockEntityRenderer<SpotlightEntity> {

    public SpotlightRenderer(BlockEntityRendererProvider.Context context) {
    }

    public Vec3 getFaceCenter(Direction d) {
        switch (d) {
            case SOUTH -> new Vec3(0.5, 0.5, 0.64);
            case NORTH -> new Vec3(0.5, 0.5, 0.36);
            case WEST -> new Vec3(0.36, 0.5, 0.5);
            case EAST -> new Vec3(0.64, 0.5, 0.5);
            case UP -> new Vec3(0.5, 0.64, 0.5);
            case DOWN -> new Vec3(0.5, 0.36, 0.5);
        }
        return new Vec3(0.5, 0.5, 0.5);
    }

    private void applyTransformations(PoseStack poseStack, Direction direction) {
        // Центрирование на грани блока
        Vec3 offset = getFaceCenter(direction);
        poseStack.translate(offset.x, offset.y, offset.z);

        // Поворот по направлению
        switch (direction) {
            case NORTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180f));
            case WEST -> poseStack.mulPose(Axis.YP.rotationDegrees(-90f));
            case EAST -> poseStack.mulPose(Axis.YP.rotationDegrees(90f));
            case UP -> poseStack.mulPose(Axis.XP.rotationDegrees(-90f));
            case DOWN -> poseStack.mulPose(Axis.XP.rotationDegrees(90f));
        }
    }

    @Override
    public void render(SpotlightEntity entity, float partialTicks, PoseStack poseStack,
                       MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {

        Direction direction = entity.getBlockState().getValue(SpotlightBlock.FACING);
        Vec3 start = getFaceCenter(direction);
        Vec3 dir = new Vec3(direction.getStepX(), direction.getStepY(), direction.getStepZ());

        poseStack.pushPose();
        applyTransformations(poseStack, direction);
        Matrix4f matrix = poseStack.last().pose();
        Matrix3f normalMatrix = poseStack.last().normal();
        VertexConsumer builder = bufferSource.getBuffer(RenderUtils.spotlightRenderSettings());
        if (entity.getBlockState().getValue(BlockStateProperties.POWERED)) {
            PrebakedGeometry.SpotlightModel.renderCone(builder, matrix, normalMatrix, combinedLight, combinedOverlay);
        }
//        float baseRadius = 9f; // разумный радиус
//        float length = 3f;
//        int segments = 30;
//
//
//        for (int i = 0; i < segments; i++) {
//            double angle1 = 2 * Math.PI * i / segments;
//            double angle2 = 2 * Math.PI * (i + 1) / segments;
//
//            Vec3 p1 = new Vec3(baseRadius * Math.cos(angle1), baseRadius * Math.sin(angle1), length);
//            Vec3 p2 = new Vec3(baseRadius * Math.cos(angle2), baseRadius * Math.sin(angle2), length);
//
//            Vec3 normal1 = p1.subtract(new Vec3(0, 0, 0)).normalize();
//            Vec3 normal2 = p2.subtract(new Vec3(0, 0, 0)).normalize();
//            Vec3 tipNormal = new Vec3(0, 0, -1); // Вершина всегда назад
//
//            addVertex(builder, matrix, normalMatrix, p1, normal1, combinedLight, combinedOverlay);
//            addVertex(builder, matrix, normalMatrix, p2, normal2, combinedLight, combinedOverlay);
//            addVertex(builder, matrix, normalMatrix, new Vec3(0, 0, 0), tipNormal, combinedLight, combinedOverlay);
//        }

        poseStack.popPose();

    }


    private void addVertex(VertexConsumer builder, Matrix4f matrix, Matrix3f normalMatrix,
                           Vec3 pos, Vec3 normal, int light, int overlay) {
        // преобразуем нормаль матрицей нормалей
        Vector3f n = new Vector3f((float) normal.x, (float) normal.y, (float) normal.z);
        normalMatrix.transform(n);
        n.normalize();

        builder.vertex(matrix, (float) pos.x, (float) pos.y, (float) pos.z)
                .color(255, 213, 0, 255)
                .uv(0, 0)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(n.x(), n.y(), n.z())
                .endVertex();
    }


}