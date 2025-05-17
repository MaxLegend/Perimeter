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

            // Первый клик — сохраняем координаты трансмиттера
            if (!tag.contains("StoredX")) {
                if (be instanceof LinearTransmitterEntity) {
                    tag.putInt("StoredX", pos.getX());
                    tag.putInt("StoredY", pos.getY());
                    tag.putInt("StoredZ", pos.getZ());
                    context.getPlayer().displayClientMessage(Component.translatable("info.transmitter_position"), true);
                    return InteractionResult.SUCCESS;
                }
            } else {
                // Второй клик — пытаемся связать с ресивером
                if (be instanceof LinearReceiverEntity receiver) {
                    BlockPos storedPos = new BlockPos(tag.getInt("StoredX"), tag.getInt("StoredY"), tag.getInt("StoredZ"));
                    BlockEntity transmitterBe = level.getBlockEntity(storedPos);

                    // Удаляем координаты независимо от результата
                    tag.remove("StoredX");
                    tag.remove("StoredY");
                    tag.remove("StoredZ");

                    if (transmitterBe instanceof LinearTransmitterEntity transmitter) {
                        BlockPos currentLinkedTransmitter = receiver.getLinkedTransmitter();

                        // Если ресивер уже связан с другим трансмиттером
                        if (currentLinkedTransmitter != null && !currentLinkedTransmitter.equals(storedPos)) {
                            context.getPlayer().displayClientMessage(Component.translatable("info.receiver_already_linked"), true);
                            return InteractionResult.FAIL;
                        }

                        // Если они уже связаны друг с другом
                        if (transmitter.isLinkedTo(pos) && currentLinkedTransmitter != null && currentLinkedTransmitter.equals(storedPos)) {
                            context.getPlayer().displayClientMessage(Component.translatable("info.already_linked"), true);
                            return InteractionResult.FAIL;
                        }

                        // Всё в порядке — устанавливаем связь
                        transmitter.setLinkedReceiver(pos);
                        receiver.setLinkedTransmitter(storedPos);

                        level.sendBlockUpdated(storedPos, transmitter.getBlockState(), transmitter.getBlockState(), 3);
                        level.sendBlockUpdated(pos, receiver.getBlockState(), receiver.getBlockState(), 3);

                        context.getPlayer().displayClientMessage(Component.translatable("info.link_estabilished"), true);
                        return InteractionResult.SUCCESS;
                    }
                }

                // Если второй клик был не по ресиверу — удалим координаты
                tag.remove("StoredX");
                tag.remove("StoredY");
                tag.remove("StoredZ");
            }
        }

        return InteractionResult.PASS;
    }
}
