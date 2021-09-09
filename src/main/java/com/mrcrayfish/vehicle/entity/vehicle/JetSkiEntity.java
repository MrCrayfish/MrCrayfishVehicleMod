package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.BoatEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class JetSkiEntity extends BoatEntity
{
    public JetSkiEntity(EntityType<? extends JetSkiEntity> type, World worldIn)
    {
        super(type, worldIn);
    }

    @Override
    public void createParticles()
    {
        if(this.state == State.IN_WATER)
        {
            if(this.getThrottle() > 0)
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

}
