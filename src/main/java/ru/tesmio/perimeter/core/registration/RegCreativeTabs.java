package ru.tesmio.perimeter.core.registration;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import ru.tesmio.perimeter.Perimeter;
import ru.tesmio.perimeter.core.PerimeterItems;

public class RegCreativeTabs {
    //сделать автоматическое добавление блоков и итемов во вкладку
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Perimeter.MODID);
    public static final RegistryObject<CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TABS.register("tab_main",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(PerimeterItems.LINEAR_SENSOR_LINKER.get())) // <-- иконка вкладки (наш блок)
                    .title(Component.translatable("itemGroup.tab_main")) // Локализуемое название
                    .displayItems((parameters, output) -> {
                        RegItems.ITEMS.getEntries().stream()
                                .map(RegistryObject::get)
                                .forEach(output::accept);
                        RegBlocks.BLOCKS.getEntries().stream()
                                .map(RegistryObject::get)
                                .map(Item::byBlock)
                                .filter(item -> item != Items.AIR) // иногда может быть AIR, если нет BlockItem
                                .forEach(output::accept);
                        RegBlocks.BLOCKS_CUSTOM_MODEL.getEntries().stream()
                                .map(RegistryObject::get)
                                .map(Item::byBlock)
                                .filter(item -> item != Items.AIR) // иногда может быть AIR, если нет BlockItem
                                .forEach(output::accept);
                        RegBlocks.BLOCKS_MANUAL.getEntries().stream()
                                .map(RegistryObject::get)
                                .map(Item::byBlock)
                                .filter(item -> item != Items.AIR) // иногда может быть AIR, если нет BlockItem
                                .forEach(output::accept);
                    })
                    .build()
    );

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }

}
