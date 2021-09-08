package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class SmartCarEntity extends LandVehicleEntity
{
    public SmartCarEntity(EntityType<? extends SmartCarEntity> type, World worldIn)
    {
        super(type, worldIn);
        this.maxUpStep = 1F;
    }
}
