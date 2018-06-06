package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Author: MrCrayfish
 */
public class ModSounds
{
    public static final SoundEvent HORN_MONO;
    public static final SoundEvent HORN_STEREO;
    public static final SoundEvent ATV_ENGINE_MONO;
    public static final SoundEvent ATV_ENGINE_STEREO;
    public static final SoundEvent GO_KART_ENGINE_MONO;
    public static final SoundEvent GO_KART_ENGINE_STEREO;
    public static final SoundEvent ELECTRIC_ENGINE_MONO;
    public static final SoundEvent ELECTRIC_ENGINE_STEREO;
    public static final SoundEvent BONK;
    public static final SoundEvent PICK_UP_VEHICLE;
    public static final SoundEvent SPEED_BOAT_ENGINE_MONO;
    public static final SoundEvent SPEED_BOAT_ENGINE_STEREO;

    static
    {
        HORN_MONO = registerSound("vehicle:horn_mono");
        HORN_STEREO = registerSound("vehicle:horn_stereo");
        ATV_ENGINE_MONO = registerSound("vehicle:atv_engine_mono");
        ATV_ENGINE_STEREO = registerSound("vehicle:atv_engine_stereo");
        GO_KART_ENGINE_MONO = registerSound("vehicle:go_kart_engine_mono");
        GO_KART_ENGINE_STEREO = registerSound("vehicle:go_kart_engine_stereo");
        ELECTRIC_ENGINE_MONO = registerSound("vehicle:electric_engine_mono");
        ELECTRIC_ENGINE_STEREO = registerSound("vehicle:electric_engine_stereo");
        BONK = registerSound("vehicle:bonk");
        PICK_UP_VEHICLE = registerSound("vehicle:pick_up_vehicle");
        SPEED_BOAT_ENGINE_MONO = registerSound("vehicle:speed_boat_engine_mono");
        SPEED_BOAT_ENGINE_STEREO = registerSound("vehicle:speed_boat_engine_stereo");
    }

    private static SoundEvent registerSound(String soundNameIn)
    {
        ResourceLocation resource = new ResourceLocation(soundNameIn);
        SoundEvent sound = new SoundEvent(resource).setRegistryName(soundNameIn);
        return sound;
    }

    @Mod.EventBusSubscriber(modid = Reference.MOD_ID)
    public static class RegistrationHandler
    {
        @SubscribeEvent
        public static void registerSounds(final RegistryEvent.Register<SoundEvent> event)
        {
            IForgeRegistry<SoundEvent> registry = event.getRegistry();
            registry.register(HORN_MONO);
            registry.register(HORN_STEREO);
            registry.register(ATV_ENGINE_MONO);
            registry.register(ATV_ENGINE_STEREO);
            registry.register(GO_KART_ENGINE_MONO);
            registry.register(GO_KART_ENGINE_STEREO);
            registry.register(ELECTRIC_ENGINE_MONO);
            registry.register(ELECTRIC_ENGINE_STEREO);
            registry.register(BONK);
            registry.register(PICK_UP_VEHICLE);
            registry.register(SPEED_BOAT_ENGINE_MONO);
            registry.register(SPEED_BOAT_ENGINE_STEREO);
        }
    }
}
