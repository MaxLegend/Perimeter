package ru.tesmio.perimeter.core;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import ru.tesmio.perimeter.Perimeter;
import ru.tesmio.perimeter.blocks.*;
import ru.tesmio.perimeter.blocks.concretechest.ConcreteChestBlock;
import ru.tesmio.perimeter.blocks.concretechest.ConcreteChestItem;
import ru.tesmio.perimeter.blocks.devices.areasensor.AreaSensor;
import ru.tesmio.perimeter.blocks.devices.linearsensor.LinearReceiverBlock;
import ru.tesmio.perimeter.blocks.devices.linearsensor.LinearTransmitterBlock;
import ru.tesmio.perimeter.blocks.devices.redstonecable.RedstoneCableBlock;
import ru.tesmio.perimeter.blocks.devices.redstonecircuit.RedstoneCircuit;
import ru.tesmio.perimeter.blocks.devices.redstonecircuit.RedstoneCircuitItem;
import ru.tesmio.perimeter.blocks.devices.soundsensor.SoundSensorBlock;
import ru.tesmio.perimeter.blocks.devices.spotlight.SpotlightBlock;
import ru.tesmio.perimeter.blocks.devices.vibrocable.VibrationCable;
import ru.tesmio.perimeter.blocks.devices.vibrocable.VibrationController;
import ru.tesmio.perimeter.items.DefaultBlockItemInfo;

import java.util.function.Supplier;

public class RegBlocks {
    public static final DeferredRegister<Block> BLOCKS_MANUAL = DeferredRegister.create(ForgeRegistries.BLOCKS, Perimeter.MODID);
    public static final DeferredRegister<Block> BLOCKS_CUSTOM_MODEL = DeferredRegister.create(ForgeRegistries.BLOCKS, Perimeter.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Perimeter.MODID);
    public static final DeferredRegister<Item> ITEM_BLOCKS = DeferredRegister.create(ForgeRegistries.ITEMS, Perimeter.MODID);


    public static void init() {
        //cube blocks
        PerimeterBlocks.CONCRETE_BLOCK = registerBlock("concrete_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE).sound(SoundType.STONE)));
        PerimeterBlocks.CONCRETE_BLOCK_CRACKED = registerBlock("concrete_block_old", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE).sound(SoundType.STONE)));

        //simple blocks with model
        PerimeterBlocks.CONCRETE_WALL = registerBlockWithModel("concrete_wall", ConcreteWall::new);
        PerimeterBlocks.ARMORED_CONCRETE_WALL = registerBlockWithModel("armored_concrete_wall", ArmoredConcreteWall::new);
        PerimeterBlocks.CONCRETE_COLUMN = registerBlockWithModel("concrete_column", ConcreteColumn::new);
        PerimeterBlocks.ARMORED_CONCRETE_COLUMN = registerBlockWithModel("armored_concrete_column", ArmoredConcreteColumn::new);
        PerimeterBlocks.CONCRETE_DOOR = registerBlockWithModel("concrete_door", ConcreteDoor::new);
        PerimeterBlocks.CONCRETE_BARS = registerBlockWithModel("concrete_bars", ConcreteBars::new);
        PerimeterBlocks.OAK_PALING_FENCE = manualDataGenRegisterBlock("oak_paling_fence", PalingFence::new);
        PerimeterBlocks.DARKOAK_PALING_FENCE = manualDataGenRegisterBlock("dark_oak_paling_fence", PalingFence::new);
        PerimeterBlocks.ACACIA_PALING_FENCE = manualDataGenRegisterBlock("acacia_paling_fence", PalingFence::new);
        PerimeterBlocks.BAMBOO_PALING_FENCE = manualDataGenRegisterBlock("bamboo_paling_fence", PalingFence::new);
        PerimeterBlocks.CRIMSON_PALING_FENCE = manualDataGenRegisterBlock("crimson_paling_fence", PalingFence::new);
        PerimeterBlocks.JUNGLE_PALING_FENCE = manualDataGenRegisterBlock("jungle_paling_fence", PalingFence::new);
        PerimeterBlocks.BIRCH_PALING_FENCE = manualDataGenRegisterBlock("birch_paling_fence", PalingFence::new);
        PerimeterBlocks.MANGROVE_PALING_FENCE = manualDataGenRegisterBlock("mangrove_paling_fence", PalingFence::new);
        PerimeterBlocks.CHERRY_PALING_FENCE = manualDataGenRegisterBlock("cherry_paling_fence", PalingFence::new);
        PerimeterBlocks.SPRUCE_PALING_FENCE = manualDataGenRegisterBlock("spruce_paling_fence", PalingFence::new);
        PerimeterBlocks.WARPED_PALING_FENCE = manualDataGenRegisterBlock("warped_paling_fence", PalingFence::new);

