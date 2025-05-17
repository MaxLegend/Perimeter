package ru.tesmio.perimeter.blocks.devices.voltagefence;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import ru.tesmio.perimeter.core.blocknetwork.BlockNetworkSystem;
import ru.tesmio.perimeter.core.blocknetwork.IBlockNetworkMember;
import ru.tesmio.perimeter.core.registration.RegBlockEntitys;

import java.util.List;

public class VoltageFenceEntity extends BlockEntity implements IBlockNetworkMember {
    private boolean active = false;
    private int signalTicks = 0;

    public VoltageFenceEntity(BlockPos pos, BlockState state) {
        super(RegBlockEntitys.VOLTAGE_FENCE_ENTITY.get(), pos, state);
    }

    public void triggerSignal() {
        if (level == null || level.isClientSide) return;
        BlockNetworkSystem.get(level).transmitSignal(level, getBlockInNetwork());
    }

    public boolean isSignalActive() {
        return active;
    }

    @Override
    public void pulse() {
        this.active = true;

    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        BlockState state = getBlockState();
        Direction facing = state.getValue(VoltageFence.FACING);

        BlockPos targetPos = worldPosition.relative(facing);
        AABB detectionBox = new AABB(worldPosition).inflate(0.5);
        ;
        if (level.hasNeighborSignal(worldPosition)) {

            List<Entity> entities = level.getEntities((Entity) null, detectionBox, Entity::isAlive);

            triggerSignal();
            if (!entities.isEmpty()) {
                for (Entity entity : entities) {

                    entity.hurt(level.damageSources().generic(), 8.0F); // 2.0F - урон, можно изменить
                }
            }
        } else {
            this.active = false;
        }

    }

    private @NotNull AABB getInteractiveAABB(Direction facing) {
        double offset = 0.8;
        double minX = worldPosition.getX();
        double minY = worldPosition.getY();
        double minZ = worldPosition.getZ();
        double maxX = worldPosition.getX() + 1.0;
        double maxY = worldPosition.getY() + 1.0;
        double maxZ = worldPosition.getZ() + 1.0;
        switch (facing) {
            case NORTH -> maxZ = minZ + offset;
            case SOUTH -> minZ = maxZ - offset;
            case WEST -> maxX = minX + offset;
            case EAST -> minX = maxX - offset;
        }
        AABB detectionBox = new AABB(minX, minY, minZ, maxX, maxY, maxZ);
        return detectionBox;
    }

    @Override
    public void setBlockInNetwork(BlockPos pos) {
        //     pos = this.getBlockPos();
    }

    @Override
    public BlockPos getBlockInNetwork() {
        return getBlockPos();
    }
}
