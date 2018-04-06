package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Author: MrCrayfish
 */
public class EntityATV extends EntityColoredVehicle
{
    //TODO make it so vehicle base can set properties
    public EntityATV(World worldIn)
    {
        super(worldIn);
        this.setMaxSpeed(15);
        this.setSize(1.5F, 1.5F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public SoundEvent getMovingSound()
    {
        return ModSounds.ATV_ENGINE_MONO;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public SoundEvent getRidingSound()
    {
        return ModSounds.ATV_ENGINE_STEREO;
    }

    @Override
    public double getMountedYOffset()
    {
        return 9 * 0.0625;
    }
}
