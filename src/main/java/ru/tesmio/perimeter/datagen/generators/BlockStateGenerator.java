package ru.tesmio.perimeter.datagen.generators;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import ru.tesmio.perimeter.Perimeter;
import ru.tesmio.perimeter.core.PerimeterBlocks;
import ru.tesmio.perimeter.core.RegBlocks;

public class BlockStateGenerator extends BlockStateProvider {
    public BlockStateGenerator(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Perimeter.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        defaultBlocksGenerator();
        registerMultipartFenceStyle(PerimeterBlocks.ACACIA_PALING_FENCE.get(), "acacia");
        registerMultipartFenceStyle(PerimeterBlocks.OAK_PALING_FENCE.get(), "oak");
        registerMultipartFenceStyle(PerimeterBlocks.DARKOAK_PALING_FENCE.get(), "dark_oak");
        registerMultipartFenceStyle(PerimeterBlocks.JUNGLE_PALING_FENCE.get(), "jungle");
        registerMultipartFenceStyle(PerimeterBlocks.BAMBOO_PALING_FENCE.get(), "bamboo");
        registerMultipartFenceStyle(PerimeterBlocks.BIRCH_PALING_FENCE.get(), "birch");
        registerMultipartFenceStyle(PerimeterBlocks.CRIMSON_PALING_FENCE.get(), "crimson");
        registerMultipartFenceStyle(PerimeterBlocks.WARPED_PALING_FENCE.get(), "warped");
        registerMultipartFenceStyle(PerimeterBlocks.SPRUCE_PALING_FENCE.get(), "spruce");
        registerMultipartFenceStyle(PerimeterBlocks.CHERRY_PALING_FENCE.get(), "cherry");
        registerMultipartFenceStyle(PerimeterBlocks.MANGROVE_PALING_FENCE.get(), "mangrove");
        //    cubeColumn(RegBlocks.ARMORED_CONCRETE_COLUMN.get(), ForgeRegistries.BLOCKS.getKey(RegBlocks.ARMORED_CONCRETE_COLUMN.get()).getPath());
    }

    private void cubeColumn(Block block, String name) {
        getVariantBuilder(block).forAllStates(state ->
                ConfiguredModel.builder()
                        .modelFile(models().cubeColumn(
                                ForgeRegistries.BLOCKS.getKey(block).getPath(),
                                modLoc("block/" + name + "_side"), modLoc("block/" + name + "_top")
                        ))
                        .build()
        );
    }

    public void defaultBlocksGenerator() {
        for (RegistryObject<Block> blocks : RegBlocks.BLOCKS.getEntries()) {
            //     if (blocks.get() != RegBlocks.ARMORED_CONCRETE_COLUMN.get()) {
            String name = ForgeRegistries.BLOCKS.getKey(blocks.get()).getPath();
            getVariantBuilder(blocks.get()).forAllStates(state ->
                    ConfiguredModel.builder()
                            .modelFile(models().cubeAll(name, modLoc("block/" + name)))
                            .build()
            );
            //        }
        }
    }

    private void registerMultipartFenceStyle(Block block, String textureBaseName) {

        ResourceLocation texture = new ResourceLocation("perimeter:block/" + textureBaseName + "_planks");

        ModelFile center = models().withExistingParent(textureBaseName + "_center", modLoc("block/template/wood_fence_center"))
                .texture("texture", texture).texture("particle", texture).ao(false);

        ModelFile side = models().withExistingParent(textureBaseName + "_side", modLoc("block/template/wood_fence_side"))
                .texture("texture", texture).texture("particle", texture).ao(false);

        getMultipartBuilder(block)
                .part().modelFile(center).addModel().end()
                .part().modelFile(side).rotationY(0).uvLock(true).addModel().condition(CrossCollisionBlock.NORTH, true).end()
                .part().modelFile(side).rotationY(180).uvLock(true).addModel().condition(CrossCollisionBlock.SOUTH, true).end()
                .part().modelFile(side).rotationY(270).uvLock(true).addModel().condition(CrossCollisionBlock.WEST, true).end()
                .part().modelFile(side).rotationY(90).uvLock(true).addModel().condition(CrossCollisionBlock.EAST, true).end();
    }


    private String name(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block).getPath();
    }

}
