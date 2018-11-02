package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.client.EntityRaytracer.IEntityRaytraceable;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.EntityPlane;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Author: MrCrayfish
 */
public class EntitySportsPlane extends EntityPlane implements IEntityRaytraceable
{
    public static final PartPosition BODY_POSITION = new PartPosition(0, 11 * 0.0625F, -8 * 0.0625F, 0.0F, 0.0F, 0.0F, 1.8F);
    public static final PartPosition FUEL_PORT_POSITION = new PartPosition(-6.25F, 4.0F, -1.0F, 0.0F, -90.0F, 0.0F, 0.25F);
    public static final PartPosition KEY_PORT_POSITION = new PartPosition(0.0F, 3.75F, 12.5F, -67.5F, 0.0F, 0.0F, 0.5F);

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
        this.setFuelCapacity(75000F);
        this.setFuelConsumption(4.0F);
        this.setBodyPosition(BODY_POSITION);
        this.setKeyHolePosition(KEY_PORT_POSITION);
    }

    @Override
    public void onClientInit()
    {
        super.onClientInit();
        body = new ItemStack(ModItems.SPORTS_PLANE_BODY);
        wheel = new ItemStack(ModItems.WHEEL);
        wing = new ItemStack(ModItems.SPORTS_PLANE_WING);
        wheelCover = new ItemStack(ModItems.SPORTS_PLANE_WHEEL_COVER);
        leg = new ItemStack(ModItems.SPORTS_PLANE_LEG);
        propeller = new ItemStack(ModItems.SPORTS_PLANE_PROPELLER);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return this.getEntityBoundingBox().grow(1.5);
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

        if(this.canDrive() && this.getControllingPassenger() != null)
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
    protected float getModifiedAccelerationSpeed()
    {
        return super.getAccelerationSpeed() * (propellerSpeed / 120F);
    }

    @Override
    public boolean canMountTrailer()
    {
        return false;
    }
}
