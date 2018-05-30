package com.mrcrayfish.vehicle.entity;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public abstract class EntitySeaVehicle extends EntityVehicle
{
    private double waterLevel;

    public EntitySeaVehicle(World worldIn)
    {
        super(worldIn);
        this.setMaxTurnAngle(65);
    }

    @Override
    public void updateVehicle()
    {

    }

    @Override
    public void updateVehicleMotion()
    {
        this.motionY = -0.04;

        if(isOnWater())
        {
            float f1 = MathHelper.sin(this.rotationYaw * 0.017453292F) / 20F; //Divide by 20 ticks
            float f2 = MathHelper.cos(this.rotationYaw * 0.017453292F) / 20F;
            this.vehicleMotionX = (-currentSpeed * f1);
            this.motionY += ((this.waterLevel - this.getEntityBoundingBox().minY) / (double) this.height) * 0.15D;
            this.vehicleMotionZ = (currentSpeed * f2);
        }
    }

    private boolean isOnWater()
    {
        waterLevel = Double.MIN_VALUE;

        BlockPos.PooledMutableBlockPos mutableBlockPos = BlockPos.PooledMutableBlockPos.retain();
        AxisAlignedBB axisAligned = this.getEntityBoundingBox();
        int minX = MathHelper.floor(axisAligned.minX);
        int maxX = MathHelper.ceil(axisAligned.maxX);
        int minY = MathHelper.floor(axisAligned.minY);
        int maxY = MathHelper.ceil(axisAligned.minY + 0.001D);
        int minZ = MathHelper.floor(axisAligned.minZ);
        int maxZ = MathHelper.ceil(axisAligned.maxZ);

        boolean inWater = false;
        try
        {
            for(int x = minX; x < maxX; ++x)
            {
                for(int y = minY; y < maxY; ++y)
                {
                    for(int z = minZ; z < maxZ; ++z)
                    {
                        mutableBlockPos.setPos(x, y, z);
                        IBlockState state = world.getBlockState(mutableBlockPos);
                        if(state.getMaterial() == Material.WATER)
                        {
                            float liquidHeight = BlockLiquid.getLiquidHeight(state, world, mutableBlockPos);
                            waterLevel = Math.max((double) liquidHeight, waterLevel);
                            inWater = axisAligned.minY < (double) liquidHeight;
                        }
                    }
                }
            }
        }
        finally
        {
            mutableBlockPos.release();
        }
        return inWater;
    }
}
