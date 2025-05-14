package ru.tesmio.perimeter.datagen.generators;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import ru.tesmio.perimeter.core.RegBlocks;
import net.minecraftforge.registries.RegistryObject;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BlockLootGenerator extends BlockLootSubProvider {
    public BlockLootGenerator() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }
    @Override
    protected void generate() {
        Stream.of(
                        RegBlocks.BLOCKS.getEntries().stream(),
                        RegBlocks.BLOCKS_CUSTOM_MODEL.getEntries().stream(),
                        RegBlocks.BLOCKS_MANUAL.getEntries().stream()
                )
                .flatMap(Function.identity()) // "Разворачивает" потоки в один
                .map(RegistryObject::get)
                .forEach(this::dropSelf);
    }
    @Override
    protected Iterable<Block> getKnownBlocks() {
        return Stream.of(
                        RegBlocks.BLOCKS.getEntries().stream(),
                        RegBlocks.BLOCKS_CUSTOM_MODEL.getEntries().stream(),
                        RegBlocks.BLOCKS_MANUAL.getEntries().stream()
                )
                .flatMap(Function.identity())
                .map(RegistryObject::get)
                .collect(Collectors.toList());
    }
}
