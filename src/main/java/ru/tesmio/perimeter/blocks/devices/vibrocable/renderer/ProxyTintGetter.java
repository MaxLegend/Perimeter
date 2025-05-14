package ru.tesmio.perimeter.blocks.devices.vibrocable.renderer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;

public class ProxyTintGetter implements BlockAndTintGetter {
    private final BlockAndTintGetter original;
    private final BlockState mimicState;
    private final BlockPos targetPos;

    public ProxyTintGetter(BlockAndTintGetter original, BlockState mimicState, BlockPos targetPos) {
        this.original = original;
        this.mimicState = mimicState;
        this.targetPos = targetPos;
    }

//    @Override
//    public int getBlockTint(BlockPos pos, ColorResolver tintIndex) {
//        // Для целевой позиции возвращаем цвет мимикрирующего блока
//        if (pos.equals(targetPos)) {
//            return Minecraft.getInstance().getBlockColors().getColor(mimicState, this, pos, tintIndex);
//        }
//        return original.getBlockTint(pos, tintIndex);
//    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return pos.equals(targetPos) ? mimicState : original.getBlockState(pos);
    }

    // Делегируем все остальные методы оригинальному объекту
    @Override
    public float getShade(Direction dir, boolean shade) {
        return original.getShade(dir, shade);
    }

    @Override
    public int getHeight() {
        return original.getHeight();
    }

    @Override
    public int getMinBuildHeight() {
        return original.getMinBuildHeight();
    }

    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
        return original.getBlockEntity(pos);
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return original.getFluidState(pos);
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return original.getLightEngine();
    }

    @Override
    public int getBlockTint(BlockPos pos, ColorResolver resolver) {
        return original.getBlockTint(pos, resolver);
    }
}
