package ru.tesmio.perimeter.blocks.devices.redstonecable;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import ru.tesmio.perimeter.core.NetworkHandler;
import ru.tesmio.perimeter.network.packets.CtrlPressedPacket;

import java.util.ArrayList;
import java.util.List;

public class RedstoneCableConnector extends Item {

    public RedstoneCableConnector(Properties properties) {
        super(properties);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                CompoundTag tag = stack.getOrCreateTag();
                if (tag.contains("LinkX")) {
                    tag.remove("LinkX");
                    tag.remove("LinkY");
                    tag.remove("LinkZ");

                    player.displayClientMessage(
                            Component.translatable("rsconnector.positions.cleared"),
                            true
                    );
                }
            }
            return InteractionResultHolder.success(stack); // важно вернуть сам предмет!
        }

        return InteractionResultHolder.pass(stack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        BlockEntity be = level.getBlockEntity(clickedPos);

        if (!(be instanceof RedstoneCableEntity cable)) return InteractionResult.PASS;

        CompoundTag tag = stack.getOrCreateTag();
        if (level.isClientSide) {
            if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 341)) { // GLFW.GLFW_KEY_LEFT_CONTROL
                NetworkHandler.INSTANCE.sendToServer(new CtrlPressedPacket(clickedPos));
                return InteractionResult.SUCCESS;
            }
        }

        if (player.isShiftKeyDown()) {
            return handleShiftClick(level, player, clickedPos, cable, tag, stack);
        }

        return InteractionResult.PASS;
    }

    private void clearConnections(Level level, Player player, BlockPos clickedPos, RedstoneCableEntity cable) {
        CompoundTag tag = player.getMainHandItem().getOrCreateTag();
        tag.remove("LinkX");
        tag.remove("LinkY");
        tag.remove("LinkZ");

        List<BlockPos> connections = new ArrayList<>(cable.getConnections());
        for (BlockPos other : connections) {
            BlockEntity otherBe = level.getBlockEntity(other);
            if (otherBe instanceof RedstoneCableEntity otherCable) {
                otherCable.removeConnection(clickedPos);
                otherCable.setChanged();
                level.sendBlockUpdated(other, otherCable.getBlockState(), otherCable.getBlockState(), 3);
            }
        }

        cable.clearConnections();
        cable.setChanged();
        level.sendBlockUpdated(clickedPos, cable.getBlockState(), cable.getBlockState(), 3);

        if (player.level().isClientSide) {
            player.displayClientMessage(Component.translatable("rsconnector.positions.clean"), true);
        }
    }

    private InteractionResult handleShiftClick(Level level, Player player, BlockPos clickedPos, RedstoneCableEntity cable, CompoundTag tag, ItemStack stack) {
        if (!level.isClientSide && cable.getConnections().size() >= 3) {
            return InteractionResult.FAIL;
        }

        if (tag.contains("LinkX")) {
            BlockPos startPos = new BlockPos(tag.getInt("LinkX"), tag.getInt("LinkY"), tag.getInt("LinkZ"));
            tag.remove("LinkX");
            tag.remove("LinkY");
            tag.remove("LinkZ");

            if (!startPos.equals(clickedPos)) {
                double distanceSq = startPos.distSqr(clickedPos);
                if (distanceSq > 400) { // 20*20
                    if (player.level().isClientSide) {
                        player.displayClientMessage(Component.translatable(
                                "rsconnector.error.too_far", 20, (int) Math.sqrt(distanceSq)).withStyle(ChatFormatting.RED), true);
                    }
                    return InteractionResult.FAIL;
                }

                BlockEntity startBe = level.getBlockEntity(startPos);
                if (startBe instanceof RedstoneCableEntity startCable) {
                    startCable.addConnection(clickedPos);
                    startCable.setChanged();
                    level.sendBlockUpdated(startPos, startCable.getBlockState(), startCable.getBlockState(), 3);

                    cable.addConnection(startPos);
                    cable.setChanged();
                    level.sendBlockUpdated(clickedPos, cable.getBlockState(), cable.getBlockState(), 3);

                    if (player.level().isClientSide) {
                        player.displayClientMessage(
                                Component.translatable("rsconnector.positions.connected", formatBlockPos(startPos), formatBlockPos(clickedPos),
                                        (int) Math.sqrt(distanceSq)).withStyle(ChatFormatting.GREEN), true);
                    }
                    stack.shrink(1);
                    return InteractionResult.SUCCESS;
                }
            }
        } else {
            tag.putInt("LinkX", clickedPos.getX());
            tag.putInt("LinkY", clickedPos.getY());
            tag.putInt("LinkZ", clickedPos.getZ());

            if (player.level().isClientSide) {
                player.displayClientMessage(
                        Component.translatable("rsconnector.positions.saved", formatBlockPos(clickedPos)), true);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }
    

    // Вспомогательный метод
    private static Component formatBlockPos(BlockPos pos) {
        return Component.literal("(" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")");
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("LinkX")) {
            BlockPos pos = new BlockPos(tag.getInt("LinkX"), tag.getInt("LinkY"), tag.getInt("LinkZ"));
            tooltip.add(Component.translatable("rsconnector.saved_point", pos.toShortString()).withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(Component.translatable("rsconnector.not_saved_point").withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
