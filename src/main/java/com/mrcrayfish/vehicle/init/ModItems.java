package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.item.*;
import net.minecraft.item.Item;

/**
 * Author: MrCrayfish
 */
public class ModItems
{
    public static final Item PANEL;
    public static final Item WHEEL;
    public static final Item ATV_BODY;
    public static final Item ATV_HANDLE_BAR;
    public static final Item DUNE_BUGGY_BODY;
    public static final Item DUNE_BUGGY_HANDLE_BAR;
    public static final Item DUNE_BUGGY_WHEEL;
    public static final Item GO_KART_BODY;
    public static final Item GO_KART_STEERING_WHEEL;
    public static final Item SHOPPING_CART_BODY;
    public static final Item MINI_BIKE_BODY;
    public static final Item MINI_BIKE_HANDLE_BAR;
    public static final Item BUMPER_CAR_BODY;
    public static final Item JET_SKI_BODY;
    public static final Item SPEED_BOAT_BODY;
    public static final Item ALUMINUM_BOAT_BODY;
    public static final Item SMART_CAR_BODY;
    public static final Item LAWN_MOWER_BODY;
    public static final Item MOPED_BODY;
    public static final Item MOPED_MUD_GUARD;
    public static final Item MOPED_HANDLE_BAR;
    public static final Item SPORTS_PLANE_BODY;
    public static final Item SPORTS_PLANE_WING;
    public static final Item SPORTS_PLANE_WHEEL_COVER;
    public static final Item SPORTS_PLANE_LEG;
    public static final Item SPORTS_PLANE_PROPELLER;
    public static final Item GOLF_CART_BODY;
    public static final Item OFF_ROADER_BODY;

    public static final Item TRAILER_BODY;
    public static final Item TOW_BAR;

    public static final Item ENGINE;
    public static final Item SPRAY_CAN;
    public static final Item JERRY_CAN;
    public static final Item INDUSTRIAL_JERRY_CAN;

    public static final Item FUEL_PORT_CLOSED;
    public static final Item FUEL_PORT_BODY;
    public static final Item FUEL_PORT_LID;
    public static final Item FUEL_PORT_2_CLOSED;
    public static final Item FUEL_PORT_2_PIPE;
    
    public static final Item WRENCH;
    public static final Item KEY;
    public static final Item KEY_PORT;

    //Parts used for rendering
    public static final Item MODELS;
    public static final Item COUCH_HELICOPTER_ARM;
    public static final Item COUCH_HELICOPTER_SKID;

