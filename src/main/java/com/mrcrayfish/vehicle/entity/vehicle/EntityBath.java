package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.EntityAirVehicle;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class EntityBath extends EntityAirVehicle
{
    public EntityBath(World worldIn)
    {
        super(worldIn);
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
    public void onClientInit()
    {
        body = new ItemStack(Item.getByNameOrId("cfm:bath_bottom"), 1, 0);
    }

    @Override
    public void updateVehicle()
    {
        if(this.isFlying())
        {
            for(int i = 0; i < 4; i++)
            {
                world.spawnParticle(EnumParticleTypes.DRIP_WATER, false, posX - 0.25 + 0.5 * rand.nextGaussian(), posY + 0.5 * rand.nextGaussian(), posZ - 0.25 + 0.5 * rand.nextGaussian(), 0, 0, 0, 0);
            }
        }
    }

    @Override
    public double getMountedYOffset()
    {
        return 0;
    }

    @Override
    public boolean canBeColored()
    {
        return false;
    }
}
