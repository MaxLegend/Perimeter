package ru.tesmio.perimeter.core.registration;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import ru.tesmio.perimeter.Perimeter;
import ru.tesmio.perimeter.blocks.devices.linearsensor.LinearSensorLinker;
import ru.tesmio.perimeter.blocks.devices.redstonecable.RedstoneCableConnector;
import ru.tesmio.perimeter.blocks.devices.redstonecircuit.CircuitComponents;
import ru.tesmio.perimeter.core.PerimeterItems;
import ru.tesmio.perimeter.items.CircuitComponent;

import java.util.function.Supplier;

public class RegItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Perimeter.MODID);

    public static void init() {
        PerimeterItems.REDSTONE_CONDENSER = registerItem("redstone_condenser", () -> new CircuitComponent(CircuitComponents.CONDENSER));
        PerimeterItems.REDSTONE_TRANSISTOR = registerItem("redstone_transistor", () -> new CircuitComponent(CircuitComponents.TRANSISTOR));
        PerimeterItems.REDSTONE_ELECTROLAMP = registerItem("redstone_electrolamp", () -> new CircuitComponent(CircuitComponents.LAMP));
        PerimeterItems.REDSTONE_RESISTOR = registerItem("redstone_resistor", () -> new CircuitComponent(CircuitComponents.RESISTOR));
        PerimeterItems.PROCESSING_CIRCUIT = registerItem("processing_circuit", () -> new Item(new Item.Properties()));
        PerimeterItems.LIGHT_CIRCUIT = registerItem("light_circuit", () -> new Item(new Item.Properties()));
        PerimeterItems.SIGNAL_CIRCUIT = registerItem("signal_circuit", () -> new Item(new Item.Properties()));

        PerimeterItems.LENS = registerItem("lens", () -> new Item(new Item.Properties()));
        PerimeterItems.LAMP = registerItem("lamp_item", () -> new Item(new Item.Properties()));
        PerimeterItems.IRON_ROD = registerItem("iron_rod", () -> new Item(new Item.Properties()));
        PerimeterItems.RAW_IRON_ROD = registerItem("raw_iron_rod", () -> new Item(new Item.Properties()));
        PerimeterItems.LINEAR_SENSOR_LINKER = registerItem("linear_sensor_linker", () -> new LinearSensorLinker(new Item.Properties().stacksTo(1)));
        PerimeterItems.REDSTONE_CABLE_ITEM = registerItem("redstone_cable_item", () -> new RedstoneCableConnector(new Item.Properties()));

    }

    private static <T extends Item> RegistryObject<T> registerItem(String name, Supplier<T> item) {
        RegistryObject<T> toReturn = ITEMS.register(name, item);
        return toReturn;
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        init();
    }
}
