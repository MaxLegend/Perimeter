package ru.tesmio.perimeter;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import ru.tesmio.perimeter.blocks.concretechest.ConcreteChestScreen;
import ru.tesmio.perimeter.blocks.devices.areasensor.screen.AreaSensorScreen;
import ru.tesmio.perimeter.core.*;
import ru.tesmio.perimeter.core.events.ClientEvents;


@Mod(Perimeter.MODID)
public class Perimeter {

    public static final String MODID = "perimeter";

    public Perimeter() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        RegCreativeTabs.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
        RegItems.register(modEventBus);
        RegBlocks.register(modEventBus);
        RegBlockEntitys.register(modEventBus);
        RegMenus.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::onClientSetup);
        modEventBus.addListener(this::addCreative);
        //   modEventBus.addListener(ClientEvents::onRenderWorldLast);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        NetworkHandler.register();
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {

        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    public void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(RegMenus.AREA_SENSOR_MENU.get(), AreaSensorScreen::new);
            MenuScreens.register(RegMenus.CONCRETE_CHEST_MENU.get(), ConcreteChestScreen::new);
 
        });
        MinecraftForge.EVENT_BUS.addListener(ClientEvents::onRenderWorldLast);

    }

}
