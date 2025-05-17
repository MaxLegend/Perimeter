package ru.tesmio.perimeter.blocks.devices.contactfence;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import ru.tesmio.perimeter.core.blocknetwork.BlockNetworkSystem;
import ru.tesmio.perimeter.core.blocknetwork.IBlockNetworkMember;
import ru.tesmio.perimeter.core.registration.RegBlockEntitys;

import java.util.Set;

public class ContactFenceEmitterEntity extends BlockEntity {
    private int lastCheck = 0;

    public ContactFenceEmitterEntity(BlockPos pos, BlockState state) {
        super(RegBlockEntitys.CONTACT_FENCE_EMITTER_ENTITY.get(), pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        lastCheck++;

        boolean hasSignal = checkNetworkActivity();
        BlockState currentState = getBlockState();
        if (currentState.getValue(ContactFenceEmitter.POWERED) != hasSignal) {
            level.setBlock(worldPosition, currentState.setValue(ContactFenceEmitter.POWERED, hasSignal), 3);
            level.updateNeighborsAt(worldPosition, currentState.getBlock());
        }
    }

    @Override
    public AABB getRenderBoundingBox() {
        return super.getRenderBoundingBox();
    }

    private boolean checkNetworkActivity() {
        if (level == null) return false;
        Set<BlockPos> members = BlockNetworkSystem.get(level).getMembers(worldPosition);

        for (BlockPos memberPos : members) {
            BlockEntity be = level.getBlockEntity(memberPos);

            if (be instanceof IBlockNetworkMember member) {

                if (member instanceof ContactFenceEntity cable && cable.isSignalActive()) {
                    return true;
                }
            }
        }
        return false;
    }

}
