package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.fluid.BlazeJuice;
import com.mrcrayfish.vehicle.fluid.EnderSap;
import com.mrcrayfish.vehicle.fluid.Fuelium;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Author: MrCrayfish
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModFluids
{
    public static final Fluid FUELIUM = new Fuelium.Source();
    public static final FlowingFluid FLOWING_FUELIUM = new Fuelium.Flowing();
    public static final Fluid ENDER_SAP = new EnderSap.Source();
    public static final FlowingFluid FLOWING_ENDER_SAP = new EnderSap.Flowing();
    public static final Fluid BLAZE_JUICE = new BlazeJuice.Source();
    public static final FlowingFluid FLOWING_BLAZE_JUICE = new BlazeJuice.Flowing();

    @SubscribeEvent
    @SuppressWarnings("unused")
    public static void register(final RegistryEvent.Register<Fluid> event)
    {
        IForgeRegistry<Fluid> registry = event.getRegistry();
        registry.register(FUELIUM);
        registry.register(FLOWING_FUELIUM);
        registry.register(ENDER_SAP);
        registry.register(FLOWING_ENDER_SAP);
        registry.register(BLAZE_JUICE);
        registry.register(FLOWING_BLAZE_JUICE);
    }
}
