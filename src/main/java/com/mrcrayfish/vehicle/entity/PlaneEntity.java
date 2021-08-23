package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.client.VehicleHelper;
import com.mrcrayfish.vehicle.common.SurfaceHelper;
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
import net.minecraftforge.common.util.Constants;

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
        float enginePower = properties.getEnginePower();
        float friction = this.isFlying() ? 0F : SurfaceHelper.getFriction(this);
        float drag = 0.001F;
        float forwardForce = Math.max((this.propellerSpeed / 200F) - 0.4F, 0F);
        float liftForce = Math.min((float) (this.velocity.length() * 20) / this.getMinimumSpeedToFly(), 1.0F);
        float elevatorForce = this.isFlying() ? liftForce : (float) Math.floor(liftForce);
        this.elevatorAngle += ((this.getMaxElevatorAngle() * this.getLift()) - this.elevatorAngle) * 0.15F;

        // Adds delta pitch and yaw to the plane based on the flaps and roll of the plane
        Vector3f elevatorDirection = new Vector3f(Vector3d.directionFromRotation(this.elevatorAngle * elevatorForce * 0.05F, 0));
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
        Vector3d dragForce = this.velocity.scale(this.velocity.length()).scale(-drag).scale(0.05);
        acceleration = acceleration.add(dragForce);
        Vector3d frictionForce = this.velocity.scale(-friction).scale(0.05);
        acceleration = acceleration.add(frictionForce);

        // Add gravity but is countered based on the lift force
        this.velocity = this.velocity.add(0, -0.05 * (1.0F - liftForce), 0);

        // Update the velocity based on the heading and acceleration
        this.velocity = CommonUtils.lerp(this.velocity, acceleration, 0.5F);

        // Updates the pitch and yaw based on the velocity
        if(this.isFlying() && this.velocity.multiply(1, 0, 1).length() > 0.001)
        {
            this.xRot = -CommonUtils.pitch(this.velocity);
            this.xRot = MathHelper.clamp(this.xRot, -89F, 89F);
            this.yRot = CommonUtils.yaw(this.velocity);
        }
        else
        {
            this.xRot = 0F;
        }

        // Finally adds velocity to the motion
        this.motion = this.motion.add(this.velocity);
    }

    protected void updatePropellerSpeed()
    {
        if(this.canDrive() && this.getControllingPassenger() != null)
        {
            float enginePower = this.getProperties().getEnginePower();
            float maxRotorSpeed = this.getMaxRotorSpeed();
            float angleOfAttack = (MathHelper.clamp(this.xRot, -90F, 90F) + 90F) / 180F;
            enginePower *= angleOfAttack;

            // Makes the plane slow down the closer it points up
            if(this.xRot < 0)
            {
                float upFactor = 1.0F - (float) Math.pow(1.0F - angleOfAttack / 0.5F, 7);
                this.propellerSpeed *= MathHelper.clamp(upFactor, 0.98F, 1.0F);
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
                float brakeForce = this.getThrottle() < 0 ? 0.99F : 0.999F;
                this.propellerSpeed *= brakeForce;
            }
        }
        else
        {
            this.propellerSpeed *= 0.95F;
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
            //TODO implement pitch
            return 200F + this.getProperties().getEnginePower();
        }
        else if(this.isFlying())
        {
            if(this.getThrottle() < 0)
            {
                return 160F;
            }
            return 180F;
        }
        return 80F;
    }

    @Override
    public void onClientUpdate()
    {
        super.onClientUpdate();

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

    protected float getMinimumSpeedToFly()
    {
        return 16F;
    }

    public float getMaxFlapAngle()
    {
        return 35F;
    }

    /**
     * This determines how quickly it approaches the max flap angle.
     */
    public float getFlapStrength()
    {
        return 0.25F;
    }

    public float getFlapSensitivity()
    {
        return 0.1F;
    }

    public float getMaxElevatorAngle()
    {
        return 45F;
    }

    public float getMaxTurnAngle()
    {
        return 2F;
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
    protected void updateEngineSound()
    {
        float normal = MathHelper.clamp(this.propellerSpeed / 200F, 0.0F, 1.25F) * 0.6F;
        //normal += (this.motion.scale(20).length() / this.getProperties().getEnginePower()) * 0.4F;
        this.enginePitch = this.getMinEnginePitch() + (this.getMaxEnginePitch() - this.getMinEnginePitch()) * MathHelper.clamp(normal, 0.0F, 1.0F);
        this.engineVolume = this.getControllingPassenger() != null && this.isEnginePowered() ? 0.2F + 0.8F * (this.propellerSpeed / 80F) : 0.001F;
    }

    @OnlyIn(Dist.CLIENT)
    public float getBladeRotation(float partialTicks)
    {
        return this.prevPropellerRotation + (this.propellerRotation - this.prevPropellerRotation) * partialTicks;
    }
}
