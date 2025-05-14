package ru.tesmio.perimeter.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import ru.tesmio.perimeter.util.IPerimeterDevice;

public class BlockPost extends Block {
    public static final EnumProperty<PillarState> STATE = EnumProperty.create("state", PillarState.class);
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty WEST = BooleanProperty.create("west");

    public BlockPost() {
        super(Properties.of().strength(1.0F).forceSolidOff());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(STATE, PillarState.MIDDLE_STATE)
                .setValue(NORTH, false)
                .setValue(EAST, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STATE, NORTH, EAST, SOUTH, WEST);
    }


    // Проверяет, может ли блок получать сигнал от соседей
    private PillarState getPillarState(LevelAccessor level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        BlockState above = level.getBlockState(pos.above());

        boolean isBelowSolid = below.isSolid();
        boolean isAboveSolid = above.isSolid();
        boolean isBelowPillar = below.getBlock() == this;
        boolean isAbovePillar = above.getBlock() == this;

        if (isBelowPillar && isAbovePillar) {
            return PillarState.MIDDLE_STATE;
        } else if (isBelowSolid) {
            return PillarState.DOWN_STATE;
        } else if (isAboveSolid) {
            return PillarState.UP_STATE;
        } else if (above.isAir() && isBelowPillar) {
            return PillarState.CAP_STATE;
        } else if (!above.isAir() && !isAbovePillar) {
            return PillarState.UP_STATE;
        } else if (!below.isAir() && above.isAir()) {
            return PillarState.MIDDLE_STATE;
        }
        return PillarState.MIDDLE_STATE; // default
    }

    @Override
    public VoxelShape getShape(BlockState s, BlockGetter g, BlockPos p, CollisionContext c) {
        if (s.getValue(STATE) == PillarState.CAP_STATE) {
            return Block.box(4.5D, 0D, 4.5D, 11.5D, 11D, 11.5D);
        }
        return Block.box(4.5D, 0D, 4.5D, 11.5D, 16D, 11.5D);
    }

    final VoxelShape[] SHPS = new VoxelShape[]{
            Block.box(5D, 0D, 5D, 11D, 16D, 11D),
            Block.box(5D, 0D, 5D, 11D, 13D, 14D),
            Block.box(4D, 0D, 4D, 12D, 16D, 12D),
            Block.box(3D, 0D, 3D, 13D, 10D, 13D)
    };


    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        // Обновляем только состояние столба (верх/низ), соединения обновляются в neighborChanged
        return state.setValue(STATE, getPillarState(level, currentPos));
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!level.isClientSide) {
            // Мгновенное обновление соседей
            updateAllNearbyStates(level, pos);

        }
    }

    private void updateAllNearbyStates(Level level, BlockPos pos) {
        // Обновляем все блоки в радиусе 1 блока
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos checkPos = pos.offset(x, y, z);
                    BlockState checkState = level.getBlockState(checkPos);

                    if (checkState.getBlock() instanceof BlockPost) {
                        BlockState newState = updatePostState(checkState, level, checkPos);
                        if (!newState.equals(checkState)) {
                            level.setBlock(checkPos, newState, Block.UPDATE_ALL);
                        }
                    }
                }
            }
        }
    }

    private BlockState updatePostState(BlockState state, LevelAccessor level, BlockPos pos) {
        // Единый метод для полного обновления состояния
        return updateConnectionStates(state, level, pos)
                .setValue(STATE, getPillarState(level, pos));
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos,
                                Block block, BlockPos fromPos, boolean isMoving) {
        if (!level.isClientSide) {
            BlockState newState = updatePostState(state, level, pos);
            if (!newState.equals(state)) {
                level.setBlock(pos, newState, Block.UPDATE_ALL);
            }
            int inputPower = level.getBestNeighborSignal(pos);
        }

    }

    private BlockState updateConnectionStates(BlockState state, LevelAccessor level, BlockPos pos) {
        BlockState newState = state;

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos neighborPos = pos.relative(direction);
            BlockState neighborState = level.getBlockState(neighborPos);

            boolean canConnect = neighborState.getBlock() instanceof IPerimeterDevice;
            newState = setConnectionState(newState, direction, canConnect);
        }

        return newState;
    }

    private BlockState setConnectionState(BlockState state, Direction direction, boolean canConnect) {
        return switch (direction) {
            case NORTH -> state.setValue(NORTH, canConnect);
            case EAST -> state.setValue(EAST, canConnect);
            case SOUTH -> state.setValue(SOUTH, canConnect);
            case WEST -> state.setValue(WEST, canConnect);
            default -> state;
        };
    }


    private void updateNeighboringPillars(Level level, BlockPos pos) {
        // Проверяем все 6 направлений (включая верх и низ)
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.relative(direction);
            BlockState neighborState = level.getBlockState(neighborPos);

            if (neighborState.getBlock() instanceof BlockPost) {
                level.scheduleTick(neighborPos, neighborState.getBlock(), 1);
            }
        }
    }

    enum PillarState implements StringRepresentable {
        DOWN_STATE("down"),
        MIDDLE_STATE("middle"),
        UP_STATE("up"),
        CAP_STATE("cap");

        private final String name;

        PillarState(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

}
