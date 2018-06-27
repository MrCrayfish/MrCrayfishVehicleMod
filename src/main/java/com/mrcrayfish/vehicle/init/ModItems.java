package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.item.ItemEngine;
import com.mrcrayfish.vehicle.item.ItemPart;
import com.mrcrayfish.vehicle.item.ItemSprayCan;
import net.minecraft.item.Item;

/**
 * Author: MrCrayfish
 */
public class ModItems
{
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

    public static final Item ENGINE;
    public static final Item SPRAY_CAN;

    static
    {
        WHEEL = new ItemPart("wheel");
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

        ENGINE = new ItemEngine("small_engine");
        SPRAY_CAN = new ItemSprayCan();
    }

    public static void register()
    {
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

        register(ENGINE);
        register(SPRAY_CAN);
    }

    private static void register(Item item)
    {
        RegistrationHandler.Items.add(item);
    }
}
