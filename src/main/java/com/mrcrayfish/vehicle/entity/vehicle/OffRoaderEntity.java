package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class OffRoaderEntity extends LandVehicleEntity
{
    public OffRoaderEntity(EntityType<? extends OffRoaderEntity> type, World worldIn)
    {
        super(type, worldIn);
    }
}
