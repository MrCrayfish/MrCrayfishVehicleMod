package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.client.EntityRaytracer.IEntityRaytraceable;
import com.mrcrayfish.vehicle.common.CustomDataParameters;
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class ShoppingCartEntity extends LandVehicleEntity implements IEntityRaytraceable
{
    private PlayerEntity pusher;

    public ShoppingCartEntity(World worldIn)
    {
        super(ModEntities.SHOPPING_CART, worldIn);
        this.setMaxTurnAngle(90);
        this.setTurnSensitivity(15);
        this.setFuelCapacity(0F);
        this.setFuelConsumption(0F);
    }

    @Override
    public void tick()
    {
        if(this.pusher != null)
        {
            this.prevRotationYaw = this.rotationYaw;
            this.prevPosX = this.getPosX();
            this.prevPosY = this.getPosY();
            this.prevPosZ = this.getPosZ();
            float x = MathHelper.sin(-pusher.rotationYaw * 0.017453292F) * 1.3F;
            float z = MathHelper.cos(-pusher.rotationYaw * 0.017453292F) * 1.3F;
            this.setPosition(pusher.getPosX() + x, pusher.getPosY(), pusher.getPosZ() + z);
            this.lastTickPosX = this.getPosX();
            this.lastTickPosY = this.getPosY();
            this.lastTickPosZ = this.getPosZ();
            this.rotationYaw = pusher.rotationYaw;
        }
        else
        {
            super.tick();
        }
    }

    @Override
    public boolean processInitialInteract(PlayerEntity player, Hand hand)
    {
        if(!world.isRemote)
        {
            if(player.isCrouching())
            {
                if(pusher == player)
                {
                    pusher = null;
                    player.getDataManager().set(CustomDataParameters.PUSHING_CART, false);
                    return true;
                }
                else if(pusher == null)
                {
                    pusher = player;
                    player.getDataManager().set(CustomDataParameters.PUSHING_CART, true);
                }
            }
            else if(pusher != player)
            {
                super.processInitialInteract(player, hand);
            }

        }
        return true;
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
        return 0.0625 * 7.5;
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
