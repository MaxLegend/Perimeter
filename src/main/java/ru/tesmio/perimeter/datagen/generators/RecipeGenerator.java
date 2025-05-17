package ru.tesmio.perimeter.datagen.generators;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
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
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, PerimeterBlocks.SMOOTH_REINFORCED_BLOCK.get())
                .requires(PerimeterBlocks.REINFORCED_BLOCK.get())
                .unlockedBy(getHasName(PerimeterBlocks.REINFORCED_BLOCK.get()), has(PerimeterBlocks.REINFORCED_BLOCK.get()))
                .save(pWriter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, PerimeterBlocks.REDSTONE_BUTTON.get(), 2)
                .requires(PerimeterItems.REDSTONE_TRANSISTOR.get())
                .requires(Items.REDSTONE)
                .unlockedBy(getHasName(PerimeterItems.REDSTONE_TRANSISTOR.get()), has(PerimeterItems.REDSTONE_TRANSISTOR.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, PerimeterBlocks.LAMP_BLOCK.get(), 2)
                .pattern("RB")
                .pattern("CD")
                .define('B', PerimeterItems.REDSTONE_CONDENSER.get())
                .define('R', Items.IRON_INGOT)
                .define('D', PerimeterItems.LENS.get())
                .define('C', PerimeterItems.LIGHT_CIRCUIT.get())
                .unlockedBy(getHasName(PerimeterItems.LIGHT_CIRCUIT.get()), has(PerimeterItems.LIGHT_CIRCUIT.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, PerimeterBlocks.TECH_LAMP.get(), 1)
                .pattern("RB")
                .pattern("CD")
                .define('B', PerimeterItems.REDSTONE_CONDENSER.get())
                .define('R', Blocks.REDSTONE_LAMP)
                .define('D', PerimeterItems.REDSTONE_TRANSISTOR.get())
                .define('C', PerimeterItems.LIGHT_CIRCUIT.get())
                .unlockedBy(getHasName(PerimeterItems.LIGHT_CIRCUIT.get()), has(PerimeterItems.LIGHT_CIRCUIT.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, PerimeterBlocks.LINEAR_SENSOR_RECEIVER.get(), 1)
                .pattern("ABC")
                .pattern("AEF")
                .pattern("ABG")
                .define('A', Items.IRON_INGOT)
                .define('B', Items.COPPER_INGOT)
                .define('C', PerimeterItems.REDSTONE_TRANSISTOR.get())
                .define('E', PerimeterItems.PROCESSING_CIRCUIT.get())
                .define('F', PerimeterItems.REDSTONE_CONDENSER.get())
                .define('G', PerimeterItems.LENS.get())
                .unlockedBy(getHasName(PerimeterItems.REDSTONE_TRANSISTOR.get()), has(PerimeterItems.REDSTONE_TRANSISTOR.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, PerimeterBlocks.LINEAR_SENSOR_TRANSMITTER.get(), 1)
                .pattern("ABC")
                .pattern("AEF")
                .pattern("ABG")
                .define('A', Items.IRON_INGOT)
                .define('B', Items.COPPER_INGOT)
                .define('C', PerimeterItems.REDSTONE_TRANSISTOR.get())
                .define('E', PerimeterItems.SIGNAL_CIRCUIT.get())
                .define('F', PerimeterItems.REDSTONE_ELECTROLAMP.get())
                .define('G', PerimeterItems.LENS.get())
                .unlockedBy(getHasName(PerimeterItems.REDSTONE_TRANSISTOR.get()), has(PerimeterItems.REDSTONE_TRANSISTOR.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, PerimeterBlocks.AREA_SENSOR.get(), 1)
                .pattern("ABC")
                .pattern("DEF")
                .pattern("ABG")
                .define('A', Items.COPPER_INGOT)
                .define('B', PerimeterItems.REDSTONE_TRANSISTOR.get())
                .define('C', PerimeterItems.REDSTONE_CONDENSER.get())
                .define('D', PerimeterItems.REDSTONE_ELECTROLAMP.get())
                .define('E', PerimeterItems.SIGNAL_CIRCUIT.get())
                .define('F', PerimeterItems.LENS.get())
                .define('G', PerimeterItems.REDSTONE_TRANSISTOR.get())
                .unlockedBy(getHasName(PerimeterItems.REDSTONE_TRANSISTOR.get()), has(PerimeterItems.REDSTONE_TRANSISTOR.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, PerimeterBlocks.SPOTLIGHT.get(), 1)
                .pattern("BRD")
                .pattern("ACD")
                .pattern("BRD")
                .define('A', PerimeterItems.LIGHT_CIRCUIT.get())
                .define('B', PerimeterItems.REDSTONE_CONDENSER.get())
                .define('R', Items.IRON_INGOT)
                .define('D', PerimeterItems.LENS.get())
                .define('C', PerimeterItems.LAMP.get())
                .unlockedBy(getHasName(PerimeterItems.LIGHT_CIRCUIT.get()), has(PerimeterItems.LIGHT_CIRCUIT.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, PerimeterBlocks.REDSTONE_CABLE.get(), 2)
                .pattern("RRR")
                .pattern("CRC")
                .pattern("RRR")
                .define('R', Items.IRON_NUGGET)
                .define('C', Items.REDSTONE)
                .unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PerimeterBlocks.BLOCK_POST.get(), 16)
                .pattern("ACA")
                .pattern("ACA")
                .pattern("ACA")
                .define('C', Items.IRON_INGOT)
                .define('A', PerimeterItems.IRON_ROD.get())
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PerimeterBlocks.CONCRETE_CHEST.get(), 1)
                .pattern("RRR")
                .pattern("RCR")
                .pattern("RRR")
                .define('R', PerimeterBlocks.REINFORCED_BLOCK.get())
                .define('C', Blocks.CHEST)
                .unlockedBy(getHasName(PerimeterBlocks.REINFORCED_BLOCK.get()), has(PerimeterBlocks.REINFORCED_BLOCK.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PerimeterBlocks.CONCRETE_BARS.get(), 2)
                .pattern(" R ")
                .pattern("RRR")
                .pattern(" R ")
                .define('R', PerimeterBlocks.REINFORCED_BLOCK.get())
                .unlockedBy(getHasName(PerimeterBlocks.REINFORCED_BLOCK.get()), has(PerimeterBlocks.REINFORCED_BLOCK.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PerimeterBlocks.CONCRETE_DOOR.get())
                .pattern("SR ")
                .pattern("SRR")
                .pattern("SR ")
                .define('S', PerimeterBlocks.CONCRETE_BLOCK.get())
                .define('R', PerimeterBlocks.REINFORCED_BLOCK.get())
                .unlockedBy(getHasName(PerimeterBlocks.CONCRETE_BLOCK.get()), has(PerimeterBlocks.CONCRETE_BLOCK.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PerimeterItems.RAW_IRON_ROD.get())
                .pattern(" R ")
                .pattern("RSR")
                .pattern("SR ")
                .define('S', Items.IRON_INGOT)
                .define('R', Items.COAL)
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PerimeterBlocks.OAK_PALING_FENCE.get(), 1)
                .pattern("S")
                .pattern("B")
                .define('S', Items.STICK)
                .define('B', Blocks.OAK_PLANKS)
                .unlockedBy(getHasName(Blocks.OAK_PLANKS), has(Blocks.OAK_PLANKS))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PerimeterBlocks.SPRUCE_PALING_FENCE.get(), 1)
                .pattern("S")
                .pattern("B")
                .define('S', Items.STICK)
                .define('B', Blocks.SPRUCE_PLANKS)
                .unlockedBy(getHasName(Blocks.SPRUCE_PLANKS), has(Blocks.SPRUCE_PLANKS))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PerimeterBlocks.JUNGLE_PALING_FENCE.get(), 1)
                .pattern("S")
                .pattern("B")
                .define('S', Items.STICK)
                .define('B', Blocks.JUNGLE_PLANKS)
                .unlockedBy(getHasName(Blocks.JUNGLE_PLANKS), has(Blocks.JUNGLE_PLANKS))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PerimeterBlocks.BIRCH_PALING_FENCE.get(), 1)
                .pattern("S")
                .pattern("B")
                .define('S', Items.STICK)
                .define('B', Blocks.BIRCH_PLANKS)
                .unlockedBy(getHasName(Blocks.BIRCH_PLANKS), has(Blocks.BIRCH_PLANKS))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PerimeterBlocks.BAMBOO_PALING_FENCE.get(), 1)
                .pattern("S")
                .pattern("B")
                .define('S', Items.STICK)
                .define('B', Blocks.BAMBOO_PLANKS)
                .unlockedBy(getHasName(Blocks.BAMBOO_PLANKS), has(Blocks.BAMBOO_PLANKS))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PerimeterBlocks.CRIMSON_PALING_FENCE.get(), 1)
                .pattern("S")
                .pattern("B")
                .define('S', Items.STICK)
                .define('B', Blocks.CRIMSON_PLANKS)
                .unlockedBy(getHasName(Blocks.CRIMSON_PLANKS), has(Blocks.CRIMSON_PLANKS))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PerimeterBlocks.CHERRY_PALING_FENCE.get(), 1)
                .pattern("S")
                .pattern("B")
                .define('S', Items.STICK)
                .define('B', Blocks.CHERRY_PLANKS)
                .unlockedBy(getHasName(Blocks.CHERRY_PLANKS), has(Blocks.CHERRY_PLANKS))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PerimeterBlocks.DARKOAK_PALING_FENCE.get(), 1)
                .pattern("S")
                .pattern("B")
                .define('S', Items.STICK)
                .define('B', Blocks.DARK_OAK_PLANKS)
                .unlockedBy(getHasName(Blocks.DARK_OAK_PLANKS), has(Blocks.DARK_OAK_PLANKS))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PerimeterBlocks.MANGROVE_PALING_FENCE.get(), 1)
                .pattern("S")
                .pattern("B")
                .define('S', Items.STICK)
                .define('B', Blocks.MANGROVE_PLANKS)
                .unlockedBy(getHasName(Blocks.MANGROVE_PLANKS), has(Blocks.MANGROVE_PLANKS))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PerimeterBlocks.WARPED_PALING_FENCE.get(), 1)
                .pattern("S")
                .pattern("B")
                .define('S', Items.STICK)
                .define('B', Blocks.WARPED_PLANKS)
                .unlockedBy(getHasName(Blocks.WARPED_PLANKS), has(Blocks.WARPED_PLANKS))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PerimeterBlocks.ACACIA_PALING_FENCE.get(), 1)
                .pattern("S")
                .pattern("B")
                .define('S', Items.STICK)
                .define('B', Blocks.ACACIA_PLANKS)
                .unlockedBy(getHasName(Blocks.ACACIA_PLANKS), has(Blocks.ACACIA_PLANKS))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, PerimeterItems.REDSTONE_CABLE_ITEM.get(), 4)
                .pattern("AAA")
                .define('A', Items.REDSTONE)
                .unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, PerimeterBlocks.REDSTONE_BOARD.get(), 4)
                .pattern("ABA")
                .pattern("AAA")
                .define('B', Items.REDSTONE)
                .define('A', Items.IRON_INGOT)
                .unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, PerimeterBlocks.VOLTAGE_FENCE.get(), 4)
                .pattern("ABA")
                .pattern("AVA")
                .define('B', Items.REDSTONE)
                .define('V', Items.COPPER_INGOT)
                .define('A', PerimeterBlocks.BLOCK_POST.get())
                .unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, PerimeterItems.LINEAR_SENSOR_LINKER.get(), 1)
                .pattern("ABB")
                .pattern("CDE")
                .pattern("BBB")
                .define('A', PerimeterItems.REDSTONE_TRANSISTOR.get())
                .define('B', Items.IRON_INGOT)
                .define('C', PerimeterItems.REDSTONE_CONDENSER.get())
                .define('D', PerimeterItems.PROCESSING_CIRCUIT.get())
                .define('E', PerimeterItems.REDSTONE_RESISTOR.get())
                .unlockedBy(getHasName(PerimeterItems.REDSTONE_TRANSISTOR.get()), has(PerimeterItems.REDSTONE_TRANSISTOR.get()))
                .save(pWriter);


        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, PerimeterItems.REDSTONE_CONDENSER.get(), 2)
                .pattern("RTR")
                .pattern("S S")
                .define('S', PerimeterItems.IRON_ROD.get())
                .define('R', Items.REDSTONE)
                .define('T', Items.IRON_NUGGET)
                .unlockedBy(getHasName(PerimeterItems.IRON_ROD.get()), has(PerimeterItems.IRON_ROD.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, PerimeterItems.LENS.get(), 1)
                .pattern(" S ")
                .pattern("SRS")
                .define('S', Blocks.GLASS_PANE)
                .define('R', Items.REDSTONE)
                .unlockedBy(getHasName(Blocks.GLASS_PANE), has(Blocks.GLASS_PANE))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, PerimeterItems.REDSTONE_TRANSISTOR.get(), 2)
                .pattern("RTR")
                .pattern("SSS")
                .define('S', PerimeterItems.IRON_ROD.get())
                .define('R', Items.REDSTONE)
                .define('T', Items.GOLD_NUGGET)
                .unlockedBy(getHasName(PerimeterItems.IRON_ROD.get()), has(PerimeterItems.IRON_ROD.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, PerimeterBlocks.SOUND_SENSOR.get(), 1)
                .pattern("RTR")
                .pattern("ASA")
                .define('S', PerimeterItems.PROCESSING_CIRCUIT.get())
                .define('R', PerimeterItems.REDSTONE_TRANSISTOR.get())
                .define('A', PerimeterItems.REDSTONE_ELECTROLAMP.get())
                .define('T', PerimeterItems.SIGNAL_CIRCUIT.get())
                .unlockedBy(getHasName(PerimeterItems.SIGNAL_CIRCUIT.get()), has(PerimeterItems.SIGNAL_CIRCUIT.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, PerimeterBlocks.VIBRO_CONTROLLER_BLOCK.get(), 1)
                .pattern("RSR")
                .pattern("ACA")
                .pattern("RSR")
                .define('R', PerimeterItems.REDSTONE_RESISTOR.get())
                .define('A', PerimeterItems.REDSTONE_TRANSISTOR.get())
                .define('S', PerimeterItems.REDSTONE_ELECTROLAMP.get())
                .define('C', PerimeterItems.PROCESSING_CIRCUIT.get())
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, PerimeterBlocks.VIBRO_CABLE_BLOCK.get(), 4)
                .pattern("RSR")
                .define('R', PerimeterItems.REDSTONE_RESISTOR.get())
                .define('S', Items.IRON_NUGGET)
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, PerimeterBlocks.BARBED_FENCE.get(), 1)
                .pattern("AS ")
                .pattern(" SA")
                .pattern("AS ")
                .define('A', Items.IRON_NUGGET)
                .define('S', PerimeterItems.IRON_ROD.get())
                .unlockedBy(getHasName(PerimeterItems.IRON_ROD.get()), has(PerimeterItems.IRON_ROD.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, PerimeterBlocks.CONTACT_FENCE.get(), 8)
                .pattern("ASR")
                .pattern("ASR")
                .pattern("ASR")
                .define('A', PerimeterBlocks.BLOCK_POST.get())
                .define('S', PerimeterBlocks.REDSTONE_CABLE.get())
                .define('R', Items.REDSTONE)
                .unlockedBy(getHasName(PerimeterBlocks.BLOCK_POST.get()), has(PerimeterBlocks.BLOCK_POST.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, PerimeterBlocks.CONTACT_FENCE_EMITTER.get(), 1)
                .pattern("SS")
                .pattern("SS")
                .define('S', PerimeterBlocks.CONTACT_FENCE.get())
                .unlockedBy(getHasName(PerimeterBlocks.CONTACT_FENCE.get()), has(PerimeterBlocks.CONTACT_FENCE.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, PerimeterItems.REDSTONE_RESISTOR.get(), 2)
                .pattern("RSR")
                .define('R', Items.IRON_NUGGET)
                .define('S', Items.REDSTONE)
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, PerimeterItems.LAMP.get(), 6)
                .pattern("R")
                .pattern("S")
                .define('S', Items.IRON_INGOT)
                .define('R', Blocks.GLASS)
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, PerimeterItems.REDSTONE_ELECTROLAMP.get(), 1)
                .pattern("R")
                .pattern("S")
                .pattern("T")
                .define('S', PerimeterItems.LAMP.get())
                .define('R', Items.REDSTONE)
                .define('T', Items.GOLD_NUGGET)
                .unlockedBy(getHasName(PerimeterItems.LAMP.get()), has(PerimeterItems.LAMP.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PerimeterBlocks.CONCRETE_BLOCK.get(), 2)
                .pattern("SR")
                .define('S', Blocks.GRAY_CONCRETE_POWDER)
                .define('R', PerimeterItems.IRON_ROD.get())
                .unlockedBy(getHasName(PerimeterItems.IRON_ROD.get()), has(PerimeterItems.IRON_ROD.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PerimeterBlocks.REINFORCED_BLOCK.get(), 2)
                .pattern(" R ")
                .pattern("RSR")
                .pattern(" R ")
                .define('S', Blocks.GRAY_CONCRETE_POWDER)
                .define('R', PerimeterItems.IRON_ROD.get())
                .unlockedBy(getHasName(PerimeterItems.IRON_ROD.get()), has(PerimeterItems.IRON_ROD.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PerimeterBlocks.CONCRETE_WALL.get(), 4)
                .pattern(" D ")
                .pattern("RSR")
                .pattern(" D ")
                .define('S', PerimeterBlocks.CONCRETE_BLOCK.get())
                .define('R', PerimeterItems.IRON_ROD.get())
                .define('D', Items.IRON_INGOT)
                .unlockedBy(getHasName(PerimeterBlocks.CONCRETE_BLOCK.get()), has(PerimeterBlocks.CONCRETE_BLOCK.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PerimeterBlocks.ARMORED_CONCRETE_WALL.get(), 4)
                .pattern("SDS")
                .pattern("DRD")
                .pattern("SDS")
                .define('R', PerimeterBlocks.REINFORCED_BLOCK.get())
                .define('S', PerimeterItems.IRON_ROD.get())
                .define('D', Items.IRON_INGOT)
                .unlockedBy(getHasName(PerimeterBlocks.REINFORCED_BLOCK.get()), has(PerimeterBlocks.REINFORCED_BLOCK.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PerimeterBlocks.CONCRETE_COLUMN.get(), 4)
                .pattern("SSS")
                .pattern("DRD")
                .pattern("SSS")
                .define('R', PerimeterBlocks.CONCRETE_BLOCK.get())
                .define('S', PerimeterItems.IRON_ROD.get())
                .define('D', Items.IRON_INGOT)
                .unlockedBy(getHasName(PerimeterBlocks.CONCRETE_BLOCK.get()), has(PerimeterBlocks.CONCRETE_BLOCK.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PerimeterBlocks.ARMORED_CONCRETE_COLUMN.get(), 4)
                .pattern("SSS")
                .pattern("SRS")
                .pattern("SSS")
                .define('R', PerimeterBlocks.REINFORCED_BLOCK.get())
                .define('S', PerimeterItems.IRON_ROD.get())
                .unlockedBy(getHasName(PerimeterBlocks.REINFORCED_BLOCK.get()), has(PerimeterBlocks.REINFORCED_BLOCK.get()))
                .save(pWriter);
    }
}
