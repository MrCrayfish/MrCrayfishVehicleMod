package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.fluid.BlazeJuice;
import com.mrcrayfish.vehicle.fluid.EnderSap;
import com.mrcrayfish.vehicle.fluid.Fuelium;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Author: MrCrayfish
 */
public class ModFluids
{
    public static final DeferredRegister<Fluid> REGISTER = DeferredRegister.create(ForgeRegistries.FLUIDS, Reference.MOD_ID);

    public static final RegistryObject<Fluid> FUELIUM = REGISTER.register("fuelium", Fuelium.Source::new);
    public static final RegistryObject<FlowingFluid> FLOWING_FUELIUM = REGISTER.register("flowing_fuelium", Fuelium.Flowing::new);
    public static final RegistryObject<Fluid> ENDER_SAP = REGISTER.register("ender_sap", EnderSap.Source::new);
    public static final RegistryObject<FlowingFluid> FLOWING_ENDER_SAP = REGISTER.register("flowing_ender_sap", EnderSap.Flowing::new);
    public static final RegistryObject<Fluid> BLAZE_JUICE = REGISTER.register("blaze_juice", BlazeJuice.Source::new);
    public static final RegistryObject<FlowingFluid> FLOWING_BLAZE_JUICE = REGISTER.register("flowing_blaze_juice", BlazeJuice.Flowing::new);
}