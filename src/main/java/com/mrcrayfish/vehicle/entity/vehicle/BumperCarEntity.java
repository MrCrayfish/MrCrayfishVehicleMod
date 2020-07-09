package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.client.EntityRayTracer.IEntityRayTraceable;
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class BumperCarEntity extends LandVehicleEntity
{
    public BumperCarEntity(EntityType<? extends BumperCarEntity> type, World worldIn)
    {
        super(type, worldIn);
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
        world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), ModSounds.BONK.get(), SoundCategory.NEUTRAL, 1.0F, 0.6F + 0.1F * this.getNormalSpeed());
        this.currentSpeed *= 0.25F;
    }

    @Override
    public SoundEvent getMovingSound()
    {
        return ModSounds.ELECTRIC_ENGINE_MONO.get();
    }

    @Override
    public SoundEvent getRidingSound()
    {
        return ModSounds.ELECTRIC_ENGINE_STEREO.get();
    }

    @Override
    public float getMaxEnginePitch()
    {
        return 0.8F;
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
