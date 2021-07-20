package com.mrcrayfish.vehicle.block;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.tileentity.IndustrialFuelDrumTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class IndustrialFuelDrumBlock extends FuelDrumBlock
{
    @Override
    public int getCapacity()
    {
        return Config.SERVER.industrialFuelDrumCapacity.get();
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return new IndustrialFuelDrumTileEntity();
    }
}
