package ru.tesmio.perimeter.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3f;
import ru.tesmio.perimeter.util.IConnectedToPostDevice;

public class RedstoneButton extends Block implements IConnectedToPostDevice {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

    public RedstoneButton() {
        super(Properties.of().strength(1.0F));
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

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction dir) {
        return state.getValue(BlockStateProperties.POWERED) ? 15 : 0;
    }

    // Делаем блок источником сигнала
    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    public VoxelShape getShape(BlockState s, BlockGetter g, BlockPos p, CollisionContext c) {
        return switch (s.getValue(FACING)) {
            case SOUTH -> Block.box(5, 5, 0, 11, 11, 4);
            case NORTH -> Block.box(5, 5, 12, 11, 11, 16);
            case WEST -> Block.box(12, 5, 5, 16, 11, 11);
            case EAST -> Block.box(0, 5, 5, 4, 11, 11);
            case DOWN -> Block.box(5, 12, 5, 11, 16, 11);
            case UP -> Block.box(5, 0, 5, 11, 4, 11);
        };
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        super.animateTick(state, world, pos, random);
        if (world.isClientSide && state.getValue(BlockStateProperties.POWERED)) {
            Direction facing = state.getValue(BlockStateProperties.FACING);
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 0.5;
            double z = pos.getZ() + 0.5;

            // Смещение к поверхности кнопки (6 пикселей = 0.375 блока)
            double offset = 0.375;

            // Случайное смещение в плоскости кнопки (6x6 пикселей = 0.375x0.375 блока)
            double dx = (random.nextDouble() - 0.5) * 0.375;
            double dy = (random.nextDouble() - 0.5) * 0.375;
            double dz = (random.nextDouble() - 0.5) * 0.375;

            // Применяем смещение в зависимости от направления кнопки
            switch (facing) {
                case DOWN -> {
                    y += offset;
                    x += dx;
                    z += dz;
                }
                case UP -> {
                    y -= offset;
                    x += dx;
                    z += dz;
                }
                case SOUTH -> {
                    z -= offset;
                    x += dx;
                    y += dy;
                }
                case NORTH -> {
                    z += offset;
                    x += dx;
                    y += dy;
                }
                case EAST -> {
                    x -= offset;
                    y += dy;
                    z += dz;
                }
                case WEST -> {
                    x += offset;
                    y += dy;
                    z += dz;
                }
            }

            // Небольшое дополнительное смещение для эффекта "вращения"
            double spinOffset = 0.05;
            switch (facing.getAxis()) {
                case X -> { // EAST/WEST
                    z += Math.sin(world.getGameTime() * 0.1) * spinOffset;
                    y += Math.cos(world.getGameTime() * 0.1) * spinOffset;
                }
                case Z -> { // NORTH/SOUTH
                    x += Math.sin(world.getGameTime() * 0.1) * spinOffset;
                    y += Math.cos(world.getGameTime() * 0.1) * spinOffset;
                }
                case Y -> { // UP/DOWN
                    x += Math.sin(world.getGameTime() * 0.1) * spinOffset;
                    z += Math.cos(world.getGameTime() * 0.1) * spinOffset;
                }
            }

            world.addParticle(new DustParticleOptions(new Vector3f(1f, 0f, 0f), 1.0f), x, y, z, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public InteractionResult use(BlockState s, Level l, BlockPos p, Player pl, InteractionHand h, BlockHitResult r) {
        l.setBlock(p, s.cycle(BlockStateProperties.POWERED), 3);
        return InteractionResult.SUCCESS;
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
}
