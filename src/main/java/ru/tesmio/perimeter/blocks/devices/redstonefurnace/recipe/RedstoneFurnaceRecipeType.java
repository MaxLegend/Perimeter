package ru.tesmio.perimeter.blocks.devices.redstonefurnace.recipe;

import net.minecraft.world.item.crafting.RecipeType;
import ru.tesmio.perimeter.Perimeter;

public class RedstoneFurnaceRecipeType implements RecipeType<RedstoneFurnaceRecipe> {
    @Override
    public String toString() {
        return Perimeter.MODID + ":redstone_furnace_recipe_type";
    }
}
