package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.world.storage.loot.functions.CopyFluidTanks;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.functions.ILootFunction;
import net.minecraft.loot.functions.LootFunctionManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Author: MrCrayfish
 */
public class ModLootFunctions
{
    public static final LootFunctionType COPY_FLUID_TANKS = register("copy_fluid_tanks", new CopyFluidTanks.Serializer());

    private static LootFunctionType register(String id, ILootSerializer<? extends ILootFunction> serializer)
    {
        return Registry.register(Registry.LOOT_FUNCTION_TYPE, new ResourceLocation(Reference.MOD_ID, id), new LootFunctionType(serializer));
    }
}