    static
    {
        PANEL = new ItemPart("panel").setCreativeTab(VehicleMod.CREATIVE_TAB);
        WHEEL = new ItemPart("wheel").setCreativeTab(VehicleMod.CREATIVE_TAB);
        ATV_BODY = new ItemPart("atv_body").setColored();
        ATV_HANDLE_BAR = new ItemPart("handle_bar");
        DUNE_BUGGY_BODY = new ItemPart("dune_buggy_body").setColored();
        DUNE_BUGGY_HANDLE_BAR = new ItemPart("dune_buggy_handle_bar");
        DUNE_BUGGY_WHEEL = new ItemPart("dune_buggy_wheel");
        GO_KART_BODY = new ItemPart("go_kart_body").setColored();
        GO_KART_STEERING_WHEEL = new ItemPart("go_kart_steering_wheel");
        SHOPPING_CART_BODY = new ItemPart("shopping_cart_body");
        MINI_BIKE_BODY = new ItemPart("mini_bike_body").setColored();
        MINI_BIKE_HANDLE_BAR = new ItemPart("mini_bike_handle_bar").setColored();
        BUMPER_CAR_BODY = new ItemPart("bumper_car_body").setColored();
        JET_SKI_BODY = new ItemPart("jet_ski_body").setColored();
        SPEED_BOAT_BODY = new ItemPart("speed_boat_body").setColored();
        ALUMINUM_BOAT_BODY = new ItemPart("aluminum_boat_body").setColored();
        SMART_CAR_BODY = new ItemPart("smart_car_body").setColored();
        LAWN_MOWER_BODY = new ItemPart("lawn_mower_body").setColored();
        MOPED_BODY = new ItemPart("moped_body").setColored();
        MOPED_MUD_GUARD = new ItemPart("moped_mud_guard").setColored();
        MOPED_HANDLE_BAR = new ItemPart("moped_handle_bar").setColored();
        SPORTS_PLANE_BODY = new ItemPart("sports_plane_body").setColored();
        SPORTS_PLANE_WING = new ItemPart("sports_plane_wing").setColored();
        SPORTS_PLANE_WHEEL_COVER = new ItemPart("sports_plane_wheel_cover").setColored();
        SPORTS_PLANE_LEG = new ItemPart("sports_plane_leg");
        SPORTS_PLANE_PROPELLER = new ItemPart("sports_plane_propeller").setColored();
        GOLF_CART_BODY = new ItemPart("golf_cart_body").setColored();
        OFF_ROADER_BODY = new ItemPart("off_roader_body").setColored();

        TRAILER_BODY = new ItemPart("trailer_body").setColored();
        TOW_BAR = new ItemPart("tow_bar");

        ENGINE = new ItemEngine("small_engine");
        SPRAY_CAN = new ItemSprayCan();
        JERRY_CAN = new ItemJerryCan("jerry_can", 5000, 100);
        INDUSTRIAL_JERRY_CAN = new ItemJerryCan("industrial_jerry_can", 15000, 150);

        FUEL_PORT_CLOSED = new ItemPart("fuel_port_closed").setColored();
        FUEL_PORT_BODY = new ItemPart("fuel_port_body").setColored();
        FUEL_PORT_LID = new ItemPart("fuel_port_lid").setColored();
        FUEL_PORT_2_CLOSED = new ItemPart("fuel_port_2_closed");
        FUEL_PORT_2_PIPE = new ItemPart("fuel_port_2_pipe");

        WRENCH = new ItemWrench();
        KEY = new ItemKey();
        KEY_PORT = new ItemPart("key_hole").setColored();

        COUCH_HELICOPTER_ARM = new ItemPart("couch_helicopter_arm");
        COUCH_HELICOPTER_SKID = new ItemPart("couch_helicopter_skid");
        MODELS = new ItemModels();
    }

    public static void register()
    {
        register(ENGINE);
        register(PANEL);
        register(WHEEL);
        register(ATV_BODY);
        register(ATV_HANDLE_BAR);
        register(DUNE_BUGGY_BODY);
        register(DUNE_BUGGY_HANDLE_BAR);
        register(DUNE_BUGGY_WHEEL);
        register(GO_KART_BODY);
        register(GO_KART_STEERING_WHEEL);
        register(SHOPPING_CART_BODY);
        register(MINI_BIKE_BODY);
        register(MINI_BIKE_HANDLE_BAR);
        register(BUMPER_CAR_BODY);
        register(JET_SKI_BODY);
        register(SPEED_BOAT_BODY);
        register(ALUMINUM_BOAT_BODY);
        register(SMART_CAR_BODY);
        register(LAWN_MOWER_BODY);
        register(MOPED_BODY);
        register(MOPED_MUD_GUARD);
        register(MOPED_HANDLE_BAR);
        register(SPORTS_PLANE_BODY);
        register(SPORTS_PLANE_WING);
        register(SPORTS_PLANE_WHEEL_COVER);
        register(SPORTS_PLANE_LEG);
        register(SPORTS_PLANE_PROPELLER);
        register(GOLF_CART_BODY);
        register(OFF_ROADER_BODY);

        register(TRAILER_BODY);
        register(TOW_BAR);

        register(SPRAY_CAN);
        register(JERRY_CAN);
        register(INDUSTRIAL_JERRY_CAN);

        register(FUEL_PORT_CLOSED);
        register(FUEL_PORT_BODY);
        register(FUEL_PORT_LID);
        register(FUEL_PORT_2_CLOSED);
        register(FUEL_PORT_2_PIPE);

        register(WRENCH);
        register(KEY);
        register(KEY_PORT);

        register(MODELS);
        register(COUCH_HELICOPTER_ARM);
        register(COUCH_HELICOPTER_SKID);
    }

    private static void register(Item item)
    {
        RegistrationHandler.Items.add(item);
    }
}
