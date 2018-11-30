package com.mrcrayfish.vehicle.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

/**
 * Author: MrCrayfish
 */
public class ModSounds
{
    public static SoundEvent hornMono;
    public static SoundEvent hornStereo;
    public static SoundEvent atvEngineMono;
    public static SoundEvent atvEngineStereo;
    public static SoundEvent goKartEngineMono;
    public static SoundEvent goKartEngineStereo;
    public static SoundEvent electricEngineMono;
    public static SoundEvent electricEngineStereo;
    public static SoundEvent bonk;
    public static SoundEvent pickUpVehicle;
    public static SoundEvent speedBoatEngineMono;
    public static SoundEvent speedBoatEngineStereo;
    public static SoundEvent sprayCanSpray;
    public static SoundEvent sprayCanShake;
    public static SoundEvent mopedEngineMono;
    public static SoundEvent mopedEngineStereo;
    public static SoundEvent sportsPlaneEngineMono;
    public static SoundEvent sportsPlaneEngineStereo;
    public static SoundEvent boostPad;
    public static SoundEvent liquidGlug;
    public static SoundEvent fuelPortOpen;
    public static SoundEvent fuelPortClose;
    public static SoundEvent fuelPort2Open;
    public static SoundEvent fuelPort2Close;
    public static SoundEvent vehicleCratePanelLand;
    public static SoundEvent jackUp;
    public static SoundEvent jackDown;

    public static void register()
    {
        hornMono = registerSound("vehicle:horn_mono");
        hornStereo = registerSound("vehicle:horn_stereo");
        atvEngineMono = registerSound("vehicle:atv_engine_mono");
        atvEngineStereo = registerSound("vehicle:atv_engine_stereo");
        goKartEngineMono = registerSound("vehicle:go_kart_engine_mono");
        goKartEngineStereo = registerSound("vehicle:go_kart_engine_stereo");
        electricEngineMono = registerSound("vehicle:electric_engine_mono");
        electricEngineStereo = registerSound("vehicle:electric_engine_stereo");
        bonk = registerSound("vehicle:bonk");
        pickUpVehicle = registerSound("vehicle:pick_up_vehicle");
        speedBoatEngineMono = registerSound("vehicle:speed_boat_engine_mono");
        speedBoatEngineStereo = registerSound("vehicle:speed_boat_engine_stereo");
        sprayCanSpray = registerSound("vehicle:spray_can_spray");
        sprayCanShake = registerSound("vehicle:spray_can_shake");
        mopedEngineMono = registerSound("vehicle:moped_engine_mono");
        mopedEngineStereo = registerSound("vehicle:moped_engine_stereo");
        sportsPlaneEngineMono = registerSound("vehicle:sports_plane_engine_mono");
        sportsPlaneEngineStereo = registerSound("vehicle:sports_plane_engine_stereo");
        boostPad = registerSound("vehicle:boost_pad");
        liquidGlug = registerSound("vehicle:liquid_glug");
        fuelPortOpen = registerSound("vehicle:fuel_port_open");
        fuelPortClose = registerSound("vehicle:fuel_port_close");
        fuelPort2Open = registerSound("vehicle:fuel_port_2_open");
        fuelPort2Close = registerSound("vehicle:fuel_port_2_close");
        vehicleCratePanelLand = registerSound("vehicle:vehicle_crate_panel_land");
        jackUp = registerSound("vehicle:jack_up");
        jackDown = registerSound("vehicle:jack_down");
    }

    private static SoundEvent registerSound(String soundNameIn)
    {
        ResourceLocation resource = new ResourceLocation(soundNameIn);
        SoundEvent sound = new SoundEvent(resource).setRegistryName(soundNameIn);
        RegistrationHandler.Sounds.register(sound);
        return sound;
    }
}
