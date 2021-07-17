package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.client.VehicleHelper;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageAltitude;
import com.mrcrayfish.vehicle.network.message.MessageTravelProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public abstract class HelicopterEntity extends PoweredVehicleEntity
{
    private static final DataParameter<Integer> ALTITUDE_CHANGE = EntityDataManager.defineId(HelicopterEntity.class, DataSerializers.INT);
    private static final DataParameter<Float> LIFT = EntityDataManager.defineId(HelicopterEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> TRAVEL_DIRECTION = EntityDataManager.defineId(HelicopterEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> TRAVEL_SPEED = EntityDataManager.defineId(HelicopterEntity.class, DataSerializers.FLOAT);

    private float lift;

    private float bladeSpeed;
    public float bladeRotation;
    public float prevBladeRotation;

    public float prevBodyRotationX;
    public float prevBodyRotationY;
    public float prevBodyRotationZ;

    public float bodyRotationX;
    public float bodyRotationY;
    public float bodyRotationZ;

    public float dirX;
    public float dirZ;

    protected HelicopterEntity(EntityType<?> entityType, World worldIn)
    {
        super(entityType, worldIn);
        this.setMaxSpeed(18F);
    }

    @Override
    public void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(ALTITUDE_CHANGE, AltitudeChange.NONE.ordinal());
        this.entityData.define(LIFT, 0F);
        this.entityData.define(TRAVEL_DIRECTION, 0F);
        this.entityData.define(TRAVEL_SPEED, 0F);
    }

    @Override
    public SoundEvent getEngineSound()
    {
        return null;
    }

    @Override
    public void updateVehicleMotion()
    {
        Entity entity = this.getControllingPassenger();
        if(entity != null && this.isFlying())
        {
            float deltaYaw = entity.getYHeadRot() % 360.0F - yRot;
            while(deltaYaw < -180.0F)
            {
                deltaYaw += 360.0F;
            }
            while(deltaYaw >= 180.0F)
            {
                deltaYaw -= 360.0F;
            }
            this.yRot = this.yRot + deltaYaw * 0.15F;
        }

        float travelDirection = this.getTravelDirection();
        if(this.getAcceleration() != AccelerationDirection.NONE || this.getTurnDirection() != TurnDirection.FORWARD)
        {
            float newDirX = MathHelper.sin(travelDirection * 0.017453292F) / 20F; //Divide by 20 ticks
            float newDirZ = MathHelper.cos(travelDirection * 0.017453292F) / 20F;
            this.dirX = this.dirX + (newDirX -this.dirX) * 0.05F;
            this.dirZ = this.dirZ + (newDirZ - this.dirZ) * 0.05F;
        }
        this.vehicleMotionX = (-this.currentSpeed * this.dirX);
        this.vehicleMotionZ = (this.currentSpeed * this.dirZ);

        Vector3d motion = this.getDeltaMovement();
        double motionY = motion.y();
        this.updateLift();
        if(this.isFueled())
        {
            motionY = this.lift * this.getBladeSpeedNormal();
            motionY -= 0.05 + (1.0 - this.getBladeSpeedNormal()) * 0.45;

        }
        else
        {
            motionY -= (0.08D - 0.08D * this.getBladeSpeedNormal());
        }
        this.setDeltaMovement(motion.x(), motionY, motion.z());
    }

    @Override
    protected void updateSpeed()
    {
        this.currentSpeed = this.getSpeed();

        Optional<IEngineTier> optional = this.getEngineTier();
        if(this.getControllingPassenger() != null && optional.isPresent())
        {
            if(!this.isFlying())
            {
                this.currentSpeed *= 0.75;
                return;
            }

            if(this.canDrive())
            {
                if(this.getTravelSpeed() != 0.0F)
                {
                    float maxSpeed = this.getActualMaxSpeed() * this.getTravelSpeed();
                    if(this.currentSpeed < maxSpeed)
                    {
                        IEngineTier engineTier = optional.get();
                        this.currentSpeed += this.getModifiedAccelerationSpeed() * engineTier.getAccelerationMultiplier();
                        if(this.currentSpeed > maxSpeed)
                        {
                            this.currentSpeed = maxSpeed;
                        }
                    }
                    if(this.currentSpeed > maxSpeed)
                    {
                        this.currentSpeed *= 0.975F;
                    }
                }
                else
                {
                    this.currentSpeed *= 0.95;
                }
            }
            else
            {
                this.currentSpeed *= 0.9;
            }
        }
        else
        {
            this.currentSpeed *= 0.5;
        }
    }

    @Override
    public void updateVehicle()
    {
        this.prevBladeRotation = this.bladeRotation;

        if(this.canDrive() && this.getControllingPassenger() != null)
        {
            this.bladeSpeed += 0.5F;
            if(this.bladeSpeed > 60F)
            {
                this.bladeSpeed = 60F;
            }
        }
        else
        {
            this.bladeSpeed *= 0.98F;
        }
        this.bladeRotation += this.bladeSpeed;
    }

    protected void updateLift()
    {
        AltitudeChange altitudeChange = this.getAltitudeChange();
        if(altitudeChange == AltitudeChange.POSITIVE)
        {
            this.lift += 0.05F;
        }
        else if(altitudeChange == AltitudeChange.NEGATIVE)
        {
            this.lift -= 0.05F;
        }
        else
        {
            lift *= 0.85F;
        }
        lift = MathHelper.clamp(this.lift, -0.5F, 0.25F);
        this.setLift(this.lift);
    }

    @Override
    public void onClientUpdate()
    {
        super.onClientUpdate();

        this.prevBodyRotationX = this.bodyRotationX;
        this.prevBodyRotationY = this.bodyRotationY;
        this.prevBodyRotationZ = this.bodyRotationZ;

        Entity entity = this.getControllingPassenger();
        if(entity != null && entity.equals(Minecraft.getInstance().player))
        {
            AltitudeChange altitudeChange = VehicleHelper.getAltitudeChange();
            if(this.getAltitudeChange() != altitudeChange)
            {
                this.setAltitudeChange(altitudeChange);
                PacketHandler.instance.sendToServer(new MessageAltitude(altitudeChange));
            }

            float travelDirection = VehicleHelper.getTravelDirection(this);
            float travelSpeed = VehicleHelper.getTravelSpeed(this);
            this.setTravelDirection(travelDirection);
            this.setTravelSpeed(travelSpeed);
            PacketHandler.instance.sendToServer(new MessageTravelProperties(travelSpeed, travelDirection));
        }

        if(this.isFlying())
        {
            this.bodyRotationX = (this.dirX * 20F * 35F) * this.getActualSpeed();
            this.bodyRotationZ = (this.dirZ * 20F * 35F) * this.getActualSpeed();
        }
        else
        {
            this.bodyRotationX *= 0.5F;
            this.bodyRotationZ *= 0.5F;
        }
    }

    @Override
    public void addPassenger(Entity passenger)
    {
        super.addPassenger(passenger);
        passenger.yRot = this.yRot;
    }

    @Override
    protected void updateTurning() {}

    @Override
    public double getPassengersRidingOffset()
    {
        return 0;
    }

    /*
     * Overridden to prevent players from taking fall damage when landing a plane
     */
    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier)
    {
        return false;
    }

    public void setAltitudeChange(AltitudeChange altitudeChange)
    {
        this.entityData.set(ALTITUDE_CHANGE, altitudeChange.ordinal());
    }

    public AltitudeChange getAltitudeChange()
    {
        return AltitudeChange.values()[this.entityData.get(ALTITUDE_CHANGE)];
    }

    public float getLift()
    {
        return this.entityData.get(LIFT);
    }

    public void setLift(float lift)
    {
        this.entityData.set(LIFT, lift);
    }

    public boolean isFlying()
    {
        return !this.onGround;
    }

    public float getBladeSpeedNormal()
    {
        return this.bladeSpeed / 60F;
    }

    @Override
    public boolean canChangeWheels()
    {
        return false;
    }

    public float getTravelDirection()
    {
        return this.entityData.get(TRAVEL_DIRECTION);
    }

    public void setTravelDirection(float travelDirection)
    {
        this.entityData.set(TRAVEL_DIRECTION, travelDirection);
    }

    public float getTravelSpeed()
    {
        return this.entityData.get(TRAVEL_SPEED);
    }

    public void setTravelSpeed(float travelSpeed)
    {
        this.entityData.set(TRAVEL_SPEED, travelSpeed);
    }

    public enum AltitudeChange
    {
        POSITIVE, NEGATIVE, NONE;

        public static AltitudeChange fromInput(boolean up, boolean down)
        {
            return up && !down ? POSITIVE : down && !up ? NEGATIVE : NONE;
        }
    }
}
