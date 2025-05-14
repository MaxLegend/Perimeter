package ru.tesmio.perimeter.util;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class PrebakedGeometry {
    public class SpotlightModel {
        // Параметры конуса
        public static final float BASE_RADIUS = 9f;
        public static final float LENGTH = 3f;
        public static final int SEGMENTS = 30;

        // Генерация вершин конуса
        public static void renderCone(VertexConsumer builder, Matrix4f poseMatrix, Matrix3f normalMatrix, int combinedLight, int combinedOverlay) {
            for (int i = 0; i < SEGMENTS; i++) {
                double angle1 = 2 * Math.PI * i / SEGMENTS;
                double angle2 = 2 * Math.PI * (i + 1) / SEGMENTS;

                Vec3 p1 = new Vec3(BASE_RADIUS * Math.cos(angle1), BASE_RADIUS * Math.sin(angle1), LENGTH);
                Vec3 p2 = new Vec3(BASE_RADIUS * Math.cos(angle2), BASE_RADIUS * Math.sin(angle2), LENGTH);

                addVertex(builder, poseMatrix, normalMatrix, p1, p1.normalize(), combinedLight, combinedOverlay);
                addVertex(builder, poseMatrix, normalMatrix, p2, p2.normalize(), combinedLight, combinedOverlay);
                addVertex(builder, poseMatrix, normalMatrix, Vec3.ZERO, new Vec3(0, 0, -1), combinedLight, combinedOverlay);
            }
        }

        private static void addVertex(VertexConsumer builder, Matrix4f poseMatrix, Matrix3f normalMatrix,
                                      Vec3 pos, Vec3 normal, int light, int overlay) {
            Vector3f n = new Vector3f((float) normal.x, (float) normal.y, (float) normal.z);
            normalMatrix.transform(n);
            n.normalize();

            builder.vertex(poseMatrix, (float) pos.x, (float) pos.y, (float) pos.z)
                    .color(255, 213, 0, 255)
                    .uv(0, 0)
                    .overlayCoords(overlay)
                    .uv2(light)
                    .normal(n.x(), n.y(), n.z())
                    .endVertex();
        }

    }
}
