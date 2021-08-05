package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.client.VehicleHelper;
import com.mrcrayfish.vehicle.common.SurfaceHelper;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageHandbrake;
import com.mrcrayfish.vehicle.util.CommonUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
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
    private static final DataParameter<Boolean> HANDBRAKE = EntityDataManager.defineId(LandVehicleEntity.class, DataSerializers.BOOLEAN);

    public float traction;

    @OnlyIn(Dist.CLIENT)
    public float frontWheelRotation;
    @OnlyIn(Dist.CLIENT)
    public float prevFrontWheelRotation;
    @OnlyIn(Dist.CLIENT)
    public float rearWheelRotation;
    @OnlyIn(Dist.CLIENT)
    public float prevRearWheelRotation;
    @OnlyIn(Dist.CLIENT)
    public int wheelieCount;
    @OnlyIn(Dist.CLIENT)
    public int prevWheelieCount;

    public LandVehicleEntity(EntityType<?> entityType, World worldIn)
    {
        super(entityType, worldIn);
    }

    @Override
    public void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(HANDBRAKE, false);
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
        this.prevFrontWheelRotation = this.frontWheelRotation;
        this.prevRearWheelRotation = this.rearWheelRotation;
        boolean oldCharging = this.charging;
        this.charging = this.velocity.length() < 1.0 && this.isHandbraking() && this.getThrottle() > 0;
        if(oldCharging && !this.charging && this.chargingAmount > 0F)
        {
            this.releaseCharge(this.chargingAmount);
        }
    }

    @Override
    public void onClientUpdate()
    {
        super.onClientUpdate();

        this.prevWheelieCount = this.wheelieCount;

        LivingEntity entity = (LivingEntity) this.getControllingPassenger();
        if(entity != null && entity.equals(Minecraft.getInstance().player))
        {
            boolean handbraking = VehicleHelper.isHandbraking();
            if(this.isHandbraking() != handbraking)
            {
                this.setHandbraking(handbraking);
                PacketHandler.instance.sendToServer(new MessageHandbrake(handbraking));
            }
        }

        if(this.isBoosting() && this.getControllingPassenger() != null)
        {
            if(this.wheelieCount < MAX_WHEELIE_TICKS)
            {
                this.wheelieCount++;
            }
        }
        else if(this.wheelieCount > 0)
        {
            this.wheelieCount--;
        }
    }

    @Override
    public void updateVehicleMotion()
    {
        VehicleProperties properties = this.getProperties();
        if(properties.getFrontAxelVec() == null || properties.getRearAxelVec() == null)
            return;

        float friction = SurfaceHelper.getFriction(this);
        float drag = 0.001F;
        float enginePower = 15F;
        float brakePower = -1F;

        // Gets the forward vector of the vehicle
        Vector3d forward = Vector3d.directionFromRotation(this.getRotationVector());

        // Calculates the distance between the front and rear axel
        PartPosition bodyPosition = properties.getBodyPosition();
        double wheelBase = properties.getFrontAxelVec().distanceTo(properties.getRearAxelVec()) * 0.0625 * bodyPosition.getScale();

        // Performs the charging motion
        if(this.charging)
        {
            float speed = 0.1F;
            Vector3d frontWheel = forward.scale(wheelBase / 2.0);
            Vector3d nextPosition = frontWheel.subtract(frontWheel.yRot((float) Math.toRadians(this.steeringAngle)));
            Vector3d nextMovement = Vector3d.ZERO.vectorTo(nextPosition).scale(speed);
            this.setDeltaMovement(nextMovement);
            this.deltaYaw = this.steeringAngle * speed;
            this.yRot -= this.deltaYaw;
            float forwardForce = MathHelper.clamp(this.getThrottle(), -1.0F, 1.0F);
            forwardForce *= this.getEngineTier().map(IEngineTier::getAccelerationMultiplier).orElse(1.0F);
            this.chargingAmount = MathHelper.clamp(this.chargingAmount + forwardForce * 0.025F, 0.0F, 1.0F);
            return;
        }
        else
        {
            this.chargingAmount = 0F;
        }

        // Updates the acceleration, applies drag and friction, then adds to the velocity
        float throttle = this.isHandbraking() ? 0F : this.getThrottle();
        float forwardForce = enginePower * MathHelper.clamp(throttle, -1.0F, 1.0F);
        forwardForce *= this.getEngineTier().map(IEngineTier::getAccelerationMultiplier).orElse(1.0F);
        if(this.isBoosting()) forwardForce += forwardForce * this.speedMultiplier;
        Vector3d acceleration = forward.scale(forwardForce).scale(0.05);
        if(this.velocity.length() < 0.05) this.velocity = Vector3d.ZERO;
        Vector3d handbrakeForce = this.velocity.scale(this.isHandbraking() ? brakePower : 0F).scale(0.05);
        Vector3d frictionForce = this.velocity.scale(-friction).scale(0.05);
        Vector3d dragForce = this.velocity.scale(this.velocity.length()).scale(-drag).scale(0.05);
        acceleration = acceleration.add(dragForce).add(frictionForce).add(handbrakeForce);
        this.velocity = this.velocity.add(acceleration);

        if(this.isSliding() && this.getThrottle() > 0)
        {
            this.traction = 0.001F; // TODO this will be determined by wheel type
        }
        else if(this.isHandbraking())
        {
            this.traction = 0.001F;
        }
        else
        {
            float baseTraction = this.isHandbraking() ? 0.01F : 0.8F;  // TODO this will be determined by wheel type
            float targetTraction = acceleration.length() > 0 ? (float) (baseTraction * MathHelper.clamp((this.velocity.length() / acceleration.length()), 0.0F, 1.0F)) : baseTraction;
            float side = MathHelper.clamp(1.0F - (float) this.velocity.normalize().cross(forward.normalize()).length() / 0.25F, 0.0F, 1.0F);
            if(this.getThrottle() <= 0) side = 0.5F;
            this.traction = this.traction + (targetTraction - this.traction) * 0.1F * side;
        }

        //TODO test with steering at the rear
        //Gets the new position of the wheels
        Vector3d frontWheel = this.position().add(forward.scale(wheelBase / 2.0));
        Vector3d rearWheel = this.position().add(forward.scale(-wheelBase / 2.0));
        frontWheel = frontWheel.add(this.velocity.yRot((float) Math.toRadians(this.getSteeringAngle())).scale(0.05));
        rearWheel = rearWheel.add(this.velocity.scale(0.05));

        //Updates the delta movement based on the new wheel positions
        Vector3d nextPosition = frontWheel.add(rearWheel).scale(0.5);
        Vector3d nextMovement = nextPosition.subtract(this.position());
        this.setDeltaMovement(nextMovement);

        // Updates the velocity based on the heading
        Vector3d heading = frontWheel.subtract(rearWheel).normalize();
        if(heading.dot(this.velocity.normalize()) > 0)
        {
            this.velocity = CommonUtils.lerp(this.velocity, heading.scale(this.velocity.length()), this.traction);
        }
        else
        {
            Vector3d reverse = heading.scale(-1).scale(Math.min(this.velocity.length(), 5F));
            this.velocity = CommonUtils.lerp(this.velocity, reverse, this.traction);
        }

        // Calculates the difference from the old yaw to the new yaw
        float vehicleDeltaYaw = CommonUtils.yaw(forward) - CommonUtils.yaw(heading);
        vehicleDeltaYaw = MathHelper.wrapDegrees(vehicleDeltaYaw);
        this.yRot -= vehicleDeltaYaw;
        this.deltaYaw = MathHelper.lerp(0.2F, this.deltaYaw, vehicleDeltaYaw);
    }

    @Override
    protected void updateTurning()
    {
        if(this.level.isClientSide())
        {
            this.steeringAngle = VehicleHelper.getSteeringAngle(this, this.isHandbraking());
        }
        else
        {
            this.steeringAngle = 0F;
        }

        float targetAngle = !this.charging && this.isSliding() ? -MathHelper.clamp(this.steeringAngle * 2, -this.getMaxSteeringAngle(), this.getMaxSteeringAngle()) : this.steeringAngle;
        this.wheelAngle = this.wheelAngle + (targetAngle - this.wheelAngle) * 0.3F;

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

    public void updateWheels()
    {
        VehicleProperties properties = this.getProperties();
        double wheelCircumference = 24.0;
        double vehicleScale = properties.getBodyPosition().getScale();
        Vector3d forward = Vector3d.directionFromRotation(this.getRotationVector());
        double direction = forward.dot(this.velocity.normalize());
        double speed = this.velocity.length() * direction;

        Wheel frontWheel = properties.getFirstFrontWheel();
        if(frontWheel != null && !this.charging)
        {
            double frontWheelCircumference = wheelCircumference * vehicleScale * frontWheel.getScaleY();
            double rotation = (speed * 16) / frontWheelCircumference;
            this.frontWheelRotation -= rotation * 20F;
        }

        if(this.isHandbraking() && !this.charging)
            return;

        if(this.charging)
        {
            float enginePower = 15F;
            speed = (enginePower * this.chargingAmount);
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

    public void setHandbraking(boolean handbraking)
    {
        this.entityData.set(HANDBRAKE, handbraking);
    }

    public boolean isHandbraking()
    {
        return this.entityData.get(HANDBRAKE);
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
        return this.yRot;
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

    @Override
    protected void updateEnginePitch()
    {
        super.updateEnginePitch();

        if(this.isSliding() && this.getThrottle() > 0 && !this.isHandbraking() || this.isBoosting())
        {
            this.enginePitch = this.getMinEnginePitch() + (this.getMaxEnginePitch() - this.getMinEnginePitch()) * this.getThrottle();
        }
    }

    public boolean isSliding()
    {
        Vector3d forward = Vector3d.directionFromRotation(this.getRotationVector());
        return this.velocity.normalize().cross(forward.normalize()).length() > 0.3;
    }

    @OnlyIn(Dist.CLIENT)
    public float getWheelieProgress(float partialTicks)
    {
        float p = MathHelper.lerp(partialTicks, this.prevWheelieCount, this.wheelieCount) / (float) MAX_WHEELIE_TICKS;
        return 1.0F - (1.0F - p) * (1.0F - p);
    }
}
