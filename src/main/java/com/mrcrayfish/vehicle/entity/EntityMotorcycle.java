package com.mrcrayfish.vehicle.entity;

import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public abstract class EntityMotorcycle extends EntityLandVehicle
{
    public float prevLeanAngle;
    public float leanAngle;

    public EntityMotorcycle(World worldIn)
    {
        super(worldIn);
    }

    @Override
    public void onEntityUpdate()
    {
        this.prevLeanAngle = this.leanAngle;
        super.onEntityUpdate();
        this.leanAngle = this.turnAngle / (float) getMaxTurnAngle();
    }
}
