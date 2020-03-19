package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.client.EntityRaytracer.IEntityRaytraceable;
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.EntityPlane;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class EntityBath extends EntityPlane implements IEntityRaytraceable
{
    public EntityBath(World worldIn)
    {
        super(worldIn);
        this.setFuelConsumption(0.0F);
    }

    @Override
    public SoundEvent getMovingSound()
    {
        return null;
    }

    @Override
    public SoundEvent getRidingSound()
    {
        return null;
    }

    @Override
    public void updateVehicle()
    {
        if(this.isFlying() && this.getControllingPassenger() != null)
        {
            for(int i = 0; i < 4; i++)
            {
                world.spawnParticle(EnumParticleTypes.DRIP_WATER, posX - 0.25 + 0.5 * rand.nextGaussian(), posY + 0.5 * rand.nextGaussian(), posZ - 0.25 + 0.5 * rand.nextGaussian(), 0, 0, 0, 0);
            }
        }
    }

    @Override
    public EngineType getEngineType()
    {
        return EngineType.NONE;
    }

    @Override
    public boolean canBeColored()
    {
        return false;
    }

    @Override
    public boolean isLockable()
    {
        return false;
    }
}
