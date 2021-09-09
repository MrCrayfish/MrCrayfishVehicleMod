package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class TractorEntity extends LandVehicleEntity
{
    public TractorEntity(EntityType<? extends TractorEntity> type, World worldIn)
    {
        super(type, worldIn);
    }
}
