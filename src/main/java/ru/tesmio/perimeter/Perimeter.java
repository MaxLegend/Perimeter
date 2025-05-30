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
import ru.tesmio.perimeter.blocks.devices.redstoneaccumulator.RedstoneAccumulatorScreen;
import ru.tesmio.perimeter.blocks.devices.redstonefurnace.RedstoneFurnaceScreen;
import ru.tesmio.perimeter.blocks.devices.redstoneworkbench.RedstoneWorkbenchScreen;
import ru.tesmio.perimeter.core.NetworkHandler;
import ru.tesmio.perimeter.core.events.ClientEvents;
import ru.tesmio.perimeter.core.registration.*;

/**
 * Mod for Minecraft 1.20.1 (Forge 47.4.0). Please contact me if you want to use this code in a commercial project.
 *
 * @author Tesmio
 * @version 1.0
 */
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
        RegRecipes.register(modEventBus);

        RegSounds.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::onClientSetup);
        modEventBus.addListener(this::addCreative);
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
            MenuScreens.register(RegMenus.REDSTONE_FURNACE_MENU.get(), RedstoneFurnaceScreen::new);
            MenuScreens.register(RegMenus.ACCUMULATOR_MENU.get(), RedstoneAccumulatorScreen::new);
            MenuScreens.register(RegMenus.WORKBENCH_MENU.get(), RedstoneWorkbenchScreen::new);
        });
        MinecraftForge.EVENT_BUS.addListener(ClientEvents::onRenderWorldLast);

    }

}
