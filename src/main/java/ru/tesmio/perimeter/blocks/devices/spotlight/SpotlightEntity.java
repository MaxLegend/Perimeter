package ru.tesmio.perimeter.blocks.devices.spotlight;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import ru.tesmio.perimeter.core.registration.RegBlockEntitys;

import java.util.ArrayList;
import java.util.List;

public class SpotlightEntity extends BlockEntity {

    // Количество блоков света, создаваемых впереди
    private static final int MAX_LIGHT_BLOCKS = 4;

    // Точки, где были размещены световые блоки
    private final List<BlockPos> activeLightBlocks = new ArrayList<>();

    public SpotlightEntity(BlockPos pos, BlockState state) {
        super(RegBlockEntitys.SPOTLIGHT_ENTITY.get(), pos, state);
    }

    public void setRemoved() {
        this.remove = true;
        this.invalidateCaps();
        this.requestModelDataUpdate();
        clearLightBlocks();
    }

    @Override
    public AABB getRenderBoundingBox() {
        return super.getRenderBoundingBox().inflate(50f);
    }

    // Серверная логика: установка света и очистка старого
    public void tickServer() {
        if (level == null || level.isClientSide) return;

        boolean powered = getBlockState().getValue(BlockStateProperties.POWERED);
        if (!powered) {
            // удаляем свет и выходим
            clearLightBlocks();
            return;
        }

        Direction forward = this.getBlockState().getValue(SpotlightBlock.FACING);

        Direction.Axis axis = forward.getAxis();
        int maxIterations = 6;
        int spacing = 2; // Интервал между уровнями

        BlockPos origin = worldPosition.relative(forward);
        if (level.getBlockState(origin).isAir()) {
            level.setBlock(origin, Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, 15), 3);
            activeLightBlocks.add(origin);
        }

        for (int i = 1; i <= maxIterations; i++) {
            BlockPos center = worldPosition.relative(forward, i * spacing);
            int radius = i;

            // 8 позиций: 4 угла и 4 центра сторон
            int[][] offsets = {
                    {radius, radius},
                    {radius, -radius},
                    {-radius, radius},
                    {-radius, -radius},
                    {0, -radius},
                    {0, radius},
                    {radius, 0},
                    {-radius, 0},
            };

            for (int[] offset : offsets) {
                int dx = offset[0];
                int dy = offset[1];
                BlockPos offsetPos;

                // Ориентируем плоскость в зависимости от оси направления
                if (axis == Direction.Axis.Z) {
                    offsetPos = center.offset(dx, dy, 0);
                } else if (axis == Direction.Axis.X) {
                    offsetPos = center.offset(0, dy, dx);
                } else {
                    offsetPos = center.offset(dx, 0, dy);
                }

                if (level.getBlockState(offsetPos).isAir()) {
                    level.setBlock(offsetPos, Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, 15), 3);
                    //  level.setBlock(offsetPos, Blocks.STONE.defaultBlockState(), 3);
                    activeLightBlocks.add(offsetPos);
                }
            }
        }

        // Планируем обновление
        level.scheduleTick(worldPosition, getBlockState().getBlock(), 20);
    }

    private void clearLightBlocks() {
        for (BlockPos p : activeLightBlocks) {
            if (level.getBlockState(p).is(Blocks.LIGHT)) {
                level.removeBlock(p, false);
            }
        }
        activeLightBlocks.clear();
    }
}
