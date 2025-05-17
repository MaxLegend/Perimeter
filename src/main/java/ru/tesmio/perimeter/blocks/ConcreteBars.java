package ru.tesmio.perimeter.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ConcreteBars extends Block {
    public ConcreteBars() {
        super(Properties.of().strength(1.0F).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Vec3 lookVec = context.getPlayer().getLookAngle();
        Direction facing;

        if (Math.abs(lookVec.y) > 0.866) {
            facing = lookVec.y > 0 ? Direction.UP : Direction.DOWN;
        } else {
            facing = context.getHorizontalDirection();
        }

        return this.defaultBlockState().setValue(BlockStateProperties.FACING, facing);
    }

    public VoxelShape getShape(BlockState s, BlockGetter g, BlockPos p, CollisionContext c) {
        return switch (s.getValue(BlockStateProperties.FACING)) {
            case UP, DOWN -> Block.box(0, 6, 0, 16, 10, 16);
            case NORTH, SOUTH -> Block.box(0, 0, 6, 16, 16, 10);
            case EAST, WEST -> Block.box(6, 0, 0, 10, 16, 16);

        };
    }
}
