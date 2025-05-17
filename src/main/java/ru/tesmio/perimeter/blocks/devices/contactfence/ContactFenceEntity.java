package ru.tesmio.perimeter.blocks.devices.contactfence;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import ru.tesmio.perimeter.core.blocknetwork.BlockNetworkSystem;
import ru.tesmio.perimeter.core.blocknetwork.IBlockNetworkMember;
import ru.tesmio.perimeter.core.registration.RegBlockEntitys;

import java.util.List;

public class ContactFenceEntity extends BlockEntity implements IBlockNetworkMember {
    private BlockPos emitterPosition;
    private boolean active = false;
    private int signalTicks = 0;

    public ContactFenceEntity(BlockPos pos, BlockState state) {
        super(RegBlockEntitys.CONTACT_FENCE_ENTITY.get(), pos, state);
    }

    public boolean isSignalActive() {
        return active;
    }

    public void triggerSignal() {
        if (level == null || level.isClientSide) return;
        BlockNetworkSystem.get(level).transmitSignal(level, getBlockInNetwork());
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
        Direction facing = state.getValue(ContactFence.FACING);

        BlockPos targetPos = worldPosition.relative(facing);
        AABB detectionBox = new AABB(targetPos);

        List<Entity> entities = level.getEntities((Entity) null, detectionBox, Entity::isAlive);
        if (!entities.isEmpty()) {
            triggerSignal();
            double centerX = detectionBox.minX + (detectionBox.getXsize() / 2);
            double centerY = detectionBox.minY + (detectionBox.getYsize() / 2);
            double centerZ = detectionBox.minZ + (detectionBox.getZsize() / 2);

            for (Entity entity : entities) {
//                if (entity instanceof Player) {
//                    if (((Player) entity).isCreative()) {
//                        return;
//                    }
//                }
                double dx = entity.getX() - centerX;
                double dy = entity.getY() - centerY;
                double dz = entity.getZ() - centerZ;

                double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
                if (length > 0) {
                    dx /= length;
                    dy /= length;
                    dz /= length;
                    double strength = 0.3;

                    entity.setDeltaMovement(entity.getDeltaMovement().add(dx * strength, dy * strength, dz * strength));
                    entity.hurtMarked = true;
                }
            }
        }
    }

    @Override
    public void setBlockInNetwork(BlockPos pos) {
        this.emitterPosition = pos;
    }

    @Override
    public BlockPos getBlockInNetwork() {
        return emitterPosition == null ? getBlockPos() : emitterPosition;
    }
}
