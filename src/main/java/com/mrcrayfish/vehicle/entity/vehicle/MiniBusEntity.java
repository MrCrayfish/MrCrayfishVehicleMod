package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class MiniBusEntity extends LandVehicleEntity
{
    public MiniBusEntity(EntityType<? extends MiniBusEntity> type, World worldIn)
    {
        super(type, worldIn);
    }
}
