package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.PlaneEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class BathEntity extends PlaneEntity
{
    public BathEntity(EntityType<? extends BathEntity> type, World worldIn)
    {
        super(type, worldIn);
    }

    @Override
    public void onVehicleTick()
    {
        if(this.isFlying() && this.getControllingPassenger() != null)
        {
            for(int i = 0; i < 4; i++)
            {
                this.level.addParticle(ParticleTypes.DRIPPING_WATER, this.getX() - 0.25 + 0.5 * random.nextGaussian(), this.getY() + 0.5 * random.nextGaussian(), this.getZ() - 0.25 + 0.5 * random.nextGaussian(), 0, 0, 0);
            }
        }
    }
}
