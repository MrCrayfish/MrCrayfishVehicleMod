package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.MotorcycleEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class MiniBikeEntity extends MotorcycleEntity
{
    public MiniBikeEntity(EntityType<? extends MiniBikeEntity> type, World worldIn)
    {
        super(type, worldIn);
    }


    @Override
    public boolean shouldRenderFuelPort()
    {
        return false;
    }
}
