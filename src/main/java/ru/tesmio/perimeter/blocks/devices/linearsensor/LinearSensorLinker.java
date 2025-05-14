package ru.tesmio.perimeter.blocks.devices.linearsensor;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public class LinearSensorLinker extends Item {

    public LinearSensorLinker(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("info.linear_sensor_linker"));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();

        if (!level.isClientSide()) {
            CompoundTag tag = stack.getOrCreateTag();
            BlockEntity be = level.getBlockEntity(pos);

            if (!tag.contains("StoredX")) {
                if (be instanceof LinearTransmitterEntity) {
                    tag.putInt("StoredX", pos.getX());
                    tag.putInt("StoredY", pos.getY());
                    tag.putInt("StoredZ", pos.getZ());
                    context.getPlayer().displayClientMessage(Component.translatable("info.transmitter_position"), true);
                    return InteractionResult.SUCCESS;
                }
            } else {
                if (be instanceof LinearReceiverEntity receiver) {
                    BlockPos storedPos = new BlockPos(tag.getInt("StoredX"), tag.getInt("StoredY"), tag.getInt("StoredZ"));
                    BlockEntity transmitterBe = level.getBlockEntity(storedPos);
                    if (transmitterBe instanceof LinearTransmitterEntity transmitter) {
                        transmitter.setLinkedReceiver(pos);
                        receiver.setLinkedTransmitter(storedPos);

                        //  Обновляем состояния на клиент
                        level.sendBlockUpdated(storedPos, transmitter.getBlockState(), transmitter.getBlockState(), 3);
                        level.sendBlockUpdated(pos, receiver.getBlockState(), receiver.getBlockState(), 3);

                        tag.remove("StoredX");
                        tag.remove("StoredY");
                        tag.remove("StoredZ");

                        context.getPlayer().displayClientMessage(Component.translatable("info.link_estabilished"), true);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }

        return InteractionResult.PASS;
    }
}
