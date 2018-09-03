package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.tileentity.TileEntityFluidPipe;
import com.mrcrayfish.vehicle.tileentity.TileEntityRefinery;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Author: MrCrayfish
 */
public class ModTileEntities
{
    public static void register()
    {
        GameRegistry.registerTileEntity(TileEntityRefinery.class, new ResourceLocation(Reference.MOD_ID, "refinery"));
        GameRegistry.registerTileEntity(TileEntityFluidPipe.class, new ResourceLocation(Reference.MOD_ID, "fluid_pipe"));
    }
}
