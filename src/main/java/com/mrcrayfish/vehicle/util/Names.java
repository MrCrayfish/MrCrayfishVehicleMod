package com.mrcrayfish.vehicle.util;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.inventory.container.EditVehicleContainer;
import com.mrcrayfish.vehicle.inventory.container.FluidMixerContainer;
import com.mrcrayfish.vehicle.inventory.container.StorageContainer;
import com.mrcrayfish.vehicle.inventory.container.WorkstationContainer;
import com.mrcrayfish.vehicle.tileentity.*;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Author: MrCrayfish
 */
public class Names
{
    private static final String PREFIX = Reference.MOD_ID + ":";

    public static class Block
    {
        public static final String JACK_HEAD = PREFIX + "jack_head";
        public static final String JACK = PREFIX + "jack";
        public static final String VEHICLE_CRATE = PREFIX + "vehicle_crate";
        public static final String WORKSTATION = PREFIX + "workstation";
        public static final String INDUSTRIAL_FUEL_DRUM = PREFIX + "industrial_fuel_drum";
        public static final String FUEL_DRUM = PREFIX + "fuel_drum";
        public static final String FLUID_PUMP = PREFIX + "fluid_pump";
        public static final String FLUID_PIPE = PREFIX + "fluid_pipe";
        public static final String FLUID_MIXER = PREFIX + "fluid_mixer";
        public static final String FLUID_EXTRACTOR = PREFIX + "fluid_extractor";
        public static final String GAS_PUMP = PREFIX + "gas_pump";
        public static final String BLAZE_JUICE = PREFIX + "blaze_juice";
        public static final String ENDER_SAP = PREFIX + "ender_sap";
        public static final String FUELIUM = PREFIX + "fuelium";
        public static final String STEEP_BOOST_RAMP = PREFIX + "steep_boost_ramp";
        public static final String BOOST_RAMP = PREFIX + "boost_ramp";
        public static final String BOOST_PAD = PREFIX + "boost_pad";
        public static final String TRAFFIC_CONE = PREFIX + "traffic_cone";
    }

    public static class Item
    {
        public static final String KEY = PREFIX + "key";
        public static final String HAMMER = PREFIX + "hammer";
        public static final String WRENCH = PREFIX + "wrench";
        public static final String INDUSTRIAL_JERRY_CAN = PREFIX + "industrial_jerry_can";
        public static final String JERRY_CAN = PREFIX + "jerry_can";
        public static final String SPRAY_CAN = PREFIX + "spray_can";
        public static final String DIAMOND_ELECTRIC_ENGINE = PREFIX + "diamond_electric_engine";
        public static final String GOLD_ELECTRIC_ENGINE = PREFIX + "gold_electric_engine";
        public static final String IRON_ELECTRIC_ENGINE = PREFIX + "iron_electric_engine";
        public static final String STONE_ELECTRIC_ENGINE = PREFIX + "stone_electric_engine";
        public static final String WOOD_ELECTRIC_ENGINE = PREFIX + "wood_electric_engine";
        public static final String DIAMOND_LARGE_ENGINE = PREFIX + "diamond_large_engine";
        public static final String GOLD_LARGE_ENGINE = PREFIX + "gold_large_engine";
        public static final String IRON_LARGE_ENGINE = PREFIX + "iron_large_engine";
        public static final String STONE_LARGE_ENGINE = PREFIX + "stone_large_engine";
        public static final String WOOD_LARGE_ENGINE = PREFIX + "wood_large_engine";
        public static final String DIAMOND_SMALL_ENGINE = PREFIX + "diamond_small_engine";
        public static final String GOLD_SMALL_ENGINE = PREFIX + "gold_small_engine";
        public static final String IRON_SMALL_ENGINE = PREFIX + "iron_small_engine";
        public static final String STONE_SMALL_ENGINE = PREFIX + "stone_small_engine";
        public static final String WOOD_SMALL_ENGINE = PREFIX + "wood_small_engine";
        public static final String PLASTIC_WHEEL = PREFIX + "plastic_wheel";
        public static final String ALL_TERRAIN_WHEEL = PREFIX + "all_terrain_wheel";
        public static final String SNOW_WHEEL = PREFIX + "snow_wheel";
        public static final String OFF_ROAD_WHEEL = PREFIX + "off_road_wheel";
        public static final String RACING_WHEEL = PREFIX + "racing_wheel";
        public static final String SPORTS_WHEEL = PREFIX + "sports_wheel";
        public static final String STANDARD_WHEEL = PREFIX + "standard_wheel";
        public static final String PANEL = PREFIX + "panel";
    }

    public static class Entity
    {
        /* Other */
        public static final String JACK = PREFIX + "jack";
        public static final String SOFACOPTER = PREFIX + "sofacopter";
        public static final String BATH = PREFIX + "bath";

        /* Special Vehicles */
        public static final String SOFA = PREFIX + "couch";
        public static final String FERTILIZER = PREFIX + "fertilizer";
        public static final String SEEDER = PREFIX + "seeder";
        public static final String FLUID_TRAILER = PREFIX + "fluid_trailer";
        public static final String STORAGE_TRAILER = PREFIX + "storage_trailer";

