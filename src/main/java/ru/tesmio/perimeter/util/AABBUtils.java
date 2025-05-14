package ru.tesmio.perimeter.util;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class AABBUtils {
    public record Edge(Vec3 start, Vec3 end) {
        public static Edge of(double x1, double y1, double z1, double x2, double y2, double z2) {
            return new Edge(new Vec3(x1, y1, z1), new Vec3(x2, y2, z2));
        }

        public Vec3 start(AABB box) {
            return new Vec3(
                    box.minX + start.x * (box.maxX - box.minX),
                    box.minY + start.y * (box.maxY - box.minY),
                    box.minZ + start.z * (box.maxZ - box.minZ)
            );
        }

        public Vec3 end(AABB box) {
            return new Vec3(
                    box.minX + end.x * (box.maxX - box.minX),
                    box.minY + end.y * (box.maxY - box.minY),
                    box.minZ + end.z * (box.maxZ - box.minZ)
            );
        }
    }

    public static final List<Edge> EDGE_ORDER = List.of(
            // Bottom square
            Edge.of(0, 0, 0, 1, 0, 0),
            Edge.of(1, 0, 0, 1, 0, 1),
            Edge.of(1, 0, 1, 0, 0, 1),
            Edge.of(0, 0, 1, 0, 0, 0),
            // Top square
            Edge.of(0, 1, 0, 1, 1, 0),
            Edge.of(1, 1, 0, 1, 1, 1),
            Edge.of(1, 1, 1, 0, 1, 1),
            Edge.of(0, 1, 1, 0, 1, 0),
            // Vertical lines
            Edge.of(0, 0, 0, 0, 1, 0),
            Edge.of(1, 0, 0, 1, 1, 0),
            Edge.of(1, 0, 1, 1, 1, 1),
            Edge.of(0, 0, 1, 0, 1, 1)
    );
}
