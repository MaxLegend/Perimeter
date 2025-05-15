package ru.tesmio.perimeter.blocks.devices.vibrocable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import ru.tesmio.perimeter.core.blocknetwork.BlockNetworkSystem;

import java.util.ArrayList;
import java.util.List;

public class VibrationCable extends Block implements EntityBlock {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");

    public VibrationCable() {
        super(Properties.of().strength(1.0F).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(AXIS, Direction.Axis.Y)
                .setValue(NORTH, false)
                .setValue(EAST, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(UP, false)
                .setValue(DOWN, false));
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return false;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        Block block = Block.byItem(stack.getItem());

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof VibrationCableEntity cableEntity)) {
            return InteractionResult.PASS;
        }

        // Если уже есть мимикрирующий блок и игрок пытается установить другой блок - пропускаем
        if (cableEntity.getMimickedState() != null && block != Blocks.AIR && !(block instanceof VibrationCable)) {
            return InteractionResult.PASS; // Позволяет обычное взаимодействие с блоком
        }

        // Обработка установки мимикрирующего блока
        if (block != Blocks.AIR && !(block instanceof VibrationCable)) {
            BlockState mimicState = block.defaultBlockState();
            if (mimicState.isCollisionShapeFullBlock(level, pos)) {

                cableEntity.setMimickedState(mimicState);
                if (!player.isCreative()) {
                    stack.shrink(1);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction.Axis axis = context.getClickedFace().getAxis();

        BlockState state = defaultBlockState().setValue(AXIS, axis);

        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.relative(direction);
            BlockState neighborState = level.getBlockState(neighborPos);

            // Проверка на подключаемость (можно вынести в отдельный метод)
            boolean canConnect = canConnectTo(neighborState, direction.getOpposite());
            state = state.setValue(getConnectionProperty(direction), canConnect);
        }

        return state;
    }

    private static BooleanProperty getConnectionProperty(Direction dir) {
        return switch (dir) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
            case UP -> UP;
            case DOWN -> DOWN;
        };
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        boolean connected = canConnectTo(neighborState, direction.getOpposite());
        return state.setValue(getConnectionProperty(direction), connected);
    }

    private boolean canConnectTo(BlockState state, Direction direction) {
        Block block = state.getBlock();

        // Пример: только если это другой кабель или контроллер
        return block instanceof VibrationCable || block instanceof VibrationController;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        // Центральная часть (4x4x4)
        VoxelShape center = Block.box(6, 6, 6, 10, 10, 10);

        // Собираем все части формы
        List<VoxelShape> shapes = new ArrayList<>();
        shapes.add(center);

        // Добавляем соединительные части в зависимости от свойств блока
        if (state.getValue(NORTH)) {
            shapes.add(Block.box(6, 6, 0, 10, 10, 6)); // Север (Z-)
        }
        if (state.getValue(SOUTH)) {
            shapes.add(Block.box(6, 6, 10, 10, 10, 16)); // Юг (Z+)
        }
        if (state.getValue(EAST)) {
            shapes.add(Block.box(10, 6, 6, 16, 10, 10)); // Восток (X+)
        }
        if (state.getValue(WEST)) {
            shapes.add(Block.box(0, 6, 6, 6, 10, 10)); // Запад (X-)
        }
        if (state.getValue(UP)) {
            shapes.add(Block.box(6, 10, 6, 10, 16, 10)); // Вверх (Y+)
        }
        if (state.getValue(DOWN)) {
            shapes.add(Block.box(6, 0, 6, 10, 6, 10)); // Вниз (Y-)
        }
        if (world.getBlockEntity(pos) instanceof VibrationCableEntity) {
            if (((VibrationCableEntity) world.getBlockEntity(pos)).getMimickedState() != null) {
                return Block.box(0, 0, 0, 16, 16, 16);
            }
        }
        // Объединяем все части в одну форму
        return shapes.stream()
                .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
                .orElse(Shapes.block());
    }


    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VibrationCableEntity(pos, state);
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
        builder.add(AXIS, UP, DOWN, NORTH, SOUTH, WEST, EAST);
    }


    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : (lvl, pos, st, be) -> {
            if (be instanceof VibrationCableEntity cable) {
                cable.tick();
            }
        };
    }
}