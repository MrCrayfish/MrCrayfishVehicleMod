package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.HelicopterEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class SofacopterEntity extends HelicopterEntity
{
    public SofacopterEntity(EntityType<? extends SofacopterEntity> type, World worldIn)
    {
        super(type, worldIn);
        this.setFuelCapacity(40000F);
        this.setFuelConsumption(0.5F);
        this.entityData.set(COLOR, 11546150);
    }

    @Override
    public boolean canBeColored()
    {
        return false;
    }
}
