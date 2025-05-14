package ru.tesmio.perimeter.util;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VoxelShapeUtil {
    public static final VoxelShape FULL_CUBE = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);


}
