package ru.tesmio.perimeter.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ArmoredConcreteColumn extends Block {
    public static final VoxelShape SHAPE =
            Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;

    public ArmoredConcreteColumn() {
        super(BlockBehaviour.Properties
                .of()
                .strength(42.5F, 16.0F)
                .sound(SoundType.STONE)
                .requiresCorrectToolForDrops());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST, false)
                .setValue(WEST, false));
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        return this.defaultBlockState()
                .setValue(NORTH, connectsTo(level, pos.north()))
                .setValue(SOUTH, connectsTo(level, pos.south()))
                .setValue(EAST, connectsTo(level, pos.east()))
                .setValue(WEST, connectsTo(level, pos.west()));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        return direction == Direction.NORTH ? state.setValue(NORTH, connectsTo(level, neighborPos)) :
                direction == Direction.SOUTH ? state.setValue(SOUTH, connectsTo(level, neighborPos)) :
                        direction == Direction.EAST  ? state.setValue(EAST, connectsTo(level, neighborPos)) :
                                direction == Direction.WEST  ? state.setValue(WEST, connectsTo(level, neighborPos)) :
                                        state;
    }

    private boolean connectsTo(LevelReader level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.getBlock() instanceof ArmoredConcreteColumn|| state.getBlock() instanceof ArmoredConcreteWall
                || state.getBlock() instanceof ConcreteWall || state.getBlock() instanceof ConcreteColumn;
    }
    public VoxelShape getShape(BlockState s, BlockGetter g, BlockPos p, CollisionContext c) {
        return SHAPE;
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST);
    }
}
