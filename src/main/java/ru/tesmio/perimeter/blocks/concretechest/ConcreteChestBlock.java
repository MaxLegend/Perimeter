package ru.tesmio.perimeter.blocks.concretechest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import ru.tesmio.perimeter.util.ShapesUtil;

public class ConcreteChestBlock extends Block implements EntityBlock {
    public static final BooleanProperty IS_OPEN = BooleanProperty.create("is_open");
    public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);
    public static final VoxelShape SHAPE_OPEN = Shapes.or(
            Block.box(0.75, 0, 0.75, 15.25, 10, 15.25),
            Block.box(1.25, 10, 1.25, 14.75, 11, 14.5));
    public static final VoxelShape SHAPE = Shapes.or(
            Block.box(0.75, 0, 0.75, 15.25, 10, 15.25),
            Block.box(1.25, 10, 1.25, 14.75, 11, 14.5),
            Block.box(0.5, 11, 0.75, 15.5, 16, 16));

    public ConcreteChestBlock() {
        super(Properties.of().strength(1.0F).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(IS_OPEN, false).setValue(FACING, Direction.NORTH));
    }

    public VoxelShape getShape(BlockState s, BlockGetter g, BlockPos p, CollisionContext ctx) {
        Direction d = s.getValue(FACING);

        return switch (s.getValue(FACING)) {

            case NORTH:
                if (s.getValue(IS_OPEN)) {
                    yield SHAPE_OPEN;
                } else {
                    yield SHAPE;
                }
            case SOUTH:
                if (s.getValue(IS_OPEN)) {
                    yield ShapesUtil.rotate(SHAPE_OPEN, ShapesUtil.RotationDegree.D180);
                } else {
                    yield ShapesUtil.rotate(SHAPE, ShapesUtil.RotationDegree.D180);
                }
            case WEST:
                if (s.getValue(IS_OPEN)) {
                    yield ShapesUtil.rotate(SHAPE_OPEN, ShapesUtil.RotationDegree.D90);
                } else {
                    yield ShapesUtil.rotate(SHAPE, ShapesUtil.RotationDegree.D90);
                }
            case EAST:
                if (s.getValue(IS_OPEN)) {
                    yield ShapesUtil.rotate(SHAPE_OPEN, ShapesUtil.RotationDegree.D270);
                } else {
                    yield ShapesUtil.rotate(SHAPE, ShapesUtil.RotationDegree.D270);
                }
            case UP:
            case DOWN:
                yield Shapes.empty();
        };

    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(IS_OPEN, FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection();
        return this.defaultBlockState().setValue(FACING, facing);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof ConcreteChestEntity chest) {
            if (!state.getValue(IS_OPEN) && player.isShiftKeyDown()) {
                state = state.setValue(IS_OPEN, true);
                level.setBlock(pos, state, 10);
                return InteractionResult.SUCCESS;
            }
            if (state.getValue(IS_OPEN)) {
                if (player.isShiftKeyDown()) {
                    level.setBlock(pos, state.setValue(IS_OPEN, false), 10);
                    return InteractionResult.SUCCESS;
                }
                NetworkHooks.openScreen((ServerPlayer) player, chest, pos);
            }
            return InteractionResult.FAIL;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ConcreteChestEntity chest) {
                // Дропаем все предметы из инвентаря сундука
                for (int i = 0; i < chest.getItemHandler().getSlots(); ++i) {
                    ItemStack itemstack = chest.getItemHandler().getStackInSlot(i);
                    if (!itemstack.isEmpty()) {
                        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), itemstack);
                    }
                }
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ConcreteChestEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

}