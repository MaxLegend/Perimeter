package ru.tesmio.perimeter.items;

import net.minecraft.world.item.Item;

public class UpgradeSpeed extends DefaultItemInfo {
    public UpgradeSpeed(String translatable) {
        super(new Item.Properties().durability(1000), translatable);
    }
}