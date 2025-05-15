package ru.tesmio.perimeter.blocks.devices.redstonecable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import ru.tesmio.perimeter.core.registration.RegBlockEntitys;

import java.util.*;

/**
 * Do not copy this class. I do not know how it works, but it works. :D
 *
 * @author Tesmio
 */
public class RedstoneCableEntity extends BlockEntity {
    private final List<BlockPos> connections = new ArrayList<>(); // Явные связи с другими кабелями
    private final Set<BlockPos> network = new HashSet<>(); // Кэш всех кабелей в сети
    private boolean networkDirty = true; // Флаг для обновления сети
    private static final int MAX_CONNECTION_DISTANCE = 24;
    private static final int UPDATE_INTERVAL = 20;
    private int ticksSinceLastUpdate = 0;

    public RedstoneCableEntity(BlockPos pos, BlockState state) {
        super(RegBlockEntitys.REDSTONE_CABLE_ENTITY.get(), pos, state);
    }

    public List<BlockPos> getConnections() {
        return connections;
    }

    public void addConnection(BlockPos target) {
        if (connections.contains(target)) return;
        if (connections.size() >= 3) return;

        // Проверка расстояния
        if (worldPosition.distSqr(target) > MAX_CONNECTION_DISTANCE * MAX_CONNECTION_DISTANCE) {

            return;
        }

        connections.add(target);
        networkDirty = true;
        setChanged();
        syncToClient();
        BlockEntity be = level.getBlockEntity(target);
        if (be instanceof RedstoneCableEntity other) {
            mergeWithOtherNetwork(other);
        }
        //       updateSignalInNetwork();
    }

    public void removeConnection(BlockPos target) {
        if (connections.remove(target)) {
            networkDirty = true;
            setChanged();
            syncToClient();

            invalidateNetwork();

            BlockEntity be = level.getBlockEntity(target);
            if (be instanceof RedstoneCableEntity cable) {
                cable.invalidateNetwork();
            }
        }
    }

