package ru.tesmio.perimeter.core.events;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.tesmio.perimeter.Perimeter;
import ru.tesmio.perimeter.blocks.devices.redstonecable.RedstoneCableRenderer;
import ru.tesmio.perimeter.blocks.devices.spotlight.SpotlightRenderer;
import ru.tesmio.perimeter.blocks.devices.vibrocable.renderer.VibrationCableRenderer;
import ru.tesmio.perimeter.core.registration.RegBlockEntitys;

import java.io.IOException;

@Mod.EventBusSubscriber(modid = Perimeter.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RegRenderers {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(RegBlockEntitys.REDSTONE_CABLE_ENTITY.get(), RedstoneCableRenderer::new);
        event.registerBlockEntityRenderer(RegBlockEntitys.SPOTLIGHT_ENTITY.get(), SpotlightRenderer::new);
        event.registerBlockEntityRenderer(RegBlockEntitys.VIBROCABLE_ENTITY.get(), VibrationCableRenderer::new);
    }

    public static ShaderInstance spotlightShaderInstance, glowSphereShaderInstance;

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) throws IOException {
        ResourceProvider resourceProvider = event.getResourceProvider();

        spotlightShaderInstance = new ShaderInstance(
                resourceProvider,
                new ResourceLocation(Perimeter.MODID, "spotlight"),
                DefaultVertexFormat.POSITION_COLOR_NORMAL);

        event.registerShader(spotlightShaderInstance, shader -> spotlightShaderInstance = shader);


    }

}
