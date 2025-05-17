package ru.tesmio.perimeter.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

public class TickableSound extends AbstractTickableSoundInstance {

    private final BlockPos pos;
    private boolean stopped = false;

    public TickableSound(SoundEvent sound, BlockPos pos) {
        super(sound, SoundSource.BLOCKS, RandomSource.create());
        this.pos = pos;
        this.looping = true; // зацикливание
        this.delay = 0;      // без задержки
        this.volume = 1.0F;
        this.pitch = 1.0F;
        this.x = pos.getX() + 0.5;
        this.y = pos.getY() + 0.5;
        this.z = pos.getZ() + 0.5;
    }

    @Override
    public void tick() {
        // Остановка, если игрок слишком далеко (опционально)
        if (Minecraft.getInstance().player == null) return;

        double distanceSq = Minecraft.getInstance().player.blockPosition().distSqr(this.pos);
        if (distanceSq > 64 * 64) { // за пределами 64 блоков — остановить
            stop();
        }

        // Можно добавить доп. условия (например, по блок-состоянию или флагу)
    }


    public void stopped() {
        this.stopped = true;
    }

    @Override
    public boolean isStopped() {
        return stopped;
    }
}
