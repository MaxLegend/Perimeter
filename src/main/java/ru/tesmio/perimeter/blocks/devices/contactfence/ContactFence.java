package ru.tesmio.perimeter.blocks.devices.contactfence;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;
import ru.tesmio.perimeter.core.PerimeterBlocks;
import ru.tesmio.perimeter.core.blocknetwork.BlockNetworkSystem;

public class ContactFence extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LEFT = BooleanProperty.create("left");
    public static final BooleanProperty RIGHT = BooleanProperty.create("right");

    public ContactFence() {
        super(BlockBehaviour.Properties.of().strength(1.0F).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(LEFT, false)
                .setValue(RIGHT, false));
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!level.isClientSide) {
            BlockNetworkSystem.get(level).onBlockAdded(level, pos);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!level.isClientSide && !state.is(newState.getBlock())) {
            BlockNetworkSystem.get(level).onBlockRemoved(level, pos);
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LEFT, RIGHT);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p, BlockState s) {
        return new ContactFenceEntity(p, s);
    }

    private boolean isConnectable(BlockState state) {
        return state.is(this) || state.is(PerimeterBlocks.CONTACT_FENCE_EMITTER.get());
    }

    private Tuple<Boolean, Boolean> getLeftRightNeighbors(LevelAccessor level, BlockPos pos, Direction facing) {
        Direction leftDir = facing.getCounterClockWise();
        Direction rightDir = facing.getClockWise();

        BlockPos leftPos = pos.relative(leftDir);
        BlockPos rightPos = pos.relative(rightDir);

        BlockState leftState = level.getBlockState(leftPos);
        BlockState rightState = level.getBlockState(rightPos);

        boolean hasLeft = isConnectable(leftState);
        boolean hasRight = isConnectable(rightState);

        return new Tuple<>(hasLeft, hasRight);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        Direction facing = state.getValue(FACING);
        Tuple<Boolean, Boolean> neighbors = getLeftRightNeighbors(level, currentPos, facing);

        return state
                .setValue(LEFT, neighbors.getA())
                .setValue(RIGHT, neighbors.getB());
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        Tuple<Boolean, Boolean> neighbors = getLeftRightNeighbors(context.getLevel(), context.getClickedPos(), facing);

        return this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(LEFT, neighbors.getA())
                .setValue(RIGHT, neighbors.getB());
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : (lvl, pos, st, be) -> {
            if (be instanceof ContactFenceEntity e) {
                e.tick();
            }
        };
    }

}
