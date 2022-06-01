package com.mrcrayfish.vehicle.client.model;

import com.mrcrayfish.vehicle.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

public class VehicleModels
{
    public static final ComponentLoader LOADER = new ComponentLoader(Reference.MOD_ID);

    /* Sports Car */
    public static final ComponentModel SPORTS_CAR_BODY = LOADER.create("vehicle/sports_car/base");
    public static final ComponentModel SPORTS_CAR_STEERING_WHEEL = LOADER.create("vehicle/sports_car/steering_wheel");
    public static final ComponentModel SPORTS_CAR_COSMETIC_DASHBOARD = LOADER.create("vehicle/sports_car/cosmetics/dashboard");
    public static final ComponentModel SPORTS_CAR_COSMETIC_HOOD = LOADER.create("vehicle/sports_car/cosmetics/hood");
    public static final ComponentModel SPORTS_CAR_COSMETIC_LEFT_DOOR = LOADER.create("vehicle/sports_car/cosmetics/left_door");
    public static final ComponentModel SPORTS_CAR_COSMETIC_RIGHT_DOOR = LOADER.create("vehicle/sports_car/cosmetics/right_door");
    public static final ComponentModel SPORTS_CAR_COSMETIC_SEAT = LOADER.create("vehicle/sports_car/cosmetics/seat");
    public static final ComponentModel SPORTS_CAR_COSMETIC_BOOT = LOADER.create("vehicle/sports_car/cosmetics/boot");
    public static final ComponentModel SPORTS_CAR_COSMETIC_ROOF = LOADER.create("vehicle/sports_car/cosmetics/roof");
    public static final ComponentModel SPORTS_CAR_COSMETIC_STOCK_DASHBOARD = LOADER.create("vehicle/sports_car/cosmetics/stock_dashboard");
    public static final ComponentModel SPORTS_CAR_COSMETIC_STOCK_FRONT_BUMPER = LOADER.create("vehicle/sports_car/cosmetics/stock_front_bumper");
    public static final ComponentModel SPORTS_CAR_COSMETIC_STOCK_REAR_BUMPER = LOADER.create("vehicle/sports_car/cosmetics/stock_rear_bumper");
    public static final ComponentModel SPORTS_CAR_COSMETIC_STOCK_HOOD = LOADER.create("vehicle/sports_car/cosmetics/stock_hood");
    public static final ComponentModel SPORTS_CAR_COSMETIC_STOCK_LEFT_DOOR = LOADER.create("vehicle/sports_car/cosmetics/stock_left_door");
    public static final ComponentModel SPORTS_CAR_COSMETIC_STOCK_RIGHT_DOOR = LOADER.create("vehicle/sports_car/cosmetics/stock_right_door");
    public static final ComponentModel SPORTS_CAR_COSMETIC_STOCK_FRONT_LIGHTS = LOADER.create("vehicle/sports_car/cosmetics/stock_front_lights");
    public static final ComponentModel SPORTS_CAR_COSMETIC_STOCK_REAR_LIGHTS = LOADER.create("vehicle/sports_car/cosmetics/stock_rear_lights");
    public static final ComponentModel SPORTS_CAR_COSMETIC_STOCK_SEAT = LOADER.create("vehicle/sports_car/cosmetics/stock_seat");
    public static final ComponentModel SPORTS_CAR_COSMETIC_STOCK_SPOILER = LOADER.create("vehicle/sports_car/cosmetics/stock_spoiler");

    /* Mini Bus */
    public static final ComponentModel MINI_BUS_BODY = LOADER.create("vehicle/mini_bus/body");
    public static final ComponentModel MINI_BUS_STEERING_WHEEL = LOADER.create("vehicle/mini_bus/steering_wheel");
    public static final ComponentModel MINI_BUS_COSMETIC_STOCK_DASHBOARD = LOADER.create("vehicle/mini_bus/cosmetics/stock_dashboard");
    public static final ComponentModel MINI_BUS_COSMETIC_STOCK_LEFT_DOOR = LOADER.create("vehicle/mini_bus/cosmetics/stock_left_door");
    public static final ComponentModel MINI_BUS_COSMETIC_STOCK_RIGHT_DOOR = LOADER.create("vehicle/mini_bus/cosmetics/stock_right_door");
    public static final ComponentModel MINI_BUS_COSMETIC_STOCK_SLIDING_DOOR = LOADER.create("vehicle/mini_bus/cosmetics/stock_sliding_door");
    public static final ComponentModel MINI_BUS_COSMETIC_STOCK_SEAT = LOADER.create("vehicle/mini_bus/cosmetics/stock_seat");
    public static final ComponentModel MINI_BUS_COSMETIC_STOCK_ROOF = LOADER.create("vehicle/mini_bus/cosmetics/stock_roof");
    public static final ComponentModel MINI_BUS_COSMETIC_ROOF_RACKS = LOADER.create("vehicle/mini_bus/cosmetics/roof_racks");
    public static final ComponentModel MINI_BUS_COSMETIC_AIRCON_LADDER_REAR_DECOR = LOADER.create("vehicle/mini_bus/cosmetics/aircon_ladder");
    public static final ComponentModel MINI_BUS_COSMETIC_FRONT_ROOF = LOADER.create("vehicle/mini_bus/cosmetics/front_roof");

