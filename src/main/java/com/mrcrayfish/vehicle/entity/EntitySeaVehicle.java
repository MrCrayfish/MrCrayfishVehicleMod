package com.mrcrayfish.vehicle.entity;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public abstract class EntitySeaVehicle extends EntityVehicle
{
    private float waterLevel;

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
        if(isOnWater())
        {
            double floatingY = ((this.waterLevel - 0.25D) - this.getEntityBoundingBox().minY) / (double) this.height;
            if(floatingY > 0.0D)
            {
                this.motionY += floatingY;
            }
            if(isUnderWater())
            {
                this.motionY += 0.04D;
            }
            this.motionY *= 0.75D;

            float f1 = MathHelper.sin(this.rotationYaw * 0.017453292F) / 20F;
            float f2 = MathHelper.cos(this.rotationYaw * 0.017453292F) / 20F;
            this.vehicleMotionX = (-currentSpeed * f1);
            this.vehicleMotionZ = (currentSpeed * f2);
        }
        else
        {
            this.motionY -= 0.08D;
        }
    }

    private boolean isOnWater()
    {
        waterLevel = Float.MIN_VALUE;

        BlockPos.PooledMutableBlockPos mutableBlockPos = BlockPos.PooledMutableBlockPos.retain();
        AxisAlignedBB axisAligned = this.getEntityBoundingBox();
        int minX = MathHelper.floor(axisAligned.minX);
        int maxX = MathHelper.ceil(axisAligned.maxX);
        int minY = MathHelper.floor(axisAligned.minY);
        int maxY = MathHelper.ceil(axisAligned.minY + 0.01D);
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
                            waterLevel = Math.max(liquidHeight, waterLevel);
                            return axisAligned.minY < (double) liquidHeight;
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

    @Nullable
    private boolean isUnderWater()
    {
        AxisAlignedBB axisAligned = this.getEntityBoundingBox();
        double height = axisAligned.minY + 0.001D;
        int minX = MathHelper.floor(axisAligned.minX);
        int maxX = MathHelper.ceil(axisAligned.maxX);
        int minY = MathHelper.floor(axisAligned.minY);
        int maxY = MathHelper.ceil(height);
        int minZ = MathHelper.floor(axisAligned.minZ);
        int maxZ = MathHelper.ceil(axisAligned.maxZ);

        boolean flag = false;
        BlockPos.PooledMutableBlockPos mutableBlockPos = BlockPos.PooledMutableBlockPos.retain();
        try
        {
            for (int x = minX; x < maxX; ++x)
            {
                for (int y = minY; y < maxY; ++y)
                {
                    for (int z = minZ; z < maxZ; ++z)
                    {
                        mutableBlockPos.setPos(x, y + 1, z);

                        IBlockState state = this.world.getBlockState(mutableBlockPos);
                        if (state.getMaterial() == Material.WATER && height < (double)BlockLiquid.getLiquidHeight(state, this.world, mutableBlockPos))
                        {
                            if (state.getValue(BlockLiquid.LEVEL) != 0)
                            {
                                return true;
                            }
                            flag = true;
                        }
                    }
                }
            }
        }
        finally
        {
            mutableBlockPos.release();
        }
        return flag;
    }
}
