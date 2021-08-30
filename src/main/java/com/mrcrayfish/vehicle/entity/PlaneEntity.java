package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.client.VehicleHelper;
import com.mrcrayfish.vehicle.common.SurfaceHelper;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.properties.PlaneProperties;
import com.mrcrayfish.vehicle.entity.properties.VehicleProperties;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.datasync.VehicleDataValue;
import com.mrcrayfish.vehicle.network.message.MessagePlaneInput;
import com.mrcrayfish.vehicle.util.CommonUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public abstract class PlaneEntity extends PoweredVehicleEntity
{
    protected static final DataParameter<Float> LIFT = EntityDataManager.defineId(PlaneEntity.class, DataSerializers.FLOAT);
    protected static final DataParameter<Float> FORWARD_INPUT = EntityDataManager.defineId(PlaneEntity.class, DataSerializers.FLOAT);
    protected static final DataParameter<Float> SIDE_INPUT = EntityDataManager.defineId(PlaneEntity.class, DataSerializers.FLOAT);
    protected static final DataParameter<Float> PLANE_ROLL = EntityDataManager.defineId(PlaneEntity.class, DataSerializers.FLOAT);

    protected final VehicleDataValue<Float> lift = new VehicleDataValue<>(this, LIFT);
    protected final VehicleDataValue<Float> forwardInput = new VehicleDataValue<>(this, FORWARD_INPUT);
    protected final VehicleDataValue<Float> sideInput = new VehicleDataValue<>(this, SIDE_INPUT);
    protected final VehicleDataValue<Float> planeRoll = new VehicleDataValue<>(this, PLANE_ROLL);

    protected Vector3d velocity = Vector3d.ZERO;
    protected float propellerSpeed;
    protected float flapAngle;
    protected float elevatorAngle;

    @OnlyIn(Dist.CLIENT)
    protected float propellerRotation;
    @OnlyIn(Dist.CLIENT)
    protected float prevPropellerRotation;
    @OnlyIn(Dist.CLIENT)
    protected float wheelRotationSpeed;
    @OnlyIn(Dist.CLIENT)
    protected float wheelRotation;
    @OnlyIn(Dist.CLIENT)
    protected float prevWheelRotation;

    protected PlaneEntity(EntityType<?> entityType, World worldIn)
    {
        super(entityType, worldIn);
        this.setSteeringSpeed(5);
    }

    @Override
    public void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(LIFT, 0F);
        this.entityData.define(FORWARD_INPUT, 0F);
        this.entityData.define(SIDE_INPUT, 0F);
        this.entityData.define(PLANE_ROLL, 0F);
    }

    @Override
    public void updateVehicleMotion()
    {
        this.motion = Vector3d.ZERO;

        this.updatePropellerSpeed();

        // Updates the planes roll based on input from the player
        this.flapAngle += (this.getSideInput() * this.getMaxFlapAngle() - this.flapAngle) * this.getFlapStrength();
        if(this.getControllingPassenger() != null && this.isFlying())
        {
            float oldPlaneRoll = this.planeRoll.get(this);
            float newPlaneRoll = oldPlaneRoll - this.flapAngle * this.getFlapSensitivity();
            newPlaneRoll = MathHelper.wrapDegrees(newPlaneRoll);
            this.planeRoll.set(this, newPlaneRoll);
        }
        else
        {
            this.planeRoll.set(this, this.planeRoll.get(this) * 0.9F);
        }

        VehicleProperties properties = this.getProperties();
        float enginePower = properties.getEnginePower() * this.getEngineTier().map(IEngineTier::getPowerMultiplier).orElse(1.0F);
        float friction = this.isFlying() ? 0F : SurfaceHelper.getFriction(this);
        float drag = 0.75F;
        float forwardForce = Math.max((this.propellerSpeed / 200F) - 0.4F, 0F);
        float liftForce = Math.min((float) (this.velocity.length() * 20) / this.getMinimumSpeedToTakeOff(), 1.0F);
        if(this.getControllingPassenger() == null) liftForce /= 2;
        float elevatorForce = this.isFlying() ? liftForce : (float) Math.floor(liftForce);
        this.elevatorAngle += ((this.getMaxElevatorAngle() * this.getLift()) - this.elevatorAngle) * this.getElevatorStrength();

        // Adds delta pitch and yaw to the plane based on the flaps and roll of the plane
        Vector3f elevatorDirection = new Vector3f(Vector3d.directionFromRotation(this.elevatorAngle * elevatorForce * this.getElevatorSensitivity(), 0));
        elevatorDirection.transform(Vector3f.ZP.rotationDegrees(this.planeRoll.get(this)));
        this.xRot += CommonUtils.pitch(elevatorDirection);
        this.yRot -= CommonUtils.yaw(elevatorDirection);

        // Makes the plane turn slightly when roll is turned to the side
        float planeRoll = this.planeRoll.get(this) % 360;
        float absPlaneRoll = Math.abs(planeRoll);
        if(absPlaneRoll >= 0 && absPlaneRoll <= 90)
        {
            float forwardFactor = 1.0F - MathHelper.degreesDifferenceAbs(this.xRot, 0F) / 90F;
            float turnStrength = 1.0F - (MathHelper.degreesDifferenceAbs(absPlaneRoll, 45F) / 45F);
            turnStrength *= Math.signum(planeRoll);
            float turnAmount = turnStrength * forwardFactor * this.getMaxTurnAngle();
            this.yRot += turnAmount;
        }

        // Makes the plane fall the closer it is to being sideways
        float fallAmount = 1.0F - MathHelper.degreesDifferenceAbs(absPlaneRoll, 90F) / 90F;
        this.xRot += Math.abs(fallAmount);

        // Updates the accelerations of the plane with drag and friction applied
        Vector3d forward = Vector3d.directionFromRotation(this.getRotationVector());
        Vector3d acceleration = forward.scale(forwardForce).scale(enginePower).scale(0.05);
        Vector3d dragForce = this.velocity.scale(this.velocity.length()).scale(-drag);
        acceleration = acceleration.add(dragForce);
        Vector3d frictionForce = this.velocity.scale(-friction).scale(0.05);
        acceleration = acceleration.add(frictionForce);
        this.velocity = this.velocity.add(acceleration);

        // Add gravity but is countered based on the lift force
        this.velocity = this.velocity.add(0, -0.08 * (1.0F - liftForce), 0);

        // Different physics when on the ground
        if(this.isOnGround() && properties.getFrontAxelVec() != null && properties.getRearAxelVec() != null)
        {
            // Gets the new position of the wheels
            PartPosition bodyPosition = properties.getBodyPosition();
            double wheelBase = properties.getFrontAxelVec().distanceTo(properties.getRearAxelVec()) * 0.0625 * bodyPosition.getScale();
            Vector3d frontWheel = this.position().add(forward.scale(wheelBase / 2.0));
            Vector3d rearWheel = this.position().add(forward.scale(-wheelBase / 2.0));
            frontWheel = frontWheel.add(this.velocity.yRot((float) Math.toRadians(this.getSteeringAngle())));
            rearWheel = rearWheel.add(this.velocity);

            // Updates the delta movement based on the new wheel positions
            Vector3d nextPosition = frontWheel.add(rearWheel).scale(0.5);
            Vector3d nextMovement = nextPosition.subtract(this.position());
            this.motion = this.motion.add(nextMovement);

            // Updates the velocity based on the new heading
            Vector3d heading = frontWheel.subtract(rearWheel).normalize();
            if(heading.dot(this.velocity.normalize()) > 0)
            {
                this.velocity = CommonUtils.lerp(this.velocity, heading.scale(this.velocity.multiply(1, 0, 1).length()), 0.5F);
            }

            // Calculates the difference from the old yaw to the new yaw
            float vehicleDeltaYaw = CommonUtils.yaw(forward) - CommonUtils.yaw(heading);
            vehicleDeltaYaw = MathHelper.wrapDegrees(vehicleDeltaYaw);
            this.yRot -= vehicleDeltaYaw;
        }
        else
        {
            // Finally adds velocity to the motion
            this.motion = this.motion.add(this.velocity);
        }

        // Updates the pitch and yaw based on the velocity
        if(this.isFlying())
        {
            this.xRot = -CommonUtils.pitch(this.velocity);
            this.xRot = MathHelper.clamp(this.xRot, -89F, 89F);
            if(this.velocity.multiply(1, 0, 1).length() > 0.001)
            {
                this.yRot = CommonUtils.yaw(this.velocity);
            }
        }
        else
        {
            this.xRot = 0F;
        }
    }

    protected void updatePropellerSpeed()
    {
        if(this.canDrive() && this.getControllingPassenger() != null)
        {
            float enginePower = this.getProperties().getEnginePower();
            enginePower *= this.getEngineTier().map(IEngineTier::getPowerMultiplier).orElse(1.0F);
            float maxRotorSpeed = this.getMaxRotorSpeed();
            float angleOfAttack = (MathHelper.clamp(this.xRot, -90F, 90F) + 90F) / 180F;
            enginePower *= angleOfAttack;

            // Makes the plane slow down the closer it points up
            if(this.xRot < 0)
            {
                float upFactor = 1.0F - (float) Math.pow(1.0F - angleOfAttack / 0.5F, 5);
                maxRotorSpeed = MathHelper.clamp(maxRotorSpeed * upFactor, Math.min(maxRotorSpeed, 90F), Math.max(maxRotorSpeed, 90F));
            }
            else
            {
                float downFactor = (float) Math.pow(angleOfAttack, 3);
                maxRotorSpeed += maxRotorSpeed * 0.4F * downFactor;
            }

            if(this.propellerSpeed <= maxRotorSpeed)
            {
                this.propellerSpeed += this.getThrottle() > 0 ? Math.sqrt(enginePower) / 5F : 0.4F;
                if(this.propellerSpeed > maxRotorSpeed)
                {
                    this.propellerSpeed = maxRotorSpeed;
                }
            }
            else
            {
                float brakeForce = this.getThrottle() < 0 ? 0.1F : 0.05F;
                this.propellerSpeed = MathHelper.lerp(brakeForce, this.propellerSpeed, maxRotorSpeed);
            }
        }
        else
        {
            float maxRotorSpeed = this.isFlying() ? 90F : 0F;
            this.propellerSpeed = MathHelper.lerp(0.05F, this.propellerSpeed, maxRotorSpeed);
        }

        if(this.level.isClientSide())
        {
            this.propellerRotation += this.propellerSpeed;
        }
    }

    protected float getMaxRotorSpeed()
    {
        if(this.getThrottle() > 0)
        {
            return 200F + this.getProperties().getEnginePower();
        }
        else if(this.isFlying())
        {
            if(this.getThrottle() < 0)
            {
                return 140F;
            }
            return 180F;
        }
        return 70F;
    }

    @Override
    public void onClientUpdate()
    {
        super.onClientUpdate();

        this.prevWheelRotation = this.wheelRotation;
        this.prevPropellerRotation = this.propellerRotation;

        LivingEntity entity = (LivingEntity) this.getControllingPassenger();
        if(entity != null && entity.equals(Minecraft.getInstance().player))
        {
            ClientPlayerEntity player = (ClientPlayerEntity) entity;
            this.setLift(VehicleHelper.getLift());
            this.setForwardInput(player.zza);
            this.setSideInput(player.xxa);
            PacketHandler.instance.sendToServer(new MessagePlaneInput(this.lift.getLocalValue(), player.zza, player.xxa));
        }
    }

    @Override
    protected void updateBodyRotations()
    {
        if(this.isFlying())
        {
            this.bodyRotationPitch = this.xRot;
            this.bodyRotationRoll = this.planeRoll.get(this);
        }
        else
        {
            this.bodyRotationPitch *= 0.75F;
            this.bodyRotationRoll *= 0.75F;
        }
        this.bodyRotationYaw = this.yRot;
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compound)
    {
        super.addAdditionalSaveData(compound);
        compound.putFloat("Lift", this.getLift());
        compound.putFloat("PlaneRoll", this.planeRoll.getLocalValue());
        compound.putFloat("PropellerSpeed", this.propellerSpeed);
        compound.putFloat("FlapAngle", this.flapAngle);
        compound.putFloat("ElevatorAngle", this.elevatorAngle);
        CompoundNBT velocity = new CompoundNBT();
        velocity.putDouble("X", this.velocity.x);
        velocity.putDouble("Y", this.velocity.y);
        velocity.putDouble("Z", this.velocity.z);
        compound.put("Velocity", velocity);
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT compound)
    {
        super.readAdditionalSaveData(compound);
        this.setLift(compound.getFloat("Lift"));
        this.planeRoll.set(this, compound.getFloat("PlaneRoll"));
        this.propellerSpeed = compound.getFloat("PropellerSpeed");
        this.flapAngle = compound.getFloat("FlapAngle");
        this.elevatorAngle = compound.getFloat("ElevatorAngle");
        CompoundNBT velocity = compound.getCompound("Velocity");
        this.velocity = new Vector3d(velocity.getDouble("X"), velocity.getDouble("Y"), velocity.getDouble("Z"));
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer)
    {
        super.writeSpawnData(buffer);
        buffer.writeDouble(this.velocity.x);
        buffer.writeDouble(this.velocity.y);
        buffer.writeDouble(this.velocity.z);
        buffer.writeFloat(this.planeRoll.get(this));
        buffer.writeFloat(this.propellerSpeed);
        buffer.writeFloat(this.flapAngle);
        buffer.writeFloat(this.elevatorAngle);
    }

    @Override
    public void readSpawnData(PacketBuffer buffer)
    {
        super.readSpawnData(buffer);
        this.velocity = new Vector3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        this.planeRoll.set(this, buffer.readFloat());
        this.propellerSpeed = buffer.readFloat();
        this.flapAngle = buffer.readFloat();
        this.elevatorAngle = buffer.readFloat();
    }

    public float getLift()
    {
        return this.lift.get(this);
    }

    public void setLift(float lift)
    {
        this.lift.set(this, lift);
    }

    public float getForwardInput()
    {
        return this.forwardInput.get(this);
    }

    public void setForwardInput(float input)
    {
        this.forwardInput.set(this, input);
    }

    public float getSideInput()
    {
        return this.sideInput.get(this);
    }

    public void setSideInput(float input)
    {
        this.sideInput.set(this, input);
    }

    public boolean isFlying()
    {
        return !this.onGround;
    }

    /**
     * The minimum speed the plane requires to take off on a flat ground. It's still possible for
     * planes to glide at lower speeds.
     */
    protected final float getMinimumSpeedToTakeOff()
    {
        return this.getProperties().getExtended(PlaneProperties.class).map(PlaneProperties::getMinimumSpeedToTakeOff).orElse(PlaneProperties.DEFAULT_MINIMUM_SPEED_TO_TAKE_OFF);
    }

    /**
     * The maximum absolute angle the flaps on the plane can pitch up or down.
     */
    public final float getMaxFlapAngle()
    {
        return this.getProperties().getExtended(PlaneProperties.class).map(PlaneProperties::getMaxFlapAngle).orElse(PlaneProperties.DEFAULT_MAX_FLAP_ANGLE);
    }

    /**
     * This determines how quickly the flaps approach it's maximum flap angle.
     */
    public final float getFlapStrength()
    {
        return this.getProperties().getExtended(PlaneProperties.class).map(PlaneProperties::getFlapStrength).orElse(PlaneProperties.DEFAULT_FLAP_STRENGTH);
    }

    /**
     * This controls how much of the max flap angle is applied each tick to the planes motion
     */
    public final float getFlapSensitivity()
    {
        return this.getProperties().getExtended(PlaneProperties.class).map(PlaneProperties::getFlapSensitivity).orElse(PlaneProperties.DEFAULT_FLAP_SENSITIVITY);
    }

    /**
     * The maximum absolute angle the elevator on the plane can pitch up or down.
     */
    public final float getMaxElevatorAngle()
    {
        return this.getProperties().getExtended(PlaneProperties.class).map(PlaneProperties::getMaxElevatorAngle).orElse(PlaneProperties.DEFAULT_MAX_ELEVATOR_ANGLE);
    }

    /**
     * This determines how quickly the elevator approaches it's maximum elevator angle.
     */
    public final float getElevatorStrength()
    {
        return this.getProperties().getExtended(PlaneProperties.class).map(PlaneProperties::getElevatorStrength).orElse(PlaneProperties.DEFAULT_ELEVATOR_STRENGTH);
    }

    /**
     * This controls how much of the max elevator angle is applied each tick to the planes motion
     */
    public final float getElevatorSensitivity()
    {
        return this.getProperties().getExtended(PlaneProperties.class).map(PlaneProperties::getElevatorSensitivity).orElse(PlaneProperties.DEFAULT_ELEVATOR_SENSITIVITY);
    }

    /**
     * Determines the maximum delta angle the plane should turn when it's rolled at the optimum angle
     */
    public final float getMaxTurnAngle()
    {
        return this.getProperties().getExtended(PlaneProperties.class).map(PlaneProperties::getMaxTurnAngle).orElse(PlaneProperties.DEFAULT_MAX_TURN_ANGLE);
    }

    /*
     * Overridden to prevent players from taking fall damage when landing a plane
     */
    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier)
    {
        return false;
    }

    @Override
    public boolean canChangeWheels()
    {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void updateEngineSound()
    {
        this.enginePitch = this.getMinEnginePitch() + (this.getMaxEnginePitch() - this.getMinEnginePitch()) * MathHelper.clamp(this.propellerSpeed / 200F, 0.0F, 2.0F);
        this.engineVolume = this.getControllingPassenger() != null && this.isEnginePowered() ? 0.2F + 0.8F * (this.propellerSpeed / 80F) : 0.001F;
    }

    @OnlyIn(Dist.CLIENT)
    public float getBladeRotation(float partialTicks)
    {
        return this.prevPropellerRotation + (this.propellerRotation - this.prevPropellerRotation) * partialTicks;
    }

    @Override
    protected void updateTurning()
    {
        if(this.level.isClientSide())
        {
            float targetAngle = this.isOnGround() ? this.getSteeringAngle() : 0F;
            this.renderWheelAngle = this.renderWheelAngle + (targetAngle - this.renderWheelAngle) * 0.3F;
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void updateWheelRotations()
    {
        VehicleProperties properties = this.getProperties();
        double wheelCircumference = 24.0;
        double vehicleScale = properties.getBodyPosition().getScale();
        Vector3d forward = Vector3d.directionFromRotation(this.getRotationVector());
        Vector3d horizontalMotion = this.motion.multiply(1, 0, 1);
        double direction = forward.dot(horizontalMotion.normalize());

        if(this.isOnGround())
        {
            this.wheelRotationSpeed = (float) (horizontalMotion.length() * direction * 20);
        }
        else
        {
            this.wheelRotationSpeed *= 0.9;
        }

        Optional<Wheel> wheel = properties.getWheels().stream().findAny();
        if(wheel.isPresent())
        {
            double frontWheelCircumference = wheelCircumference * vehicleScale * wheel.get().getScaleY();
            double rotation = (this.wheelRotationSpeed * 16) / frontWheelCircumference;
            this.wheelRotation -= rotation * 20F;
        }
    }

    @Override
    public float getWheelRotation(Wheel wheel, float partialTicks)
    {
        return MathHelper.lerp(partialTicks, this.prevWheelRotation, this.wheelRotation);
    }

}
