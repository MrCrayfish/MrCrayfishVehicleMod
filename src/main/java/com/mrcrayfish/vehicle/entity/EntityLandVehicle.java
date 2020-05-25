package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.client.render.Wheel;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageDrift;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public abstract class EntityLandVehicle extends EntityPoweredVehicle
{
    private static final DataParameter<Boolean> DRIFTING = EntityDataManager.createKey(EntityLandVehicle.class, DataSerializers.BOOLEAN);

    public float drifting;
    public float additionalYaw;
    public float prevAdditionalYaw;

    public float frontWheelRotation;
    public float prevFrontWheelRotation;
    public float rearWheelRotation;
    public float prevRearWheelRotation;

    public EntityLandVehicle(World worldIn)
    {
        super(worldIn);
    }

    @Override
    public void entityInit()
    {
        super.entityInit();
        this.dataManager.register(DRIFTING, false);
    }

    @Override
    public void onUpdateVehicle()
    {
        super.onUpdateVehicle();
        this.updateWheels();
    }

    @Override
    public void updateVehicle()
    {
        prevAdditionalYaw = additionalYaw;
        prevFrontWheelRotation = frontWheelRotation;
        prevRearWheelRotation = rearWheelRotation;

        this.updateDrifting();
    }

    @Override
    public void onClientUpdate()
    {
        super.onClientUpdate();
        EntityLivingBase entity = (EntityLivingBase) this.getControllingPassenger();
        if(entity != null && entity.equals(Minecraft.getMinecraft().player))
        {
            boolean drifting = VehicleMod.proxy.isDrifting();
            if(this.isDrifting() != drifting)
            {
                this.setDrifting(drifting);
                PacketHandler.INSTANCE.sendToServer(new MessageDrift(drifting));
            }
        }
    }

    @Override
    public void updateVehicleMotion()
    {
        float currentSpeed = this.currentSpeed;

        if(speedMultiplier > 1.0F)
        {
            speedMultiplier = 1.0F;
        }

        /* Applies the speed multiplier to the current speed */
        currentSpeed = currentSpeed + (currentSpeed * speedMultiplier);

        VehicleProperties properties = this.getProperties();
        if(properties.getFrontAxelVec() != null && properties.getRearAxelVec() != null)
        {
            AccelerationDirection acceleration = this.getAcceleration();
            if(acceleration == AccelerationDirection.CHARGING && this.charging)
            {
                PartPosition bodyPosition = properties.getBodyPosition();
                Vec3d frontAxel = properties.getFrontAxelVec().scale(0.0625F).scale(bodyPosition.getScale());
                Vec3d nextFrontAxel = frontAxel.rotateYaw((this.turnAngle / 20F) * 0.017453292F);
                Vec3d deltaAxel = frontAxel.subtract(nextFrontAxel).rotateYaw(-this.rotationYaw * 0.017453292F);
                double deltaYaw = -this.turnAngle / 20F;
                this.rotationYaw += deltaYaw;
                this.deltaYaw = (float) -deltaYaw;
                this.vehicleMotionX = (float) deltaAxel.x;
                if(!this.launching)
                {
                    this.motionY -= 0.08D;
                }
                this.vehicleMotionZ = (float) deltaAxel.z;
                return;
            }

            PartPosition bodyPosition = properties.getBodyPosition();
            Vec3d nextFrontAxelVec = new Vec3d(0, 0, currentSpeed / 20F).rotateYaw(this.wheelAngle * 0.017453292F);
            nextFrontAxelVec = nextFrontAxelVec.add(properties.getFrontAxelVec().scale(0.0625));
            Vec3d nextRearAxelVec = new Vec3d(0, 0, currentSpeed / 20F);
            nextRearAxelVec = nextRearAxelVec.add(properties.getRearAxelVec().scale(0.0625));
            double deltaYaw = Math.toDegrees(Math.atan2(nextRearAxelVec.z - nextFrontAxelVec.z, nextRearAxelVec.x - nextFrontAxelVec.x)) + 90;
            if(this.isRearWheelSteering())
            {
                deltaYaw -= 180;
            }
            this.rotationYaw += deltaYaw;
            this.deltaYaw = (float) -deltaYaw;

            Vec3d nextVehicleVec = nextFrontAxelVec.add(nextRearAxelVec).scale(0.5);
            nextVehicleVec = nextVehicleVec.subtract(properties.getFrontAxelVec().add(properties.getRearAxelVec()).scale(0.0625).scale(0.5));
            nextVehicleVec = nextVehicleVec.scale(bodyPosition.getScale()).rotateYaw((-this.rotationYaw + 90) * 0.017453292F);

            float targetRotation = (float) Math.toDegrees(Math.atan2(nextVehicleVec.z, nextVehicleVec.x));
            float f1 = MathHelper.sin(targetRotation * 0.017453292F) / 20F * (currentSpeed > 0 ? 1 : -1);
            float f2 = MathHelper.cos(targetRotation * 0.017453292F) / 20F * (currentSpeed > 0 ? 1 : -1);
            this.vehicleMotionX = (-currentSpeed * f1);
            if(!launching)
            {
                this.motionY -= 0.08D;
            }
            this.vehicleMotionZ = (currentSpeed * f2);
        }
        else
        {
            float f1 = MathHelper.sin(this.rotationYaw * 0.017453292F) / 20F; //Divide by 20 ticks
            float f2 = MathHelper.cos(this.rotationYaw * 0.017453292F) / 20F;
            this.vehicleMotionX = (-currentSpeed * f1);
            if(!launching)
            {
                this.motionY -= 0.08D;
            }
            this.vehicleMotionZ = (currentSpeed * f2);
        }
    }

    @Override
    protected void updateTurning()
    {
        this.turnAngle = VehicleMod.proxy.getTargetTurnAngle(this, this.isDrifting());
        this.wheelAngle = this.turnAngle * Math.max(0.1F, 1.0F - Math.abs(currentSpeed / this.getMaxSpeed()));

        VehicleProperties properties = this.getProperties();
        if(properties.getFrontAxelVec() == null || properties.getRearAxelVec() == null)
        {
            this.deltaYaw = this.wheelAngle * (currentSpeed / 30F) / 2F;
        }

        if(world.isRemote)
        {
            this.targetWheelAngle = this.isDrifting() ? -35F * (this.turnAngle / (float) this.getMaxTurnAngle()) * this.getNormalSpeed() : this.wheelAngle - 35F * (this.turnAngle / (float) this.getMaxTurnAngle()) * drifting;
            this.renderWheelAngle = this.renderWheelAngle + (this.targetWheelAngle - this.renderWheelAngle) * (this.isDrifting() ? 0.35F : 0.5F);
        }
    }

    private void updateDrifting()
    {
        TurnDirection turnDirection = this.getTurnDirection();
        if(this.getControllingPassenger() != null && this.isDrifting())
        {
            if(turnDirection != TurnDirection.FORWARD)
            {
                AccelerationDirection acceleration = this.getAcceleration();
                if(acceleration == AccelerationDirection.FORWARD)
                {
                    this.currentSpeed *= 0.975F;
                }
                this.drifting = Math.min(1.0F, this.drifting + 0.025F);
            }
        }
        else
        {
            this.drifting *= 0.95F;
        }
        this.additionalYaw = 25F * drifting * (turnAngle / (float) this.getMaxTurnAngle()) * Math.min(this.getActualMaxSpeed(), this.getActualSpeed() * 2F);

        //Updates the delta yaw to consider drifting
        this.deltaYaw = this.wheelAngle * (currentSpeed / 30F) / (this.isDrifting() ? 1.5F : 2F);
    }

    public void updateWheels()
    {
        VehicleProperties properties = VehicleProperties.getProperties(this.getClass());
        double wheelCircumference = 16.0;
        double vehicleScale = properties.getBodyPosition().getScale();
        double speed = this.getSpeed();

        Wheel frontWheel = properties.getFirstFrontWheel();
        if(frontWheel != null && !this.charging)
        {
            double frontWheelCircumference = wheelCircumference * vehicleScale * frontWheel.getScaleY();
            double rotation = (speed * 16) / frontWheelCircumference;
            this.frontWheelRotation -= rotation * 20F;
        }

        Wheel rearWheel = properties.getFirstRearWheel();
        if(rearWheel != null)
        {
            double rearWheelCircumference = wheelCircumference * vehicleScale * rearWheel.getScaleY();
            double rotation = (speed * 16) / rearWheelCircumference;
            this.rearWheelRotation -= rotation * 20F;
        }
    }

    @Override
    public void createParticles()
    {
        if(this.canDrive())
        {
            super.createParticles();
        }
    }

    @Override
    protected void removePassenger(Entity passenger)
    {
        super.removePassenger(passenger);
        if(this.getControllingPassenger() == null)
        {
            this.rotationYaw -= this.additionalYaw;
            this.additionalYaw = 0;
            this.drifting = 0;
        }
    }

    public void setDrifting(boolean drifting)
    {
        this.dataManager.set(DRIFTING, drifting);
    }

    public boolean isDrifting()
    {
        return this.dataManager.get(DRIFTING);
    }

    @Override
    protected float getModifiedAccelerationSpeed()
    {
        if(trailer != null)
        {
            if(trailer.getPassengers().size() > 0)
            {
                return super.getModifiedAccelerationSpeed() * 0.5F;
            }
            else
            {
                return super.getModifiedAccelerationSpeed() * 0.8F;
            }
        }
        return super.getModifiedAccelerationSpeed();
    }

    @Override
    public float getModifiedRotationYaw()
    {
        return this.rotationYaw - this.additionalYaw;
    }

    public boolean isRearWheelSteering()
    {
        VehicleProperties properties = this.getProperties();
        return properties.getFrontAxelVec() != null && properties.getRearAxelVec() != null && properties.getFrontAxelVec().z < properties.getRearAxelVec().z;
    }

    @Override
    protected boolean canCharge()
    {
        return true;
    }

    public boolean canWheelie()
    {
        return true;
    }
}
