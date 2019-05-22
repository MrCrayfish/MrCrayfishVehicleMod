package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
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
    public static SoundEvent vehicleImpact;
    public static SoundEvent vehicleDestroyed;
    public static SoundEvent vehicleThud;
    public static SoundEvent airWrenchGun;
    public static SoundEvent tractorEngineMono;
    public static SoundEvent tractorEngineStereo;
    public static SoundEvent nozzlePickUp;
    public static SoundEvent nozzlePutDown;

    public static void register()
    {
        hornMono = registerSound("horn_mono");
        hornStereo = registerSound("horn_stereo");
        atvEngineMono = registerSound("atv_engine_mono");
        atvEngineStereo = registerSound("atv_engine_stereo");
        goKartEngineMono = registerSound("go_kart_engine_mono");
        goKartEngineStereo = registerSound("go_kart_engine_stereo");
        electricEngineMono = registerSound("electric_engine_mono");
        electricEngineStereo = registerSound("electric_engine_stereo");
        bonk = registerSound("bonk");
        pickUpVehicle = registerSound("pick_up_vehicle");
        speedBoatEngineMono = registerSound("speed_boat_engine_mono");
        speedBoatEngineStereo = registerSound("speed_boat_engine_stereo");
        sprayCanSpray = registerSound("spray_can_spray");
        sprayCanShake = registerSound("spray_can_shake");
        mopedEngineMono = registerSound("moped_engine_mono");
        mopedEngineStereo = registerSound("moped_engine_stereo");
        sportsPlaneEngineMono = registerSound("sports_plane_engine_mono");
        sportsPlaneEngineStereo = registerSound("sports_plane_engine_stereo");
        boostPad = registerSound("boost_pad");
        liquidGlug = registerSound("liquid_glug");
        fuelPortOpen = registerSound("fuel_port_open");
        fuelPortClose = registerSound("fuel_port_close");
        fuelPort2Open = registerSound("fuel_port_2_open");
        fuelPort2Close = registerSound("fuel_port_2_close");
        vehicleCratePanelLand = registerSound("vehicle_crate_panel_land");
        jackUp = registerSound("jack_up");
        jackDown = registerSound("jack_down");
        vehicleImpact = registerSound("vehicle_impact");
        vehicleDestroyed = registerSound("vehicle_destroyed");
        vehicleThud = registerSound("vehicle_thud");
        airWrenchGun = registerSound("air_wrench_gun");
        tractorEngineMono = registerSound("tractor_engine_mono");
        tractorEngineStereo = registerSound("tractor_engine_stereo");
        nozzlePickUp = registerSound("nozzle_pick_up");
        nozzlePutDown = registerSound("nozzle_put_down");
    }

    private static SoundEvent registerSound(String soundNameIn)
    {
        soundNameIn = Reference.MOD_ID + ":" + soundNameIn;
        ResourceLocation resource = new ResourceLocation(soundNameIn);
        SoundEvent sound = new SoundEvent(resource).setRegistryName(soundNameIn);
        RegistrationHandler.Sounds.register(sound);
        return sound;
    }
}
