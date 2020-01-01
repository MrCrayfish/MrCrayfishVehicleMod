package com.mrcrayfish.vehicle.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public abstract class BoatEntity extends PoweredVehicleEntity
{
    protected State state;
    protected State previousState;
    private float waterLevel;

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
        if(this.state == State.ON_WATER || this.state == State.UNDER_WATER)
        {
            if(this.state == State.UNDER_WATER)
            {
                this.getMotion().add(0, 0.12, 0);
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
            if(this.previousState == State.UNDER_WATER || this.previousState == State.ON_WATER)
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
        previousState = state;
        state = getState();
        if(state == State.IN_AIR)
        {
            deltaYaw *= 2;
        }
    }

    private boolean checkInWater()
    {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.ceil(axisalignedbb.maxX);
        int k = MathHelper.floor(axisalignedbb.minY);
        int l = MathHelper.ceil(axisalignedbb.minY + 0.001D);
        int i1 = MathHelper.floor(axisalignedbb.minZ);
        int j1 = MathHelper.ceil(axisalignedbb.maxZ);
        boolean flag = false;
        this.waterLevel = Float.MIN_VALUE;

        try(BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.retain())
        {
            for(int k1 = i; k1 < j; ++k1)
            {
                for(int l1 = k; l1 < l; ++l1)
                {
                    for(int i2 = i1; i2 < j1; ++i2)
                    {
                        pooledMutable.setPos(k1, l1, i2);
                        IFluidState ifluidstate = this.world.getFluidState(pooledMutable);
                        if(ifluidstate.isTagged(FluidTags.WATER))
                        {
                            float f = (float) l1 + ifluidstate.func_215679_a(this.world, pooledMutable);
                            this.waterLevel = Math.max(f, this.waterLevel);
                            flag |= axisalignedbb.minY < (double) f;
                        }
                    }
                }
            }
        }

        return flag;
    }

    private boolean isOnWater()
    {
        this.waterLevel = Float.MIN_VALUE;

        AxisAlignedBB axisAligned = this.getBoundingBox();
        int minX = MathHelper.floor(axisAligned.minX);
        int maxX = MathHelper.ceil(axisAligned.maxX);
        int minY = MathHelper.floor(axisAligned.minY);
        int maxY = MathHelper.ceil(axisAligned.minY + 0.01D);
        int minZ = MathHelper.floor(axisAligned.minZ);
        int maxZ = MathHelper.ceil(axisAligned.maxZ);

        boolean inWater = false;
        try(BlockPos.PooledMutable mutableBlockPos = BlockPos.PooledMutable.retain())
        {
            for(int x = minX; x < maxX; ++x)
            {
                for(int y = minY; y < maxY; ++y)
                {
                    for(int z = minZ; z < maxZ; ++z)
                    {
                        mutableBlockPos.setPos(x, y, z);
                        IFluidState fluidState = this.world.getFluidState(mutableBlockPos);
                        if (fluidState.isTagged(FluidTags.WATER))
                        {
                            this.waterLevel = Math.max(this.waterLevel, fluidState.func_215679_a(this.world, mutableBlockPos));
                            return axisAligned.minY < (double) this.waterLevel;
                        }
                        /*BlockState state = this.world.getBlockState(mutableBlockPos);
                        if(state.getMaterial() == Material.WATER)
                        {
                            float liquidHeight = BlockLiquid.getLiquidHeight(state, world, mutableBlockPos);
                            this.waterLevel = Math.max(liquidHeight, this.waterLevel);
                            return axisAligned.minY < (double) liquidHeight;
                        }*/
                    }
                }
            }
        }
        return inWater;
    }

    private boolean isUnderWater()
    {
        AxisAlignedBB axisAligned = this.getBoundingBox();
        double height = axisAligned.minY + 0.001D;
        int minX = MathHelper.floor(axisAligned.minX);
        int maxX = MathHelper.ceil(axisAligned.maxX);
        int minY = MathHelper.floor(axisAligned.minY);
        int maxY = MathHelper.ceil(height);
        int minZ = MathHelper.floor(axisAligned.minZ);
        int maxZ = MathHelper.ceil(axisAligned.maxZ);

        boolean flag = false;
        try(BlockPos.PooledMutable mutableBlockPos = BlockPos.PooledMutable.retain())
        {
            for (int x = minX; x < maxX; ++x)
            {
                for (int y = minY; y < maxY; ++y)
                {
                    for (int z = minZ; z < maxZ; ++z)
                    {
                        mutableBlockPos.setPos(x, y + 1, z);

                        IFluidState fluidState = this.world.getFluidState(mutableBlockPos);
                        if (fluidState.isTagged(FluidTags.WATER))
                        {
                            if(height < (double) fluidState.func_215679_a(this.world, mutableBlockPos))
                            {
                                if(fluidState.getLevel() != 0)
                                {
                                    return true;
                                }
                                flag = true;
                            }
                        }
                    }
                }
            }
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
