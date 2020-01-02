package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.fluid.BlazeJuice;
import com.mrcrayfish.vehicle.fluid.EnderSap;
import com.mrcrayfish.vehicle.fluid.Fuelium;
import net.minecraft.fluid.Fluid;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

/**
 * Author: MrCrayfish
 */
@ObjectHolder(Reference.MOD_ID)
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModFluids
{
    public static final Fluid FUELIUM = null;
    public static final Fluid FLOWING_FUELIUM = null;
    public static final Fluid ENDER_SAP = null;
    public static final Fluid FLOWING_ENDER_SAP = null;
    public static final Fluid BLAZE_JUICE = null;
    public static final Fluid FLOWING_BLAZE_JUICE = null;

    @SubscribeEvent
    @SuppressWarnings("unused")
    public static void register(final RegistryEvent.Register<Fluid> event)
    {
        registerFluid(event.getRegistry(), new Fuelium.Source(), new Fuelium.Flowing());
        registerFluid(event.getRegistry(), new EnderSap.Source(), new EnderSap.Flowing());
        registerFluid(event.getRegistry(), new BlazeJuice.Source(), new BlazeJuice.Flowing());
    }

    private static void registerFluid(IForgeRegistry<Fluid> registry, Fluid still, Fluid flowing)
    {
        registry.register(still);
        registry.register(flowing);
    }
}
