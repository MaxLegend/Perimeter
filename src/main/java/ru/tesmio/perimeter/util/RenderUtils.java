package ru.tesmio.perimeter.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import ru.tesmio.perimeter.core.events.RegRenderers;


/**
 * RenderUtils by Tesmio
 */
public class RenderUtils {

    public static final RenderStateShard.TransparencyStateShard TRANCPARENCY = new RenderStateShard.TransparencyStateShard("custom_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.depthMask(false);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.defaultBlendFunc();
    }
    );

    public static final RenderType LIGHT_COLOR_RENDER = RenderType.create(
            "light_color_render",
            DefaultVertexFormat.NEW_ENTITY,
            VertexFormat.Mode.TRIANGLES,
            256,
            false,
            true,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionColorLightmapShader))
                    .setCullState(new RenderStateShard.CullStateShard(false)) // Отключаем culling
                    .setLightmapState(new RenderStateShard.LightmapStateShard(true))
                    .setOverlayState(new RenderStateShard.OverlayStateShard(true))
                    .createCompositeState(false)
    );

    public static final RenderType SPHERE_GLOW_RENDER = RenderType.create(
            "glow_sphere",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.TRIANGLE_STRIP,
            256,
            false,
            true,
            RenderType.CompositeState.builder().setShaderState(new RenderStateShard.ShaderStateShard(() -> RegRenderers.glowSphereShaderInstance))
                    .setCullState(new RenderStateShard.CullStateShard(false)) // Отключаем culling
                    .setTransparencyState(TRANCPARENCY)
                    .createCompositeState(false));

    public static final RenderType SPOTLIGHT_RENDER_SETTINGS = RenderType.create(
            "spotlight_render_type",
            DefaultVertexFormat.POSITION_COLOR_NORMAL,
            VertexFormat.Mode.TRIANGLES,
            256,
            false,
            true,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(() -> RegRenderers.spotlightShaderInstance))
                    .setCullState(new RenderStateShard.CullStateShard(false)) // Отключаем culling
                    .setLightmapState(new RenderStateShard.LightmapStateShard(true))
                    .setTransparencyState(TRANCPARENCY)
                    .setOverlayState(new RenderStateShard.OverlayStateShard(true))

                    .createCompositeState(false)
    );
    static Minecraft mc = Minecraft.getInstance();
    private static TextureAtlasSprite sprite = mc.getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
            .apply(new ResourceLocation("perimeter:block/redstone_cable_center"));

    static final Vector3f up = new Vector3f(0, 1, 0);
    static final Vector3f down = new Vector3f(0, -1, 0);
    static final Vector3f north = new Vector3f(0, 0, -1);
    static final Vector3f south = new Vector3f(0, 0, 1);
    static final Vector3f west = new Vector3f(-1, 0, 0);
    static final Vector3f east = new Vector3f(1, 0, 0);

    public static RenderType lightColorRender() {

        return LIGHT_COLOR_RENDER;
    }

    public static RenderType spotlightRenderSettings() {
        return SPOTLIGHT_RENDER_SETTINGS;
    }

    public static ShaderInstance getSpotlightShader() {
        return RegRenderers.spotlightShaderInstance;
    }

    public static RenderType glowSphereRenderSettings() {
        return SPHERE_GLOW_RENDER;
    }

    public static void renderStretchedVoxel(PoseStack poseStack, MultiBufferSource bufferSource, float width, float height, float depth, float r, float g, float b, float a, int light, int overlay) {
        VertexConsumer vc = bufferSource.getBuffer(glowSphereRenderSettings());

        // VertexConsumer vc = bufferSource.getBuffer(RenderUtils.NO_CULL_SOLID);
        // VertexConsumer vc = bufferSource.getBuffer(RenderType.solid());

        Matrix4f mat = poseStack.last().pose();
        Matrix3f normalMat = poseStack.last().normal();

        float uMin = sprite.getU0();
        float vMin = sprite.getV0();
        float uMax = sprite.getU1();
        float vMax = sprite.getV1();

        float x0 = -width / 2f;
        float x1 = width / 2f;
        float y0 = -height / 2f;
        float y1 = height / 2f;
        float z0 = -depth / 2f;
        float z1 = depth / 2f;

        putQuad(vc, mat, normalMat, x0, y1, z1, x1, y1, z1, x1, y1, z0, x0, y1, z0, r, g, b, a, light, overlay, uMin, vMin, uMax, vMax, up);
        putQuad(vc, mat, normalMat, x0, y0, z0, x1, y0, z0, x1, y0, z1, x0, y0, z1, r, g, b, a, light, overlay, uMin, vMin, uMax, vMax, down);
        putQuad(vc, mat, normalMat, x0, y1, z0, x1, y1, z0, x1, y0, z0, x0, y0, z0, r, g, b, a, light, overlay, uMin, vMin, uMax, vMax, north);
        putQuad(vc, mat, normalMat, x1, y1, z1, x0, y1, z1, x0, y0, z1, x1, y0, z1, r, g, b, a, light, overlay, uMin, vMin, uMax, vMax, south);
        putQuad(vc, mat, normalMat, x0, y1, z1, x0, y1, z0, x0, y0, z0, x0, y0, z1, r, g, b, a, light, overlay, uMin, vMin, uMax, vMax, west);
        putQuad(vc, mat, normalMat, x1, y1, z0, x1, y1, z1, x1, y0, z1, x1, y0, z0, r, g, b, a, light, overlay, uMin, vMin, uMax, vMax, east);
    }


    private static void putQuad(VertexConsumer vc, Matrix4f mat, Matrix3f normMat, float x0, float y0, float z0, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float r, float g, float b, float a, int light, int overlay, float u0, float v0, float u1, float v1, Vector3f normal) {
        vc.vertex(mat, x0, y0, z0).color(r, g, b, a).uv(u0, v1).overlayCoords(overlay).uv2(light).normal(normMat, normal.x(), normal.y(), normal.z()).endVertex();
        vc.vertex(mat, x1, y1, z1).color(r, g, b, a).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(normMat, normal.x(), normal.y(), normal.z()).endVertex();
        vc.vertex(mat, x2, y2, z2).color(r, g, b, a).uv(u1, v0).overlayCoords(overlay).uv2(light).normal(normMat, normal.x(), normal.y(), normal.z()).endVertex();
        vc.vertex(mat, x3, y3, z3).color(r, g, b, a).uv(u0, v0).overlayCoords(overlay).uv2(light).normal(normMat, normal.x(), normal.y(), normal.z()).endVertex();
    }

    public static void renderLineAsBox(PoseStack poseStack, Vec3 start, Vec3 end, float thickness, float r, float g, float b, float a) {

        Vec3 dir = end.subtract(start).normalize();
        Vec3 up = new Vec3(0, 1, 0);

        if (Math.abs(dir.dot(up)) > 0.99) {
            up = new Vec3(1, 0, 0);
        }

        Vec3 side1 = dir.cross(up).normalize().scale(thickness / 2.0);
        Vec3 side2 = dir.cross(side1).normalize().scale(thickness / 2.0);

        Vec3 p1 = start.add(side1).add(side2);
        Vec3 p2 = start.add(side1).subtract(side2);
        Vec3 p3 = start.subtract(side1).subtract(side2);
        Vec3 p4 = start.subtract(side1).add(side2);

        Vec3 p5 = end.add(side1).add(side2);
        Vec3 p6 = end.add(side1).subtract(side2);
        Vec3 p7 = end.subtract(side1).subtract(side2);
        Vec3 p8 = end.subtract(side1).add(side2);

        Matrix4f matrix = poseStack.last().pose();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();

        RenderSystem.enableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        // Сторона 1 (боковая)
        buffer.vertex(matrix, (float) p1.x, (float) p1.y, (float) p1.z).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, (float) p2.x, (float) p2.y, (float) p2.z).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, (float) p6.x, (float) p6.y, (float) p6.z).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, (float) p5.x, (float) p5.y, (float) p5.z).color(r, g, b, a).endVertex();

        // Сторона 2
        buffer.vertex(matrix, (float) p2.x, (float) p2.y, (float) p2.z).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, (float) p3.x, (float) p3.y, (float) p3.z).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, (float) p7.x, (float) p7.y, (float) p7.z).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, (float) p6.x, (float) p6.y, (float) p6.z).color(r, g, b, a).endVertex();

        // Сторона 3
        buffer.vertex(matrix, (float) p3.x, (float) p3.y, (float) p3.z).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, (float) p4.x, (float) p4.y, (float) p4.z).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, (float) p8.x, (float) p8.y, (float) p8.z).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, (float) p7.x, (float) p7.y, (float) p7.z).color(r, g, b, a).endVertex();

        // Сторона 4
        buffer.vertex(matrix, (float) p4.x, (float) p4.y, (float) p4.z).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, (float) p1.x, (float) p1.y, (float) p1.z).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, (float) p5.x, (float) p5.y, (float) p5.z).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, (float) p8.x, (float) p8.y, (float) p8.z).color(r, g, b, a).endVertex();

        // Крышка (начало)
        buffer.vertex(matrix, (float) p1.x, (float) p1.y, (float) p1.z).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, (float) p2.x, (float) p2.y, (float) p2.z).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, (float) p3.x, (float) p3.y, (float) p3.z).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, (float) p4.x, (float) p4.y, (float) p4.z).color(r, g, b, a).endVertex();

        // Крышка (конец)
        buffer.vertex(matrix, (float) p5.x, (float) p5.y, (float) p5.z).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, (float) p6.x, (float) p6.y, (float) p6.z).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, (float) p7.x, (float) p7.y, (float) p7.z).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, (float) p8.x, (float) p8.y, (float) p8.z).color(r, g, b, a).endVertex();

        tesselator.end();

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }


    // Метод для рисования куба
    public static void renderCube(PoseStack poseStack, BlockPos pos, float size, float r, float g, float b) {
        // Перемещение к позиции
        poseStack.pushPose();
        poseStack.translate(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F); // Смещаем куб в центр блока

        // Прорисовка куба с использованием BufferBuilder
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        float min = -size / 2.0F;
        float max = size / 2.0F;

        // Рисуем все 6 граней куба
        // Передняя грань
        buffer.vertex(poseStack.last().pose(), min, min, max).color(r, g, b, 1.0F).endVertex();
        buffer.vertex(poseStack.last().pose(), max, min, max).color(r, g, b, 1.0F).endVertex();
        buffer.vertex(poseStack.last().pose(), max, max, max).color(r, g, b, 1.0F).endVertex();
        buffer.vertex(poseStack.last().pose(), min, max, max).color(r, g, b, 1.0F).endVertex();

        // Задняя грань
        buffer.vertex(poseStack.last().pose(), min, min, min).color(r, g, b, 1.0F).endVertex();
        buffer.vertex(poseStack.last().pose(), min, max, min).color(r, g, b, 1.0F).endVertex();
        buffer.vertex(poseStack.last().pose(), max, max, min).color(r, g, b, 1.0F).endVertex();
        buffer.vertex(poseStack.last().pose(), max, min, min).color(r, g, b, 1.0F).endVertex();

        // Левая грань
        buffer.vertex(poseStack.last().pose(), min, min, min).color(r, g, b, 1.0F).endVertex();
        buffer.vertex(poseStack.last().pose(), min, min, max).color(r, g, b, 1.0F).endVertex();
        buffer.vertex(poseStack.last().pose(), min, max, max).color(r, g, b, 1.0F).endVertex();
        buffer.vertex(poseStack.last().pose(), min, max, min).color(r, g, b, 1.0F).endVertex();

        // Правая грань
        buffer.vertex(poseStack.last().pose(), max, min, min).color(r, g, b, 1.0F).endVertex();
        buffer.vertex(poseStack.last().pose(), max, max, min).color(r, g, b, 1.0F).endVertex();
        buffer.vertex(poseStack.last().pose(), max, max, max).color(r, g, b, 1.0F).endVertex();
        buffer.vertex(poseStack.last().pose(), max, min, max).color(r, g, b, 1.0F).endVertex();

        // Верхняя грань
// Верхняя грань (y = max)
        buffer.vertex(poseStack.last().pose(), min, max, min).color(r, g, b, 1.0F).endVertex();
        buffer.vertex(poseStack.last().pose(), min, max, max).color(r, g, b, 1.0F).endVertex();
        buffer.vertex(poseStack.last().pose(), max, max, max).color(r, g, b, 1.0F).endVertex();
        buffer.vertex(poseStack.last().pose(), max, max, min).color(r, g, b, 1.0F).endVertex();

        // Нижняя грань
        buffer.vertex(poseStack.last().pose(), min, min, min).color(r, g, b, 1.0F).endVertex();
        buffer.vertex(poseStack.last().pose(), max, min, min).color(r, g, b, 1.0F).endVertex();
        buffer.vertex(poseStack.last().pose(), max, min, max).color(r, g, b, 1.0F).endVertex();
        buffer.vertex(poseStack.last().pose(), min, min, max).color(r, g, b, 1.0F).endVertex();

        tesselator.end();
        poseStack.popPose();
    }


}
