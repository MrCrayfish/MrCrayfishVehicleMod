package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.util.SoundNames;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Author: MrCrayfish
 */
@GameRegistry.ObjectHolder(Reference.MOD_ID)
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
    public static final SoundEvent DIRT_BIKE_ENGINE_MONO = null;
    public static final SoundEvent DIRT_BIKE_ENGINE_STEREO = null;

    public static void register()
    {
        register(SoundNames.HORN_MONO);
        register(SoundNames.HORN_STEREO);
        register(SoundNames.ATV_ENGINE_MONO);
        register(SoundNames.ATV_ENGINE_STEREO);
        register(SoundNames.GO_KART_ENGINE_MONO);
        register(SoundNames.GO_KART_ENGINE_STEREO);
        register(SoundNames.ELECTRIC_ENGINE_MONO);
        register(SoundNames.ELECTRIC_ENGINE_STEREO);
        register(SoundNames.BONK);
        register(SoundNames.PICK_UP_VEHICLE);
        register(SoundNames.SPEED_BOAT_ENGINE_MONO);
        register(SoundNames.SPEED_BOAT_ENGINE_STEREO);
        register(SoundNames.SPRAY_CAN_SPRAY);
        register(SoundNames.SPRAY_CAN_SHAKE);
        register(SoundNames.MOPED_ENGINE_MONO);
        register(SoundNames.MOPED_ENGINE_STEREO);
        register(SoundNames.SPORTS_PLANE_ENGINE_MONO);
        register(SoundNames.SPORTS_PLANE_ENGINE_STEREO);
        register(SoundNames.BOOST_PAD);
        register(SoundNames.LIQUID_GLUG);
        register(SoundNames.FUEL_PORT_OPEN);
        register(SoundNames.FUEL_PORT_CLOSE);
        register(SoundNames.FUEL_PORT_2_OPEN);
        register(SoundNames.FUEL_PORT_2_CLOSE);
        register(SoundNames.VEHICLE_CRATE_PANEL_LAND);
        register(SoundNames.JACK_UP);
        register(SoundNames.JACK_DOWN);
        register(SoundNames.VEHICLE_IMPACT);
        register(SoundNames.VEHICLE_DESTROYED);
        register(SoundNames.VEHICLE_THUD);
        register(SoundNames.AIR_WRENCH_GUN);
        register(SoundNames.TRACTOR_ENGINE_MONO);
        register(SoundNames.TRACTOR_ENGINE_STEREO);
        register(SoundNames.NOZZLE_PICK_UP);
        register(SoundNames.NOZZLE_PUT_DOWN);
        register(SoundNames.MINI_BUS_ENGINE_MONO);
        register(SoundNames.MINI_BUS_ENGINE_STEREO);
        register(SoundNames.DIRT_BIKE_ENGINE_MONO);
        register(SoundNames.DIRT_BIKE_ENGINE_STEREO);
    }

    private static void register(String soundNameIn)
    {
        ResourceLocation resource = new ResourceLocation(soundNameIn);
        SoundEvent sound = new SoundEvent(resource).setRegistryName(soundNameIn);
        RegistrationHandler.Sounds.register(sound);
    }
}
