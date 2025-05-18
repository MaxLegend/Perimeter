package ru.tesmio.perimeter.core.registration;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import ru.tesmio.perimeter.Perimeter;
import ru.tesmio.perimeter.blocks.devices.redstonefurnace.recipe.RedstoneFurnaceRecipe;

public class RegRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Perimeter.MODID);

    public static final RegistryObject<RecipeSerializer<RedstoneFurnaceRecipe>> REDSTONE_FURNACE_RECIPE =
            SERIALIZERS.register("redstone_furnace", () -> RedstoneFurnaceRecipe.Serializer.INSTANCE);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
