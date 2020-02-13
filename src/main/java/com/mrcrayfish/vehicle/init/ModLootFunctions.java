package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.world.storage.loot.functions.CopyFluidTanks;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;

/**
 * Author: MrCrayfish
 */
public class ModLootFunctions
{
    public static void register()
    {
        LootFunctionManager.registerFunction(new CopyFluidTanks.Serializer());
    }
}