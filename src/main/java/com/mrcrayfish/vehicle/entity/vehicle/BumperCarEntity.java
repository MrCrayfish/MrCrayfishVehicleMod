package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.client.EntityRaytracer.IEntityRaytraceable;
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class BumperCarEntity extends LandVehicleEntity implements IEntityRaytraceable
{
    public BumperCarEntity(World worldIn)
    {
        super(ModEntities.BUMPER_CAR, worldIn);
        this.setMaxSpeed(10);
        this.setTurnSensitivity(20);
        this.stepHeight = 0.625F;
        //TODO figure out fuel system
    }

    @Override
    public void applyEntityCollision(Entity entityIn)
    {
        if(entityIn instanceof BumperCarEntity && this.isBeingRidden())
        {
            applyBumperCollision((BumperCarEntity) entityIn);
        }
    }

    private void applyBumperCollision(BumperCarEntity entity)
    {
        this.setMotion(this.getMotion().add(this.vehicleMotionX * 2, 0, this.vehicleMotionZ * 2));
        world.playSound(null, this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), ModSounds.BONK, SoundCategory.NEUTRAL, 1.0F, 0.6F + 0.1F * this.getNormalSpeed());
        this.currentSpeed *= 0.25F;
    }

    @Override
    public SoundEvent getMovingSound()
    {
        return ModSounds.ELECTRIC_ENGINE_MONO;
    }

    @Override
    public SoundEvent getRidingSound()
    {
        return ModSounds.ELECTRIC_ENGINE_STEREO;
    }

    @Override
    public float getMaxEnginePitch()
    {
        return 0.8F;
    }

    @Override
    public double getMountedYOffset()
    {
        return 3 * 0.0625F;
    }

    @Override
    public EngineType getEngineType()
    {
        return EngineType.ELECTRIC_MOTOR;
    }

    @Override
    public boolean canBeColored()
    {
        return true;
    }

    @Override
    public boolean isLockable()
    {
        return false;
    }
}
