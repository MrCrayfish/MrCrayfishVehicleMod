package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.item.*;
import com.mrcrayfish.vehicle.util.ItemNames;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Author: MrCrayfish
 */
@GameRegistry.ObjectHolder(Reference.MOD_ID)
public class ModItems
{
    public static final Item PANEL = null;
    public static final Item WHEEL = null;
    public static final Item ATV_BODY = null;
    public static final Item ATV_HANDLE_BAR = null;
    public static final Item DUNE_BUGGY_BODY = null;
    public static final Item DUNE_BUGGY_HANDLE_BAR = null;
    public static final Item GO_KART_BODY = null;
    public static final Item GO_KART_STEERING_WHEEL = null;
    public static final Item SHOPPING_CART_BODY = null;
    public static final Item MINI_BIKE_BODY = null;
    public static final Item MINI_BIKE_HANDLE_BAR = null;
    public static final Item BUMPER_CAR_BODY = null;
    public static final Item JET_SKI_BODY = null;
    public static final Item SPEED_BOAT_BODY = null;
    public static final Item ALUMINUM_BOAT_BODY = null;
    public static final Item SMART_CAR_BODY = null;
    public static final Item LAWN_MOWER_BODY = null;
    public static final Item MOPED_BODY = null;
    public static final Item MOPED_MUD_GUARD = null;
    public static final Item MOPED_HANDLE_BAR = null;
    public static final Item SPORTS_PLANE_BODY = null;
    public static final Item SPORTS_PLANE_WING = null;
    public static final Item SPORTS_PLANE_WHEEL_COVER = null;
    public static final Item SPORTS_PLANE_LEG = null;
    public static final Item SPORTS_PLANE_PROPELLER = null;
    public static final Item GOLF_CART_BODY = null;
    public static final Item OFF_ROADER_BODY = null;
    public static final Item TRACTOR_BODY = null;
    public static final Item TRAILER_BODY = null;
    public static final Item TOW_BAR = null;
    public static final Item SMALL_ENGINE = null;
    public static final Item LARGE_ENGINE = null;
    public static final Item ELECTRIC_ENGINE = null;
    public static final Item SPRAY_CAN = null;
    public static final Item JERRY_CAN = null;
    public static final Item INDUSTRIAL_JERRY_CAN = null;
    public static final Item FUEL_PORT_CLOSED = null;
    public static final Item FUEL_PORT_BODY = null;
    public static final Item FUEL_PORT_LID = null;
    public static final Item FUEL_PORT_2_CLOSED = null;
    public static final Item FUEL_PORT_2_PIPE = null;
    public static final Item WRENCH = null;
    public static final Item HAMMER = null;
    public static final Item KEY = null;
    public static final Item KEY_HOLE = null;
    public static final Item MODELS = null;
    public static final Item COUCH_HELICOPTER_ARM = null;
    public static final Item COUCH_HELICOPTER_SKID = null;

    public static void register()
    {
        register(new ItemPart(ItemNames.PANEL).setCreativeTab(VehicleMod.CREATIVE_TAB));
        register(new ItemWheel().setColored());
        register(new ItemPart(ItemNames.ATV_BODY).setColored());
        register(new ItemPart(ItemNames.ATV_HANDLE_BAR));
        register(new ItemPart(ItemNames.DUNE_BUGGY_BODY).setColored());
        register(new ItemPart(ItemNames.DUNE_BUGGY_HANDLE_BAR));
        register(new ItemPart(ItemNames.GO_KART_BODY).setColored());
        register(new ItemPart(ItemNames.GO_KART_STEERING_WHEEL));
        register(new ItemPart(ItemNames.SHOPPING_CART_BODY).setColored());
        register(new ItemPart(ItemNames.MINI_BIKE_BODY).setColored());
        register(new ItemPart(ItemNames.MINI_BIKE_HANDLE_BAR).setColored());
        register(new ItemPart(ItemNames.BUMPER_CAR_BODY).setColored());
        register(new ItemPart(ItemNames.JET_SKI_BODY).setColored());
        register(new ItemPart(ItemNames.SPEED_BOAT_BODY).setColored());
        register(new ItemPart(ItemNames.ALUMINUM_BOAT_BODY).setColored());
        register(new ItemPart(ItemNames.SMART_CAR_BODY).setColored());
        register(new ItemPart(ItemNames.LAWN_MOWER_BODY).setColored());
        register(new ItemPart(ItemNames.MOPED_BODY).setColored());
        register(new ItemPart(ItemNames.MOPED_MUD_GUARD).setColored());
        register(new ItemPart(ItemNames.MOPED_HANDLE_BAR).setColored());
        register(new ItemPart(ItemNames.SPORTS_PLANE_BODY).setColored());
        register(new ItemPart(ItemNames.SPORTS_PLANE_WING).setColored());
        register(new ItemPart(ItemNames.SPORTS_PLANE_WHEEL_COVER).setColored());
        register(new ItemPart(ItemNames.SPORTS_PLANE_LEG));
        register(new ItemPart(ItemNames.SPORTS_PLANE_PROPELLER).setColored());
        register(new ItemPart(ItemNames.GOLF_CART_BODY).setColored());
        register(new ItemPart(ItemNames.OFF_ROADER_BODY).setColored());
        register(new ItemPart(ItemNames.TRACTOR_BODY).setColored());
        register(new ItemPart(ItemNames.TRAILER_BODY).setColored());
        register(new ItemPart(ItemNames.TOW_BAR));
        register(new ItemEngine(ItemNames.SMALL_ENGINE, EngineType.SMALL_MOTOR));
        register(new ItemEngine(ItemNames.LARGE_ENGINE, EngineType.LARGE_MOTOR));
        register(new ItemEngine(ItemNames.ELECTRIC_ENGINE, EngineType.ELECTRIC_MOTOR));
        register(new ItemSprayCan());
        register(new ItemJerryCan(ItemNames.JERRY_CAN, 5000, 100));
        register(new ItemJerryCan(ItemNames.INDUSTRIAL_JERRY_CAN, 15000, 150));
        register(new ItemPart(ItemNames.FUEL_PORT_CLOSED).setColored());
        register(new ItemPart(ItemNames.FUEL_PORT_BODY).setColored());
        register(new ItemPart(ItemNames.FUEL_PORT_LID).setColored());
        register(new ItemPart(ItemNames.FUEL_PORT_2_CLOSED));
        register(new ItemPart(ItemNames.FUEL_PORT_2_PIPE));
        register(new ItemVehicleTool(ItemNames.WRENCH));
        register(new ItemHammer());
        register(new ItemKey());
        register(new ItemPart(ItemNames.KEY_HOLE).setColored());
        register(new ItemPart(ItemNames.COUCH_HELICOPTER_ARM));
        register(new ItemPart(ItemNames.COUCH_HELICOPTER_SKID));
        register(new ItemModels());
    }

    private static void register(Item item)
    {
        RegistrationHandler.Items.add(item);
    }
}
