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
public abstract class EntityBoat extends EntityPoweredVehicle
{
    protected State state;
    protected State previousState;
    private float waterLevel;

    public EntityBoat(World worldIn)
    {
        super(worldIn);
        this.setMaxTurnAngle(65);
    }

    @Override
    public boolean canChangeWheels()
    {
        return false;
    }

    @Override
    public void updateVehicleMotion()
    {
        if(state == State.ON_WATER || state == State.UNDER_WATER)
        {
            if(state == State.UNDER_WATER)
            {
                this.motionY += 0.12D;
            }
            else
            {
                double floatingY = ((waterLevel - 0.35D + (0.25D * Math.min(1.0F, getNormalSpeed())) - posY)) / (double) height;
                motionY += floatingY * 0.05D;
                if(Math.abs(floatingY) < 0.1D && motionY > 0 && Math.abs(motionY) < 0.1D)
                {
                    setPosition(posX, waterLevel - 0.35D + (0.25D * Math.min(1.0F, getNormalSpeed())), posZ);
                    motionY = 0.0D;
                }
                motionY *= 0.75D;
            }

            float f1 = MathHelper.sin(rotationYaw * 0.017453292F) / 20F;
            float f2 = MathHelper.cos(rotationYaw * 0.017453292F) / 20F;
            vehicleMotionX = (-currentSpeed * f1);
            vehicleMotionZ = (currentSpeed * f2);
            motionX *= 0.5;
            motionZ *= 0.5;
        }
        else if(state == State.IN_AIR)
        {
            motionY -= 0.08D;
            if(previousState == State.UNDER_WATER || previousState == State.ON_WATER)
            {
                motionX = vehicleMotionX;
                motionZ = vehicleMotionZ;
                vehicleMotionX = 0;
                vehicleMotionZ = 0;
            }
        }
        else
        {
            vehicleMotionX *= 0.75F;
            vehicleMotionZ *= 0.75F;
        }
    }

    @Override
    public void updateVehicle()
    {
        previousState = state;
        state = getState();
        if(state == State.IN_AIR)
        {
            deltaYaw *= 2;
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

    protected State getState()
    {
        if(isUnderWater())
        {
            return State.UNDER_WATER;
        }
        else if(isOnWater())
        {
            return State.ON_WATER;
        }
        else if(onGround)
        {
            return State.ON_LAND;
        }
        else
        {
            return State.IN_AIR;
        }
    }

    @Override
    protected void updateGroundState()
    {
        this.wheelsOnGround = this.getState() == State.ON_WATER || this.getState() == State.UNDER_WATER;
    }

    protected enum State
    {
        ON_WATER, UNDER_WATER, IN_AIR, ON_LAND
    }
}
