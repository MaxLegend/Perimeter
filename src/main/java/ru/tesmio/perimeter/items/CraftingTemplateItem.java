package ru.tesmio.perimeter.items;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CraftingTemplateItem extends Item {
    public CraftingTemplateItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public Component getName(ItemStack stack) {
        // Показываем другой текст если шаблон заполнен
        return Component.translatable(
                isFilled(stack) ? "item.mod.filled_template" : "item.mod.empty_template"
        );
    }

    public static boolean isFilled(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains("Recipe");
    }

    // Метод для чтения рецепта из шаблона
    public static NonNullList<ItemStack> getRecipeFromTemplate(ItemStack template) {
        NonNullList<ItemStack> grid = NonNullList.withSize(9, ItemStack.EMPTY);

        if (isFilled(template)) {
            CompoundTag recipeTag = template.getTag().getCompound("Recipe");

            for (int i = 0; i < 9; i++) {
                String key = "slot_" + i;
                if (recipeTag.contains(key)) {
                    grid.set(i, ItemStack.of(recipeTag.getCompound(key)));
                }
            }
        }

        return grid;
    }
}
