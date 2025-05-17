package ru.tesmio.perimeter.blocks.devices.voltagefence;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import ru.tesmio.perimeter.core.blocknetwork.BlockNetworkSystem;
import ru.tesmio.perimeter.util.IConnectedToPostDevice;
import ru.tesmio.perimeter.util.ShapesUtil;

public class VoltageFence extends Block implements EntityBlock, IConnectedToPostDevice {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public VoltageFence() {
        super(Properties.of().strength(1.0F).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        super.animateTick(state, world, pos, random);
        VoltageFenceEntity e = (VoltageFenceEntity) world.getBlockEntity(pos);
        if (world.isClientSide && e.isSignalActive()) {
            double x = pos.getX() + 0.9 + (random.nextDouble() - 0.9);
            double y = pos.getY() + 1.0;
            double z = pos.getZ() + 0.9 + (random.nextDouble() - 0.9);
            world.addParticle(new DustParticleOptions(new Vector3f(1f, 0f, 0f), 1.0f), x, y, z, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        VoxelShape shape = Block.box(0, 0, 7, 16, 16, 9);
        switch (state.getValue(FACING)) {
            case NORTH:
                return shape;
            case SOUTH:
                return ShapesUtil.rotate(shape, ShapesUtil.RotationDegree.D180);
            case WEST:
                return ShapesUtil.rotate(shape, ShapesUtil.RotationDegree.D90);
            case EAST:
                return ShapesUtil.rotate(shape, ShapesUtil.RotationDegree.D270);
        }
        return ShapesUtil.FULL_CUBE;
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
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        return this.defaultBlockState().setValue(FACING, facing);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p, BlockState s) {
        return new VoltageFenceEntity(p, s);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : (lvl, pos, st, be) -> {
            if (be instanceof VoltageFenceEntity e) {
                e.tick();
            }
        };
    }
}
