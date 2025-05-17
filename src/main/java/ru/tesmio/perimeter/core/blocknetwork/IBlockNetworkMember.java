package ru.tesmio.perimeter.core.blocknetwork;

import net.minecraft.core.BlockPos;
import ru.tesmio.perimeter.blocks.devices.vibrocable.VibrationCableEntity;

/**
 * Determines that the entity is a member of the block-neighbor network.
 * If you want to see an implementation example, check VibrationCableEntity
 *
 * @author Tesmio
 * @version 05-15-2025
 * @see BlockNetworkSystem
 * @see BlockNetworkSavedData
 * @see VibrationCableEntity
 */
public interface IBlockNetworkMember {
    /**
     * Signal transmission implementation method
     */
    void pulse();

    /**
     * Add block in network
     *
     * @param pos
     */
    void setBlockInNetwork(BlockPos pos);

    /**
     * Get block in network
     */
    BlockPos getBlockInNetwork();
}
