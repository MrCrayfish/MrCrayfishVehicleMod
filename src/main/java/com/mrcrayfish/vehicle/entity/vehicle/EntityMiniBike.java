package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.client.EntityRaytracer.IEntityRaytraceable;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.EntityMotorcycle;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Author: MrCrayfish
 */
public class EntityMiniBike extends EntityMotorcycle implements IEntityRaytraceable
{
    public static final float AXLE_OFFSET = -1.7F;
    public static final float WHEEL_OFFSET = 4.0F;
    public static final PartPosition BODY_POSITION = new PartPosition(0, 0, 0.1, 0, 0, 0, 1.05);
    private static final Vec3d HELD_OFFSET_VEC = new Vec3d(6D, 0D, 0D);
    private static final Vec3d TRAILER_OFFSET_VEC = new Vec3d(0D, -0.0625D, -0.5D);

    /**
     * ItemStack instances used for rendering
     */
    @SideOnly(Side.CLIENT)
    public ItemStack handleBar;

    public EntityMiniBike(World worldIn)
    {
        super(worldIn);
        this.setMaxSpeed(18F);
        this.setTurnSensitivity(12);
        this.setAxleOffset(AXLE_OFFSET);
        this.setWheelOffset(WHEEL_OFFSET);
        this.setBodyPosition(BODY_POSITION);
        this.setHeldOffset(HELD_OFFSET_VEC);
        this.setTrailerOffset(TRAILER_OFFSET_VEC);
        this.setFuelCapacity(15000F);
        this.setFuelConsumption(1.5F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onClientInit()
    {
        super.onClientInit();
        body = new ItemStack(ModItems.MINI_BIKE_BODY);
        wheel = new ItemStack(ModItems.WHEEL);
        handleBar = new ItemStack(ModItems.MINI_BIKE_HANDLE_BAR);
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key)
    {
        super.notifyDataManagerChange(key);
        if(world.isRemote)
        {
            if(COLOR.equals(key))
            {
                if(!handleBar.hasTagCompound())
                {
                    handleBar.setTagCompound(new NBTTagCompound());
                }
                handleBar.getTagCompound().setInteger("color", this.dataManager.get(COLOR));
            }
        }
    }

    @Override
    public SoundEvent getMovingSound()
    {
        return ModSounds.goKartEngineMono;
    }

    @Override
    public SoundEvent getRidingSound()
    {
        return ModSounds.goKartEngineStereo;
    }

    @Override
    public EngineType getEngineType()
    {
        return EngineType.SMALL_MOTOR;
    }

    @Override
    public float getMinEnginePitch()
    {
        return 0.5F;
    }

    @Override
    public float getMaxEnginePitch()
    {
        return 1.8F;
    }

    @Override
    public boolean shouldShowEngineSmoke()
    {
        return true;
    }

    @Override
    public Vec3d getEngineSmokePosition()
    {
        return new Vec3d(0, 0.55, 0);
    }

    @Override
    public double getMountedYOffset()
    {
        return 9.5 * 0.0625;
    }

    @Override
    public boolean canBeColored()
    {
        return true;
    }

    @Override
    public boolean shouldRenderEngine()
    {
        return true;
    }

    @Override
    public boolean shouldRenderFuelPort()
    {
        return false;
    }

    @Override
    public boolean isLockable()
    {
        return false;
    }
}
