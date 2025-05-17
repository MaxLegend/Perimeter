package ru.tesmio.perimeter.core.events;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.PlayLevelSoundEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.tesmio.perimeter.blocks.devices.soundsensor.SoundSensorBlock;

@Mod.EventBusSubscriber
public class SoundSensorEvent {
    private static final int DETECTION_RADIUS = 6;

    @SubscribeEvent
    public static void onSoundPlayed(PlayLevelSoundEvent.AtPosition event) {

        Level level = (Level) event.getLevel();
        BlockPos soundPos = BlockPos.containing(event.getPosition());

        BlockPos.betweenClosedStream(
                        soundPos.offset(-DETECTION_RADIUS, -DETECTION_RADIUS, -DETECTION_RADIUS),
                        soundPos.offset(DETECTION_RADIUS, DETECTION_RADIUS, DETECTION_RADIUS))
                .forEach(pos -> {
                    BlockState s = level.getBlockState(pos);
                    if (s.getBlock() instanceof SoundSensorBlock) {
                        SoundSensorBlock.EnumSoundMode mode = s.getValue(SoundSensorBlock.MODE);
                        if (getReactOnSound(mode, event.getSource())) {
                            SoundSensorBlock.reactToSound(level, pos);
                        }

                    }
                });
    }

    public static boolean getReactOnSound(SoundSensorBlock.EnumSoundMode mode, SoundSource soundSource) {
        return switch (mode) {
            case ALL -> true;
            case BLOCKS -> soundSource == SoundSource.BLOCKS;
            case HOSTILE_ENTITIES -> soundSource == SoundSource.HOSTILE;
            case NEUTRAL_ENTITES -> soundSource == SoundSource.NEUTRAL;
            case PLAYERS -> soundSource == SoundSource.PLAYERS;
            case WEATHERS -> soundSource == SoundSource.WEATHER;
        };
    }
}

