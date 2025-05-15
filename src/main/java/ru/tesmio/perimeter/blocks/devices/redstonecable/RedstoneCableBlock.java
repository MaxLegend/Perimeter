package ru.tesmio.perimeter.blocks.devices.redstonecable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import ru.tesmio.perimeter.core.registration.RegBlockEntitys;
import ru.tesmio.perimeter.util.IConnectedToPostDevice;

import java.util.ArrayList;

public class RedstoneCableBlock extends Block implements EntityBlock, IConnectedToPostDevice {

    public static final DirectionProperty ATTACHED_FACE = BlockStateProperties.FACING;
    public static final IntegerProperty POWER = BlockStateProperties.POWER;

    public RedstoneCableBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(ATTACHED_FACE, Direction.NORTH)
                .setValue(POWER, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ATTACHED_FACE, POWER);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RedstoneCableEntity(pos, state);
    }

    // Установка направления при размещении блока
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(ATTACHED_FACE, context.getClickedFace());
    }

    // Формы в зависимости от стороны крепления
    final VoxelShape[] SHAPES = new VoxelShape[]{
            Shapes.create(0.31D, 0.31D, 0.65D, 0.69D, 0.69D, 1D), // NORTH
            Shapes.create(0.31D, 0.31D, 0D, 0.69D, 0.69D, 0.35D), // SOUTH
            Shapes.create(0D, 0.31D, 0.31D, 0.35D, 0.69D, 0.69D), // EAST
            Shapes.create(0.65D, 0.31D, 0.31D, 1D, 0.69D, 0.69D), // WEST
            Shapes.create(0.31D, 0D, 0.31D, 0.69D, 0.35D, 0.69D), // UP
            Shapes.create(0.31D, 0.65D, 0.31D, 0.69D, 1D, 0.69D)  // DOWN
    };

    @Override
    public VoxelShape getShape(BlockState s, BlockGetter g, BlockPos p, CollisionContext c) {
        return switch (s.getValue(ATTACHED_FACE)) {
            case SOUTH -> SHAPES[1];
            case NORTH -> SHAPES[0];
            case WEST -> SHAPES[3];
            case EAST -> SHAPES[2];
            case DOWN -> SHAPES[5];
            case UP -> SHAPES[4];
        };
    }

    // Вызывается при изменении соседнего блока
    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof RedstoneCableEntity cable) {

                //              cable.updateSignalInNetwork(); // Прямой вызов обновления
            }
        }
    }

    // Разрешаем аналоговый сигнал
    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    // Делаем блок источником сигнала
    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    // Возвращаем уровень сигнала на выход
    @Override
    public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction dir) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof RedstoneCableEntity cable) {
            int signal = cable.getSignal();

            return signal;
        }
        return 0;
    }

    // Привязка тика сущности
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == RegBlockEntitys.REDSTONE_CABLE_ENTITY.get() ? RedstoneCableEntity::tick : null;
    }

    // (по желанию) очистка логики при разрушении блока
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {

        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof RedstoneCableEntity cable) {
                for (BlockPos otherPos : new ArrayList<>(cable.getConnections())) {
                    BlockEntity otherBe = level.getBlockEntity(otherPos);
                    if (otherBe instanceof RedstoneCableEntity otherCable) {
                        level.removeBlockEntity(pos);
                        otherCable.removeConnection(pos);
                    }
                }
                cable.clearConnections();
            }
        }

        super.onRemove(state, level, pos, newState, isMoving);
    }
}
