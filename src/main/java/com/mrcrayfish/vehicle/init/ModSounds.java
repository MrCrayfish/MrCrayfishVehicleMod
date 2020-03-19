package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Author: MrCrayfish
 */
public class ModSounds
{
    public static final DeferredRegister<SoundEvent> SOUNDS = new DeferredRegister<>(ForgeRegistries.SOUND_EVENTS, Reference.MOD_ID);
    
    public static final RegistryObject<SoundEvent> HORN_MONO = register("horn_mono");
    public static final RegistryObject<SoundEvent> HORN_STEREO = register("horn_stereo");
    public static final RegistryObject<SoundEvent> ATV_ENGINE_MONO = register("atv_engine_mono");
    public static final RegistryObject<SoundEvent> ATV_ENGINE_STEREO = register("atv_engine_stereo");
    public static final RegistryObject<SoundEvent> GO_KART_ENGINE_MONO = register("go_kart_engine_mono");
    public static final RegistryObject<SoundEvent> GO_KART_ENGINE_STEREO = register("go_kart_engine_stereo");
    public static final RegistryObject<SoundEvent> ELECTRIC_ENGINE_MONO = register("electric_engine_mono");
    public static final RegistryObject<SoundEvent> ELECTRIC_ENGINE_STEREO = register("electric_engine_stereo");
    public static final RegistryObject<SoundEvent> BONK = register("bonk");
    public static final RegistryObject<SoundEvent> PICK_UP_VEHICLE = register("pick_up_vehicle");
    public static final RegistryObject<SoundEvent> SPEED_BOAT_ENGINE_MONO = register("speed_boat_engine_mono");
    public static final RegistryObject<SoundEvent> SPEED_BOAT_ENGINE_STEREO = register("speed_boat_engine_stereo");
    public static final RegistryObject<SoundEvent> SPRAY_CAN_SPRAY = register("spray_can_spray");
    public static final RegistryObject<SoundEvent> SPRAY_CAN_SHAKE = register("spray_can_shake");
    public static final RegistryObject<SoundEvent> MOPED_ENGINE_MONO = register("moped_engine_mono");
    public static final RegistryObject<SoundEvent> MOPED_ENGINE_STEREO = register("moped_engine_stereo");
    public static final RegistryObject<SoundEvent> SPORTS_PLANE_ENGINE_MONO = register("sports_plane_engine_mono");
    public static final RegistryObject<SoundEvent> SPORTS_PLANE_ENGINE_STEREO = register("sports_plane_engine_stereo");
    public static final RegistryObject<SoundEvent> BOOST_PAD = register("boost_pad");
    public static final RegistryObject<SoundEvent> LIQUID_GLUG = register("liquid_glug");
    public static final RegistryObject<SoundEvent> FUEL_PORT_OPEN = register("fuel_port_open");
    public static final RegistryObject<SoundEvent> FUEL_PORT_CLOSE = register("fuel_port_close");
    public static final RegistryObject<SoundEvent> FUEL_PORT_2_OPEN = register("fuel_port_2_open");
    public static final RegistryObject<SoundEvent> FUEL_PORT_2_CLOSE = register("fuel_port_2_close");
    public static final RegistryObject<SoundEvent> VEHICLE_CRATE_PANEL_LAND = register("vehicle_crate_panel_land");
    public static final RegistryObject<SoundEvent> JACK_UP = register("jack_up");
    public static final RegistryObject<SoundEvent> JACK_DOWN = register("jack_down");
    public static final RegistryObject<SoundEvent> VEHICLE_IMPACT = register("vehicle_impact");
    public static final RegistryObject<SoundEvent> VEHICLE_DESTROYED = register("vehicle_destroyed");
    public static final RegistryObject<SoundEvent> VEHICLE_THUD = register("vehicle_thud");
    public static final RegistryObject<SoundEvent> AIR_WRENCH_GUN = register("air_wrench_gun");
    public static final RegistryObject<SoundEvent> TRACTOR_ENGINE_MONO = register("tractor_engine_mono");
    public static final RegistryObject<SoundEvent> TRACTOR_ENGINE_STEREO = register("tractor_engine_stereo");
    public static final RegistryObject<SoundEvent> NOZZLE_PICK_UP = register("nozzle_pick_up");
    public static final RegistryObject<SoundEvent> NOZZLE_PUT_DOWN = register("nozzle_put_down");
    public static final RegistryObject<SoundEvent> MINI_BUS_ENGINE_MONO = register("mini_bus_engine_mono");
    public static final RegistryObject<SoundEvent> MINI_BUS_ENGINE_STEREO = register("mini_bus_engine_stereo");

    private static RegistryObject<SoundEvent> register(String id)
    {
        return ModSounds.SOUNDS.register(id, () -> new SoundEvent(new ResourceLocation(Reference.MOD_ID, id)));
    }
}