        //devices
        PerimeterBlocks.AREA_SENSOR = BLOCKS_CUSTOM_MODEL.register("area_sensor", AreaSensor::new);
        PerimeterBlocks.AREA_SENSOR_ITEM = ITEM_BLOCKS.register("area_sensor", () -> new DefaultBlockItemInfo(PerimeterBlocks.AREA_SENSOR.get(), new Item.Properties(), "info.area_sensor"));
        PerimeterBlocks.LINEAR_SENSOR_TRANSMITTER = BLOCKS_CUSTOM_MODEL.register("linear_transmitter", LinearTransmitterBlock::new);
        PerimeterBlocks.LINEAR_SENSOR_TRANSMITTER_ITEM = ITEM_BLOCKS.register("linear_transmitter", () -> new DefaultBlockItemInfo(PerimeterBlocks.LINEAR_SENSOR_TRANSMITTER.get(), new Item.Properties(), "info.linear_transmitter"));
        PerimeterBlocks.LINEAR_SENSOR_RECEIVER = BLOCKS_CUSTOM_MODEL.register("linear_receiver", LinearReceiverBlock::new);
        PerimeterBlocks.LINEAR_SENSOR_RECEIVER_ITEM = ITEM_BLOCKS.register("linear_receiver", () -> new DefaultBlockItemInfo(PerimeterBlocks.LINEAR_SENSOR_RECEIVER.get(), new Item.Properties(), "info.linear_receiver"));
        PerimeterBlocks.REDSTONE_CABLE = BLOCKS_CUSTOM_MODEL.register("redstone_cable_block", () -> new RedstoneCableBlock(BlockBehaviour.Properties.copy(Blocks.STONE).sound(SoundType.STONE)));
        PerimeterBlocks.REDSTONE_CABLE_ITEM = ITEM_BLOCKS.register("redstone_cable_block", () -> new DefaultBlockItemInfo(PerimeterBlocks.REDSTONE_CABLE.get(), new Item.Properties(), "info.redstone_cable"));
        PerimeterBlocks.SPOTLIGHT = registerBlockWithModel("spotlight_block", SpotlightBlock::new);
        PerimeterBlocks.BLOCK_POST = registerBlockWithModel("block_post", BlockPost::new);
        PerimeterBlocks.LAMP_BLOCK = registerBlockWithModel("lamp", LampBlock::new);
        PerimeterBlocks.REDSTONE_BOARD = BLOCKS_CUSTOM_MODEL.register("redstone_board", RedstoneCircuit::new);
        PerimeterBlocks.REDSTONE_BOARD_ITEM = ITEM_BLOCKS.register("redstone_board", () -> new RedstoneCircuitItem(PerimeterBlocks.REDSTONE_BOARD.get()));
        PerimeterBlocks.REDSTONE_BUTTON = registerBlockWithModel("redstone_button", RedstoneButton::new);
        PerimeterBlocks.CONCRETE_CHEST = BLOCKS_CUSTOM_MODEL.register("concrete_chest", ConcreteChestBlock::new);
        PerimeterBlocks.CONCRETE_CHEST_ITEM = ITEM_BLOCKS.register("concrete_chest", () -> new ConcreteChestItem(PerimeterBlocks.CONCRETE_CHEST.get()));
        PerimeterBlocks.VIBRO_CABLE_BLOCK = BLOCKS_CUSTOM_MODEL.register("vibro_cable_block", VibrationCable::new);
        PerimeterBlocks.VIBRO_CABLE_ITEM = ITEM_BLOCKS.register("vibro_cable_block", () -> new DefaultBlockItemInfo(PerimeterBlocks.VIBRO_CABLE_BLOCK.get(), new Item.Properties(), "info.vibro_cable"));
        PerimeterBlocks.VIBRO_CONTROLLER_BLOCK = BLOCKS_CUSTOM_MODEL.register("vibro_controller_block", VibrationController::new);
        PerimeterBlocks.VIBRO_CONTROLLER_ITEM = ITEM_BLOCKS.register("vibro_controller_block", () -> new DefaultBlockItemInfo(PerimeterBlocks.VIBRO_CONTROLLER_BLOCK.get(), new Item.Properties(), "info.vibro_controller"));

        PerimeterBlocks.SOUND_SENSOR = BLOCKS_CUSTOM_MODEL.register("sound_sensor", SoundSensorBlock::new);
        PerimeterBlocks.SOUND_SENSOR_ITEM = ITEM_BLOCKS.register("sound_sensor", () -> new DefaultBlockItemInfo(PerimeterBlocks.SOUND_SENSOR.get(), new Item.Properties(), "info.sound_sensor"));


    }

    public static void register(IEventBus eventBus) {
        BLOCKS_MANUAL.register(eventBus);
        BLOCKS_CUSTOM_MODEL.register(eventBus);
        ITEM_BLOCKS.register(eventBus);
        BLOCKS.register(eventBus);
        init();
    }

    private static <T extends Block> RegistryObject<T> manualDataGenRegisterBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS_MANUAL.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<T> registerBlockWithModel(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS_CUSTOM_MODEL.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ITEM_BLOCKS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
}
