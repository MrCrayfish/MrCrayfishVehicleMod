package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

/**
 * Author: MrCrayfish
 */
public class ModFluids
{
    public static final Fluid FUELIUM;

    static
    {
        FUELIUM = new Fluid("fuelium", new ResourceLocation(Reference.MOD_ID, "fluids/fuelium_still"), new ResourceLocation(Reference.MOD_ID, "fluids/fuelium_flow")).setDensity(900).setViscosity(900);
    }

    public static void register()
    {
        FluidRegistry.addBucketForFluid(FUELIUM);
    }
}
