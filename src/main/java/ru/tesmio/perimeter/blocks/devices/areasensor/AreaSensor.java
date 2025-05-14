package ru.tesmio.perimeter.blocks.devices.areasensor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import ru.tesmio.perimeter.blocks.devices.areasensor.screen.AreaSensorMenu;
import ru.tesmio.perimeter.core.NetworkHandler;
import ru.tesmio.perimeter.network.packets.AreaSensorPacketClient;
import ru.tesmio.perimeter.util.IPerimeterDevice;


public class AreaSensor extends Block implements EntityBlock, IPerimeterDevice {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

    // Настройки блока: прочность и необходимость правильного инструмента
    public AreaSensor() {
        super(BlockBehaviour.Properties.of().strength(1.5f).requiresCorrectToolForDrops());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(BlockStateProperties.POWERED, false).setValue(BlockStateProperties.FACING, Direction.NORTH));
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.POWERED, FACING);
    }

    final VoxelShape[] SHAPES = new VoxelShape[]{
            Shapes.create(0.31D, 0.31D, 0.65D, 0.69D, 0.69D, 1D),
            Shapes.create(0.31D, 0.31D, 0D, 0.69D, 0.69D, 0.35D),
            Shapes.create(0D, 0.31D, 0.31D, 0.35D, 0.69D, 0.69D),
            Shapes.create(0.65D, 0.31D, 0.31D, 1D, 0.69D, 0.69D),
            Shapes.create(0.31D, 0D, 0.31D, 0.69D, 0.35D, 0.69D),
            Shapes.create(0.31D, 0.65D, 0.31D, 0.69D, 1D, 0.69D)
    };

    public VoxelShape getShape(BlockState s, BlockGetter g, BlockPos p, CollisionContext c) {
        return switch (s.getValue(FACING)) {
            case SOUTH -> SHAPES[1];
            case NORTH -> SHAPES[0];
            case WEST -> SHAPES[3];
            case EAST -> SHAPES[2];
            case DOWN -> SHAPES[5];
            case UP -> SHAPES[4];
        };
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clickedFace = context.getClickedFace();
        context.getHorizontalDirection();
        Direction facing = switch (clickedFace) {
            case UP -> Direction.UP;
            case DOWN -> Direction.DOWN;
            case NORTH -> Direction.NORTH;
            case SOUTH -> Direction.SOUTH;
            case WEST -> Direction.WEST;
            case EAST -> Direction.EAST;
        };

        return this.defaultBlockState().setValue(FACING, facing);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(BlockStateProperties.POWERED) ? 15 : 0;
    }

    public boolean isSignalSource(BlockState s) {
        return true;
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof AreaSensorEntity entity) {
            entity.tickServer(state);
        }
        world.scheduleTick(pos, this, 1);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AreaSensorEntity(pos, state);
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, world, pos, oldState, isMoving);
        if (!world.isClientSide) {
            world.scheduleTick(pos, this, 1);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof AreaSensorEntity sensor) {

                MenuProvider provider = new SimpleMenuProvider((id, inv, p) ->
                        new AreaSensorMenu(id, inv, be), Component.literal("Sensor Radius"));

                NetworkHooks.openScreen((ServerPlayer) player, provider, buf -> {
                    buf.writeLong(be.getBlockPos().asLong());
                });

                NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                        new AreaSensorPacketClient(pos, sensor.getRange()));
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

}
