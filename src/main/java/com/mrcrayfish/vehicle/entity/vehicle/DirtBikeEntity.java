package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.MotorcycleEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class DirtBikeEntity extends MotorcycleEntity
{
    public DirtBikeEntity(EntityType<? extends DirtBikeEntity> type, World worldIn)
    {
        super(type, worldIn);
    }
}
