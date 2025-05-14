package ru.tesmio.perimeter.blocks.devices.redstonecircuit;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class RedstoneCircuitItem extends BlockItem {
    public RedstoneCircuitItem(Block block) {
        super(block,
                new Item.Properties());
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        if (!Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("info.redstone_board"));
            tooltip.add(Component.translatable("info.hold_shift"));
        } else if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("recipe.light_circuit").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("recipe.signal_circuit").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("recipe.processing_circuit").withStyle(ChatFormatting.GRAY));
        }
    }
}
