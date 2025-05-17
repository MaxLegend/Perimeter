package ru.tesmio.perimeter.blocks.devices.linearsensor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import ru.tesmio.perimeter.util.IConnectedToPostDevice;

public class LinearReceiverBlock extends Block implements EntityBlock, IConnectedToPostDevice {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

    public LinearReceiverBlock() {
        super(BlockBehaviour.Properties
                .of()
                .strength(1F, 1F)
                .sound(SoundType.STONE)
                .requiresCorrectToolForDrops());
        this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof LinearReceiverEntity receiver) {
                receiver.invalidateLinks(); // твой метод
                level.removeBlockEntity(pos);
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    final VoxelShape[] SHAPES = new VoxelShape[]{
            Shapes.create(0.31D, 0.31D, 0.65D, 0.69D, 0.69D, 1D),
            Shapes.create(0.31D, 0.31D, 0D, 0.69D, 0.69D, 0.35D),
            Shapes.create(0D, 0.31D, 0.31D, 0.35D, 0.69D, 0.69D),
            Shapes.create(0.65D, 0.31D, 0.31D, 1D, 0.69D, 0.69D),
            Shapes.create(0.31D, 0D, 0.31D, 0.69D, 0.35D, 0.69D),
            Shapes.create(0.31D, 0.65D, 0.31D, 0.69D, 1D, 0.69D)
    };

    public VoxelShape getShape(BlockState s, BlockGetter g, BlockPos p, CollisionContext c) {
        return switch (s.getValue(FACING)) {
            case SOUTH -> SHAPES[1];
            case NORTH -> SHAPES[0];
            case WEST -> SHAPES[3];
            case EAST -> SHAPES[2];
            case DOWN -> SHAPES[5];
            case UP -> SHAPES[4];
        };
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
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LinearReceiverEntity(pos, state);
    }


}
