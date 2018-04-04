package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Author: MrCrayfish
 */
public class ModSounds
{
    public static final SoundEvent ATV_DRIVING;

    static
    {
        ATV_DRIVING = registerSound("vehicle:driving");
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
            event.getRegistry().register(ATV_DRIVING);
        }
    }
}
