package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.client.EntityRaytracer.IEntityRaytraceable;
import com.mrcrayfish.vehicle.entity.BoatEntity;
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.init.ModEntities;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class JetSkiEntity extends BoatEntity implements IEntityRaytraceable
{
    public JetSkiEntity(World worldIn)
    {
        super(ModEntities.JET_SKI, worldIn);
        this.setMaxSpeed(15F);
        this.setTurnSensitivity(15);
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
                    this.world.addParticle(ParticleTypes.SPLASH, this.getPosX() + ((double) this.rand.nextFloat() - 0.5D) * (double) this.getWidth(), this.getBoundingBox().minY + 0.1D, this.getPosZ() + ((double) this.rand.nextFloat() - 0.5D) * (double) this.getWidth(), -this.getMotion().x * 4.0D, 1.5D, -this.getMotion().z * 4.0D);
                }

                for(int i = 0; i < 5; i++)
                {
                    this.world.addParticle(ParticleTypes.BUBBLE, this.getPosX() + ((double) this.rand.nextFloat() - 0.5D) * (double) this.getWidth(), this.getBoundingBox().minY + 0.1D, this.getPosZ() + ((double) this.rand.nextFloat() - 0.5D) * (double) this.getWidth(), -this.getMotion().x * 2.0D, 0.0D, -this.getMotion().z * 2.0D);
                }
            }
        }
    }

    @Override
    public SoundEvent getMovingSound()
    {
        return ModSounds.SPEED_BOAT_ENGINE_MONO;
    }

    @Override
    public SoundEvent getRidingSound()
    {
        return ModSounds.SPEED_BOAT_ENGINE_STEREO;
    }

    @Override
    public EngineType getEngineType()
    {
        return EngineType.SMALL_MOTOR;
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
    public double getMountedYOffset()
    {
        return 10 * 0.0625;
    }

    @Override
    public void updatePassenger(Entity passenger)
    {
        if (this.isPassenger(passenger))
        {
            float offset = 0.0F;
            float yOffset = (float)((!this.isAlive() ? 0.01D : this.getMountedYOffset()) + passenger.getYOffset());

            if (this.getPassengers().size() > 1)
            {
                int index = this.getPassengers().indexOf(passenger);
                if (index > 0)
                {
                    offset += index * -0.5F;
                }
            }

            Vec3d vec3d = (new Vec3d((double)offset, 0.0D, 0.0D)).rotateYaw(-this.rotationYaw * 0.017453292F - ((float)Math.PI / 2F));
            passenger.setPosition(this.getPosX() + vec3d.x, this.getPosY() + (double)yOffset, this.getPosZ() + vec3d.z);
            passenger.rotationYaw -= deltaYaw;
            passenger.setRotationYawHead(passenger.rotationYaw);
            this.applyYawToEntity(passenger);
        }
    }

    @Override
    protected boolean canFitPassenger(Entity passenger)
    {
        return this.getPassengers().size() < 2;
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
