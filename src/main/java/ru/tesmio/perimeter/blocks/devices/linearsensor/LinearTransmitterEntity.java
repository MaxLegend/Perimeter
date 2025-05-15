package ru.tesmio.perimeter.blocks.devices.linearsensor;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import ru.tesmio.perimeter.cache.ClientTransmitterCache;
import ru.tesmio.perimeter.core.registration.RegBlockEntitys;

import java.util.List;


public class LinearTransmitterEntity extends BlockEntity {
    private BlockPos linkedReceiver;

    public LinearTransmitterEntity(BlockPos pos, BlockState state) {
        super(RegBlockEntitys.LINEAR_TRANSMITTER.get(), pos, state);
    }

    public void setLinkedReceiver(BlockPos receiverPos) {
        this.linkedReceiver = receiverPos;

        setChanged();
    }


    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && level.isClientSide) {
            ClientTransmitterCache.add(this);
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (level != null && level.isClientSide) {
            ClientTransmitterCache.remove(this);
        }
    }

    public BlockPos getLinkedReceiver() {
        return linkedReceiver;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("LinkedReceiver")) {
            int[] posArr = tag.getIntArray("LinkedReceiver");
            linkedReceiver = new BlockPos(posArr[0], posArr[1], posArr[2]);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (linkedReceiver != null) {
            tag.putIntArray("LinkedReceiver", new int[]{linkedReceiver.getX(), linkedReceiver.getY(), linkedReceiver.getZ()});
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        if (linkedReceiver != null) {
            tag.putBoolean("HasLinkedReceiver", true);
            tag.putIntArray("LinkedReceiver", new int[]{

                    linkedReceiver.getX(),
                    linkedReceiver.getY(),
                    linkedReceiver.getZ()
            });
        } else {
            tag.putBoolean("HasLinkedReceiver", false);// явно пишем "пустой" тег
        }
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        if (tag.contains("HasLinkedReceiver") && !tag.getBoolean("HasLinkedReceiver")) {
            linkedReceiver = null;

        } else if (tag.contains("LinkedReceiver")) {
            int[] posArr = tag.getIntArray("LinkedReceiver");
            linkedReceiver = new BlockPos(posArr[0], posArr[1], posArr[2]);

        }
    }


    public boolean isLinkedTo(BlockPos pos) {
        return linkedReceiver != null && linkedReceiver.equals(pos);
    }

    public void clearLink() {
        this.linkedReceiver = null;
        setChanged();

        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            handleUpdateTag(tag);
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntity be) {
        if (level.isClientSide) return;
        if (be instanceof LinearTransmitterEntity) {
            LinearTransmitterEntity lte = (LinearTransmitterEntity) be;
            BlockPos receiverPos = lte.getLinkedReceiver();
            if (receiverPos == null) {
                if (state.getValue(BlockStateProperties.POWERED)) {
                    level.setBlock(pos, state.setValue(BlockStateProperties.POWERED, false), 3);
                }
                return;
            }

            Vec3 start = Vec3.atCenterOf(pos);
            Vec3 end = Vec3.atCenterOf(receiverPos);
            AABB boundingBox = new AABB(start, end).inflate(0.2); // чуть шире луча

            List<Entity> entities = level.getEntities((Entity) null, boundingBox, e -> e.isPickable() && !(e instanceof ItemEntity));
            HitResult blockHit = level.clip(new ClipContext(
                    start,
                    end,
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    null
            ));

            boolean blockObstructed = false;
            if (blockHit.getType() == HitResult.Type.BLOCK) {
                BlockPos hitPos = ((BlockHitResult) blockHit).getBlockPos();
                BlockState hitState = level.getBlockState(hitPos);

                if (hitState.isRedstoneConductor(level, hitPos)) {
                    blockObstructed = true;
                }
            }
            boolean beamBlocked = blockObstructed || entities.stream().anyMatch(entity -> {
                AABB entityBox = entity.getBoundingBox();
                return entityBox.clip(start, end).isPresent();
            });

            boolean currentlyPowered = state.getValue(BlockStateProperties.POWERED);
            if (beamBlocked != currentlyPowered) {
                level.setBlock(pos, state.setValue(BlockStateProperties.POWERED, beamBlocked), 3);
            }
        }
    }
}
