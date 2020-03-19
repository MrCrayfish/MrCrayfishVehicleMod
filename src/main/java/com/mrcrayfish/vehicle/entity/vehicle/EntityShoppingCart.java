package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.client.EntityRaytracer.IEntityRaytraceable;
import com.mrcrayfish.vehicle.common.CommonEvents;
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.EntityLandVehicle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class EntityShoppingCart extends EntityLandVehicle implements IEntityRaytraceable
{
    private EntityPlayer pusher;

    public EntityShoppingCart(World worldIn)
    {
        super(worldIn);
        this.setMaxTurnAngle(90);
        this.setTurnSensitivity(15);
        this.setFuelCapacity(0F);
        this.setFuelConsumption(0F);
    }

    @Override
    public void onUpdate()
    {
        if(pusher != null)
        {
            this.prevRotationYaw = this.rotationYaw;
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
            float x = MathHelper.sin(-pusher.rotationYaw * 0.017453292F) * 1.3F;
            float z = MathHelper.cos(-pusher.rotationYaw * 0.017453292F) * 1.3F;
            this.posX = pusher.posX + x;
            this.posY = pusher.posY;
            this.posZ = pusher.posZ + z;
            this.lastTickPosX = this.posX;
            this.lastTickPosY = this.posY;
            this.lastTickPosZ = this.posZ;
            this.rotationYaw = pusher.rotationYaw;
        }
        else
        {
            super.onUpdate();
        }
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
    public EngineType getEngineType()
    {
        return EngineType.NONE;
    }

    @Override
    public boolean isLockable()
    {
        return false;
    }

    @Override
    public boolean canBeColored()
    {
        return true;
    }
}
