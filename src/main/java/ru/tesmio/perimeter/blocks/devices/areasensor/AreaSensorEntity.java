package ru.tesmio.perimeter.blocks.devices.areasensor;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import ru.tesmio.perimeter.core.registration.RegBlockEntitys;

import java.util.List;

public class AreaSensorEntity extends BlockEntity {

    private int RANGE = 3; // радиус действия
    private int tickCounter = 0;
    private boolean powered = false;

    public AreaSensorEntity(BlockPos pos, BlockState state) {
        super(RegBlockEntitys.AREA_SENSOR_ENTITY.get(), pos, state);
    }

    public void tickServer(BlockState state) {
        if (level == null || level.isClientSide) return;

        tickCounter++;
        if (tickCounter < 20) return;
        tickCounter = 0;
        boolean foundVisibleEntity = detectVisibleEntity(level, worldPosition);

        if (foundVisibleEntity != powered) {

            powered = foundVisibleEntity;
            level.setBlock(worldPosition, state.setValue(BlockStateProperties.POWERED, powered), 3);
            level.updateNeighborsAt(worldPosition, state.getBlock());
        }
    }
    // по умолчанию

    public int getRange() {
        return RANGE;
    }

    public void setRange(int range) {
        this.RANGE = Mth.clamp(range, 1, 8);
        setChanged(); // для сохранения
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        RANGE = tag.getInt("Range");
    }

    public ContainerData getContainerData() {
        return containerData;
    }

    private final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int index) {
            return getRange();
        }

        @Override
        public void set(int index, int value) {
            setRange(value); // Обновляем значение
        }

        @Override
        public int getCount() {
            return 1;
        }
    };

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Range", RANGE);
    }

    private boolean detectVisibleEntity(Level level, BlockPos pos) {
        AABB scanArea = new AABB(pos).inflate(RANGE);
        List<Entity> entities = level.getEntities((Entity) null, scanArea, entity -> !(entity instanceof ItemEntity));
        if (entities.isEmpty()) {
            return false;
        }

        for (Entity entity : entities) {
            boolean isVisible = isEntityVisible(level, pos, entity);
            boolean isInAir = isEntityInAir(level, entity);
            if (isVisible) {
                return true;
            }
        }
        return false;
    }

    private boolean isEntityInAir(Level level, Entity entity) {
        BlockPos entityPos = entity.blockPosition();
        BlockState blockState = level.getBlockState(entityPos);
        return blockState.isAir();
    }

    private boolean isEntityVisible(Level level, BlockPos fromPos, Entity entity) {
        Vec3 start = Vec3.atCenterOf(fromPos);
        Vec3 end = entity.getBoundingBox().getCenter();
        AABB checkArea = new AABB(fromPos.offset(-RANGE, -RANGE, -RANGE), fromPos.offset(RANGE, RANGE, RANGE));

        if (checkArea.contains(start)) {
            Vec3 currentPos = start;
            while (currentPos.distanceTo(end) > 0.1) {
                currentPos = currentPos.add(end.subtract(currentPos).normalize().scale(0.1));
                ClipContext ctx = new ClipContext(currentPos, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null);
                BlockHitResult hit = level.clip(ctx);
                if (hit.getType() != HitResult.Type.MISS) {
                    BlockState blockState = level.getBlockState(hit.getBlockPos());
                    Block block = blockState.getBlock();
                    if (hit.getBlockPos().equals(fromPos) || blockState.isAir()) {
                        continue;
                    }
                    if (blockState.isSolid()) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }
}