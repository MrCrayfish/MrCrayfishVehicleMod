package com.mrcrayfish.vehicle.entity.vehicle;

import javax.annotation.Nullable;

import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.client.EntityRaytracer.IEntityRaytraceBoxProvider;
import com.mrcrayfish.vehicle.entity.EntityAirVehicle;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModSounds;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Author: MrCrayfish
 */
public class EntitySportsPlane extends EntityAirVehicle implements IEntityRaytraceBoxProvider
{
    public float wheelSpeed;
    public float wheelRotation;
    public float prevWheelRotation;

    public float propellerSpeed;
    public float propellerRotation;
    public float prevPropellerRotation;

    /**
     * ItemStack instances used for rendering
     */
    @SideOnly(Side.CLIENT)
    public ItemStack wing;
    @SideOnly(Side.CLIENT)
    public ItemStack wheelCover;
    @SideOnly(Side.CLIENT)
    public ItemStack leg;
    @SideOnly(Side.CLIENT)
    public ItemStack propeller;

    public EntitySportsPlane(World worldIn)
    {
        super(worldIn);
        this.setAccelerationSpeed(0.5F);
        this.setMaxSpeed(25F);
        this.setMaxTurnAngle(25);
        this.setTurnSensitivity(2);
        this.setSize(3F, 1.6875F);
    }

    @Override
    public void onClientInit()
    {
        body = new ItemStack(ModItems.SPORTS_PLANE_BODY);
        wheel = new ItemStack(ModItems.WHEEL);
        wing = new ItemStack(ModItems.SPORTS_PLANE_WING);
        wheelCover = new ItemStack(ModItems.SPORTS_PLANE_WHEEL_COVER);
        leg = new ItemStack(ModItems.SPORTS_PLANE_LEG);
        propeller = new ItemStack(ModItems.SPORTS_PLANE_PROPELLER);
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key)
    {
        super.notifyDataManagerChange(key);
        if(world.isRemote)
        {
            if(COLOR.equals(key))
            {
                int color = this.dataManager.get(COLOR);
                this.setPartColor(wing, color);
                this.setPartColor(wheelCover, color);
                this.setPartColor(propeller, color);
            }
        }
    }

    private void setPartColor(ItemStack stack, int color)
    {
        if(!stack.hasTagCompound())
        {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setInteger("color", color);
    }

    @Override
    public void updateVehicle()
    {
        prevWheelRotation = wheelRotation;
        prevPropellerRotation = propellerRotation;

        if(this.onGround)
        {
            wheelSpeed = currentSpeed / 30F;
        }
        else
        {
            wheelSpeed *= 0.95F;
        }
        wheelRotation -= (90F * wheelSpeed);

        if(this.getControllingPassenger() != null)
        {
            propellerSpeed += 1F;
            if(propellerSpeed > 120F)
            {
                propellerSpeed = 120F;
            }
        }
        else
        {
            propellerSpeed *= 0.95F;
        }
        propellerRotation += propellerSpeed;
    }

    @Override
    public SoundEvent getMovingSound()
    {
        return ModSounds.SPORTS_PLANE_ENGINE_MONO;
    }

    @Override
    public SoundEvent getRidingSound()
    {
        return ModSounds.SPORTS_PLANE_ENGINE_STEREO;
    }

    @Override
    public double getMountedYOffset()
    {
        return 12 * 0.0625;
    }

    @Override
    public boolean canBeColored()
    {
        return true;
    }

    @Override
    public float getAccelerationSpeed()
    {
        return super.getAccelerationSpeed() * (propellerSpeed / 120F);
    }

    @Override
    public boolean processHit(@Nullable ItemStack partHit, @Nullable AxisAlignedBB boxHit)//TODO debug method - delete this method and this comment before release
    {
        if (partHit != null && partHit.hasTagCompound())
        {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString(partHit.getTagCompound().getString(EntityRaytracer.PART_NAME)).setStyle(new Style().setColor(TextFormatting.values()[Minecraft.getMinecraft().world.rand.nextInt(15) + 1])));
            return true;
        }
        return IEntityRaytraceBoxProvider.super.processHit(partHit, boxHit);
    }
}
