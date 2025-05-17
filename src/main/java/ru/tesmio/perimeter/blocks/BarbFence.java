package ru.tesmio.perimeter.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import ru.tesmio.perimeter.blocks.standart.BlockHorizontalConnect;
import ru.tesmio.perimeter.util.ShapesUtil;

public class BarbFence extends BlockHorizontalConnect {

    boolean isN, isS, isE, isW;

    public BarbFence() {
        super(BlockBehaviour.Properties
                .of()
                .strength(2.5F, 2.0F)
                .sound(SoundType.WOOD)
                .requiresCorrectToolForDrops().noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false));
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        if (!world.isClientSide && entity instanceof LivingEntity livingEntity) {
            if (livingEntity instanceof Player) {
                if (!((Player) livingEntity).isCreative()) {

                    livingEntity.hurt(world.damageSources().generic(), 1.0F);
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 4, false, false, true
                    ));
                }
            }

        }
    }

    public VoxelShape getShape(BlockState s, BlockGetter g, BlockPos p, CollisionContext c) {
        VoxelShape NORTH_SHAPE = Shapes.or(BASE_SHAPE, Block.box(7.5, 1.5, 0, 8.5, 14.5, 8.5));
        VoxelShape SOUTH_SHAPE = ShapesUtil.rotate(NORTH_SHAPE, ShapesUtil.RotationDegree.D180);
        VoxelShape WEST_SHAPE = ShapesUtil.rotate(NORTH_SHAPE, ShapesUtil.RotationDegree.D90);
        VoxelShape EAST_SHAPE = ShapesUtil.rotate(NORTH_SHAPE, ShapesUtil.RotationDegree.D270);

        isN = s.getValue(NORTH);
        isS = s.getValue(SOUTH);
        isW = s.getValue(WEST);
        isE = s.getValue(EAST);
        VoxelShape shape = BASE_SHAPE;
        boolean isNorth = s.getValue(NORTH);
        boolean isSouth = s.getValue(SOUTH);
        boolean isWest = s.getValue(WEST);
        boolean isEast = s.getValue(EAST);
        if (isNorth) {
            shape = Shapes.or(shape, NORTH_SHAPE);
        }
        if (isSouth) {
            shape = Shapes.or(shape, SOUTH_SHAPE);
        }
        if (isWest) {
            shape = Shapes.or(shape, WEST_SHAPE);
        }
        if (isEast) {
            shape = Shapes.or(shape, EAST_SHAPE);
        }

        return shape;
    }


}
