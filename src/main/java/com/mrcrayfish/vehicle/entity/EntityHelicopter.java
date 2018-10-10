package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageAltitude;
import net.minecraft.client.Minecraft;
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
    private static final DataParameter<Integer> ALTITUDE_CHANGE = EntityDataManager.createKey(EntityPlane.class, DataSerializers.VARINT);
    private static final DataParameter<Float> LIFT = EntityDataManager.createKey(EntityPlane.class, DataSerializers.FLOAT);

    private float lift;

    private float bladeSpeed;
    private float bladeRotation;
    private float prevBladeRotation;

    public float prevBodyRotationX;
    public float prevBodyRotationY;
    public float prevBodyRotationZ;

    public float bodyRotationX;
    public float bodyRotationY;
    public float bodyRotationZ;

    private float travelDirection;
    private float dirX;
    private float dirZ;

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
        if(entity != null)
        {
            this.rotationYaw = entity.getRotationYawHead();
        }

        float travelDirection = this.getTravelDirection();
        if(this.getAcceleration() != AccelerationDirection.NONE || this.getTurnDirection() != TurnDirection.FORWARD)
        {
            float newDirX = MathHelper.sin(travelDirection * 0.017453292F) / 20F; //Divide by 20 ticks
            float newDirZ = MathHelper.cos(travelDirection * 0.017453292F) / 20F;
            dirX = dirX + (newDirX - dirX) * 0.05F;
            dirZ = dirZ + (newDirZ - dirZ) * 0.05F;
        }
        this.vehicleMotionX = (-currentSpeed * dirX);
        this.vehicleMotionZ = (currentSpeed * dirZ);

        this.updateLift();
        this.motionY = this.lift * this.getBladeSpeedNormal();
        this.motionY -= 0.05 + (1.0 - this.getBladeSpeedNormal()) * 0.45;
    }

    @Override
    protected void updateSpeed()
    {
        currentSpeed = this.getSpeed();

        EngineType engineType = this.getEngineType();
        AccelerationDirection acceleration = this.getAcceleration();
        TurnDirection turnDirection = this.getTurnDirection();
        if(this.getControllingPassenger() != null)
        {
            if(this.canDrive())
            {
                if(acceleration != AccelerationDirection.NONE || turnDirection != TurnDirection.FORWARD)
                {
                    this.currentSpeed += this.getAccelerationSpeed() * engineType.getAccelerationMultiplier();
                    if(this.currentSpeed > this.getMaxSpeed() + engineType.getAdditionalMaxSpeed())
                    {
                        this.currentSpeed = this.getMaxSpeed() + engineType.getAdditionalMaxSpeed();
                    }
                }
                else
                {
                    this.currentSpeed *= 0.9;
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

        if(!isFlying())
        {
            this.currentSpeed *= 0.75;
        }
    }

    @Override
    public void updateVehicle()
    {
        prevBladeRotation = bladeRotation;

        if(this.canDrive() && this.getControllingPassenger() != null)
        {
            bladeSpeed += 1F;
            if(bladeSpeed > 120F)
            {
                bladeSpeed = 120F;
            }
        }
        else
        {
            bladeSpeed *= 0.75F;
        }
        bladeRotation += bladeSpeed;
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
            this.lift *= 0.85F;
        }
        this.lift = MathHelper.clamp(this.lift, -1.0F, 0.25F);
        this.setLift(this.lift);
    }

    private float getTravelDirection()
    {
        float travelDirection = this.rotationYaw;
        AccelerationDirection accelerationDirection = this.getAcceleration();
        TurnDirection turnDirection = this.getTurnDirection();
        if(this.getControllingPassenger() != null)
        {
            if(accelerationDirection == AccelerationDirection.FORWARD)
            {
                travelDirection += turnDirection.dir * -45F;
            }
            else if(accelerationDirection == AccelerationDirection.REVERSE)
            {
                travelDirection += 180F;
                travelDirection += turnDirection.dir * 45F;
            }
            else
            {
                travelDirection += turnDirection.dir * -90F;
            }
        }
        return travelDirection;
    }

    @Override
    public void onClientUpdate()
    {
        super.onClientUpdate();

        this.prevBodyRotationX = this.bodyRotationX;
        this.prevBodyRotationY = this.bodyRotationY;
        this.prevBodyRotationZ = this.bodyRotationZ;

        EntityLivingBase entity = (EntityLivingBase) this.getControllingPassenger();
        if(entity != null && entity.equals(Minecraft.getMinecraft().player))
        {
            boolean flapUp = Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown();
            boolean flapDown = Minecraft.getMinecraft().gameSettings.keyBindSprint.isKeyDown();

            AltitudeChange altitudeChange = AltitudeChange.fromInput(flapUp, flapDown);
            if(this.getAltitudeChange() != altitudeChange)
            {
                this.setAltitudeChange(altitudeChange);
                PacketHandler.INSTANCE.sendToServer(new MessageAltitude(altitudeChange));
            }
        }

        if(this.isFlying())
        {
            this.bodyRotationX = (float) -Math.toDegrees(Math.atan2(motionY, currentSpeed / 20F));
            this.bodyRotationZ = (this.turnAngle / (float) getMaxTurnAngle()) * 20F;
        }
        else
        {
            this.bodyRotationX *= 0.5F;
            this.bodyRotationZ *= 0.5F;
        }
    }

    @Override
    protected void updateTurning() {}

    @Override
    public double getMountedYOffset()
    {
        return 0;
    }

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
        return bladeSpeed / 120F;
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
