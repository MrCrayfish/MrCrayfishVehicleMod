package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Author: MrCrayfish
 */
public class EntityGoKart extends EntityColoredVehicle
{
    /**
     * ItemStack instances used for rendering
     */
    @SideOnly(Side.CLIENT)
    public ItemStack steeringWheel;

    public EntityGoKart(World worldIn)
    {
        super(worldIn);
        this.setMaxSpeed(20F);
        this.setSize(1.5F, 1.5F);
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();

        if(world.isRemote)
        {
            body = new ItemStack(ModItems.GO_KART_BODY);
            wheel = new ItemStack(ModItems.WHEEL);
            steeringWheel = new ItemStack(ModItems.GO_KART_STEERING_WHEEL);
        }
    }

    @Override
    public SoundEvent getMovingSound()
    {
        return ModSounds.GO_KART_ENGINE_MONO;
    }

    @Override
    public SoundEvent getRidingSound()
    {
        return ModSounds.GO_KART_ENGINE_STEREO;
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
    public double getMountedYOffset()
    {
        return 0;
    }
}
