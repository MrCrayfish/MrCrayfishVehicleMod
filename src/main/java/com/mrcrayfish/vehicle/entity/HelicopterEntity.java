package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.client.VehicleHelper;
import com.mrcrayfish.vehicle.entity.properties.HelicopterProperties;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.datasync.VehicleDataValue;
import com.mrcrayfish.vehicle.network.message.MessageHelicopterInput;
import com.mrcrayfish.vehicle.util.CommonUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
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
public abstract class HelicopterEntity extends PoweredVehicleEntity
{
    protected static final DataParameter<Float> LIFT = EntityDataManager.defineId(HelicopterEntity.class, DataSerializers.FLOAT);
    protected static final DataParameter<Float> FORWARD_INPUT = EntityDataManager.defineId(HelicopterEntity.class, DataSerializers.FLOAT);
    protected static final DataParameter<Float> SIDE_INPUT = EntityDataManager.defineId(HelicopterEntity.class, DataSerializers.FLOAT);

    protected final VehicleDataValue<Float> lift = new VehicleDataValue<>(this, LIFT);
    protected final VehicleDataValue<Float> forwardInput = new VehicleDataValue<>(this, FORWARD_INPUT);
    protected final VehicleDataValue<Float> sideInput = new VehicleDataValue<>(this, SIDE_INPUT);

    protected Vector3d velocity = Vector3d.ZERO;
    protected float bladeSpeed;

    @OnlyIn(Dist.CLIENT)
    protected float bladeRotation;
    @OnlyIn(Dist.CLIENT)
    protected float prevBladeRotation;

    protected HelicopterEntity(EntityType<?> entityType, World worldIn)
    {
        super(entityType, worldIn);
    }

