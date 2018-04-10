package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.EntityColoredVehicle;
import com.mrcrayfish.vehicle.entity.EntityVehicle;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class EntityCouch extends EntityVehicle
{
    public EntityCouch(World worldIn)
    {
        super(worldIn);
        this.setMaxSpeed(10);
        this.setSize(1.0F, 1.0F);
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();

        if(world.isRemote)
        {
            body = new ItemStack(Item.getByNameOrId("cfm:couch_jeb"), 1, 0);
            wheel = new ItemStack(ModItems.WHEEL);
        }
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
        return 0.525;
    }
}
