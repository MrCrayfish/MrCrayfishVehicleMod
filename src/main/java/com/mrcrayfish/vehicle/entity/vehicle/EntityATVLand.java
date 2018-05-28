package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.EntityColoredLandVehicle;
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
public class EntityATVLand extends EntityColoredLandVehicle
{
    /**
     * ItemStack instances used for rendering
     */
    @SideOnly(Side.CLIENT)
    public ItemStack handleBar;

    //TODO make it so vehicle base can set properties
    public EntityATVLand(World worldIn)
    {
        super(worldIn);
        this.setMaxSpeed(15);
        this.setSize(1.5F, 1.0F);
    }

    @Override
    public void entityInit()
    {
        super.entityInit();

        if(world.isRemote)
        {
            body = new ItemStack(ModItems.ATV_BODY);
            handleBar = new ItemStack(ModItems.ATV_HANDLE_BAR);
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
        return 9 * 0.0625;
    }

    @Override
    public boolean shouldRenderEngine()
    {
        return false;
    }
}
