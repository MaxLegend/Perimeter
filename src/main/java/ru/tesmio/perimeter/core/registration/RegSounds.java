package ru.tesmio.perimeter.core.registration;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import ru.tesmio.perimeter.Perimeter;

public class RegSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Perimeter.MODID);
    //public static final RegistryObject<SoundEvent> VOLTAGE_BUZZ = registerSound("voltage_buzz");

    private static RegistryObject<SoundEvent> registerSound(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(
                new ResourceLocation(Perimeter.MODID, name)
        ));
    }

    public static void register(IEventBus bus) {
        SOUNDS.register(bus);
    }
}
