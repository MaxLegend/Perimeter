package ru.tesmio.perimeter.core.registration;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import ru.tesmio.perimeter.Perimeter;
import ru.tesmio.perimeter.blocks.concretechest.ConcreteChestEntity;
import ru.tesmio.perimeter.blocks.devices.areasensor.AreaSensorEntity;
import ru.tesmio.perimeter.blocks.devices.contactfence.ContactFenceEmitterEntity;
import ru.tesmio.perimeter.blocks.devices.contactfence.ContactFenceEntity;
import ru.tesmio.perimeter.blocks.devices.linearsensor.LinearReceiverEntity;
import ru.tesmio.perimeter.blocks.devices.linearsensor.LinearTransmitterEntity;
import ru.tesmio.perimeter.blocks.devices.redstoneaccumulator.RedstoneAccumulatorEntity;
import ru.tesmio.perimeter.blocks.devices.redstonecable.RedstoneCableEntity;
import ru.tesmio.perimeter.blocks.devices.redstonecircuit.RedstoneCircuitEntity;
import ru.tesmio.perimeter.blocks.devices.redstonefurnace.RedstoneFurnaceEntity;
import ru.tesmio.perimeter.blocks.devices.redstoneworkbench.RedstoneWorkbenchEntity;
import ru.tesmio.perimeter.blocks.devices.soundsensor.SoundSensorBlockEntity;
import ru.tesmio.perimeter.blocks.devices.spotlight.SpotlightEntity;
import ru.tesmio.perimeter.blocks.devices.vibrocable.VibrationCableEntity;
import ru.tesmio.perimeter.blocks.devices.vibrocable.VibrationControllerEntity;
import ru.tesmio.perimeter.blocks.devices.voltagefence.VoltageFenceEntity;
import ru.tesmio.perimeter.core.PerimeterBlocks;

public class RegBlockEntitys {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Perimeter.MODID);


    public static final RegistryObject<BlockEntityType<AreaSensorEntity>> AREA_SENSOR_ENTITY =
            BLOCK_ENTITIES.register("area_sensor",
                    () -> BlockEntityType.Builder.of(AreaSensorEntity::new, PerimeterBlocks.AREA_SENSOR.get()).build(null));

    public static final RegistryObject<BlockEntityType<LinearTransmitterEntity>> LINEAR_TRANSMITTER =
            BLOCK_ENTITIES.register("linear_transmitter",
                    () -> BlockEntityType.Builder.of(LinearTransmitterEntity::new, PerimeterBlocks.LINEAR_SENSOR_TRANSMITTER.get()).build(null));
    public static final RegistryObject<BlockEntityType<LinearReceiverEntity>> LINEAR_RECEIVER =
            BLOCK_ENTITIES.register("linear_receiver",
                    () -> BlockEntityType.Builder.of(LinearReceiverEntity::new, PerimeterBlocks.LINEAR_SENSOR_RECEIVER.get()).build(null));
    public static final RegistryObject<BlockEntityType<RedstoneCableEntity>> REDSTONE_CABLE_ENTITY =
            BLOCK_ENTITIES.register("redstone_cable_entity",
                    () -> BlockEntityType.Builder.of(RedstoneCableEntity::new, PerimeterBlocks.REDSTONE_CABLE.get()).build(null));
    public static final RegistryObject<BlockEntityType<SpotlightEntity>> SPOTLIGHT_ENTITY =
            BLOCK_ENTITIES.register("spotlight_entity",
                    () -> BlockEntityType.Builder.of(SpotlightEntity::new, PerimeterBlocks.SPOTLIGHT.get()).build(null));
    public static final RegistryObject<BlockEntityType<RedstoneCircuitEntity>> CIRCUIT_BOARD_ENTITY =
            BLOCK_ENTITIES.register("redstone_circuit_entity",
                    () -> BlockEntityType.Builder.of(RedstoneCircuitEntity::new, PerimeterBlocks.REDSTONE_BOARD.get()).build(null));
    public static final RegistryObject<BlockEntityType<ConcreteChestEntity>> CONCRETE_CHEST_ENTITY =
            BLOCK_ENTITIES.register("concrete_chest_entity",
                    () -> BlockEntityType.Builder.of(ConcreteChestEntity::new, PerimeterBlocks.CONCRETE_CHEST.get()).build(null));
    public static final RegistryObject<BlockEntityType<VibrationCableEntity>> VIBROCABLE_ENTITY =
            BLOCK_ENTITIES.register("vibro_cable_entity",
                    () -> BlockEntityType.Builder.of(VibrationCableEntity::new, PerimeterBlocks.VIBRO_CABLE_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<VibrationControllerEntity>> VIBROCABLE_CONTROLLER_ENTITY =
            BLOCK_ENTITIES.register("vibro_cable_controller_entity",
                    () -> BlockEntityType.Builder.of(VibrationControllerEntity::new, PerimeterBlocks.VIBRO_CONTROLLER_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<SoundSensorBlockEntity>> SOUND_SENSOR_ENTITY =
            BLOCK_ENTITIES.register("sound_sensor_entity",
                    () -> BlockEntityType.Builder.of(SoundSensorBlockEntity::new, PerimeterBlocks.SOUND_SENSOR.get()).build(null));
    public static final RegistryObject<BlockEntityType<ContactFenceEntity>> CONTACT_FENCE_ENTITY =
            BLOCK_ENTITIES.register("contact_fence_entity",
                    () -> BlockEntityType.Builder.of(ContactFenceEntity::new, PerimeterBlocks.CONTACT_FENCE.get()).build(null));
    public static final RegistryObject<BlockEntityType<ContactFenceEmitterEntity>> CONTACT_FENCE_EMITTER_ENTITY =
            BLOCK_ENTITIES.register("contact_fence_emitter_entity",
                    () -> BlockEntityType.Builder.of(ContactFenceEmitterEntity::new, PerimeterBlocks.CONTACT_FENCE_EMITTER.get()).build(null));
    public static final RegistryObject<BlockEntityType<VoltageFenceEntity>> VOLTAGE_FENCE_ENTITY =
            BLOCK_ENTITIES.register("voltage_fence_entity",
                    () -> BlockEntityType.Builder.of(VoltageFenceEntity::new, PerimeterBlocks.VOLTAGE_FENCE.get()).build(null));
    public static final RegistryObject<BlockEntityType<RedstoneFurnaceEntity>> REDSTONE_FURNACE_ENTITY =
            BLOCK_ENTITIES.register("redstone_furnace_entity",
                    () -> BlockEntityType.Builder.of(RedstoneFurnaceEntity::new, PerimeterBlocks.REDSTONE_FURNACE.get()).build(null));
    public static final RegistryObject<BlockEntityType<RedstoneAccumulatorEntity>> REDSTONE_ACCUMULATOR_ENTITY =
            BLOCK_ENTITIES.register("redstone_accumulator_entity",
                    () -> BlockEntityType.Builder.of(RedstoneAccumulatorEntity::new, PerimeterBlocks.REDSTONE_ACCUMULATOR.get()).build(null));
    public static final RegistryObject<BlockEntityType<RedstoneWorkbenchEntity>> WORKBENCH_ENTITY =
            BLOCK_ENTITIES.register("redstone_workbench_entity",
                    () -> BlockEntityType.Builder.of(RedstoneWorkbenchEntity::new, PerimeterBlocks.REDSTONE_WORKBENCH.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}