package ru.tesmio.perimeter.core.events;


import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.BlockEvent;
import ru.tesmio.perimeter.blocks.devices.linearsensor.LinearReceiverEntity;
import ru.tesmio.perimeter.blocks.devices.linearsensor.LinearTransmitterEntity;
import ru.tesmio.perimeter.cache.ClientTransmitterCache;
import ru.tesmio.perimeter.util.RenderUtils;

import java.util.Iterator;

public class ClientEvents {
    public static final double MAX_RENDER_DISTANCE = 32.0;

    public static void onRenderWorldLast(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS) {
            Minecraft mc = Minecraft.getInstance();
            PoseStack poseStack = event.getPoseStack();
            Camera camera = mc.gameRenderer.getMainCamera();
            Vec3 camPos = camera.getPosition();

            for (LinearTransmitterEntity transmitter : ClientTransmitterCache.getAll()) {
                BlockPos transmitterPos = transmitter.getBlockPos();
                BlockPos linkedPos = transmitter.getLinkedReceiver();
                Vec3 transmitterCenter = Vec3.atCenterOf(transmitterPos);
                double distanceSq = transmitterCenter.distanceToSqr(camPos);

                if (distanceSq > MAX_RENDER_DISTANCE * MAX_RENDER_DISTANCE) {
                    return;
                }
                if (linkedPos != null) {
                    float glowScale = 1.2f;
                    float glowAlpha = 0.3f;
                    float thickness = 0.05F;
                    Vec3 start = new Vec3(transmitterPos.getX() + 0.5, transmitterPos.getY() + 0.5, transmitterPos.getZ() + 0.5).subtract(camPos);
                    Vec3 end = new Vec3(linkedPos.getX() + 0.5, linkedPos.getY() + 0.5, linkedPos.getZ() + 0.5).subtract(camPos);
                    Vec3 glowStart = start.add(start.subtract(end).normalize().scale(thickness * (glowScale - 1) / 2));
                    Vec3 glowEnd = end.add(end.subtract(start).normalize().scale(thickness * (glowScale - 1) / 2));
                    RenderUtils.renderLineAsBox(poseStack, glowStart, glowEnd, thickness * glowScale, 1.0F, 0, 0, glowAlpha);
                }
            }
        }
    }

    public static void onBlockBreak(BlockEvent.BreakEvent event) {

    }

    public static void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) return;
        if (event.phase == TickEvent.Phase.END) {
            Iterator<LinearTransmitterEntity> iterator = ClientTransmitterCache.getAll().iterator();

            while (iterator.hasNext()) {
                LinearTransmitterEntity transmitter = iterator.next();
                BlockPos transmitterPos = transmitter.getBlockPos();

                // Проверка: существует ли передатчик в мире
                BlockEntity current = minecraft.level.getBlockEntity(transmitterPos);
                if (!(current instanceof LinearTransmitterEntity)) {
                    iterator.remove();
                    System.out.println("[DEBUG] Удален: передатчик исчез с позиции -> " + transmitterPos);
                    continue;
                }

                // Проверка: существует ли связанный ресивер
                BlockPos receiverPos = transmitter.getLinkedReceiver();
                if (receiverPos == null) {
                    iterator.remove();
                    System.out.println("[DEBUG] Удален: нет связанного ресивера -> " + transmitterPos);
                    continue;
                }

                BlockEntity receiverEntity = minecraft.level.getBlockEntity(receiverPos);
                if (!(receiverEntity instanceof LinearReceiverEntity receiver) ||
                        receiver.getLinkedTransmitter() == null ||
                        !receiver.getLinkedTransmitter().equals(transmitterPos)) {

                    iterator.remove();
                    System.out.println("[DEBUG] Удален: ресивер недействителен -> " + receiverPos);
                }
            }
        }
    }
}