package ru.tesmio.perimeter.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import ru.tesmio.perimeter.util.IPerimeterDevice;

public class LampBlock extends Block implements IPerimeterDevice {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

    public LampBlock() {
        super(Properties.of().strength(1.0F).lightLevel(state -> 0));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(BlockStateProperties.POWERED, false).setValue(BlockStateProperties.FACING, Direction.NORTH));
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(BlockStateProperties.POWERED) ? 15 : 0; // 12 - уровень света при включении, 0 - при выключении
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos,
                                Block blockIn, BlockPos fromPos, boolean isMoving) {
        boolean powered = level.hasNeighborSignal(pos);
        if (powered != state.getValue(BlockStateProperties.POWERED)) {
            level.setBlock(pos, state.setValue(BlockStateProperties.POWERED, powered), Block.UPDATE_ALL);
            //state.setValue(BlockStateProperties.POWERED, powered).setValue(FACING, state.getValue(FACING));
        }
        super.neighborChanged(state, level, pos, blockIn, fromPos, isMoving);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.POWERED, FACING);
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
}
