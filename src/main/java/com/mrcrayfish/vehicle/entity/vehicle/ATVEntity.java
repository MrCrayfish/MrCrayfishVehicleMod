package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class ATVEntity extends LandVehicleEntity
{
    public ATVEntity(EntityType<? extends ATVEntity> type, World worldIn)
    {
        super(type, worldIn);
    }
}
