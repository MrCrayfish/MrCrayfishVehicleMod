package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.item.ItemColoredVehicleBody;
import com.mrcrayfish.vehicle.item.ItemPart;
import net.minecraft.item.Item;

/**
 * Author: MrCrayfish
 */
public class ModItems
{
    public static final Item BODY;
    public static final Item WHEEL;
    public static final Item HANDLE_BAR;

    static
    {
        BODY = new ItemColoredVehicleBody("atv");
        WHEEL = new ItemPart("wheel");
        HANDLE_BAR = new ItemPart("handle_bar");
    }

    public static void register()
    {
        register(BODY);
        register(WHEEL);
        register(HANDLE_BAR);
    }

    private static void register(Item item)
    {
        RegistrationHandler.Items.add(item);
    }
}
