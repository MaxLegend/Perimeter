package ru.tesmio.perimeter.blocks.devices.vibrocable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import ru.tesmio.perimeter.core.blocknetwork.BlockNetworkSystem;
import ru.tesmio.perimeter.core.blocknetwork.IBlockNetworkMember;
import ru.tesmio.perimeter.core.registration.RegBlockEntitys;

import java.util.List;


public class VibrationCableEntity extends BlockEntity implements IBlockNetworkMember {
    private BlockPos controllerPos;
    private boolean active = false;
    private int signalTicks = 0;
    private BlockState mimickedState = null;

    public VibrationCableEntity(BlockPos pos, BlockState state) {
        super(RegBlockEntitys.VIBROCABLE_ENTITY.get(), pos, state);
    }

    public BlockState getMimickedState() {
        return mimickedState;
    }

    public void setMimickedState(BlockState state) {

        this.mimickedState = state;
        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);

    }

    @Override
    public AABB getRenderBoundingBox() {
        return super.getRenderBoundingBox().inflate(1.3f);
    }

    public boolean isVibrationSignalActive() {
        return active;
    }

    public void triggerSignal() {
        if (level == null || level.isClientSide) return;
        //   System.out.println("[VibrationCable] Triggering signal for controller at: " + getController());
        BlockNetworkSystem.get(level).transmitSignal(level, getBlockInNetwork());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("MimickedState")) {
            BlockState state = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), tag.getCompound("MimickedState"));
            mimickedState = state;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (mimickedState != null) {
            tag.put("MimickedState", NbtUtils.writeBlockState(mimickedState));
        }
    }

    @Override
    public void pulse() {
        this.active = true;
        this.signalTicks = 5; // сигнал держится 5 тиков
    }

    public void tick() {
        if (signalTicks > 0) {
            signalTicks--;
            if (signalTicks == 0) {
                active = false;
            }
        }
        if (level == null || level.isClientSide) return;

        BlockState state = getBlockState();
        Direction.Axis axis = state.getValue(VibrationCable.AXIS);
        double x = worldPosition.getX() + 0.5;
        double y = worldPosition.getY() + 0.5;
        double z = worldPosition.getZ() + 0.5;

        AABB detectionBox = switch (axis) {
            case X -> new AABB(
                    x - 0.5, y - 1.75, z - 1.75,
                    x + 0.5, y + 1.75, z + 1.75
            ); // плоскость YZ (ширина по YZ, 1 блок по X)
            case Y -> new AABB(
                    x - 1.75, y - 0.5, z - 1.75,
                    x + 1.75, y + 0.5, z + 1.75
            ); // плоскость XZ (ширина по XZ, 1 блок по Y)
            case Z -> new AABB(
                    x - 1.75, y - 1.75, z - 0.5,
                    x + 1.75, y + 1.75, z + 0.5
            ); // плоскость XY (ширина по XY, 1 блок по Z)
        };

        List<Entity> entities = level.getEntities((Entity) null, detectionBox, Entity::isAlive);
        if (!entities.isEmpty()) {
            triggerSignal();
        }
    }


    @Override
    public void setBlockInNetwork(BlockPos pos) {
        this.controllerPos = pos;
    }

    @Override
    public BlockPos getBlockInNetwork() {
        return controllerPos == null ? getBlockPos() : controllerPos;
    }
}