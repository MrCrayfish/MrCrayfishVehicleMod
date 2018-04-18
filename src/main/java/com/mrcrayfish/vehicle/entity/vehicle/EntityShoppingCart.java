package com.mrcrayfish.vehicle.entity.vehicle;

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
public class EntityShoppingCart extends EntityVehicle
{
    public EntityShoppingCart(World worldIn)
    {
        super(worldIn);
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();

        if(world.isRemote)
        {
            body = new ItemStack(ModItems.SHOPPING_CART_BODY);
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
        return 0.0625 * 7.5;
    }
}
