package com.mrcrayfish.vehicle.init;

import java.awt.Color;

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
    public static final Fluid ENDER_SAP;
    public static final Fluid BLAZE_JUICE;

    static
    {
        FUELIUM = new Fluid("fuelium", new ResourceLocation(Reference.MOD_ID, "fluids/fuelium_still"), new ResourceLocation(Reference.MOD_ID, "fluids/fuelium_flow")).setDensity(900).setViscosity(900).setColor(new Color(148, 242, 45));
        ENDER_SAP = new Fluid("ender_sap", new ResourceLocation(Reference.MOD_ID, "fluids/ender_sap_still"), new ResourceLocation(Reference.MOD_ID, "fluids/ender_sap_flow")).setViscosity(3000).setColor(new Color(10, 93, 80));
        BLAZE_JUICE = new Fluid("blaze_juice", new ResourceLocation(Reference.MOD_ID, "fluids/blaze_juice_still"), new ResourceLocation(Reference.MOD_ID, "fluids/blaze_juice_flow")).setViscosity(800).setColor(new Color(254, 198, 0));
    }

    public static void register()
    {
        FluidRegistry.addBucketForFluid(FUELIUM);
        FluidRegistry.addBucketForFluid(ENDER_SAP);
        FluidRegistry.addBucketForFluid(BLAZE_JUICE);
    }
}
