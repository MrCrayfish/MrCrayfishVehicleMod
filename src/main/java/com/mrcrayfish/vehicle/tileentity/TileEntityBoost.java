package com.mrcrayfish.vehicle.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

/**
 * Author: MrCrayfish
 */
public class TileEntityBoost extends TileEntitySynced
{
    private float speedMultiplier;

    public TileEntityBoost() {}

    public TileEntityBoost(float defaultSpeedMultiplier)
    {
        this.speedMultiplier = defaultSpeedMultiplier;
    }

    public float getSpeedMultiplier()
    {
        return speedMultiplier;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        if(compound.hasKey("speedMultiplier", Constants.NBT.TAG_FLOAT))
        {
            this.speedMultiplier = compound.getFloat("speedMultiplier");
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setFloat("speedMultiplier", this.speedMultiplier);
        return compound;
    }
}

