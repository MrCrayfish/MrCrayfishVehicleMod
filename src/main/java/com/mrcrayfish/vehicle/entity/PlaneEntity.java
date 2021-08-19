package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.client.VehicleHelper;
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
    protected float prevPlaneRoll;

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
        this.prevPlaneRoll = this.planeRoll.get(this);

        this.motion = Vector3d.ZERO;

        if(this.getControllingPassenger() != null)
        {
            float newPlaneRoll = this.prevPlaneRoll - this.getSideInput() * 5F;
            newPlaneRoll = MathHelper.wrapDegrees(newPlaneRoll);
            this.planeRoll.set(this, newPlaneRoll);

            //TODO engine should cut out if below a threshold

            Vector3f forward = new Vector3f(Vector3d.directionFromRotation(this.getLift() * 0.5F, 0));
            forward.transform(Vector3f.ZP.rotationDegrees(newPlaneRoll));

            Vector3d deltaForward = new Vector3d(forward);
            this.xRot += CommonUtils.pitch(deltaForward) * 2F;
            this.yRot -= CommonUtils.yaw(deltaForward) * 2F;
            this.velocity = CommonUtils.lerp(this.velocity, this.getForward().scale(this.getThrottle()), 0.5F);
        }

        this.motion = this.motion.add(this.velocity);
    }

    @Override
    public void onClientUpdate()
    {
        super.onClientUpdate();

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
            this.bodyRotationYaw = this.yRot;
            this.bodyRotationRoll = this.planeRoll.get(this);
        }
        else
        {
            this.bodyRotationPitch *= 0.75F;
            this.bodyRotationRoll *= 0.75F;
        }
    }

    public void updateLift()
    {
        //TODO reimplement lift for planes
        /*FlapDirection flapDirection = getFlapDirection();
        if(flapDirection == FlapDirection.UP)
        {
            this.lift += 0.04F * (Math.min(Math.max(currentSpeed - 5F, 0F), 15F) / 15F);
        }
        else if(flapDirection == FlapDirection.DOWN)
        {
            this.lift -= 0.06F * (Math.min(currentSpeed, 15F) / 15F);
        }
        this.setLift(this.lift);*/
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compound)
    {
        super.addAdditionalSaveData(compound);
        compound.putFloat("Lift", this.getLift());
        compound.putFloat("PlaneRoll", this.planeRoll.getLocalValue());
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
        if(compound.contains("Lift", Constants.NBT.TAG_FLOAT))
        {
            this.setLift(compound.getFloat("Lift"));
        }
        this.planeRoll.set(this, compound.getFloat("PlaneRoll"));
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
    }

    @Override
    public void readSpawnData(PacketBuffer buffer)
    {
        super.readSpawnData(buffer);
        this.velocity = new Vector3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
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
}
