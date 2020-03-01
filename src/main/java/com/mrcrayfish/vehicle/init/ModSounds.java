package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.util.Names;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

/**
 * Author: MrCrayfish
 */
@ObjectHolder(Reference.MOD_ID)
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModSounds
{
    public static final SoundEvent HORN_MONO = null;
    public static final SoundEvent HORN_STEREO = null;
    public static final SoundEvent ATV_ENGINE_MONO = null;
    public static final SoundEvent ATV_ENGINE_STEREO = null;
    public static final SoundEvent GO_KART_ENGINE_MONO = null;
    public static final SoundEvent GO_KART_ENGINE_STEREO = null;
    public static final SoundEvent ELECTRIC_ENGINE_MONO = null;
    public static final SoundEvent ELECTRIC_ENGINE_STEREO = null;
    public static final SoundEvent BONK = null;
    public static final SoundEvent PICK_UP_VEHICLE = null;
    public static final SoundEvent SPEED_BOAT_ENGINE_MONO = null;
    public static final SoundEvent SPEED_BOAT_ENGINE_STEREO = null;
    public static final SoundEvent SPRAY_CAN_SPRAY = null;
    public static final SoundEvent SPRAY_CAN_SHAKE = null;
    public static final SoundEvent MOPED_ENGINE_MONO = null;
    public static final SoundEvent MOPED_ENGINE_STEREO = null;
    public static final SoundEvent SPORTS_PLANE_ENGINE_MONO = null;
    public static final SoundEvent SPORTS_PLANE_ENGINE_STEREO = null;
    public static final SoundEvent BOOST_PAD = null;
    public static final SoundEvent LIQUID_GLUG = null;
    public static final SoundEvent FUEL_PORT_OPEN = null;
    public static final SoundEvent FUEL_PORT_CLOSE = null;
    public static final SoundEvent FUEL_PORT_2_OPEN = null;
    public static final SoundEvent FUEL_PORT_2_CLOSE = null;
    public static final SoundEvent VEHICLE_CRATE_PANEL_LAND = null;
    public static final SoundEvent JACK_UP = null;
    public static final SoundEvent JACK_DOWN = null;
    public static final SoundEvent VEHICLE_IMPACT = null;
    public static final SoundEvent VEHICLE_DESTROYED = null;
    public static final SoundEvent VEHICLE_THUD = null;
    public static final SoundEvent AIR_WRENCH_GUN = null;
    public static final SoundEvent TRACTOR_ENGINE_MONO = null;
    public static final SoundEvent TRACTOR_ENGINE_STEREO = null;
    public static final SoundEvent NOZZLE_PICK_UP = null;
    public static final SoundEvent NOZZLE_PUT_DOWN = null;
    public static final SoundEvent MINI_BUS_ENGINE_MONO = null;
    public static final SoundEvent MINI_BUS_ENGINE_STEREO = null;

    private static SoundEvent buildSound(String soundNameIn)
    {
        ResourceLocation resource = new ResourceLocation(soundNameIn);
        return new SoundEvent(resource).setRegistryName(soundNameIn);
    }

    @SubscribeEvent
    public static void buildSound(final RegistryEvent.Register<SoundEvent> event)
    {
        IForgeRegistry<SoundEvent> registry = event.getRegistry();
        registry.register(buildSound(Names.Sound.HORN_MONO));
        registry.register(buildSound(Names.Sound.HORN_STEREO));
        registry.register(buildSound(Names.Sound.ATV_ENGINE_MONO));
        registry.register(buildSound(Names.Sound.ATV_ENGINE_STEREO));
        registry.register(buildSound(Names.Sound.GO_KART_ENGINE_MONO));
        registry.register(buildSound(Names.Sound.GO_KART_ENGINE_STEREO));
        registry.register(buildSound(Names.Sound.ELECTRIC_ENGINE_MONO));
        registry.register(buildSound(Names.Sound.ELECTRIC_ENGINE_STEREO));
        registry.register(buildSound(Names.Sound.BONK));
        registry.register(buildSound(Names.Sound.PICK_UP_VEHICLE));
        registry.register(buildSound(Names.Sound.SPEED_BOAT_ENGINE_MONO));
        registry.register(buildSound(Names.Sound.SPEED_BOAT_ENGINE_STEREO));
        registry.register(buildSound(Names.Sound.SPRAY_CAN_SPRAY));
        registry.register(buildSound(Names.Sound.SPRAY_CAN_SHAKE));
        registry.register(buildSound(Names.Sound.MOPED_ENGINE_MONO));
        registry.register(buildSound(Names.Sound.MOPED_ENGINE_STEREO));
        registry.register(buildSound(Names.Sound.SPORTS_PLANE_ENGINE_MONO));
        registry.register(buildSound(Names.Sound.SPORTS_PLANE_ENGINE_STEREO));
        registry.register(buildSound(Names.Sound.BOOST_PAD));
        registry.register(buildSound(Names.Sound.LIQUID_GLUG));
        registry.register(buildSound(Names.Sound.FUEL_PORT_OPEN));
        registry.register(buildSound(Names.Sound.FUEL_PORT_CLOSE));
        registry.register(buildSound(Names.Sound.FUEL_PORT_2_OPEN));
        registry.register(buildSound(Names.Sound.FUEL_PORT_2_CLOSE));
        registry.register(buildSound(Names.Sound.VEHICLE_CRATE_PANEL_LAND));
        registry.register(buildSound(Names.Sound.JACK_UP));
        registry.register(buildSound(Names.Sound.JACK_DOWN));
        registry.register(buildSound(Names.Sound.VEHICLE_IMPACT));
        registry.register(buildSound(Names.Sound.VEHICLE_DESTROYED));
        registry.register(buildSound(Names.Sound.VEHICLE_THUD));
        registry.register(buildSound(Names.Sound.AIR_WRENCH_GUN));
        registry.register(buildSound(Names.Sound.TRACTOR_ENGINE_MONO));
        registry.register(buildSound(Names.Sound.TRACTOR_ENGINE_STEREO));
        registry.register(buildSound(Names.Sound.NOZZLE_PICK_UP));
        registry.register(buildSound(Names.Sound.NOZZLE_PUT_DOWN));
        registry.register(buildSound(Names.Sound.MINI_BUS_ENGINE_MONO));
        registry.register(buildSound(Names.Sound.MINI_BUS_ENGINE_STEREO));
    }
}
