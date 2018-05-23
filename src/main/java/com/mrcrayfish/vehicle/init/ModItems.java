package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.item.ItemColoredPart;
import com.mrcrayfish.vehicle.item.ItemEngine;
import com.mrcrayfish.vehicle.item.ItemPart;
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

    public static final Item ENGINE;

    static
    {
        WHEEL = new ItemPart("wheel");
        ATV_BODY = new ItemColoredPart("atv");
        ATV_HANDLE_BAR = new ItemPart("handle_bar");
        DUNE_BUGGY_BODY = new ItemPart("dune_buggy_body");
        DUNE_BUGGY_HANDLE_BAR = new ItemPart("dune_buggy_handle_bar");
        DUNE_BUGGY_WHEEL = new ItemPart("dune_buggy_wheel");
        GO_KART_BODY = new ItemColoredPart("go_kart");
        GO_KART_STEERING_WHEEL = new ItemPart("go_kart_steering_wheel");
        SHOPPING_CART_BODY = new ItemPart("shopping_cart_body");
        MINI_BIKE_BODY = new ItemColoredPart("mini_bike_body");
        MINI_BIKE_HANDLE_BAR = new ItemColoredPart("mini_bike_handle_bar");
        BUMPER_CAR_BODY = new ItemColoredPart("bumper_car_body");

        ENGINE = new ItemEngine("small_engine");
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

        register(ENGINE);
    }

    private static void register(Item item)
    {
        RegistrationHandler.Items.add(item);
    }
}
