package ru.tesmio.perimeter.blocks.devices.vibrocable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import ru.tesmio.perimeter.blocks.devices.vibrocable.network.IVibrationNetworkMember;
import ru.tesmio.perimeter.blocks.devices.vibrocable.network.VibrationNetworkSystem;
import ru.tesmio.perimeter.core.RegBlockEntitys;

import java.util.Set;

public class VibrationControllerEntity extends BlockEntity {
    private int lastCheck = 0;

    public VibrationControllerEntity(BlockPos pos, BlockState state) {
        super(RegBlockEntitys.VIBROCABLE_CONTROLLER_ENTITY.get(), pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        lastCheck++;

        boolean hasSignal = checkNetworkActivity();
        //    System.out.println("hasSignal " + hasSignal);
        BlockState currentState = getBlockState();
        if (currentState.getValue(VibrationController.POWERED) != hasSignal) {
            level.setBlock(worldPosition, currentState.setValue(VibrationController.POWERED, hasSignal), 3);
            level.updateNeighborsAt(worldPosition, currentState.getBlock());
        }
    }

    @Override
    public AABB getRenderBoundingBox() {
        return super.getRenderBoundingBox().inflate(1.2f);
    }

    private boolean checkNetworkActivity() {
        if (level == null) return false;
        Set<BlockPos> members = VibrationNetworkSystem.get(level).getMembers(worldPosition);
       
        for (BlockPos memberPos : members) {
            BlockEntity be = level.getBlockEntity(memberPos);
            if (be instanceof IVibrationNetworkMember member) {
                if (member instanceof VibrationCableEntity cable && cable.isVibrationSignalActive()) {
                    return true;
                }
            }
        }
        return false;
    }
}
