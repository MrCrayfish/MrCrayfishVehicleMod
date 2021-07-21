package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.BoatEntity;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class JetSkiEntity extends BoatEntity
{
    public JetSkiEntity(EntityType<? extends JetSkiEntity> type, World worldIn)
    {
        super(type, worldIn);
        this.setMaxSpeed(15F);
        this.setTurnSensitivity(10);
        this.setFuelConsumption(0.5F);
    }

    @Override
    public FuelPortType getFuelPortType()
    {
        return FuelPortType.SMALL;
    }

    @Override
    public void createParticles()
    {
        if(state == State.IN_WATER)
        {
            if(this.getAcceleration() == AccelerationDirection.FORWARD)
            {
                for(int i = 0; i < 5; i++)
                {
                    this.level.addParticle(ParticleTypes.SPLASH, this.getX() + ((double) this.random.nextFloat() - 0.5D) * (double) this.getBbWidth(), this.getBoundingBox().minY + 0.1D, this.getZ() + ((double) this.random.nextFloat() - 0.5D) * (double) this.getBbWidth(), -this.getDeltaMovement().x * 4.0D, 1.5D, -this.getDeltaMovement().z * 4.0D);
                }

                for(int i = 0; i < 5; i++)
                {
                    this.level.addParticle(ParticleTypes.BUBBLE, this.getX() + ((double) this.random.nextFloat() - 0.5D) * (double) this.getBbWidth(), this.getBoundingBox().minY + 0.1D, this.getZ() + ((double) this.random.nextFloat() - 0.5D) * (double) this.getBbWidth(), -this.getDeltaMovement().x * 2.0D, 0.0D, -this.getDeltaMovement().z * 2.0D);
                }
            }
        }
    }

    @Override
    public SoundEvent getEngineSound()
    {
        return ModSounds.ENTITY_SPEED_BOAT_ENGINE.get();
    }

    @Override
    public float getMinEnginePitch()
    {
        return 1.2F;
    }

    @Override
    public float getMaxEnginePitch()
    {
        return 2.2F;
    }

    @Override
    public boolean canBeColored()
    {
        return true;
    }

    //TODO remove and add key support
    @Override
    public boolean isLockable()
    {
        return false;
    }
}
