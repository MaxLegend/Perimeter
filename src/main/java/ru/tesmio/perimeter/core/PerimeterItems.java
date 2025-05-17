package ru.tesmio.perimeter.core;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;
import ru.tesmio.perimeter.core.registration.RegItems;

/**
 * The constants are defined here. See registration here ->
 *
 * @author Tesmio
 * @see RegItems
 */
public class PerimeterItems {
    public static RegistryObject<Item>
            RAW_IRON_ROD,
            IRON_ROD,
            LENS, LAMP,
            LINEAR_SENSOR_LINKER,
            REDSTONE_CABLE_ITEM;
    public static RegistryObject<Item>
            REDSTONE_BOARD,
            PROCESSING_CIRCUIT,
            LIGHT_CIRCUIT,
            SIGNAL_CIRCUIT,
            REDSTONE_RESISTOR,
            REDSTONE_ELECTROLAMP,
            REDSTONE_TRANSISTOR,
            REDSTONE_CONDENSER;

}
