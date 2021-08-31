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
        this.entityData.set(COLOR, 11546150);
    }

}
