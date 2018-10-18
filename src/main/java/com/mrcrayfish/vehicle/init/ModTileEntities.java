package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.tileentity.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Author: MrCrayfish
 */
public class ModTileEntities
{
    public static void register()
    {
        GameRegistry.registerTileEntity(TileEntityFluidExtractor.class, new ResourceLocation(Reference.MOD_ID, "fluid_extractor"));
        GameRegistry.registerTileEntity(TileEntityFluidPipe.class, new ResourceLocation(Reference.MOD_ID, "fluid_pipe"));
        GameRegistry.registerTileEntity(TileEntityFluidPump.class, new ResourceLocation(Reference.MOD_ID, "fluid_pump"));
        GameRegistry.registerTileEntity(TileEntityFuelDrum.class, new ResourceLocation(Reference.MOD_ID, "fuel_drum"));
        GameRegistry.registerTileEntity(TileEntityFluidMixer.class, new ResourceLocation(Reference.MOD_ID, "fluid_mixer"));
        GameRegistry.registerTileEntity(TileEntityVehicleCrate.class, new ResourceLocation(Reference.MOD_ID, "vehicle_crate"));
    }
}
