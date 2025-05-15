package ru.tesmio.perimeter.core;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import ru.tesmio.perimeter.core.registration.RegBlocks;

/**
 * The constants are defined here. See registration here ->
 *
 * @author Tesmio
 * @see RegBlocks
 */
public class PerimeterBlocks {
    //cube blocks
    public static RegistryObject<Block>
            CONCRETE_BLOCK,
            CONCRETE_BLOCK_CRACKED;

    //simple blocks with model
    public static RegistryObject<Block>
            CONCRETE_DOOR,
            CONCRETE_BARS,
            CONCRETE_WALL,
            CONCRETE_COLUMN,
            ARMORED_CONCRETE_WALL,
            ARMORED_CONCRETE_COLUMN,
            OAK_PALING_FENCE,
            ACACIA_PALING_FENCE,
            BAMBOO_PALING_FENCE,
            BIRCH_PALING_FENCE,
            CHERRY_PALING_FENCE,
            CRIMSON_PALING_FENCE,
            DARKOAK_PALING_FENCE,
            JUNGLE_PALING_FENCE,
            MANGROVE_PALING_FENCE,
            SPRUCE_PALING_FENCE,
            WARPED_PALING_FENCE;

    //devices with entity and logic
    public static RegistryObject<Block>
            CONCRETE_CHEST,
            AREA_SENSOR,
            REDSTONE_CABLE,
            SPOTLIGHT,
            BLOCK_POST,
            LAMP_BLOCK,
            REDSTONE_BOARD,
            REDSTONE_BUTTON,
            LINEAR_SENSOR_TRANSMITTER,
            VIBRO_CABLE_BLOCK,
            VIBRO_CONTROLLER_BLOCK,
            SOUND_SENSOR,
            CONTACT_FENCE,
            CONTACT_FENCE_EMITTER,
            LINEAR_SENSOR_RECEIVER;

    public static RegistryObject<Item>
            CONCRETE_CHEST_ITEM,
            REDSTONE_BOARD_ITEM,
            AREA_SENSOR_ITEM,
            LINEAR_SENSOR_TRANSMITTER_ITEM,
            LINEAR_SENSOR_RECEIVER_ITEM,
            VIBRO_CABLE_ITEM,
            VIBRO_CONTROLLER_ITEM,
            SOUND_SENSOR_ITEM,
            CONTACT_FENCE_ITEM,
            CONTACT_FENCE_EMITTER_ITEM,
            REDSTONE_CABLE_ITEM;
}
