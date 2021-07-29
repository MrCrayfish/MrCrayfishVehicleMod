package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.client.VehicleHelper;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageDrift;
import com.mrcrayfish.vehicle.util.CommonUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Author: MrCrayfish
 */
public abstract class LandVehicleEntity extends PoweredVehicleEntity
{
    private static final DataParameter<Boolean> DRIFTING = EntityDataManager.defineId(LandVehicleEntity.class, DataSerializers.BOOLEAN);

    public float drifting;
    public float additionalYaw;
    public float prevAdditionalYaw;

    @OnlyIn(Dist.CLIENT)
    public float frontWheelRotation;
    @OnlyIn(Dist.CLIENT)
    public float prevFrontWheelRotation;
    @OnlyIn(Dist.CLIENT)
    public float rearWheelRotation;
    @OnlyIn(Dist.CLIENT)
    public float prevRearWheelRotation;

    public LandVehicleEntity(EntityType<?> entityType, World worldIn)
    {
        super(entityType, worldIn);
    }

    @Override
    public void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(DRIFTING, false);
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
        //this.updateDrifting();
    }

    @Override
    public void onClientUpdate()
    {
        super.onClientUpdate();
        LivingEntity entity = (LivingEntity) this.getControllingPassenger();
        if(entity != null && entity.equals(Minecraft.getInstance().player))
        {
            boolean drifting = VehicleHelper.isDrifting();
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
        VehicleProperties properties = this.getProperties();
        if(properties.getFrontAxelVec() == null || properties.getRearAxelVec() == null)
            return;

        float friction = 0.3F;
        float drag = 0.001F;
        float enginePower = 5F;
        float traction = 0.8F;
        Vector3d forward = Vector3d.directionFromRotation(this.getRotationVector());

        // Updates the acceleration. Applies drag and friction
        float forwardForce = enginePower * MathHelper.clamp(this.getThrottle(), -0.5F, 1.0F);

        Vector3d acceleration = forward.scale(forwardForce).scale(0.05);
        if(this.velocity.length() < 0.01) this.velocity = Vector3d.ZERO;
        Vector3d frictionForce = this.velocity.scale(-friction).scale(0.05);
        Vector3d dragForce = this.velocity.scale(this.velocity.length()).scale(-drag).scale(0.05);
        acceleration = acceleration.add(dragForce).add(frictionForce);

        // Calculates the new heading based on the wheel positions
        PartPosition bodyPosition = properties.getBodyPosition();
        double wheelBase = properties.getFrontAxelVec().distanceTo(properties.getRearAxelVec()) * 0.0625 * bodyPosition.getScale();

        //TODO test with steering at the rear
        //Gets the new position of the wheels
        Vector3d frontWheel = this.position().add(forward.scale(wheelBase / 2.0));
        Vector3d rearWheel = this.position().add(forward.scale(-wheelBase / 2.0));
        frontWheel = frontWheel.add(this.velocity.yRot((float) Math.toRadians(this.getSteeringAngle())).scale(0.05));
        rearWheel = rearWheel.add(this.velocity.scale(0.05));

        // Updates the delta movement based on the new wheel positions
        Vector3d nextPosition = frontWheel.add(rearWheel).scale(0.5);
        Vector3d nextMovement = nextPosition.subtract(this.position());
        this.setDeltaMovement(nextMovement);

        // Updates the velocity based on the heading
        Vector3d heading = frontWheel.subtract(rearWheel).normalize();
        if(heading.dot(this.velocity.normalize()) > 0)
        {
            this.velocity = CommonUtils.lerp(this.velocity, heading.scale(this.velocity.length()), traction);
            //this.velocity = heading.scale(this.velocity.length());
        }
        else
        {
            this.velocity = heading.scale(-1).scale(Math.min(this.velocity.length(), 5F));
        }

        this.velocity = this.velocity.add(acceleration);

        // Calculates the difference from the old yaw to the new yaw
        this.deltaYaw = CommonUtils.yaw(forward) - CommonUtils.yaw(heading);
        if(this.deltaYaw < -180.0D)
        {
            this.deltaYaw += 360.0F;
        }
        else if(this.deltaYaw >= 180.0D)
        {
            this.deltaYaw -= 360.0F;
        }
        this.yRot -= this.deltaYaw;

        //TODO need to reintegrate
        //this.speedMultiplier

        //TODO add back charging
        /*AccelerationDirection acceleration = this.getAcceleration();
        if(acceleration == AccelerationDirection.CHARGING && this.charging)
        {
            PartPosition bodyPosition = properties.getBodyPosition();
            Vector3d frontAxel = properties.getFrontAxelVec().scale(0.0625F).scale(bodyPosition.getScale());
            Vector3d nextFrontAxel = frontAxel.yRot((this.turnAngle / 20F) * 0.017453292F);
            Vector3d deltaAxel = frontAxel.subtract(nextFrontAxel).yRot(-this.yRot * 0.017453292F);
            double deltaYaw = -this.turnAngle / 20F;
            this.yRot += deltaYaw;
            this.deltaYaw = (float) -deltaYaw;
            this.vehicleMotionX = (float) deltaAxel.x();
            if(!this.launching)
            {
                this.setDeltaMovement(this.getDeltaMovement().add(0, -0.08, 0));
            }
            this.vehicleMotionZ = (float) deltaAxel.z();
            return;
        }*/
    }

    @Override
    protected void updateTurning()
    {
        if(this.level.isClientSide())
        {
            this.steeringAngle = VehicleHelper.getSteeringAngle(this, this.isDrifting());
        }
        else
        {
            this.steeringAngle = 0F;
        }

        this.wheelAngle = this.steeringAngle * Math.max(0.45F, 1.0F - Math.abs(this.currentSpeed / 20F));

        VehicleProperties properties = this.getProperties();
        if(properties.getFrontAxelVec() == null || properties.getRearAxelVec() == null)
        {
            this.deltaYaw = this.wheelAngle * (this.currentSpeed / 30F) / 2F;
        }

        //TODO fix wheel angle for drifting
        /*if(this.level.isClientSide)
        {
            this.targetWheelAngle = this.isDrifting() ? -35F * (this.turnAngle / (float) this.getMaxSteeringAngle()) * this.getNormalSpeed() : this.wheelAngle - 35F * (this.turnAngle / (float) this.getMaxSteeringAngle()) * drifting;
            this.renderWheelAngle = this.renderWheelAngle + (this.targetWheelAngle - this.renderWheelAngle) * (this.isDrifting() ? 0.35F : 0.5F);
        }*/
    }

    /*private void updateDrifting()
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
        this.additionalYaw = 25F * this.drifting * (this.turnAngle / (float) this.getMaxSteeringAngle()) * Math.min(this.getActualMaxSpeed(), this.getActualSpeed() * 2F);

        //Updates the delta yaw to consider drifting
        this.deltaYaw = this.wheelAngle * (this.currentSpeed / 30F) / (this.isDrifting() ? 1.5F : 2F);
    }*/

    public void updateWheels()
    {
        VehicleProperties properties = this.getProperties();
        double wheelCircumference = 24.0;
        double vehicleScale = properties.getBodyPosition().getScale();
        Vector3d forward = Vector3d.directionFromRotation(this.getRotationVector());
        double speed = this.velocity.length() * forward.dot(this.velocity.normalize());

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
            this.yRot -= this.additionalYaw;
            this.additionalYaw = 0;
            this.drifting = 0;
        }
    }

    public void setDrifting(boolean drifting)
    {
        this.entityData.set(DRIFTING, drifting);
    }

    public boolean isDrifting()
    {
        return this.entityData.get(DRIFTING);
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
        return this.yRot - this.additionalYaw;
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
