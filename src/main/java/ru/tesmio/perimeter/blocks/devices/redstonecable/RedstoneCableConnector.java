package ru.tesmio.perimeter.blocks.devices.redstonecable;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;

public class RedstoneCableConnector extends Item {

    public RedstoneCableConnector(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        BlockEntity be = level.getBlockEntity(clickedPos);


        if (!(be instanceof RedstoneCableEntity cable)) {
            return InteractionResult.PASS;
        }

        CompoundTag tag = stack.getOrCreateTag();
        // --- Ctrl + ПКМ: очистка всех связей у этого блока (и у тех, с кем он был связан) ---
        if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 341)) {
            // Если в NBT есть сохранённая точка — очистим её
            if (tag.contains("LinkX")) {
                tag.remove("LinkX");
                tag.remove("LinkY");
                tag.remove("LinkZ");
            }
            // Для каждого в connections у cable — удалить симметричную связь
            for (BlockPos other : new ArrayList<>(cable.getConnections())) {
                BlockEntity otherBe = level.getBlockEntity(other);
                if (otherBe instanceof RedstoneCableEntity otherCable) {
                    otherCable.removeConnection(clickedPos);
                    otherCable.setChanged();
                    level.sendBlockUpdated(other, otherCable.getBlockState(), otherCable.getBlockState(), 3);
                }
            }
            cable.clearConnections();  // у себя
            cable.setChanged();
            level.sendBlockUpdated(clickedPos, cable.getBlockState(), cable.getBlockState(), 3);

            if (player.level().isClientSide) {
                player.displayClientMessage(Component.translatable("rsconnector.positions.clean"), true);
            }
            return InteractionResult.SUCCESS;
        }

        // --- Shift + ПКМ: либо сохраняем начальную точку, либо, если она уже есть, делаем соединение ---
        if (player.isShiftKeyDown()) {
            if (((RedstoneCableEntity) be).getConnections().size() >= 3) {
                if (player.level().isClientSide) {
                    player.displayClientMessage(Component.translatable("rsconnector.error.max_connections"), true);
                }
                return InteractionResult.FAIL;
            }
            if (tag.contains("LinkX")) {
                BlockPos startPos = new BlockPos(tag.getInt("LinkX"), tag.getInt("LinkY"), tag.getInt("LinkZ"));
                tag.remove("LinkX");
                tag.remove("LinkY");
                tag.remove("LinkZ");
                // проверяем, что это два разных блока
                if (!startPos.equals(clickedPos)) {
                    double distanceSq = startPos.distSqr(clickedPos);
                    if (distanceSq > 20 * 20) {
                        if (player.level().isClientSide) {
                            player.displayClientMessage(Component.translatable(
                                    "rsconnector.error.too_far", 20, (int) Math.sqrt(distanceSq)
                            ).withStyle(ChatFormatting.RED), true);
                        }
                        return InteractionResult.FAIL;
                    }
                }
                if (!startPos.equals(clickedPos)) {
                    BlockEntity startBe = level.getBlockEntity(startPos);
                    if (startBe instanceof RedstoneCableEntity startCable) {

                        // **Симметричное добавление** на оба конца
                        startCable.addConnection(clickedPos);
                        startCable.setChanged();
                        level.sendBlockUpdated(startPos, startCable.getBlockState(), startCable.getBlockState(), 3);

                        cable.addConnection(startPos);
                        cable.setChanged();
                        level.sendBlockUpdated(clickedPos, cable.getBlockState(), cable.getBlockState(), 3);

                        if (player.level().isClientSide) {
                            double distanceSq = startPos.distSqr(clickedPos);
                            player.displayClientMessage(
                                    Component.translatable("rsconnector.positions.connected", formatBlockPos(startPos), formatBlockPos(clickedPos), (int) Math.sqrt(distanceSq)).withStyle(ChatFormatting.GREEN), true
                            );
                        }
                        return InteractionResult.SUCCESS;
                    }
                }
            } else {
                // Сохраняем точку для следующего клика
                tag.putInt("LinkX", clickedPos.getX());
                tag.putInt("LinkY", clickedPos.getY());
                tag.putInt("LinkZ", clickedPos.getZ());
                if (player.level().isClientSide) {
                    player.displayClientMessage(
                            Component.translatable("rsconnector.positions.saved", formatBlockPos(clickedPos)), true);
                }
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
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
