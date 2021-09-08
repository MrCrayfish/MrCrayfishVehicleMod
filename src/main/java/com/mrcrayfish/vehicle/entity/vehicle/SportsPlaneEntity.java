package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.PlaneEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class SportsPlaneEntity extends PlaneEntity
{
    public SportsPlaneEntity(EntityType<? extends SportsPlaneEntity> type, World worldIn)
    {
        super(type, worldIn);
    }

    @Override
    public AxisAlignedBB getBoundingBoxForCulling()
    {
        return this.getBoundingBox().inflate(1.5);
    }
}
