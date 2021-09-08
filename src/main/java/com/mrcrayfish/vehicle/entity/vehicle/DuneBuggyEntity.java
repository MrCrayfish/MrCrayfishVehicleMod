package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class DuneBuggyEntity extends LandVehicleEntity
{
    public DuneBuggyEntity(EntityType<? extends DuneBuggyEntity> type, World worldIn)
    {
        super(type, worldIn);
        this.maxUpStep = 0.5F;
    }

    @Override
    public void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.set(COLOR, 0xF2B116);
    }

}
