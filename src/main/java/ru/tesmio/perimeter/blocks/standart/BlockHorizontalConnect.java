package ru.tesmio.perimeter.blocks.standart;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockHorizontalConnect extends Block {
    public static final BooleanProperty NORTH = BooleanProperty.create("n");
    public static final BooleanProperty SOUTH = BooleanProperty.create("s");
    public static final BooleanProperty WEST = BooleanProperty.create("w");
    public static final BooleanProperty EAST = BooleanProperty.create("e");
    public static final VoxelShape BASE_SHAPE = Block.box(7.5, 0, 7.5, 8.5, 16, 8.5);

    public BlockHorizontalConnect(Properties p) {
        super(p);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST, false)
                .setValue(WEST, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        return this.defaultBlockState().setValue(NORTH, connectsTo(level, pos.north())).setValue(SOUTH, connectsTo(level, pos.south())).setValue(EAST, connectsTo(level, pos.east())).setValue(WEST, connectsTo(level, pos.west()));
    }

    @Override
    public BlockState updateShape(BlockState s, Direction d, BlockState s2, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        return d == Direction.NORTH ? s.setValue(NORTH, connectsTo(level, neighborPos)) : d == Direction.SOUTH ? s.setValue(SOUTH, connectsTo(level, neighborPos)) : d == Direction.EAST ? s.setValue(EAST, connectsTo(level, neighborPos)) : d == Direction.WEST ? s.setValue(WEST, connectsTo(level, neighborPos)) : s;
    }

    private boolean connectsTo(LevelReader level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.getBlock() instanceof BlockHorizontalConnect;
    }
}
