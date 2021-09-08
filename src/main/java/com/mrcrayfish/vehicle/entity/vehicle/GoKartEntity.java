package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class GoKartEntity extends LandVehicleEntity
{
    public GoKartEntity(EntityType<? extends GoKartEntity> type, World worldIn)
    {
        super(type, worldIn);
        this.maxUpStep = 0.625F;
    }


    @Override
    public boolean shouldRenderFuelPort()
    {
        return false;
    }
}
