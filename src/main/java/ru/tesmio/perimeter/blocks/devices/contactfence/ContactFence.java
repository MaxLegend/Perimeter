package ru.tesmio.perimeter.blocks.devices.contactfence;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import ru.tesmio.perimeter.core.PerimeterBlocks;
import ru.tesmio.perimeter.core.blocknetwork.BlockNetworkSystem;
import ru.tesmio.perimeter.util.ShapesUtil;

public class ContactFence extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LEFT = BooleanProperty.create("left");
    public static final BooleanProperty RIGHT = BooleanProperty.create("right");

    protected final VoxelShape BASE_SHAPE = Block.box(6.5D, 0D, 2.5D, 9.5D, 16D, 5.5D);
    protected final VoxelShape LEFT_SHAPE = Shapes.or(BASE_SHAPE, Block.box(0D, 3.6D, 0.65D, 8D, 13.85D, 0.9D), Block.box(7.25D, 3D, 0D, 8.75D, 14.5D, 4D));
    protected final VoxelShape RIGHT_SHAPE = Shapes.or(BASE_SHAPE, Block.box(8D, 3.6D, 0.65D, 16D, 13.85D, 0.9D), Block.box(7.25D, 3D, 0D, 8.75D, 14.5D, 4D));
    protected final VoxelShape BOTH_SHAPE = Shapes.or(BASE_SHAPE, Block.box(0D, 3.6D, 0.65D, 8D, 13.85D, 0.9D), Block.box(8D, 3.6D, 0.65D, 16D, 13.85D, 0.9D), Block.box(7.25D, 3D, 0D, 8.75D, 14.5D, 4D));

    public ContactFence() {
        super(BlockBehaviour.Properties.of().strength(1.0F).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(LEFT, false)
                .setValue(RIGHT, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        boolean left = state.getValue(LEFT);
        boolean right = state.getValue(RIGHT);
        boolean both = left && right;
        VoxelShape shape = BASE_SHAPE;
        if (both) {
            shape = BOTH_SHAPE;
        } else if (left) {
            shape = LEFT_SHAPE;
        } else if (right) {
            shape = RIGHT_SHAPE;
        }
        switch (facing) {
            case NORTH -> {
            }
            case SOUTH -> shape = ShapesUtil.rotate(shape, ShapesUtil.RotationDegree.D180);
            case EAST -> shape = ShapesUtil.rotate(shape, ShapesUtil.RotationDegree.D270);
            case WEST -> shape = ShapesUtil.rotate(shape, ShapesUtil.RotationDegree.D90);

        }
        return shape;
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

    private boolean isConnectable(BlockState state, Direction facing) {
        if (state.is(PerimeterBlocks.CONTACT_FENCE_EMITTER.get())) {
            return true;
        }
        if (state.is(this)) {

            if (state.hasProperty(ContactFence.FACING)) {
                return state.getValue(ContactFence.FACING) == facing;
            }
        }
        return false;
    }

    private Tuple<Boolean, Boolean> getLeftRightNeighbors(LevelAccessor level, BlockPos pos, Direction facing) {
        Direction leftDir = facing.getCounterClockWise();
        Direction rightDir = facing.getClockWise();

        BlockPos leftPos = pos.relative(leftDir);
        BlockPos rightPos = pos.relative(rightDir);

        BlockState leftState = level.getBlockState(leftPos);
        BlockState rightState = level.getBlockState(rightPos);

        boolean hasLeft = isConnectable(leftState, facing);
        boolean hasRight = isConnectable(rightState, facing);

        return new Tuple<>(hasLeft, hasRight);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
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
