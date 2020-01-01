package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.client.EntityRaytracer;
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
public class TractorEntity extends LandVehicleEntity implements EntityRaytracer.IEntityRaytraceable
{
    public TractorEntity(World worldIn)
    {
        super(ModEntities.TRACTOR, worldIn);
        this.setMaxSpeed(6);
        this.setTurnSensitivity(3);
    }

    @Override
    public SoundEvent getMovingSound()
    {
        return ModSounds.TRACTOR_ENGINE_MONO;
    }

    @Override
    public SoundEvent getRidingSound()
    {
        return ModSounds.TRACTOR_ENGINE_STEREO;
    }

    @Override
    public float getMinEnginePitch()
    {
        return 0.8F;
    }

    @Override
    public float getMaxEnginePitch()
    {
        return 1.6F;
    }

    @Override
    public EngineType getEngineType()
    {
        return EngineType.LARGE_MOTOR;
    }

    @Override
    public FuelPortType getFuelPortType()
    {
        return FuelPortType.DEFAULT;
    }

    @Override
    public double getMountedYOffset()
    {
        return 12 * 0.0625;
    }

    @Override
    public boolean shouldRenderEngine()
    {
        return true;
    }

    @Override
    public boolean shouldShowEngineSmoke()
    {
        return true;
    }

    @Override
    public Vec3d getEngineSmokePosition()
    {
        return new Vec3d(-0.125, 1.9375, 1.125);
    }

    @Override
    public boolean canTowTrailer()
    {
        return true;
    }

    @Override
    public boolean canMountTrailer()
    {
        return false;
    }

    @Override
    public boolean canBeColored()
    {
        return true;
    }

    @Override
    public void updatePassenger(Entity passenger)
    {
        if(this.isPassenger(passenger))
        {
            float yOffset = (float) ((!this.isAlive() ? 0.01D : this.getMountedYOffset()) + passenger.getYOffset());
            float zOffset = -10F * 0.0625F;
            Vec3d vec3d = new Vec3d(zOffset, yOffset, 0).rotateYaw(-(this.rotationYaw - additionalYaw) * 0.017453292F - ((float) Math.PI / 2F));
            passenger.setPosition(this.func_226277_ct_() + vec3d.x, this.func_226278_cu_() + vec3d.y, this.func_226281_cx_() + vec3d.z);
            passenger.rotationYaw -= deltaYaw;
            passenger.setRotationYawHead(passenger.rotationYaw);
            this.applyYawToEntity(passenger);
        }
    }
}
