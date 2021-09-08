package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class CouchEntity extends LandVehicleEntity
{
    public CouchEntity(EntityType<? extends CouchEntity> type, World worldIn)
    {
        super(type, worldIn);
        this.entityData.set(COLOR, 11546150);
    }

}
