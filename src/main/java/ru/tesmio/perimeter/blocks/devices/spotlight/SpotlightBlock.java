package ru.tesmio.perimeter.blocks.devices.spotlight;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import ru.tesmio.perimeter.util.IPerimeterDevice;

public class SpotlightBlock extends Block implements EntityBlock, IPerimeterDevice {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

    public SpotlightBlock() {
        super(Properties.of().strength(1.0F));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(BlockStateProperties.POWERED, false).setValue(BlockStateProperties.FACING, Direction.NORTH));
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.POWERED, FACING);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof SpotlightEntity) {
            level.removeBlockEntity(pos);
        }
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

    // Привязка BlockEntity
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SpotlightEntity(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState s, BlockGetter g, BlockPos p, CollisionContext c) {
        return switch (s.getValue(FACING)) {
            case SOUTH -> Block.box(2, 2, 0, 14, 14, 11);
            case NORTH -> Block.box(2, 2, 5, 14, 14, 16);
            case WEST -> Block.box(5, 2, 2, 16, 14, 14);
            case EAST -> Block.box(0, 2, 2, 11, 14, 14);
            case DOWN -> Block.box(2, 5, 2, 14, 16, 14);
            case UP -> Block.box(2, 0, 2, 14, 11, 14);
        };
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos,
                                Block blockIn, BlockPos fromPos, boolean isMoving) {
        boolean powered = level.hasNeighborSignal(pos);
        if (powered != state.getValue(BlockStateProperties.POWERED)) {
            level.setBlock(pos, state.setValue(BlockStateProperties.POWERED, powered), 3);
        }
        super.neighborChanged(state, level, pos, blockIn, fromPos, isMoving);
    }

    // Tick блока нужен для автоматической очистки света
    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {

        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof SpotlightEntity projector) {
            projector.tickServer();
        }
    }

    // Запускаем тик каждый тик
    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving) {
        world.scheduleTick(pos, this, 1);
    }
}