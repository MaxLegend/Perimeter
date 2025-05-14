package ru.tesmio.perimeter.blocks.devices.redstonecable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import ru.tesmio.perimeter.util.RenderUtils;

public class RedstoneCableRenderer implements BlockEntityRenderer<RedstoneCableEntity> {

    public RedstoneCableRenderer(BlockEntityRendererProvider.Context ctx) {
        super();

    }


    @Override
    public int getViewDistance() {
        return 128;
    }

    @Override
    public void render(RedstoneCableEntity entity, float partialTicks, PoseStack stack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        Vec3 center = Vec3.atCenterOf(entity.getBlockPos()).subtract(Vec3.atCenterOf(entity.getBlockPos()));
        BlockPos blockPos = entity.getBlockPos();

        for (BlockPos connection : entity.getConnections()) {

            Vec3 end = Vec3.atCenterOf(connection).subtract(Vec3.atCenterOf(blockPos));

            renderCurvedCuboid(stack, buffer, center.add(0.5f, 0.5f, 0.5f), end.add(0.5f, 0.5f, 0.5f), packedLight, packedOverlay);
        }

    }


    public static void renderCurvedCuboid(PoseStack poseStack, MultiBufferSource buffer,
                                          Vec3 from, Vec3 to, int light, int overlay) {

        VertexConsumer builder = buffer.getBuffer(RenderUtils.lightColorRender());

        int segments = 12;
        float thickness = 0.015F;

        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();
        Matrix3f normal = pose.normal();

        for (int i = 0; i < segments; i++) {
            float t1 = i / (float) segments;
            float t2 = (i + 1) / (float) segments;

            Vec3 p1 = interpolateCurved(from, to, t1);
            Vec3 p2 = interpolateCurved(from, to, t2);

            drawThickSegment(builder, matrix, normal, p1, p2, thickness, light, overlay);
        }
    }

    private static Vec3 interpolateCurved(Vec3 from, Vec3 to, float t) {
        Vec3 linear = from.lerp(to, t);
        if (Math.abs(from.x - to.x) < 0.001 && Math.abs(from.z - to.z) < 0.001) {
            return linear;
        }
        double curveAmplitude = 0.4;
        double curve = Math.sin(t * Math.PI) * -curveAmplitude; // провисание вниз
        return new Vec3(linear.x, linear.y + curve, linear.z);
    }


    private static void drawThickSegment(VertexConsumer builder, Matrix4f matrix, Matrix3f normal,
                                         Vec3 p1, Vec3 p2, float thickness, int light, int overlay) {
        // Вычисляем вектор направления
        Vec3 dir = p2.subtract(p1).normalize();
        //    Vec3 up = new Vec3(0, 1, 0);
        Vec3 up = Math.abs(dir.y) > 0.999 ? new Vec3(1, 0, 0) : new Vec3(0, 1, 0);
        Vec3 right = dir.cross(up).normalize().scale(thickness);
        Vec3 forward = dir.cross(right).normalize().scale(thickness);

        // Вершины прямоугольного параллелепипеда
        Vec3[] corners = new Vec3[]{
                p1.add(right).add(forward),
                p1.add(right).subtract(forward),
                p1.subtract(right).subtract(forward),
                p1.subtract(right).add(forward),

                p2.add(right).add(forward),
                p2.add(right).subtract(forward),
                p2.subtract(right).subtract(forward),
                p2.subtract(right).add(forward),
        };

        int[][] faces = {
                {0, 1, 2, 3}, // bottom
                {7, 6, 5, 4}, // top
                {0, 4, 5, 1}, // right
                {1, 5, 6, 2}, // front
                {2, 6, 7, 3}, // left
                {3, 7, 4, 0}, // back
        };

        for (int[] face : faces) {
            for (int idx : face) {
                Vec3 normalVec = corners[face[1]].subtract(corners[face[0]])
                        .cross(corners[face[2]].subtract(corners[face[1]]))
                        .normalize();
                Vec3 v = corners[idx];
                builder.vertex(matrix, (float) v.x, (float) v.y, (float) v.z)
                        .color(0.3f, 0, 0, 1f)
                        .uv(0, 0)
                        .overlayCoords(overlay)
                        .uv2(light)
                        .normal(normal, (float) normalVec.x, (float) normalVec.y, (float) normalVec.z)
                        .endVertex();
            }
        }
    }
}
