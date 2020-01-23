package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.client.EntityRaytracer.IEntityRaytraceable;
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class ATVEntity extends LandVehicleEntity implements IEntityRaytraceable
{
    public ATVEntity(World worldIn)
    {
        super(ModEntities.ATV, worldIn);
        this.setMaxSpeed(15);
        this.setFuelCapacity(20000F);
    }

    @Override
    public FuelPortType getFuelPortType()
    {
        return FuelPortType.SMALL;
    }

    @Override
    public SoundEvent getMovingSound()
    {
        return ModSounds.ATV_ENGINE_MONO;
    }

    @Override
    public SoundEvent getRidingSound()
    {
        return ModSounds.ATV_ENGINE_STEREO;
    }

    @Override
    public double getMountedYOffset()
    {
        return 9.5 * 0.0625;
    }

    @Override
    public EngineType getEngineType()
    {
        return EngineType.SMALL_MOTOR;
    }

    @Override
    public boolean canBeColored()
    {
        return true;
    }

    @Override
    public boolean canTowTrailer()
    {
        return true;
    }

    @Override
    protected boolean canFitPassenger(Entity passenger)
    {
        return this.getPassengers().size() < 2;
    }

    @Override
    public void updatePassenger(Entity passenger)
    {
        if(this.isPassenger(passenger))
        {
            float offset = 0.0F;
            float yOffset = (float) ((!this.isAlive() ? 0.01D : this.getMountedYOffset()) + passenger.getYOffset());

            if(this.getPassengers().size() > 1)
            {
                int index = this.getPassengers().indexOf(passenger);
                if(index > 0)
                {
                    offset += index * -0.625F;
                    yOffset += 0.1F;
                }
            }

            Vec3d vec3d = (new Vec3d((double) offset, 0.0D, 0.0D)).rotateYaw(-(this.rotationYaw - additionalYaw) * 0.017453292F - ((float) Math.PI / 2F));
            passenger.setPosition(this.getPosX() + vec3d.x, this.getPosY() + (double) yOffset, this.getPosZ() + vec3d.z);
            passenger.rotationYaw -= deltaYaw;
            passenger.setRotationYawHead(passenger.rotationYaw);
            this.applyYawToEntity(passenger);
        }
    }
}
