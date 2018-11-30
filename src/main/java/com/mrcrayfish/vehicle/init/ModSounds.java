package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;

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
    public static final SoundEvent SPRAY_CAN_SPRAY;
    public static final SoundEvent SPRAY_CAN_SHAKE;
    public static final SoundEvent MOPED_ENGINE_MONO;
    public static final SoundEvent MOPED_ENGINE_STEREO;
    public static final SoundEvent SPORTS_PLANE_ENGINE_MONO;
    public static final SoundEvent SPORTS_PLANE_ENGINE_STEREO;
    public static final SoundEvent BOOST_PAD;
    public static final SoundEvent LIQUID_GLUG;
    public static final SoundEvent FUEL_PORT_OPEN;
    public static final SoundEvent FUEL_PORT_CLOSE;
    public static final SoundEvent FUEL_PORT_2_OPEN;
    public static final SoundEvent FUEL_PORT_2_CLOSE;
    public static final SoundEvent VEHICLE_CRATE_PANEL_LAND;
    public static final SoundEvent JACK_UP;
    public static final SoundEvent JACK_DOWN;

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
        SPRAY_CAN_SPRAY = registerSound("vehicle:spray_can_spray");
        SPRAY_CAN_SHAKE = registerSound("vehicle:spray_can_shake");
        MOPED_ENGINE_MONO = registerSound("vehicle:moped_engine_mono");
        MOPED_ENGINE_STEREO = registerSound("vehicle:moped_engine_stereo");
        SPORTS_PLANE_ENGINE_MONO = registerSound("vehicle:sports_plane_engine_mono");
        SPORTS_PLANE_ENGINE_STEREO = registerSound("vehicle:sports_plane_engine_stereo");
        BOOST_PAD = registerSound("vehicle:boost_pad");
        LIQUID_GLUG = registerSound("vehicle:liquid_glug");
        FUEL_PORT_OPEN = registerSound("vehicle:fuel_port_open");
        FUEL_PORT_CLOSE = registerSound("vehicle:fuel_port_close");
        FUEL_PORT_2_OPEN = registerSound("vehicle:fuel_port_2_open");
        FUEL_PORT_2_CLOSE = registerSound("vehicle:fuel_port_2_close");
        VEHICLE_CRATE_PANEL_LAND = registerSound("vehicle:vehicle_crate_panel_land");
        JACK_UP = registerSound("vehicle:jack_up");
        JACK_DOWN = registerSound("vehicle:jack_down");
    }

    private static SoundEvent registerSound(String soundNameIn)
    {
        ResourceLocation resource = new ResourceLocation(soundNameIn);
        SoundEvent sound = new SoundEvent(resource).setRegistryName(soundNameIn);
        RegistrationHandler.add(sound);
        return sound;
    }

    @Mod.EventBusSubscriber(modid = Reference.MOD_ID)
    public static class RegistrationHandler
    {
        private static List<SoundEvent> sounds = new ArrayList<>();

        private static void add(SoundEvent sound)
        {
            sounds.add(sound);
        }

        @SubscribeEvent
        public static void registerSounds(final RegistryEvent.Register<SoundEvent> event)
        {
            IForgeRegistry<SoundEvent> registry = event.getRegistry();
            sounds.forEach(registry::register);
            sounds.clear();
        }
    }
}