    /* Moped */
    public static final ComponentModel MOPED_BODY = LOADER.create("vehicle/moped/body");
    public static final ComponentModel MOPED_MUD_GUARD = LOADER.create("vehicle/moped/mud_guard");
    public static final ComponentModel MOPED_HANDLES = LOADER.create("vehicle/moped/handles");
    public static final ComponentModel MOPED_COSMETIC_STOCK_SEAT = LOADER.create("vehicle/moped/cosmetics/stock_seat");
    public static final ComponentModel MOPED_COSMETIC_STOCK_TRAY = LOADER.create("vehicle/moped/cosmetics/stock_tray");
    public static final ComponentModel MOPED_COSMETIC_STOCK_FRONT_LIGHT = LOADER.create("vehicle/moped/cosmetics/stock_front_light");

    /* Dirt Bike */
    public static final ComponentModel DIRT_BIKE_BODY = LOADER.create("vehicle/dirt_bike/body");
    public static final ComponentModel DIRT_BIKE_HANDLES = LOADER.create("vehicle/dirt_bike/handles");

    /* Quad Bike */
    public static final ComponentModel QUAD_BIKE_BODY = LOADER.create("vehicle/quad_bike/base");
    public static final ComponentModel QUAD_BIKE_HANDLES = LOADER.create("vehicle/quad_bike/handles");

    /* Go Kart */
    public static final ComponentModel GO_KART_BODY = LOADER.create("vehicle/go_kart/base");
    public static final ComponentModel GO_KART_STEERING_WHEEL = LOADER.create("vehicle/go_kart_steering_wheel");

    /* Sports Plane */
    public static final ComponentModel SPORTS_PLANE_BODY = LOADER.create("vehicle/sports_plane/base");
    public static final ComponentModel SPORTS_PLANE_WINGS = LOADER.create("vehicle/sports_plane/cosmetics/wings");
    public static final ComponentModel SPORTS_PLANE_SEAT = LOADER.create("vehicle/sports_plane/cosmetics/seat");
    public static final ComponentModel SPORTS_PLANE_PROPELLER = LOADER.create("vehicle/sports_plane/cosmetics/propeller");
    public static final ComponentModel SPORTS_PLANE_LEFT_AILERON = LOADER.create("vehicle/sports_plane/cosmetics/left_aileron");
    public static final ComponentModel SPORTS_PLANE_RIGHT_AILERON = LOADER.create("vehicle/sports_plane/cosmetics/right_aileron");

    public static final ComponentModel JET_SKI_BODY = LOADER.create("vehicle/jet_ski_body");
    public static final ComponentModel LAWN_MOWER_BODY = LOADER.create("vehicle/lawn_mower_body");
    public static final ComponentModel SPORTS_PLANE = LOADER.create("vehicle/sports_plane_body");
    public static final ComponentModel SPORTS_PLANE_WING = LOADER.create("vehicle/sports_plane_wing");
    public static final ComponentModel SPORTS_PLANE_WHEEL_COVER = LOADER.create("vehicle/sports_plane_wheel_cover");
    public static final ComponentModel SPORTS_PLANE_LEG = LOADER.create("vehicle/sports_plane_leg");
    public static final ComponentModel GOLF_CART_BODY = LOADER.create("vehicle/golf_cart_body");
    public static final ComponentModel OFF_ROADER_BODY = LOADER.create("vehicle/off_roader_body");
    public static final ComponentModel TRACTOR = LOADER.create("vehicle/tractor_body");
    public static final ComponentModel VEHICLE_TRAILER = LOADER.create("vehicle/trailer_body");
    public static final ComponentModel STORAGE_TRAILER = LOADER.create("vehicle/trailer_chest_body");
    public static final ComponentModel SEEDER_TRAILER = LOADER.create("vehicle/trailer_seeder_body");
    public static final ComponentModel FERTILIZER_TRAILER = LOADER.create("vehicle/trailer_fertilizer_body");
    public static final ComponentModel FLUID_TRAILER = LOADER.create("vehicle/trailer_fluid_body");

    public static final ComponentModel VEHICLE_CRATE_SIDE = LOADER.create("vehicle/vehicle_crate_panel_side");
    public static final ComponentModel VEHICLE_CRATE_TOP = LOADER.create("vehicle/vehicle_crate_panel_top");
    public static final ComponentModel JACK_PISTON_HEAD = LOADER.create("vehicle/jack_piston_head");
    public static final ComponentModel SEED_SPIKER = LOADER.create("vehicle/seed_spiker");
    public static final ComponentModel NOZZLE = LOADER.create("vehicle/nozzle");
    public static final ComponentModel TOW_BAR = LOADER.create("vehicle/tow_bar");
    public static final ComponentModel BIG_TOW_BAR = LOADER.create("vehicle/big_tow_bar");
    public static final ComponentModel FUEL_DOOR_CLOSED = LOADER.create("vehicle/fuel_door_closed");
    public static final ComponentModel FUEL_DOOR_OPEN = LOADER.create("vehicle/fuel_door_open");
    public static final ComponentModel SMALL_FUEL_DOOR_CLOSED = LOADER.create("vehicle/small_fuel_door_closed");
    public static final ComponentModel SMALL_FUEL_DOOR_OPEN = LOADER.create("vehicle/small_fuel_door_open");
    public static final ComponentModel KEY_HOLE = LOADER.create("vehicle/key_hole");
    public static final ComponentModel SOFA_HELICOPTER_ARM = LOADER.create("vehicle/sofa_helicopter_arm");
    public static final ComponentModel SOFA_HELICOPTER_SKID = LOADER.create("vehicle/sofa_helicopter_skid");

    // Special use to reference existing models
    public static final ComponentModel RED_SOFA = new ComponentModel(new ModelResourceLocation("cfm:red_sofa", "inventory"));
}
