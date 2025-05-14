package ru.tesmio.perimeter.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class DefaultBlockItemInfo extends BlockItem {
    String translatable;

    public DefaultBlockItemInfo(Block block, Properties properties, String translatable) {
        super(block, properties);
        this.translatable = translatable;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(translatable));
    }
}