    public void invalidateNetwork() {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();
        queue.add(this.worldPosition);

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            if (!visited.add(current)) continue;

            BlockEntity be = level.getBlockEntity(current);
            if (be instanceof RedstoneCableEntity cable) {
                cable.networkDirty = true;
                cable.setChanged();

                for (BlockPos neighbor : cable.getConnectedCables()) {
                    if (!visited.contains(neighbor)) {
                        queue.add(neighbor);
                    }
                }
            }
        }
    }

    public void clearConnections() {
        List<BlockPos> oldConnections = new ArrayList<>(connections);
        connections.clear();
        networkDirty = true;
        setChanged();
        syncToClient();
        invalidateNetwork();

        for (BlockPos target : oldConnections) {
            BlockEntity be = level.getBlockEntity(target);
            if (be instanceof RedstoneCableEntity cable) {
                cable.invalidateNetwork();
            }
        }
    }

    public List<BlockPos> getConnectedCables() {
        List<BlockPos> result = new ArrayList<>();
        for (BlockPos pos : connections) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof RedstoneCableEntity && !result.contains(pos)) {
                result.add(pos);
            }
        }
        return result;
    }

    // Перестроение списка всех блоков в сети
    public void rebuildNetwork() {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();
        queue.add(worldPosition);

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            if (!visited.add(current)) continue;

            BlockEntity be = level.getBlockEntity(current);
            if (be instanceof RedstoneCableEntity cable) {
                for (BlockPos neighbor : cable.getConnectedCables()) {
                    if (!visited.contains(neighbor)) {
                        queue.add(neighbor);
                    }
                }
            }
        }

        network.clear();
        network.addAll(visited);

    }

    /**
     * Стадия оценки: определяет максимальный сигнал от внешних источников.
     */
    private int computeNetworkInputPower() {
        int maxInput = 0;

        for (BlockPos pos : network) {
            for (Direction dir : Direction.values()) {
                BlockPos neighborPos = pos.relative(dir);
                BlockState neighborState = level.getBlockState(neighborPos);
                Block block = neighborState.getBlock();

                // Пропускаем кабели
                if (block instanceof RedstoneCableBlock) continue;
                if (block instanceof RedStoneWireBlock) continue;


                int signal = level.getSignal(neighborPos, dir.getOpposite());
                if (signal > 0) {
                    maxInput = Math.max(maxInput, signal);
                }
            }
        }

        return maxInput;
    }

    private void applySignalToNetwork(int signal) {
        for (BlockPos pos : network) {
            BlockState state = level.getBlockState(pos);
            if (state.getBlock() instanceof RedstoneCableBlock) {
                int old = state.getValue(RedstoneCableBlock.POWER);
                if (old != signal) {
                    level.setBlock(pos, state.setValue(RedstoneCableBlock.POWER, signal), 3);
                }
            }
        }
    }

    private boolean isUpdating = false;
    private int ticksWithoutInput = 0;
    private static final int SIGNAL_LOSS_DELAY = 2; // тик(и) ожидания перед сбросом сигнала
    private int cachedInputSignal = 0; // последний подтверждённый входной сигнал

    public void updateSignalInNetwork() {

        if (isUpdating) return; // защита от рекурсии
        isUpdating = true;
        try {
            if (networkDirty) {
                rebuildNetwork();
                networkDirty = false;
            }

            int input = computeNetworkInputPower();

            if (input > 0) {
                cachedInputSignal = input;
                ticksWithoutInput = 0;
            } else {
                ticksWithoutInput++;
                if (ticksWithoutInput >= SIGNAL_LOSS_DELAY) {
                    cachedInputSignal = 0;
                }
            }


            applySignalToNetwork(cachedInputSignal);

        } finally {
            isUpdating = false;
        }
    }

    private void mergeWithOtherNetwork(RedstoneCableEntity other) {
        Set<BlockPos> network1 = new HashSet<>(this.network);
        Set<BlockPos> network2 = new HashSet<>(other.network);

        // Определяем какая сеть больше
        Set<BlockPos> larger = network1.size() >= network2.size() ? network1 : network2;
        Set<BlockPos> smaller = network1.size() < network2.size() ? network1 : network2;

        // Объединённый результат
        Set<BlockPos> merged = new HashSet<>(larger);
        merged.addAll(smaller);

        // Обновляем все участвующие блоки, не модифицируя исходный набор в процессе
        for (BlockPos pos : merged) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof RedstoneCableEntity cable) {
                cable.network.clear();
                cable.network.addAll(merged);
                cable.networkDirty = false;
            }
        }
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T be) {
        if (!(be instanceof RedstoneCableEntity cable)) return;
        if (level.isClientSide) return;
        cable.ticksSinceLastUpdate++;
        if (cable.ticksSinceLastUpdate >= UPDATE_INTERVAL) {
            cable.ticksSinceLastUpdate = 0;

            cable.updateSignalInNetwork();
        }

    }

    public int getSignal() {
        return getBlockState().getValue(RedstoneCableBlock.POWER);
    }

    private void syncToClient() {
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }


    // ===== СЕРИАЛИЗАЦИЯ =====

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ListTag list = new ListTag();
        for (BlockPos pos : connections) {
            CompoundTag posTag = new CompoundTag();
            posTag.putInt("x", pos.getX());
            posTag.putInt("y", pos.getY());
            posTag.putInt("z", pos.getZ());
            list.add(posTag);
        }
        tag.put("Connections", list);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        connections.clear();
        ListTag list = tag.getList("Connections", Tag.TAG_COMPOUND);
        for (Tag t : list) {
            CompoundTag posTag = (CompoundTag) t;
            connections.add(new BlockPos(posTag.getInt("x"), posTag.getInt("y"), posTag.getInt("z")));
        }
        networkDirty = true;
    }

    @Override
    public AABB getRenderBoundingBox() {
        return super.getRenderBoundingBox().inflate(50f);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        this.load(tag);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }
}
