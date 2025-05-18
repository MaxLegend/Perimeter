package ru.tesmio.perimeter.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class DefaultItemInfo extends Item {
    String translatable;

    public DefaultItemInfo(Properties properties, String translatable) {
        super(properties);
        this.translatable = translatable;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(translatable));
    }
}