package ru.tesmio.perimeter.blocks.devices.redstonecircuit;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import ru.tesmio.perimeter.items.CircuitComponent;

public class RedstoneCircuit extends Block implements EntityBlock {
    public static final IntegerProperty COMPONENT_COUNT = IntegerProperty.create("components", 0, 4);

    public RedstoneCircuit() {
        super(Properties.of()
                .strength(0.5f));

        this.registerDefaultState(this.stateDefinition.any().setValue(COMPONENT_COUNT, 0));
    }

    public VoxelShape getShape(BlockState s, BlockGetter g, BlockPos p, CollisionContext c) {
        return Block.box(4, 0, 4, 12, 4, 12);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RedstoneCircuitEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(COMPONENT_COUNT);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);

        if (isComponent(heldItem) && state.getValue(COMPONENT_COUNT) < 4) {

            if (!level.isClientSide) {

                processComponent(heldItem, state, level, pos, player);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }

    private boolean isComponent(ItemStack stack) {
        return stack.getItem() instanceof CircuitComponent;
    }

    private void processComponent(ItemStack component, BlockState state, Level level, BlockPos pos, Player player) {
        int newCount = state.getValue(COMPONENT_COUNT) + 1;

        BlockEntity be = level.getBlockEntity(pos);

        if (be instanceof RedstoneCircuitEntity) {
            ((RedstoneCircuitEntity) be).addComponent(component.getItem());
            level.setBlock(pos, state.setValue(COMPONENT_COUNT, newCount), 3);

            player.displayClientMessage(
                    Component.translatable("redstone_circuit.add", Component.translatable(String.valueOf(component.getItem()))),
                    true
            );

            if (newCount == 4) {
                completeCircuit(level, pos, (RedstoneCircuitEntity) be);
            }
        }

        if (!player.isCreative()) {
            component.shrink(8);
        }
    }

    private void completeCircuit(Level level, BlockPos pos, RedstoneCircuitEntity tile) {
        ItemStack result = tile.getResult();
        level.removeBlock(pos, false);
        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), result);
        level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1.0f, 1.5f);
    }

}
