package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.Config;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public abstract class TrailerEntity extends VehicleEntity
{
    public static final DataParameter<Integer> PULLING_ENTITY = EntityDataManager.defineId(TrailerEntity.class, DataSerializers.INT);

    private Entity pullingEntity;

    public float wheelRotation;
    public float prevWheelRotation;

    public TrailerEntity(EntityType<?> entityType, World worldIn)
    {
        super(entityType, worldIn);
        this.maxUpStep = 1.0F;
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(PULLING_ENTITY, -1);
    }

    @Override
    public boolean isPickable()
    {
        return true;
    }

    @Override
    public void onUpdateVehicle()
    {
        this.prevWheelRotation = this.wheelRotation;

        Vector3d motion = this.getDeltaMovement();
        this.setDeltaMovement(motion.x(), motion.y() - 0.08, motion.z());

        if(this.level.isClientSide)
        {
            int entityId = this.entityData.get(PULLING_ENTITY);
            if(entityId != -1)
            {
                Entity entity = this.level.getEntity(this.entityData.get(PULLING_ENTITY));
                if(entity instanceof PlayerEntity || (entity instanceof VehicleEntity && ((VehicleEntity) entity).canTowTrailer()))
                {
                    this.pullingEntity = entity;
                }
                else if(this.pullingEntity != null)
                {
                    this.pullingEntity = null;
                }
            }
            else if(this.pullingEntity != null)
            {
                this.pullingEntity = null;
            }
        }

        if(this.pullingEntity != null && !this.level.isClientSide)
        {
            double threshold = Config.SERVER.trailerDetachThreshold.get() + Math.abs(this.getHitchOffset() / 16.0) * this.getProperties().getBodyPosition().getScale();
            if(this.pullingEntity.distanceTo(this) > threshold)
            {
                this.level.playSound(null, this.pullingEntity.blockPosition(), SoundEvents.ITEM_BREAK, SoundCategory.PLAYERS, 1.0F, 1.0F);
                this.pullingEntity = null;
                return;
            }
        }

        if(this.pullingEntity != null)
        {
            if(!this.pullingEntity.isAlive() || (this.pullingEntity instanceof VehicleEntity && ((VehicleEntity) this.pullingEntity).getTrailer() != this))
            {
                this.pullingEntity = null;
                return;
            }
            this.updatePullingMotion();
        }
        else if(!level.isClientSide)
        {
            motion = this.getDeltaMovement();
            this.move(MoverType.SELF, new Vector3d(motion.x() * 0.75, motion.y(), motion.z() * 0.75));
        }

        this.checkInsideBlocks();

        float speed = (float) (Math.sqrt(Math.pow(this.getX() - this.xo, 2) + Math.pow(this.getY() - this.yo, 2) + Math.pow(this.getZ() - this.zo, 2)) * 20);
        wheelRotation -= 90F * (speed / 10F);
    }

    private void updatePullingMotion()
    {
        Vector3d towBar = pullingEntity.position();
        if(pullingEntity instanceof VehicleEntity)
        {
            VehicleEntity vehicle = (VehicleEntity) pullingEntity;
            Vector3d towBarVec = vehicle.getProperties().getTowBarPosition();
            towBarVec = new Vector3d(towBarVec.x * 0.0625, towBarVec.y * 0.0625, towBarVec.z * 0.0625 + vehicle.getProperties().getBodyPosition().getZ());
            if(vehicle instanceof LandVehicleEntity)
            {
                LandVehicleEntity landVehicle = (LandVehicleEntity) vehicle;
                towBar = towBar.add(towBarVec.yRot((float) Math.toRadians(-vehicle.yRot + landVehicle.additionalYaw)));
            }
            else
            {
                towBar = towBar.add(towBarVec.yRot((float) Math.toRadians(-vehicle.yRot)));
            }
        }

        this.yRot = (float) Math.toDegrees(Math.atan2(towBar.z - this.getZ(), towBar.x - this.getX()) - Math.toRadians(90F));
        double deltaRot = (double) (this.yRotO - this.yRot);
        if (deltaRot < -180.0D)
        {
            this.yRotO += 360.0F;
        }
        else if (deltaRot >= 180.0D)
        {
            this.yRotO -= 360.0F;
        }

        Vector3d vec = new Vector3d(0, 0, this.getHitchOffset() * 0.0625).yRot((float) Math.toRadians(-this.yRot)).add(towBar);
        Vector3d motion = this.getDeltaMovement();
        this.setDeltaMovement(vec.x - this.getX(), motion.y(), vec.z - this.getZ());
        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    @Override
    public double getPassengersRidingOffset()
    {
        return 0.0;
    }

    public boolean setPullingEntity(Entity pullingEntity)
    {
        if(pullingEntity instanceof PlayerEntity || (pullingEntity instanceof VehicleEntity && pullingEntity.getVehicle() == null && ((VehicleEntity) pullingEntity).canTowTrailer()))
        {
            this.pullingEntity = pullingEntity;
            this.entityData.set(PULLING_ENTITY, pullingEntity.getId());
            return true;
        }
        else
        {
            this.pullingEntity = null;
            this.entityData.set(PULLING_ENTITY, -1);
            return false;
        }
    }

    @Nullable
    public Entity getPullingEntity()
    {
        return pullingEntity;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void lerpTo(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport)
    {
        this.lerpX = x;
        this.lerpY = y;
        this.lerpZ = z;
        this.lerpYaw = (double) yaw;
        this.lerpPitch = (double) pitch;
        this.lerpSteps = 1;
    }

    @Override
    public boolean canMountTrailer()
    {
        return false;
    }

    public abstract double getHitchOffset();

    @Override
    protected boolean canRide(Entity entityIn)
    {
        return false;
    }
}
