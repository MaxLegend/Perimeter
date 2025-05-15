package ru.tesmio.perimeter.blocks.devices.soundsensor;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import ru.tesmio.perimeter.core.registration.RegBlockEntitys;

public class SoundSensorBlockEntity extends BlockEntity {
    private SoundSensorBlock.EnumSoundMode mode = SoundSensorBlock.EnumSoundMode.ALL;

    public SoundSensorBlockEntity(BlockPos pos, BlockState state) {
        super(RegBlockEntitys.SOUND_SENSOR_ENTITY.get(), pos, state);
    }

    public SoundSensorBlock.EnumSoundMode cycleMode() {
        this.mode = this.mode.next();
        setChanged();
        return this.mode;
    }

    public SoundSensorBlock.EnumSoundMode getMode() {
        return this.mode;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("Mode", this.mode.getSerializedName());
    }

    @Override
    public void load(CompoundTag tag) {
        String modeName = tag.getString("Mode");
        this.mode = SoundSensorBlock.EnumSoundMode.byName(modeName);
    }

    public SoundSensorBlock.EnumSoundMode next() {
        return this.mode.next();
    }
}
