package ru.tesmio.perimeter.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PalingFence extends CrossCollisionBlock {
    private final VoxelShape[] occlusionByIndex;
    public PalingFence() {
        super(1.0F, 1.0F, 16.0F, 16.0F, 15.0F, BlockBehaviour.Properties
                .of()
                .strength(2.5F, 2.0F)
                .sound(SoundType.WOOD)
                .requiresCorrectToolForDrops());
        this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false).setValue(WATERLOGGED, false));
        this.occlusionByIndex = this.makeShapes(2.0F, 1.0F, 16.0F, 6.0F, 15.0F);
    }
    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        if (!world.isClientSide) {
            if (entity instanceof LivingEntity e) {
                VoxelShape blockShape = state.getShape(world, pos);
                AABB blockBounds = blockShape.bounds().move(pos); // Переводим в мировые координаты

                // Получаем хитбокс сущности (нижняя половина, чтобы проверять только "ноги")
                AABB entityFeetBox = new AABB(
                        entity.getBoundingBox().minX,
                        entity.getBoundingBox().minY,
                        entity.getBoundingBox().minZ,
                        entity.getBoundingBox().maxX,
                        entity.getBoundingBox().minY + 0.1, // Проверяем только нижнюю часть ног
                        entity.getBoundingBox().maxZ
                );

                if (entityFeetBox.intersects(blockBounds)) {

                    e.hurt(world.damageSources().generic(), 1.0F);

                }
            }
        }
    }
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter getter, BlockPos pos) {
        return this.occlusionByIndex[this.getAABBIndex(state)];
    }

    public VoxelShape getVisualShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return this.getShape(state, getter, pos, context);
    }
    public boolean isPathfindable(BlockState state, BlockGetter getter, BlockPos pos, PathComputationType type) {
        return false;
    }

    public boolean connectsTo(BlockState state, boolean isConnectBlock, Direction dir) {
     //   Block block = state.getBlock();
        boolean isFence = this.isSameFence(state);
      //  boolean $$5 = block instanceof FenceGateBlock && FenceGateBlock.connectsToDirection(state, dir);
        return !isExceptionForConnection(state) && isConnectBlock || isFence ;
    }
    private boolean isSameFence(BlockState state) {
        return state.is(this);
    }
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockGetter getter = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        FluidState fluidstate = ctx.getLevel().getFluidState(ctx.getClickedPos());
        BlockPos posN = pos.north();
        BlockPos posE = pos.east();
        BlockPos posS = pos.south();
        BlockPos posW = pos.west();
        BlockState sN = getter.getBlockState(posN);
        BlockState sE = getter.getBlockState(posE);
        BlockState sS = getter.getBlockState(posS);
        BlockState sW = getter.getBlockState(posW);
        return super.getStateForPlacement(ctx)
                .setValue(NORTH, this.connectsTo(sN, sN.isFaceSturdy(getter, posN, Direction.SOUTH), Direction.SOUTH))
                .setValue(EAST, this.connectsTo(sE, sE.isFaceSturdy(getter, posE, Direction.WEST), Direction.WEST))
                .setValue(SOUTH, this.connectsTo(sS, sS.isFaceSturdy(getter, posS, Direction.NORTH), Direction.NORTH))
                .setValue(WEST, this.connectsTo(sW, sW.isFaceSturdy(getter, posW, Direction.EAST), Direction.EAST))
                .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);


    }
    public BlockState updateShape(BlockState state, Direction dir, BlockState state2, LevelAccessor accessor, BlockPos pos, BlockPos pos2) {
        if (state.getValue(WATERLOGGED)) {
            accessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(accessor));
        }

        return dir.getAxis().getPlane() == Direction.Plane.HORIZONTAL ?
                state.setValue(PROPERTY_BY_DIRECTION.get(dir),
                        this.connectsTo(state2, state2.isFaceSturdy(accessor, pos2, dir.getOpposite()), dir.getOpposite()))
                : super.updateShape(state, dir, state2, accessor, pos, pos2);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> state) {
        state.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED);
    }
}
