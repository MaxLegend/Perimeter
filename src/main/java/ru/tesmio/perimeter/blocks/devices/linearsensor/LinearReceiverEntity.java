package ru.tesmio.perimeter.blocks.devices.linearsensor;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import ru.tesmio.perimeter.core.registration.RegBlockEntitys;

public class LinearReceiverEntity extends BlockEntity {
    private BlockPos linkedTransmitter;

    public LinearReceiverEntity(BlockPos pos, BlockState state) {
        super(RegBlockEntitys.LINEAR_RECEIVER.get(), pos, state);
    }

    public void setLinkedTransmitter(BlockPos transmitterPos) {
        this.linkedTransmitter = transmitterPos;
        setChanged();
    }

    public BlockPos getLinkedTransmitter() {
        return linkedTransmitter;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("LinkedTransmitter")) {
            int[] posArr = tag.getIntArray("LinkedTransmitter");
            linkedTransmitter = new BlockPos(posArr[0], posArr[1], posArr[2]);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (linkedTransmitter != null) {
            tag.putIntArray("LinkedTransmitter", new int[]{linkedTransmitter.getX(), linkedTransmitter.getY(), linkedTransmitter.getZ()});
        }
    }

    public void invalidateLinks() {
        if (linkedTransmitter != null && level != null && !level.isClientSide) {
            BlockEntity be = level.getBlockEntity(linkedTransmitter);
            if (be instanceof LinearTransmitterEntity transmitter && transmitter.isLinkedTo(this.getBlockPos())) {
                transmitter.clearLink(); // у передатчика сбрасываем привязку
                level.sendBlockUpdated(linkedTransmitter, level.getBlockState(linkedTransmitter), level.getBlockState(linkedTransmitter), 3);
            }
            linkedTransmitter = null;
            setChanged();
        }
    }
}
