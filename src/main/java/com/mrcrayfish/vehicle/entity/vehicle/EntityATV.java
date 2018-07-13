package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.client.EntityRaytracer.IEntityRaytraceable;
import com.mrcrayfish.vehicle.entity.EntityLandVehicle;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Author: MrCrayfish
 */
public class EntityATV extends EntityLandVehicle implements IEntityRaytraceable
{
    /**
     * ItemStack instances used for rendering
     */
    @SideOnly(Side.CLIENT)
    public ItemStack handleBar;

    //TODO make it so vehicle base can set properties
    public EntityATV(World worldIn)
    {
        super(worldIn);
        this.setMaxSpeed(15);
        this.setSize(1.5F, 1.0F);
        this.setHeldOffset(new Vec3d(4D, 3.5D, 0D));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onClientInit()
    {
        body = new ItemStack(ModItems.ATV_BODY);
        handleBar = new ItemStack(ModItems.ATV_HANDLE_BAR);
        wheel = new ItemStack(ModItems.WHEEL);
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

    @Override
    public boolean canBeColored()
    {
        return true;
    }
}
