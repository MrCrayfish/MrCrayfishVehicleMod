package com.mrcrayfish.vehicle.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public abstract class BoatEntity extends PoweredVehicleEntity
{
    protected State state;
    protected State previousState;
    private double waterLevel;

    public BoatEntity(EntityType<?> entityType, World worldIn)
    {
        super(entityType, worldIn);
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
        if(this.state == State.IN_WATER || this.state == State.UNDER_WATER)
        {
            if(this.state == State.UNDER_WATER)
            {
                this.setMotion(this.getMotion().add(0, 0.08, 0));
            }
            else
            {
                double floatingY = ((this.waterLevel - 0.35D + (0.25D * Math.min(1.0F, getNormalSpeed())) - this.func_226278_cu_())) / (double) this.getHeight();
                this.setMotion(this.getMotion().add(0, floatingY * 0.05, 0));
                if(Math.abs(floatingY) < 0.1 && this.getMotion().y > 0 && Math.abs(this.getMotion().y) < 0.1)
                {
                    this.setPosition(this.func_226277_ct_(), this.waterLevel - 0.35 + (0.25 * Math.min(1.0F, getNormalSpeed())), this.func_226281_cx_());
                    this.setMotion(this.getMotion().mul(1.0, 0.0, 1.0));
                }
                this.setMotion(this.getMotion().mul(1.0, 0.75, 1.0));
            }

            float f1 = MathHelper.sin(this.rotationYaw * 0.017453292F) / 20F;
            float f2 = MathHelper.cos(this.rotationYaw * 0.017453292F) / 20F;
            this.vehicleMotionX = (-currentSpeed * f1);
            this.vehicleMotionZ = (currentSpeed * f2);
            this.setMotion(this.getMotion().mul(0.5, 1.0, 0.5));
        }
        else if(this.state == State.IN_AIR)
        {
            this.setMotion(this.getMotion().add(0, -0.08, 0));
            if(this.previousState == State.UNDER_WATER || this.previousState == State.IN_WATER)
            {
                this.setMotion(new Vec3d(this.vehicleMotionX, this.getMotion().y, this.vehicleMotionZ));
                this.vehicleMotionX = 0;
                this.vehicleMotionZ = 0;
            }
        }
        else
        {
            this.vehicleMotionX *= 0.75F;
            this.vehicleMotionZ *= 0.75F;
        }
    }

    @Override
    public void updateVehicle()
    {
        this.previousState = this.state;
        this.state = this.getState();
        if( this.state == State.IN_AIR)
        {
            this.deltaYaw *= 2;
        }
    }

    private boolean checkInWater()
    {
        AxisAlignedBB boundingBox = this.getBoundingBox();
        int minX = MathHelper.floor(boundingBox.minX);
        int maxX = MathHelper.ceil(boundingBox.maxX);
        int minY = MathHelper.floor(boundingBox.minY);
        int maxY = MathHelper.ceil(boundingBox.minY + 0.001D);
        int minZ = MathHelper.floor(boundingBox.minZ);
        int maxZ = MathHelper.ceil(boundingBox.maxZ);
        boolean inWater = false;
        this.waterLevel = Double.MIN_VALUE;

        try(BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.retain())
        {
            for(int x = minX; x < maxX; x++)
            {
                for(int y = minY; y < maxY; y++)
                {
                    for(int z = minZ; z < maxZ; z++)
                    {
                        pooledMutable.setPos(x, y, z);
                        IFluidState fluidState = this.world.getFluidState(pooledMutable);
                        if(fluidState.isTagged(FluidTags.WATER))
                        {
                            float waterLevel = (float) y + fluidState.func_215679_a(this.world, pooledMutable);
                            this.waterLevel = Math.max((double) waterLevel, this.waterLevel);
                            inWater |= boundingBox.minY < (double) waterLevel;
                        }
                    }
                }
            }
        }

        return inWater;
    }

    @Nullable
    private State getUnderwaterState()
    {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        double height = axisalignedbb.maxY + 0.001D;
        int minX = MathHelper.floor(axisalignedbb.minX);
        int maxX = MathHelper.ceil(axisalignedbb.maxX);
        int minY = MathHelper.floor(axisalignedbb.maxY);
        int maxY = MathHelper.ceil(height);
        int minZ = MathHelper.floor(axisalignedbb.minZ);
        int maxZ = MathHelper.ceil(axisalignedbb.maxZ);
        boolean underWater = false;

        try(BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.retain())
        {
            for(int x = minX; x < maxX; x++)
            {
                for(int y = minY; y < maxY; y++)
                {
                    for(int z = minZ; z < maxZ; z++)
                    {
                        pooledMutable.setPos(x, y, z);
                        IFluidState fluidState = this.world.getFluidState(pooledMutable);
                        if(fluidState.isTagged(FluidTags.WATER) && height < (double) ((float) pooledMutable.getY() + fluidState.func_215679_a(this.world, pooledMutable)))
                        {
                            if(!fluidState.isSource())
                            {
                                return State.UNDER_FLOWING_WATER;
                            }
                            underWater = true;
                        }
                    }
                }
            }
        }

        return underWater ? State.UNDER_WATER : null;
    }

    protected State getState()
    {
        State state = this.getUnderwaterState();
        if(state != null)
        {
            return state;
        }
        else if(this.checkInWater())
        {
            return State.IN_WATER;
        }
        else if(this.onGround)
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
        this.wheelsOnGround = this.getState() == State.IN_WATER || this.getState() == State.UNDER_WATER;
    }

    protected enum State
    {
        IN_WATER,
        UNDER_WATER,
        UNDER_FLOWING_WATER,
        ON_LAND,
        IN_AIR;
    }
}
