package ru.tesmio.perimeter.datagen.generators;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import ru.tesmio.perimeter.Perimeter;
import ru.tesmio.perimeter.core.registration.RegBlocks;
import ru.tesmio.perimeter.core.registration.RegItems;

import java.util.LinkedHashMap;

public class ItemModelGenerator extends ItemModelProvider {
    private static LinkedHashMap<ResourceKey<TrimMaterial>, Float> trimMaterials = new LinkedHashMap<>();

    public ItemModelGenerator(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Perimeter.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        ModelFile itemGenerated = getExistingFile(mcLoc("item/generated"));
        manualItemWoodFenceGenerator("oak");
        manualItemWoodFenceGenerator("dark_oak");
        manualItemWoodFenceGenerator("acacia");
        manualItemWoodFenceGenerator("birch");
        manualItemWoodFenceGenerator("crimson");
        manualItemWoodFenceGenerator("jungle");
        manualItemWoodFenceGenerator("bamboo");
        manualItemWoodFenceGenerator("spruce");
        manualItemWoodFenceGenerator("cherry");
        manualItemWoodFenceGenerator("mangrove");
        manualItemWoodFenceGenerator("warped");
        defaultItemGenerator(itemGenerated);
        defaultItemBlockGenerator();
    }

    private void defaultItemGenerator(ModelFile parent) {
        for (RegistryObject<Item> item : RegItems.ITEMS.getEntries()) {
            String name = ForgeRegistries.ITEMS.getKey(item.get()).getPath();

            getBuilder(name)
                    .parent(parent)
                    .texture("layer0", modLoc("item/" + name));
        }
    }

    private void manualItemWoodFenceGenerator(String name) {
        getBuilder(name + "_paling_fence").parent(getExistingFile(modLoc("block/" + name + "_center")));
    }

    private void defaultItemBlockGenerator() {
        for (RegistryObject<Block> block : RegBlocks.BLOCKS.getEntries()) {
            String name = ForgeRegistries.BLOCKS.getKey(block.get()).getPath();
            getBuilder(name).parent(getExistingFile(modLoc("block/" + name)));
        }

    }
}
