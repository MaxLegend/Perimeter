package ru.tesmio.perimeter.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import ru.tesmio.perimeter.util.IConnectedToPostDevice;

public class TechLamp extends Block implements IConnectedToPostDevice {
    public static final IntegerProperty POWER = BlockStateProperties.POWER;

    public TechLamp() {
        super(Properties.of().strength(1.0F).lightLevel(state -> 0));
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(POWER);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWER);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (!level.isClientSide) {
            int redstonePower = level.getBestNeighborSignal(pos);

            level.setBlock(pos, state.setValue(POWER, redstonePower), Block.UPDATE_ALL);

            super.neighborChanged(state, level, pos, blockIn, fromPos, isMoving);
        }
    }
}
