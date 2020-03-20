package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.fluid.BlazeJuice;
import com.mrcrayfish.vehicle.fluid.EnderSap;
import com.mrcrayfish.vehicle.fluid.Fuelium;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Author: MrCrayfish
 */
public class ModFluids
{
    public static final DeferredRegister<Fluid> FLUIDS = new DeferredRegister<>(ForgeRegistries.FLUIDS, Reference.MOD_ID);

    public static final RegistryObject<Fluid> FUELIUM = FLUIDS.register("fuelium", Fuelium.Source::new);
    public static final RegistryObject<FlowingFluid> FLOWING_FUELIUM = FLUIDS.register("flowing_fuelium", Fuelium.Flowing::new);
    public static final RegistryObject<Fluid> ENDER_SAP = FLUIDS.register("ender_sap", EnderSap.Source::new);
    public static final RegistryObject<FlowingFluid> FLOWING_ENDER_SAP = FLUIDS.register("flowing_ender_sap", EnderSap.Flowing::new);
    public static final RegistryObject<Fluid> BLAZE_JUICE = FLUIDS.register("blaze_juice", BlazeJuice.Source::new);
    public static final RegistryObject<FlowingFluid> FLOWING_BLAZE_JUICE = FLUIDS.register("flowing_blaze_juice", BlazeJuice.Flowing::new);
}