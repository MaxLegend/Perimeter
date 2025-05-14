package ru.tesmio.perimeter.blocks.devices.soundsensor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import ru.tesmio.perimeter.util.IPerimeterDevice;

public class SoundSensorBlock extends Block implements EntityBlock, IPerimeterDevice {
    public static final BooleanProperty ACTIVATED = BooleanProperty.create("activated");
    public static final EnumProperty<EnumSoundMode> MODE = EnumProperty.create("mode", EnumSoundMode.class);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

    public SoundSensorBlock() {
        super(BlockBehaviour.Properties.of().strength(1.5f).requiresCorrectToolForDrops());
        this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVATED, false).setValue(MODE, EnumSoundMode.ALL).setValue(BlockStateProperties.FACING, Direction.NORTH));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SoundSensorBlockEntity(pos, state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clickedFace = context.getClickedFace();
        context.getHorizontalDirection();
        Direction facing = switch (clickedFace) {
            case UP -> Direction.UP;
            case DOWN -> Direction.DOWN;
            case NORTH -> Direction.NORTH;
            case SOUTH -> Direction.SOUTH;
            case WEST -> Direction.WEST;
            case EAST -> Direction.EAST;
        };

        return this.defaultBlockState().setValue(FACING, facing);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        //    Minecraft.getInstance().player.displayClientMessage(Component.literal("Режим детектора: " + EnumSoundMode.ALL.getSerializedName()), true);
    }


    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof SoundSensorBlockEntity be) {
            EnumSoundMode newMode = be.cycleMode();
            level.setBlock(pos, state.setValue(MODE, newMode), Block.UPDATE_ALL);

            //    player.displayClientMessage(Component.translatable("sound_sensor.mode", newMode.getSerializedName()), true);
            player.displayClientMessage(
                    Component.translatable("sound_sensor.mode")
                            .append(": ")
                            .append(newMode.getTranslatedName()),
                    true
            );
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    public static void reactToSound(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!state.getValue(ACTIVATED)) {

            level.setBlock(pos, state.setValue(ACTIVATED, true), Block.UPDATE_ALL);

            // Автоотключение через 20 тиков (1 секунда)
            level.scheduleTick(pos, state.getBlock(), 20);


        }
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(ACTIVATED) ? 15 : 0;
    }

    public boolean isSignalSource(BlockState s) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVATED, FACING, MODE);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(ACTIVATED)) {
            level.setBlock(pos, state.setValue(ACTIVATED, false), Block.UPDATE_ALL);
        }
    }

    @Override
    public VoxelShape getShape(BlockState s, BlockGetter g, BlockPos p, CollisionContext c) {
        return switch (s.getValue(FACING)) {
            case SOUTH -> Block.box(2, 2, 0, 14, 14, 11);
            case NORTH -> Block.box(2, 2, 5, 14, 14, 16);
            case WEST -> Shapes.or(
                    Block.box(5, 4, 6, 16, 12, 10),
                    Block.box(5, 6, 4, 16, 10, 12));
            case EAST -> Shapes.or(
                    Block.box(0, 4, 6, 11, 12, 10),
                    Block.box(0, 6, 4, 11, 10, 12));
            case DOWN -> Shapes.or(
                    Block.box(4, 5, 6, 12, 16, 10),
                    Block.box(6, 5, 4, 10, 16, 12));
            case UP -> Shapes.or(
                    Block.box(4, 0, 6, 12, 11, 10),
                    Block.box(6, 0, 4, 10, 11, 12));
        };
    }

    public enum EnumSoundMode implements StringRepresentable {
        ALL("all"),
        BLOCKS("blocks"),
        HOSTILE_ENTITIES("hostile_entities"),
        NEUTRAL_ENTITES("neutral_entities"),
        PLAYERS("players"),
        WEATHERS("weathers");

        private final String displayName;

        EnumSoundMode(String displayName) {
            this.displayName = displayName;
        }

        public Component getTranslatedName() {
            return Component.translatable("sound_sensor.mode." + this.displayName);
        }

        public static EnumSoundMode byName(String name) {
            for (EnumSoundMode mode : values()) {
                if (mode.getSerializedName().equals(name)) {
                    return mode;
                }
            }
            return ALL; // Значение по умолчанию при ошибке
        }

        public EnumSoundMode next() {
            return values()[(this.ordinal() + 1) % values().length];
        }

        @Override
        public String getSerializedName() {
            return this.displayName;
        }
    }
}
