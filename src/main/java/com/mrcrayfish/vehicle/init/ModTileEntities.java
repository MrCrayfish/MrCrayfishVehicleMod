package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.tileentity.TileEntityFluidPipe;
import com.mrcrayfish.vehicle.tileentity.TileEntityFluidPump;
import com.mrcrayfish.vehicle.tileentity.TileEntityFuelDrum;
import com.mrcrayfish.vehicle.tileentity.TileEntityFluidExtractor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Author: MrCrayfish
 */
public class ModTileEntities
{
    public static void register()
    {
        GameRegistry.registerTileEntity(TileEntityFluidExtractor.class, new ResourceLocation(Reference.MOD_ID, "refinery"));
        GameRegistry.registerTileEntity(TileEntityFluidPipe.class, new ResourceLocation(Reference.MOD_ID, "fluid_pipe"));
        GameRegistry.registerTileEntity(TileEntityFluidPump.class, new ResourceLocation(Reference.MOD_ID, "fluid_pump"));
        GameRegistry.registerTileEntity(TileEntityFuelDrum.class, new ResourceLocation(Reference.MOD_ID, "fuel_drum"));
    }
}
