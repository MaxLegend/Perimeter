package ru.tesmio.perimeter.datagen.generators;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import ru.tesmio.perimeter.Perimeter;
import ru.tesmio.perimeter.core.PerimeterBlocks;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class BlockTagGenerator extends BlockTagsProvider {
    public BlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Perimeter.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(PerimeterBlocks.CONCRETE_BLOCK.get())
                .add(PerimeterBlocks.CONCRETE_WALL.get())
                .add(PerimeterBlocks.ARMORED_CONCRETE_WALL.get());

        this.tag(BlockTags.MINEABLE_WITH_AXE)
                .add(PerimeterBlocks.OAK_PALING_FENCE.get())
                .add(PerimeterBlocks.DARKOAK_PALING_FENCE.get())
                .add(PerimeterBlocks.BIRCH_PALING_FENCE.get())
                .add(PerimeterBlocks.CRIMSON_PALING_FENCE.get())
                .add(PerimeterBlocks.SPRUCE_PALING_FENCE.get())
                .add(PerimeterBlocks.MANGROVE_PALING_FENCE.get())
                .add(PerimeterBlocks.CHERRY_PALING_FENCE.get())
                .add(PerimeterBlocks.WARPED_PALING_FENCE.get())
                .add(PerimeterBlocks.JUNGLE_PALING_FENCE.get())
                .add(PerimeterBlocks.BAMBOO_PALING_FENCE.get())
                .add(PerimeterBlocks.ACACIA_PALING_FENCE.get());


        this.tag(BlockTags.NEEDS_IRON_TOOL)
                .add(PerimeterBlocks.CONCRETE_BLOCK.get())
                .add(PerimeterBlocks.CONCRETE_WALL.get())
                .add(PerimeterBlocks.ARMORED_CONCRETE_WALL.get());
    }
}
