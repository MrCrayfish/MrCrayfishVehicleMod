package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class ShoppingCartEntity extends LandVehicleEntity
{
    private PlayerEntity pusher;

    public ShoppingCartEntity(EntityType<? extends ShoppingCartEntity> type, World worldIn)
    {
        super(type, worldIn);
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
            this.yRotO = this.yRot;
            this.xo = this.getX();
            this.yo = this.getY();
            this.zo = this.getZ();
            float x = MathHelper.sin(-pusher.yRot * 0.017453292F) * 1.3F;
            float z = MathHelper.cos(-pusher.yRot * 0.017453292F) * 1.3F;
            this.setPos(pusher.getX() + x, pusher.getY(), pusher.getZ() + z);
            this.xOld = this.getX();
            this.yOld = this.getY();
            this.zOld = this.getZ();
            this.yRot = pusher.yRot;
        }
        else
        {
            super.tick();
        }
    }

    @Override
    public SoundEvent getEngineSound()
    {
        return null;
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
