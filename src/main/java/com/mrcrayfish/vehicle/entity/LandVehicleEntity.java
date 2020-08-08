package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.client.render.Wheel;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageDrift;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public abstract class LandVehicleEntity extends PoweredVehicleEntity
{
    private static final DataParameter<Boolean> DRIFTING = EntityDataManager.createKey(LandVehicleEntity.class, DataSerializers.BOOLEAN);

    public float drifting;
    public float additionalYaw;
    public float prevAdditionalYaw;

    public float frontWheelRotation;
    public float prevFrontWheelRotation;
    public float rearWheelRotation;
    public float prevRearWheelRotation;

    public LandVehicleEntity(EntityType<?> entityType, World worldIn)
    {
        super(entityType, worldIn);
    }

    @Override
    public void registerData()
    {
        super.registerData();
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
        this.prevAdditionalYaw = this.additionalYaw;
        this.prevFrontWheelRotation = this.frontWheelRotation;
        this.prevRearWheelRotation = this.rearWheelRotation;
        this.updateDrifting();
    }

    @Override
    public void onClientUpdate()
    {
        super.onClientUpdate();
        LivingEntity entity = (LivingEntity) this.getControllingPassenger();
        if(entity != null && entity.equals(Minecraft.getInstance().player))
        {
            boolean drifting = VehicleMod.PROXY.isDrifting();
            if(this.isDrifting() != drifting)
            {
                this.setDrifting(drifting);
                PacketHandler.instance.sendToServer(new MessageDrift(drifting));
            }
        }
    }

    @Override
    public void updateVehicleMotion()
    {
        float currentSpeed = this.currentSpeed;

        if(this.speedMultiplier > 1.0F)
        {
            this.speedMultiplier = 1.0F;
        }

        /* Applies the speed multiplier to the current speed */
        currentSpeed = currentSpeed + (currentSpeed * this.speedMultiplier);

        VehicleProperties properties = this.getProperties();
        if(properties.getFrontAxelVec() != null && properties.getRearAxelVec() != null)
        {
            AccelerationDirection acceleration = this.getAcceleration();
            if(acceleration == AccelerationDirection.CHARGING && this.charging)
            {
                PartPosition bodyPosition = properties.getBodyPosition();
                Vector3d frontAxel = properties.getFrontAxelVec().scale(0.0625F).scale(bodyPosition.getScale());
                Vector3d nextFrontAxel = frontAxel.rotateYaw((this.turnAngle / 20F) * 0.017453292F);
                Vector3d deltaAxel = frontAxel.subtract(nextFrontAxel).rotateYaw(-this.rotationYaw * 0.017453292F);
                double deltaYaw = -this.turnAngle / 20F;
                this.rotationYaw += deltaYaw;
                this.deltaYaw = (float) -deltaYaw;
                this.vehicleMotionX = (float) deltaAxel.getX();
                if(!this.launching)
                {
                    this.setMotion(this.getMotion().add(0, -0.08, 0));
                }
                this.vehicleMotionZ = (float) deltaAxel.getZ();
                return;
            }

            PartPosition bodyPosition = properties.getBodyPosition();
            Vector3d nextFrontAxelVec = new Vector3d(0, 0, currentSpeed / 20F).rotateYaw(this.wheelAngle * 0.017453292F);
            nextFrontAxelVec = nextFrontAxelVec.add(properties.getFrontAxelVec().scale(0.0625));
            Vector3d nextRearAxelVec = new Vector3d(0, 0, currentSpeed / 20F);
            nextRearAxelVec = nextRearAxelVec.add(properties.getRearAxelVec().scale(0.0625));
            double deltaYaw = Math.toDegrees(Math.atan2(nextRearAxelVec.z - nextFrontAxelVec.z, nextRearAxelVec.x - nextFrontAxelVec.x)) + 90;
            if(this.isRearWheelSteering())
            {
                deltaYaw -= 180;
            }
            this.rotationYaw += deltaYaw;
            this.deltaYaw = (float) -deltaYaw;

            Vector3d nextVehicleVec = nextFrontAxelVec.add(nextRearAxelVec).scale(0.5);
            nextVehicleVec = nextVehicleVec.subtract(properties.getFrontAxelVec().add(properties.getRearAxelVec()).scale(0.0625).scale(0.5));
            nextVehicleVec = nextVehicleVec.scale(bodyPosition.getScale()).rotateYaw((-this.rotationYaw + 90) * 0.017453292F);

            float targetRotation = (float) Math.toDegrees(Math.atan2(nextVehicleVec.z, nextVehicleVec.x));
            float f1 = MathHelper.sin(targetRotation * 0.017453292F) / 20F * (currentSpeed > 0 ? 1 : -1);
            float f2 = MathHelper.cos(targetRotation * 0.017453292F) / 20F * (currentSpeed > 0 ? 1 : -1);
            this.vehicleMotionX = (-currentSpeed * f1);
            if(!launching)
            {
                this.setMotion(this.getMotion().add(0, -0.08, 0));
            }
            this.vehicleMotionZ = (currentSpeed * f2);
        }
        else
        {
            float f1 = MathHelper.sin(this.rotationYaw * 0.017453292F) / 20F;
            float f2 = MathHelper.cos(this.rotationYaw * 0.017453292F) / 20F;
            this.vehicleMotionX = (-currentSpeed * f1);
            if(!launching)
            {
                this.setMotion(this.getMotion().add(0, -0.08, 0));
            }
            this.vehicleMotionZ = (currentSpeed * f2);
        }
    }

    @Override
    protected void updateTurning()
    {
        this.turnAngle = VehicleMod.PROXY.getTargetTurnAngle(this, this.isDrifting());
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
        this.additionalYaw = 25F * this.drifting * (this.turnAngle / (float) this.getMaxTurnAngle()) * Math.min(this.getActualMaxSpeed(), this.getActualSpeed() * 2F);

        //Updates the delta yaw to consider drifting
        this.deltaYaw = this.wheelAngle * (this.currentSpeed / 30F) / (this.isDrifting() ? 1.5F : 2F);
    }

    public void updateWheels()
    {
        VehicleProperties properties = this.getProperties();
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
