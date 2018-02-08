package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.item.ItemPart;
import net.minecraft.item.Item;

/**
 * Author: MrCrayfish
 */
public class ModItems
{
    public static final Item BODY;
    public static final Item WHEEL;

    static
    {
        BODY = new ItemPart("body");
        WHEEL = new ItemPart("wheel");
    }

    public static void register()
    {
        register(BODY);
        register(WHEEL);
    }

    private static void register(Item item)
    {
        RegistrationHandler.Items.add(item);
    }
}
