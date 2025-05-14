package ru.tesmio.perimeter.datagen.generators;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import ru.tesmio.perimeter.core.PerimeterBlocks;
import ru.tesmio.perimeter.core.PerimeterItems;

import java.util.List;
import java.util.function.Consumer;

public class RecipeGenerator extends RecipeProvider implements IConditionBuilder {
    private static final List<ItemLike> IRONROD_SMELTABLES = List.of(PerimeterItems.RAW_IRON_ROD.get());

    public RecipeGenerator(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        oreBlasting(pWriter, IRONROD_SMELTABLES, RecipeCategory.MISC, PerimeterItems.IRON_ROD.get(), 0.45f, 250, "sapphire");
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PerimeterItems.RAW_IRON_ROD.get())
                .pattern(" R ")
                .pattern("RSR")
                .pattern("SR ")
                .define('S', Items.IRON_INGOT)
                .define('R', Items.COAL)
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PerimeterBlocks.CONCRETE_BLOCK.get(), 2)
                .pattern(" R ")
                .pattern("RSR")
                .pattern(" R ")
                .define('S', Blocks.GRAY_CONCRETE_POWDER)
                .define('R', PerimeterItems.IRON_ROD.get())
                .unlockedBy(getHasName(PerimeterItems.IRON_ROD.get()), has(PerimeterItems.IRON_ROD.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PerimeterBlocks.CONCRETE_WALL.get(), 4)
                .pattern(" D ")
                .pattern("RSR")
                .pattern(" D ")
                .define('S', PerimeterBlocks.CONCRETE_BLOCK.get())
                .define('R', PerimeterItems.IRON_ROD.get())
                .define('D', Items.IRON_INGOT)
                .unlockedBy(getHasName(PerimeterBlocks.CONCRETE_BLOCK.get()), has(PerimeterBlocks.CONCRETE_BLOCK.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PerimeterBlocks.ARMORED_CONCRETE_WALL.get(), 4)
                .pattern("SDS")
                .pattern("DRD")
                .pattern("SDS")
                .define('R', PerimeterBlocks.CONCRETE_BLOCK.get())
                .define('S', PerimeterItems.IRON_ROD.get())
                .define('D', Items.IRON_INGOT)
                .unlockedBy(getHasName(PerimeterBlocks.CONCRETE_BLOCK.get()), has(PerimeterBlocks.CONCRETE_BLOCK.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PerimeterBlocks.CONCRETE_COLUMN.get(), 4)
                .pattern("SSS")
                .pattern("DRD")
                .pattern("SSS")
                .define('R', PerimeterBlocks.CONCRETE_BLOCK.get())
                .define('S', PerimeterItems.IRON_ROD.get())
                .define('D', Items.IRON_INGOT)
                .unlockedBy(getHasName(PerimeterBlocks.CONCRETE_BLOCK.get()), has(PerimeterBlocks.CONCRETE_BLOCK.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PerimeterBlocks.ARMORED_CONCRETE_COLUMN.get(), 4)
                .pattern("SSS")
                .pattern("SRS")
                .pattern("SSS")
                .define('R', PerimeterBlocks.CONCRETE_BLOCK.get())
                .define('S', PerimeterItems.IRON_ROD.get())
                .unlockedBy(getHasName(PerimeterBlocks.CONCRETE_BLOCK.get()), has(PerimeterBlocks.CONCRETE_BLOCK.get()))
                .save(pWriter);
    }
}
