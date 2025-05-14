package ru.tesmio.perimeter.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ConcreteDoor extends DoorBlock {

    public ConcreteDoor() {
        super(Properties.of().strength(1.0F), BlockSetType.STONE);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        state = state.cycle(OPEN);
        level.setBlock(pos, state, 10);

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    public VoxelShape getShape(BlockState p_52807_, BlockGetter p_52808_, BlockPos p_52809_, CollisionContext p_52810_) {
        VoxelShape S_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 6.0);
        VoxelShape N_AABB = Block.box(0.0, 0.0, 10.0, 16.0, 16.0, 16.0);
        VoxelShape W_AABB = Block.box(10.0, 0.0, 0.0, 16.0, 16.0, 16.0);
        VoxelShape E_AABB = Block.box(0.0, 0.0, 0.0, 6.0, 16.0, 16.0);
        Direction $$4 = p_52807_.getValue(FACING);
        boolean $$5 = !(Boolean) p_52807_.getValue(OPEN);
        boolean $$6 = p_52807_.getValue(HINGE) == DoorHingeSide.RIGHT;
        switch ($$4) {
            case EAST:
            default:
                return $$5 ? E_AABB : ($$6 ? N_AABB : S_AABB);
            case SOUTH:
                return $$5 ? S_AABB : ($$6 ? E_AABB : W_AABB);
            case WEST:
                return $$5 ? W_AABB : ($$6 ? S_AABB : N_AABB);
            case NORTH:
                return $$5 ? N_AABB : ($$6 ? W_AABB : E_AABB);
        }
    }

    // Реакция на редстоун
    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean moved) {
        boolean hasRedstoneSignal = level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.relative(state.getValue(HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN));
        if (!this.defaultBlockState().is(neighborBlock) && hasRedstoneSignal != state.getValue(POWERED)) {
            if (hasRedstoneSignal != state.getValue(OPEN)) {
                level.setBlock(pos, state.setValue(OPEN, hasRedstoneSignal).setValue(POWERED, hasRedstoneSignal), 2);
            }
        }
    }
}
