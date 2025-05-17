package ru.tesmio.perimeter.blocks.devices.contactfence;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import ru.tesmio.perimeter.core.blocknetwork.BlockNetworkSystem;

public class ContactFenceEmitter extends Block implements EntityBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty DOWN = BooleanProperty.create("down");
    protected final VoxelShape SHAPE = Shapes.or(
            Block.box(0, 0, 0, 16, 3, 16),
            Block.box(2.5, 0, 2.5, 4.5, 16, 4.5),
            Block.box(11.5, 0, 11.5, 13.5, 16, 13.5),
            Block.box(2.5, 0, 11.5, 4.5, 16, 13.5),
            Block.box(11.5, 0, 2.5, 13.5, 16, 4.5)
    );
    protected final VoxelShape SHAPE_MID = Shapes.or(
            Block.box(2.5, 0, 2.5, 4.5, 16, 4.5),
            Block.box(11.5, 0, 11.5, 13.5, 16, 13.5),
            Block.box(2.5, 0, 11.5, 4.5, 16, 13.5),
            Block.box(11.5, 0, 2.5, 13.5, 16, 4.5)
    );

    public ContactFenceEmitter() {
        super(BlockBehaviour.Properties.of().strength(1.5f).requiresCorrectToolForDrops().forceSolidOff().noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false).setValue(DOWN, true));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {

        return state.setValue(DOWN, level.getBlockState(currentPos.below()).isSolid());
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(DOWN, context.getLevel().getBlockState(context.getClickedPos()).isSolid());
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!level.isClientSide && !state.is(newState.getBlock())) {
            BlockNetworkSystem.get(level).onBlockRemoved(level, pos);
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return state.getValue(DOWN) ? SHAPE : SHAPE_MID;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!level.isClientSide) {
            BlockNetworkSystem.get(level).onBlockAdded(level, pos);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED, DOWN);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(POWERED) ? 15 : 0;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ContactFenceEmitterEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : (lvl, pos, st, be) -> {
            if (be instanceof ContactFenceEmitterEntity controller) {

                controller.tick();
            }
        };
    }
}
