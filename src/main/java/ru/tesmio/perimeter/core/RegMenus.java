package ru.tesmio.perimeter.core;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import ru.tesmio.perimeter.Perimeter;
import ru.tesmio.perimeter.blocks.concretechest.ConcreteChestMenu;
import ru.tesmio.perimeter.blocks.devices.areasensor.screen.AreaSensorMenu;

public class RegMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, Perimeter.MODID);

    public static final RegistryObject<MenuType<AreaSensorMenu>> AREA_SENSOR_MENU =
            registerMenuType(AreaSensorMenu::new, "area_sensor_menu");
    public static final RegistryObject<MenuType<ConcreteChestMenu>> CONCRETE_CHEST_MENU =
            MENUS.register("concrete_chest_menu", () ->
                    IForgeMenuType.create((windowId, inv, data) -> {
                        BlockPos pos = data.readBlockPos(); // Получаем позицию сундука
                        BlockEntity be = inv.player.level().getBlockEntity(pos);
                        return new ConcreteChestMenu(windowId, inv, be);
                    })
            );

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(IContainerFactory<T> factory, String name) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));

    }


    public static void register(IEventBus bus) {
        MENUS.register(bus);
    }

}
