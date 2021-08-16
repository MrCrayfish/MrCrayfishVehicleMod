package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.client.VehicleHelper;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.datasync.VehicleDataValue;
import com.mrcrayfish.vehicle.network.message.MessageFlaps;
import com.mrcrayfish.vehicle.network.message.MessagePlaneInput;
import com.mrcrayfish.vehicle.util.CommonUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

/**
 * Author: MrCrayfish
 */
public abstract class PlaneEntity extends PoweredVehicleEntity
{
    //TODO Create own data parameter system if problems continue to occur
    private static final DataParameter<Integer> FLAP_DIRECTION = EntityDataManager.defineId(PlaneEntity.class, DataSerializers.INT);
    private static final DataParameter<Float> LIFT = EntityDataManager.defineId(PlaneEntity.class, DataSerializers.FLOAT);
    protected static final DataParameter<Float> FORWARD_INPUT = EntityDataManager.defineId(PlaneEntity.class, DataSerializers.FLOAT);
    protected static final DataParameter<Float> SIDE_INPUT = EntityDataManager.defineId(PlaneEntity.class, DataSerializers.FLOAT);

    protected final VehicleDataValue<Float> lift = new VehicleDataValue<>(this, LIFT);
    protected final VehicleDataValue<Float> forwardInput = new VehicleDataValue<>(this, FORWARD_INPUT);
    protected final VehicleDataValue<Float> sideInput = new VehicleDataValue<>(this, SIDE_INPUT);

    protected Vector3d velocity = Vector3d.ZERO;

    protected float planeRoll;
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
        this.entityData.define(FLAP_DIRECTION, FlapDirection.NONE.ordinal());
        this.entityData.define(LIFT, 0F);
        this.entityData.define(FORWARD_INPUT, 0F);
        this.entityData.define(SIDE_INPUT, 0F);
    }

    @Override
    public void updateVehicleMotion()
    {
        this.prevPlaneRoll = this.planeRoll;

        this.motion = Vector3d.ZERO;

        if(this.getControllingPassenger() != null)
        {
            this.planeRoll -= this.getSideInput() * 5F;
            this.planeRoll = MathHelper.wrapDegrees(this.planeRoll);

            //TODO engine should cut out if below a threshold

            Vector3f forward = new Vector3f(Vector3d.directionFromRotation(this.getLift(), 0));
            forward.transform(Vector3f.ZP.rotationDegrees(this.planeRoll));

            Vector3d deltaForward = new Vector3d(forward);
            this.xRot += CommonUtils.pitch(deltaForward) * 2F;
            this.yRot -= CommonUtils.yaw(deltaForward) * 2F;
            this.velocity = CommonUtils.lerp(this.velocity, this.getForward().scale(this.getThrottle()), 0.05F);
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
            FlapDirection flapDirection = VehicleHelper.getFlapDirection();
            if(this.getFlapDirection() != flapDirection)
            {
                this.setFlapDirection(flapDirection);
                PacketHandler.instance.sendToServer(new MessageFlaps(flapDirection));
            }

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
            this.bodyRotationRoll = this.planeRoll;
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
        compound.putInt("FlapDirection", this.getFlapDirection().ordinal());
        compound.putFloat("Lift", this.getLift());
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT compound)
    {
        super.readAdditionalSaveData(compound);
        if(compound.contains("FlapDirection", Constants.NBT.TAG_INT))
        {
            this.setFlapDirection(FlapDirection.values()[compound.getInt("FlapDirection")]);
        }
        if(compound.contains("Lift", Constants.NBT.TAG_FLOAT))
        {
            this.setLift(compound.getFloat("Lift"));
        }
    }

    public void setFlapDirection(FlapDirection flapDirection)
    {
        this.entityData.set(FLAP_DIRECTION, flapDirection.ordinal());
    }

    public FlapDirection getFlapDirection()
    {
        return FlapDirection.values()[this.entityData.get(FLAP_DIRECTION)];
    }

    public float getLift()
    {
        return this.entityData.get(LIFT);
    }

    public void setLift(float lift)
    {
        this.entityData.set(LIFT, lift);
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

    public enum FlapDirection
    {
        UP,
        DOWN,
        NONE;

        public static FlapDirection fromInput(boolean up, boolean down)
        {
            return up && !down ? UP : down && !up ? DOWN : NONE;
        }
    }
}
