package ru.tesmio.perimeter.core.events;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.tesmio.perimeter.Perimeter;
import ru.tesmio.perimeter.blocks.devices.redstonecircuit.RedstoneCircuit;

@Mod.EventBusSubscriber(modid = Perimeter.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ModClientEvents {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        int[] counter = {0};
        Component[] recipes = {
                Component.translatable("recipe.light_circuit"),
                Component.translatable("recipe.signal_circuit"),
                Component.translatable("recipe.signal_circuit")
        };
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        // Проверяем наведение на блок и зажатие Shift
        HitResult hit = mc.hitResult;
        if (hit != null
                && hit.getType() == HitResult.Type.BLOCK
                && InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 341)) {

            BlockPos pos = ((BlockHitResult) hit).getBlockPos();
            BlockState state = mc.level.getBlockState(pos);
            Component message = Component.literal("")
                    .append(Component.translatable("recipe.light_circuit"));
            //        .append(Component.translatable("recipe.signal_circuit"))
            //           .append(Component.translatable("recipe.processing_circuit"));
            if (state.getBlock() instanceof RedstoneCircuit) {
                //  mc.player.sendSystemMessage(Component.translatable("recipe.light_circuit"));
                new Thread(() -> {
                    try {
                        mc.player.displayClientMessage(Component.translatable("recipe.light_circuit"), true);
                        Thread.sleep(3200); // Задержка 1.5 сек
                        mc.player.displayClientMessage(Component.translatable("recipe.signal_circuit"), true);
                        Thread.sleep(3200);
                        mc.player.displayClientMessage(Component.translatable("recipe.processing_circuit"), true);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
                //   mc.player.sendSystemMessage(Component.translatable("recipe.signal_circuit"));
                //      mc.player.displayClientMessage(message.copy().append("/n").append(Component.translatable("recipe.signal_circuit")), true);
            }
        }
    }


}
