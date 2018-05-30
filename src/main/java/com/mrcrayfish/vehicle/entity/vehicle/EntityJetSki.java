package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.EntityColoredSeaVehicle;
import com.mrcrayfish.vehicle.init.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Author: MrCrayfish
 */
public class EntityJetSki extends EntityColoredSeaVehicle
{
    public float prevLeanAngle;
    public float leanAngle;

    /**
     * ItemStack instances used for rendering
     */
    @SideOnly(Side.CLIENT)
    public ItemStack handle_bar;

    public EntityJetSki(World worldIn)
    {
        super(worldIn);
    }

    @Override
    public void entityInit()
    {
        super.entityInit();

        if(world.isRemote)
        {
            body = new ItemStack(ModItems.JET_SKI_BODY);
            handle_bar = new ItemStack(ModItems.ATV_HANDLE_BAR);
        }
    }

    @Override
    public void updateVehicle()
    {
        this.prevLeanAngle = this.leanAngle;
        this.leanAngle = this.turnAngle / (float) getMaxTurnAngle();
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
    public double getMountedYOffset()
    {
        return 10 * 0.0625;
    }
}
