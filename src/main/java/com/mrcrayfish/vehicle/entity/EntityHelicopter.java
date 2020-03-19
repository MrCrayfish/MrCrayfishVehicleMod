package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageAltitude;
import com.mrcrayfish.vehicle.network.message.MessageTravelProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public abstract class EntityHelicopter extends EntityPoweredVehicle
{
    private static final DataParameter<Integer> ALTITUDE_CHANGE = EntityDataManager.createKey(EntityHelicopter.class, DataSerializers.VARINT);
    private static final DataParameter<Float> LIFT = EntityDataManager.createKey(EntityHelicopter.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> TRAVEL_DIRECTION = EntityDataManager.createKey(EntityHelicopter.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> TRAVEL_SPEED = EntityDataManager.createKey(EntityHelicopter.class, DataSerializers.FLOAT);

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

    protected EntityHelicopter(World worldIn)
    {
        super(worldIn);
        this.setMaxSpeed(18F);
    }

    @Override
    public void entityInit()
    {
        super.entityInit();
        this.dataManager.register(ALTITUDE_CHANGE, AltitudeChange.NONE.ordinal());
        this.dataManager.register(LIFT, 0F);
        this.dataManager.register(TRAVEL_DIRECTION, 0F);
        this.dataManager.register(TRAVEL_SPEED, 0F);
    }

    @Override
    public SoundEvent getMovingSound()
    {
        return null;
    }

    @Override
    public SoundEvent getRidingSound()
    {
        return null;
    }

    @Override
    public void updateVehicleMotion()
    {
        EntityLivingBase entity = (EntityLivingBase) this.getControllingPassenger();
        if(entity != null && this.isFlying())
        {
            float deltaYaw = entity.getRotationYawHead() % 360.0F - rotationYaw;
            while(deltaYaw < -180.0F)
            {
                deltaYaw += 360.0F;
            }
            while(deltaYaw >= 180.0F)
            {
                deltaYaw -= 360.0F;
            }
            this.rotationYaw = rotationYaw + deltaYaw * 0.15F;
        }

        float travelDirection = this.getTravelDirection();
        if(this.getAcceleration() != AccelerationDirection.NONE || this.getTurnDirection() != TurnDirection.FORWARD)
        {
            float newDirX = MathHelper.sin(travelDirection * 0.017453292F) / 20F; //Divide by 20 ticks
            float newDirZ = MathHelper.cos(travelDirection * 0.017453292F) / 20F;
            dirX = dirX + (newDirX - dirX) * 0.05F;
            dirZ = dirZ + (newDirZ - dirZ) * 0.05F;
        }
        vehicleMotionX = (-currentSpeed * dirX);
        vehicleMotionZ = (currentSpeed * dirZ);

        this.updateLift();
        if(this.isFueled())
        {
            motionY = lift * this.getBladeSpeedNormal();
            motionY -= 0.05 + (1.0 - this.getBladeSpeedNormal()) * 0.45;
        }
        else
        {
            motionY -= (0.08D - 0.08D * this.getBladeSpeedNormal());
        }
    }

    @Override
    protected void updateSpeed()
    {
        currentSpeed = this.getSpeed();

        EngineTier engineTier = this.getEngineTier();
        AccelerationDirection acceleration = this.getAcceleration();
        TurnDirection turnDirection = this.getTurnDirection();
        if(this.getControllingPassenger() != null)
        {
            if(!this.isFlying())
            {
                currentSpeed *= 0.75;
                return;
            }

            if(this.canDrive())
            {
                if(this.getTravelSpeed() != 0.0F)
                {
                    float maxSpeed = this.getActualMaxSpeed() * this.getTravelSpeed();
                    if(currentSpeed < maxSpeed)
                    {
                        currentSpeed += this.getModifiedAccelerationSpeed() * engineTier.getAccelerationMultiplier();
                        if(currentSpeed > maxSpeed)
                        {
                            currentSpeed = maxSpeed;
                        }
                    }
                    if(currentSpeed > maxSpeed)
                    {
                        currentSpeed *= 0.975F;
                    }
                }
                else
                {
                    currentSpeed *= 0.95;
                }
            }
            else
            {
                currentSpeed *= 0.9;
            }
        }
        else
        {
            currentSpeed *= 0.5;
        }
    }

    @Override
    public void updateVehicle()
    {
        prevBladeRotation = bladeRotation;

        if(this.canDrive() && this.getControllingPassenger() != null)
        {
            bladeSpeed += 0.5F;
            if(bladeSpeed > 60F)
            {
                bladeSpeed = 60F;
            }
        }
        else
        {
            bladeSpeed *= 0.98F;
        }
        bladeRotation += bladeSpeed;
    }

    protected void updateLift()
    {
        AltitudeChange altitudeChange = this.getAltitudeChange();
        if(altitudeChange == AltitudeChange.POSITIVE)
        {
            lift += 0.05F;
        }
        else if(altitudeChange == AltitudeChange.NEGATIVE)
        {
            lift -= 0.05F;
        }
        else
        {
            lift *= 0.85F;
        }
        lift = MathHelper.clamp(lift, -0.5F, 0.25F);
        this.setLift(lift);
    }

    @Override
    public void onClientUpdate()
    {
        super.onClientUpdate();

        prevBodyRotationX = bodyRotationX;
        prevBodyRotationY = bodyRotationY;
        prevBodyRotationZ = bodyRotationZ;

        EntityLivingBase entity = (EntityLivingBase) this.getControllingPassenger();
        if(entity != null && entity.equals(Minecraft.getMinecraft().player))
        {
            AltitudeChange altitudeChange = VehicleMod.proxy.getAltitudeChange();
            if(this.getAltitudeChange() != altitudeChange)
            {
                this.setAltitudeChange(altitudeChange);
                PacketHandler.INSTANCE.sendToServer(new MessageAltitude(altitudeChange));
            }

            float travelDirection = VehicleMod.proxy.getTravelDirection(this);
            float travelSpeed = VehicleMod.proxy.getTravelSpeed(this);
            this.setTravelDirection(travelDirection);
            this.setTravelSpeed(travelSpeed);
            PacketHandler.INSTANCE.sendToServer(new MessageTravelProperties(travelSpeed, travelDirection));
        }

        if(this.isFlying())
        {
            bodyRotationX = (dirX * 20F * 35F) * this.getActualSpeed();
            bodyRotationZ = (dirZ * 20F * 35F) * this.getActualSpeed();
        }
        else
        {
            bodyRotationX *= 0.5F;
            bodyRotationZ *= 0.5F;
        }
    }

    @Override
    public void addPassenger(Entity passenger)
    {
        super.addPassenger(passenger);
        passenger.rotationYaw = rotationYaw;
    }

    @Override
    protected void updateTurning() {}

    @Override
    public double getMountedYOffset()
    {
        return 0;
    }

    /*
     * Overridden to prevent players from taking fall damage when landing a plane
     */
    @Override
    public void fall(float distance, float damageMultiplier) {}

    public void setAltitudeChange(AltitudeChange altitudeChange)
    {
        this.dataManager.set(ALTITUDE_CHANGE, altitudeChange.ordinal());
    }

    public AltitudeChange getAltitudeChange()
    {
        return AltitudeChange.values()[this.dataManager.get(ALTITUDE_CHANGE)];
    }

    public float getLift()
    {
        return this.dataManager.get(LIFT);
    }

    public void setLift(float lift)
    {
        this.dataManager.set(LIFT, lift);
    }

    public boolean isFlying()
    {
        return !this.onGround;
    }

    public float getBladeSpeedNormal()
    {
        return bladeSpeed / 60F;
    }

    @Override
    public boolean canChangeWheels()
    {
        return false;
    }

    public float getTravelDirection()
    {
        return this.dataManager.get(TRAVEL_DIRECTION);
    }

    public void setTravelDirection(float travelDirection)
    {
        this.dataManager.set(TRAVEL_DIRECTION, travelDirection);
    }

    public float getTravelSpeed()
    {
        return this.dataManager.get(TRAVEL_SPEED);
    }

    public void setTravelSpeed(float travelSpeed)
    {
        this.dataManager.set(TRAVEL_SPEED, travelSpeed);
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
