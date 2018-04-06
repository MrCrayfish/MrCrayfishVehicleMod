package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.item.ItemColoredVehicleBody;
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

    static
    {
        WHEEL = new ItemPart("wheel");
        ATV_BODY = new ItemColoredVehicleBody("atv");
        ATV_HANDLE_BAR = new ItemPart("handle_bar");
    }

    public static void register()
    {
        register(WHEEL);
        register(ATV_BODY);
        register(ATV_HANDLE_BAR);
    }

    private static void register(Item item)
    {
        RegistrationHandler.Items.add(item);
    }
}
