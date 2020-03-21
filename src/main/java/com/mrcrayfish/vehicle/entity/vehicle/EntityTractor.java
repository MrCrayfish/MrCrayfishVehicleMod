package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.EntityLandVehicle;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class EntityTractor extends EntityLandVehicle implements EntityRaytracer.IEntityRaytraceable
{
    public EntityTractor(World worldIn)
    {
        super(worldIn);
        this.setMaxSpeed(6);
        this.setTurnSensitivity(3);
        this.setSize(1.5F, 1.5F);
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
}
