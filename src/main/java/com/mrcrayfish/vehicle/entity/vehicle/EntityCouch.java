package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.client.EntityRaytracer.IEntityRaytraceable;
import com.mrcrayfish.vehicle.entity.EntityLandVehicle;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Author: MrCrayfish
 */
public class EntityCouch extends EntityLandVehicle implements IEntityRaytraceable
{
    public EntityCouch(World worldIn)
    {
        super(worldIn);
        this.setMaxSpeed(10);
        this.setSize(1.0F, 1.0F);
        this.setHeldOffset(new Vec3d(2D, 2D, 0D));
        this.setTrailerOffset(new Vec3d(0D, 0D, -0.25D));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onClientInit()
    {
        super.onClientInit();
        body = new ItemStack(Item.getByNameOrId("cfm:couch_jeb"), 1, 0);
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
        return 0.525;
    }
}
