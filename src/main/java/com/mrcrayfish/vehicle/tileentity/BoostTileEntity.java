package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.init.ModTileEntities;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;

/**
 * Author: MrCrayfish
 */
public class BoostTileEntity extends TileEntitySynced
{
    private float speedMultiplier;

    public BoostTileEntity()
    {
        super(ModTileEntities.BOOST.get());
    }

    public BoostTileEntity(float defaultSpeedMultiplier)
    {
        super(ModTileEntities.BOOST.get());
        this.speedMultiplier = defaultSpeedMultiplier;
    }

    public float getSpeedMultiplier()
    {
        return speedMultiplier;
    }

    @Override
    public void read(CompoundNBT compound)
    {
        super.read(compound);
        if(compound.contains("SpeedMultiplier", Constants.NBT.TAG_FLOAT))
        {
            this.speedMultiplier = compound.getFloat("SpeedMultiplier");
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound)
    {
        compound.putFloat("SpeedMultiplier", this.speedMultiplier);
        return super.write(compound);
    }
}