        /* Trailers */
        public static final String VEHICLE_TRAILER = PREFIX + "vehicle_trailer";
        public static final String TRACTOR = PREFIX + "tractor";
        public static final String OFF_ROADER = PREFIX + "off_roader";
        public static final String GOLF_CART = PREFIX + "golf_cart";
        public static final String SPORTS_PLANE = PREFIX + "sports_plane";
        public static final String MOPED = PREFIX + "moped";
        public static final String LAWN_MOWER = PREFIX + "lawn_mower";
        public static final String SMART_CAR = PREFIX + "smart_car";
        public static final String ALUMINUM_BOAT = PREFIX + "aluminum_boat";
        public static final String SPEED_BOAT = PREFIX + "speed_boat";
        public static final String JET_SKI = PREFIX + "jet_ski";
        public static final String BUMPER_CAR = PREFIX + "bumper_car";
        public static final String MINI_BIKE = PREFIX + "mini_bike";
        public static final String SHOPPING_CART = PREFIX + "shopping_cart";
        public static final String GO_KART = PREFIX + "go_kart";
        public static final String DUNE_BUGGY = PREFIX + "dune_buggy";

        /* Motor Vehicles */
        public static final String ATV = PREFIX + "atv";
    }

    public static class Sound
    {
        public static final String NOZZLE_PUT_DOWN = PREFIX + "nozzle_put_down";
        public static final String NOZZLE_PICK_UP = PREFIX + "nozzle_pick_up";
        public static final String TRACTOR_ENGINE_STEREO = PREFIX + "tractor_engine_stereo";
        public static final String TRACTOR_ENGINE_MONO = PREFIX + "tractor_engine_mono";
        public static final String AIR_WRENCH_GUN = PREFIX + "air_wrench_gun";
        public static final String VEHICLE_THUD = PREFIX + "vehicle_thud";
        public static final String VEHICLE_DESTROYED = PREFIX + "vehicle_destroyed";
        public static final String VEHICLE_IMPACT = PREFIX + "vehicle_impact";
        public static final String JACK_DOWN = PREFIX + "jack_down";
        public static final String JACK_UP = PREFIX + "jack_up";
        public static final String VEHICLE_CRATE_PANEL_LAND = PREFIX + "vehicle_crate_panel_land";
        public static final String FUEL_PORT_2_CLOSE = PREFIX + "fuel_port_2_close";
        public static final String FUEL_PORT_2_OPEN = PREFIX + "fuel_port_2_open";
        public static final String FUEL_PORT_CLOSE = PREFIX + "fuel_port_close";
        public static final String FUEL_PORT_OPEN = PREFIX + "fuel_port_open";
        public static final String LIQUID_GLUG = PREFIX + "liquid_glug";
        public static final String BOOST_PAD = PREFIX + "boost_pad";
        public static final String SPORTS_PLANE_ENGINE_STEREO = PREFIX + "sports_plane_engine_stereo";
        public static final String SPORTS_PLANE_ENGINE_MONO = PREFIX + "sports_plane_engine_mono";
        public static final String MOPED_ENGINE_STEREO = PREFIX + "moped_engine_stereo";
        public static final String MOPED_ENGINE_MONO = PREFIX + "moped_engine_mono";
        public static final String SPRAY_CAN_SHAKE = PREFIX + "spray_can_shake";
        public static final String SPRAY_CAN_SPRAY = PREFIX + "spray_can_spray";
        public static final String SPEED_BOAT_ENGINE_STEREO = PREFIX + "speed_boat_engine_stereo";
        public static final String SPEED_BOAT_ENGINE_MONO = PREFIX + "speed_boat_engine_mono";
        public static final String PICK_UP_VEHICLE = PREFIX + "pick_up_vehicle";
        public static final String BONK = PREFIX + "bonk";
        public static final String ELECTRIC_ENGINE_STEREO = PREFIX + "electric_engine_stereo";
        public static final String ELECTRIC_ENGINE_MONO = PREFIX + "electric_engine_mono";
        public static final String GO_KART_ENGINE_STEREO = PREFIX + "go_kart_engine_stereo";
        public static final String GO_KART_ENGINE_MONO = PREFIX + "go_kart_engine_mono";
        public static final String ATV_ENGINE_STEREO = PREFIX + "atv_engine_stereo";
        public static final String ATV_ENGINE_MONO = PREFIX + "atv_engine_mono";
        public static final String HORN_STEREO = PREFIX + "horn_stereo";
        public static final String HORN_MONO = PREFIX + "horn_mono";
    }

    public static class Container
    {
        public static final String FLUID_EXTRACTOR = PREFIX + "fluid_extractor";
        public static final String FLUID_MIXER = PREFIX + "fluid_mixer";
        public static final String EDIT_VEHICLE = PREFIX + "edit_vehicle";
        public static final String WORKSTATION = PREFIX + "workstation";
        public static final String STORAGE = PREFIX + "storage";
    }

    public static class TileEntity
    {
        public static final String FLUID_EXTRACTOR = PREFIX + "fluid_extractor";
        public static final String FLUID_PIPE = PREFIX + "fluid_pipe";
        public static final String FLUID_PUMP = PREFIX + "fluid_pump";
        public static final String FLUID_MIXER = PREFIX + "fluid_mixer";
        public static final String FUEL_DRUM = PREFIX + "fuel_drum";
        public static final String INDUSTRIAL_FUEL_DRUM = PREFIX + "industrial_fuel_drum";
        public static final String VEHICLE_CRATE = PREFIX + "vehicle_crate";
        public static final String WORKSTATION = PREFIX + "workstation";
        public static final String JACK = PREFIX + "jack";
        public static final String BOOST = PREFIX + "boost";
        public static final String GAS_PUMP = PREFIX + "gas_pump";
        public static final String GAS_PUMP_TANK = PREFIX + "gas_pump_tank";
    }
}
