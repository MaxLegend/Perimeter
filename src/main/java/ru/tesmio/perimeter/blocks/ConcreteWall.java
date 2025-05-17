package ru.tesmio.perimeter.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import ru.tesmio.perimeter.util.ShapesUtil;


public class ConcreteWall extends HorizontalDirectionalBlock {
    final VoxelShape BOXS[] = new VoxelShape[]{Block.box(0D, 0D, 6D, 16D, 16D, 10D),
            Block.box(0D, 0D, 6D, 16D, 16D, 10D),
            Block.box(6D, 0D, 0D, 10D, 16D, 16D),
            Block.box(6D, 0D, 0D, 10D, 16D, 16D)};

    public ConcreteWall() {
        super(BlockBehaviour.Properties
                .of()
                .strength(24.5F, 12.0F)
                .sound(SoundType.STONE)
                .requiresCorrectToolForDrops()
        );
    }

    public RenderShape getRenderShape(BlockState s) {
        return RenderShape.MODEL;
    }

    public VoxelShape getShape(BlockState s, BlockGetter g, BlockPos p, CollisionContext c) {
        switch (s.getValue(FACING)) {
            case NORTH:
                return BOXS[0];
            case SOUTH:
                return BOXS[1];
            case WEST:
                return BOXS[2];
            case EAST:
                return BOXS[3];
        }
        return ShapesUtil.FULL_CUBE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

}
