package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class QuadBikeEntity extends LandVehicleEntity
{
    public QuadBikeEntity(EntityType<? extends QuadBikeEntity> type, World worldIn)
    {
        super(type, worldIn);
    }
}