    @Override
    public void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(LIFT, 0F);
        this.entityData.define(FORWARD_INPUT, 0F);
        this.entityData.define(SIDE_INPUT, 0F);
    }

    @Override
    public void updateVehicleMotion()
    {
        this.motion = Vector3d.ZERO;

        boolean operating = this.canDrive() && this.getControllingPassenger() != null;
        Entity entity = this.getControllingPassenger();
        if(entity != null && this.isFlying() && operating)
        {
            float deltaYaw = entity.getYHeadRot() % 360.0F - this.yRot;
            while(deltaYaw < -180.0F)
            {
                deltaYaw += 360.0F;
            }
            while(deltaYaw >= 180.0F)
            {
                deltaYaw -= 360.0F;
            }
            this.yRot += deltaYaw * this.getRotateStrength();
        }

        this.updateBladeSpeed();

        Vector3d heading = Vector3d.ZERO;
        if(this.isFlying())
        {
            // Calculates the movement based on the input from the controlling passenger
            float enginePower = this.getEnginePower();
            Vector3d input = this.getInput();
            if(operating && input.length() > 0)
            {
                Vector3d movementForce = input.scale(enginePower).scale(0.05);
                heading = heading.add(movementForce);
            }

            // Makes the helicopter slowly fall due to it tilting during travel
            Vector3d downForce = new Vector3d(0, -1.5F * (this.velocity.multiply(1, 0, 1).scale(20).length() / enginePower), 0).scale(0.05);
            heading = heading.add(downForce);

            // Adds a slight drag to the helicopter as it travels through the air
            Vector3d dragForce = this.velocity.scale(this.velocity.length()).scale(-this.getDrag());
            heading = heading.add(dragForce);
        }
        else
        {
            // Slows the helicopter if it's only the ground
            this.velocity = this.velocity.multiply(0.85, 0, 0.85);
        }

        // Adds gravity and the lift needed to counter it
        float gravity = -1.6F;
        float lift = 1.6F * (this.bladeSpeed / 200F);
        heading = heading.add(0, gravity + lift, 0);

        // Clamps the speed based on the global speed limit
        heading = CommonUtils.clampSpeed(heading.scale(20)).scale(0.05);

        // Lerps the velocity to the new heading
        this.velocity = CommonUtils.lerp(this.velocity, heading, this.getMovementStrength());
        this.motion = this.motion.add(this.velocity);

        this.xRot = this.getPitch();

        // Makes the helicopter fall if it's not being operated by a pilot
        if(!operating)
        {
            this.setDeltaMovement(this.getDeltaMovement().add(0, -0.04, 0));
        }
    }

    private float getPitch()
    {
        return -(float) new Vector3d(-this.motion.x, 0, this.motion.z).scale(this.getMaxLeanAngle()).yRot((float) Math.toRadians(-(this.yRot + 90))).x;
    }

    protected Vector3d getInput()
    {
        if(this.getControllingPassenger() != null)
        {
            double strafe = MathHelper.clamp(this.getSideInput(), -1.0F, 1.0F);
            double forward = MathHelper.clamp(this.getForwardInput(), -1.0F, 1.0F);
            Vector3d input = new Vector3d(strafe, 0, forward).yRot((float) Math.toRadians(-this.yRot));
            return input.length() > 1.0 ? input.normalize() : input;
        }
        return Vector3d.ZERO;
    }

    protected void updateBladeSpeed()
    {
        if(this.canDrive() && this.getControllingPassenger() != null)
        {
            float enginePower = this.getEnginePower();
            float maxBladeSpeed = this.getMaxBladeSpeed();
            if(this.bladeSpeed < maxBladeSpeed)
            {
                this.bladeSpeed += this.getLift() > 0 ? (enginePower / 4F) : 0.5F;
                if(this.bladeSpeed > maxBladeSpeed)
                {
                    this.bladeSpeed = maxBladeSpeed;
                }
            }
            else
            {
                this.bladeSpeed *= 0.95F;
            }
        }
        else
        {
            this.bladeSpeed *= 0.95F;
        }

        if(this.level.isClientSide())
        {
            this.bladeRotation += this.bladeSpeed;
        }
    }

    protected float getMaxBladeSpeed()
    {
        if(this.getLift() > 0)
        {
            return 200F + this.getEnginePower();
        }
        else if(this.isFlying())
        {
            if(this.getLift() < 0)
            {
                return 150F;
            }
            return 200F;
        }
        return 80F;
    }

    @Override
    public void onClientUpdate()
    {
        super.onClientUpdate();

        this.prevBladeRotation = this.bladeRotation;

        Entity entity = this.getControllingPassenger();
        if(entity != null && entity.equals(Minecraft.getInstance().player))
        {
            ClientPlayerEntity player = (ClientPlayerEntity) entity;
            float lift = VehicleHelper.getLift();
            this.setLift(lift);
            this.setForwardInput(player.zza);
            this.setSideInput(player.xxa);
            PacketHandler.getPlayChannel().sendToServer(new MessageHelicopterInput(lift, player.zza, player.xxa));
        }
    }

    @Override
    protected void updateBodyRotations()
    {
        if(this.isFlying())
        {
            double leanAngle = this.getMaxLeanAngle();
            Vector3d rotation = new Vector3d(-this.motion.x, 0, this.motion.z).scale(leanAngle).yRot((float) Math.toRadians(-(this.yRot + 90)));
            this.bodyRotationPitch = -(float) rotation.x;
            this.bodyRotationRoll = (float) rotation.z;
        }
        else
        {
            this.bodyRotationPitch *= 0.5;
            this.bodyRotationRoll *= 0.5;
        }
        this.bodyRotationYaw = this.yRot;
    }

    @Override
    protected void updateEngineSound()
    {
        float normal = MathHelper.clamp(this.bladeSpeed / 200F, 0.0F, 1.25F) * 0.6F;
        normal += (this.motion.scale(20).length() / this.getEnginePower()) * 0.4F;
        this.enginePitch = this.getMinEnginePitch() + (this.getMaxEnginePitch() - this.getMinEnginePitch()) * MathHelper.clamp(normal, 0.0F, 1.0F);
        this.engineVolume = this.getControllingPassenger() != null && this.isEnginePowered() ? 0.2F + 0.8F * (this.bladeSpeed / 80F) : 0.001F;
    }

    @Override
    public boolean canApplyYawOffset(Entity passenger)
    {
        return passenger != this.getControllingPassenger();
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

    public final float getMovementStrength()
    {
        return this.getHelicopterProperties().getMovementStrength();
    }

    public final float getRotateStrength()
    {
        return this.getHelicopterProperties().getRotateStrength();
    }

    public final float getMaxLeanAngle()
    {
        return this.getHelicopterProperties().getMaxLeanAngle();
    }

    public final float getDrag()
    {
        return this.getHelicopterProperties().getDrag();
    }

    protected HelicopterProperties getHelicopterProperties()
    {
        return this.getProperties().getExtended(HelicopterProperties.class);
    }

    @OnlyIn(Dist.CLIENT)
    public float getBladeRotation(float partialTicks)
    {
        return this.prevBladeRotation + (this.bladeRotation - this.prevBladeRotation) * partialTicks;
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer)
    {
        super.writeSpawnData(buffer);
        buffer.writeFloat(this.bladeSpeed);
        buffer.writeDouble(this.velocity.x);
        buffer.writeDouble(this.velocity.y);
        buffer.writeDouble(this.velocity.z);
    }

    @Override
    public void readSpawnData(PacketBuffer buffer)
    {
        super.readSpawnData(buffer);
        this.bladeSpeed = buffer.readFloat();
        this.velocity = new Vector3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compound)
    {
        super.addAdditionalSaveData(compound);
        compound.putFloat("BladeSpeed", this.bladeSpeed);
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
        this.bladeSpeed = compound.getFloat("BladeSpeed");
        CompoundNBT velocity = compound.getCompound("Velocity");
        this.velocity = new Vector3d(velocity.getDouble("X"), velocity.getDouble("Y"), velocity.getDouble("Z"));
    }
}
