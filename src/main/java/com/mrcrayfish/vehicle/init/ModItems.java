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
    public static final Item SMALL_ENGINE = null;
    public static final Item LARGE_ENGINE = null;
    public static final Item ELECTRIC_ENGINE = null;
    public static final Item SPRAY_CAN = null;
    public static final Item JERRY_CAN = null;
    public static final Item INDUSTRIAL_JERRY_CAN = null;
    public static final Item WRENCH = null;
    public static final Item HAMMER = null;
    public static final Item KEY = null;
    public static final Item MODELS = null;

    public static void register()
    {
        register(new ItemPart(ItemNames.PANEL).setCreativeTab(VehicleMod.CREATIVE_TAB));
        register(new ItemWheel().setColored());
        register(new ItemEngine(ItemNames.SMALL_ENGINE, EngineType.SMALL_MOTOR));
        register(new ItemEngine(ItemNames.LARGE_ENGINE, EngineType.LARGE_MOTOR));
        register(new ItemEngine(ItemNames.ELECTRIC_ENGINE, EngineType.ELECTRIC_MOTOR));
        register(new ItemSprayCan());
        register(new ItemJerryCan(ItemNames.JERRY_CAN, 5000, 100));
        register(new ItemJerryCan(ItemNames.INDUSTRIAL_JERRY_CAN, 15000, 150));
        register(new ItemVehicleTool(ItemNames.WRENCH));
        register(new ItemHammer());
        register(new ItemKey());
        register(new ItemModels());
    }

    private static void register(Item item)
    {
        RegistrationHandler.Items.add(item);
    }
}
