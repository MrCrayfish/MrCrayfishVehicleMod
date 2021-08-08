package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.client.VehicleHelper;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.datasync.VehicleDataValue;
import com.mrcrayfish.vehicle.network.message.MessageLift;
import com.mrcrayfish.vehicle.util.CommonUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundEvent;
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
    protected static final DataParameter<Float> TRAVEL_DIRECTION = EntityDataManager.defineId(HelicopterEntity.class, DataSerializers.FLOAT);
    protected static final DataParameter<Float> TRAVEL_SPEED = EntityDataManager.defineId(HelicopterEntity.class, DataSerializers.FLOAT);

    protected final VehicleDataValue<Float> lift = new VehicleDataValue<>(this, LIFT);

    protected Vector3d velocity = Vector3d.ZERO;

    protected float bladeSpeed;
    protected float bladeRotation;
    protected float prevBladeRotation;

    @OnlyIn(Dist.CLIENT)
    protected float bodyRotationX;
    @OnlyIn(Dist.CLIENT)
    protected float prevBodyRotationX;
    @OnlyIn(Dist.CLIENT)
    protected float bodyRotationY;
    @OnlyIn(Dist.CLIENT)
    protected float prevBodyRotationY;
    @OnlyIn(Dist.CLIENT)
    protected float bodyRotationZ;
    @OnlyIn(Dist.CLIENT)
    protected float prevBodyRotationZ;

    protected HelicopterEntity(EntityType<?> entityType, World worldIn)
    {
        super(entityType, worldIn);
    }

    @Override
    public void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(LIFT, 0F);
        this.entityData.define(TRAVEL_DIRECTION, 0F);
        this.entityData.define(TRAVEL_SPEED, 0F);
    }

    @Override
    public SoundEvent getEngineSound()
    {
        return null;
    }

    @Override
    public void updateVehicleMotion()
    {
        this.velocity = Vector3d.ZERO;

        Entity entity = this.getControllingPassenger();
        if(entity != null && this.isFlying())
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
            this.yRotO = this.yRot;
            this.yRot = this.yRot + deltaYaw * 0.05F;
        }

        VehicleProperties properties = this.getProperties();
        float bladeLength = 6F;
        float enginePower = properties.getEnginePower();
        float drag = 0.001F;

        float targetBladeSpeed = 0F;
        if(this.canDrive() && this.getControllingPassenger() != null)
        {
            targetBladeSpeed = 10F;
            targetBladeSpeed += properties.getEnginePower() * this.getLift() * bladeLength;
        }

        this.bladeSpeed = this.bladeSpeed + (targetBladeSpeed - this.bladeSpeed) * 0.05F;

        if(this.isFlying())
        {
            Vector3d input = this.getInput();
            if(input.length() > 0)
            {
                Vector3d movementForce = input.normalize().scale(enginePower).scale(0.05);
                this.velocity = this.velocity.add(movementForce);
            }

            Vector3d downForce = new Vector3d(0, -1.5F * (this.motion.multiply(1, 0, 1).scale(20).length() / enginePower), 0).scale(0.05);
            this.velocity = this.velocity.add(downForce);

            Vector3d dragForce = this.motion.scale(this.motion.length()).scale(-drag);
            this.velocity = this.velocity.add(dragForce);
        }
        else
        {
            this.motion = this.motion.scale(0.85F);
        }

        // Add lift force to fight gravity
        Vector3d liftForce = new Vector3d(0, 0.02 * (this.bladeSpeed / 10F), 0);
        this.velocity = this.velocity.add(liftForce);

        // Add gravity
        this.velocity = this.velocity.add(0, -0.08, 0);

        this.motion = CommonUtils.lerp(this.motion, this.velocity, 0.025F);

        if(this.level.isClientSide())
        {
            this.onPostClientUpdate();
        }
    }

    private Vector3d getInput()
    {
        Entity entity = this.getControllingPassenger();
        if(entity instanceof PlayerEntity)
        {
            PlayerEntity player = (PlayerEntity) entity;
            return new Vector3d(player.xxa, 0, player.zza).yRot((float) Math.toRadians(-this.yRot));
        }
        return Vector3d.ZERO;
    }

    @Override
    public void onClientUpdate()
    {
        super.onClientUpdate();

        this.prevBladeRotation = this.bladeRotation;
        this.prevBodyRotationX = this.bodyRotationX;
        this.prevBodyRotationY = this.bodyRotationY;
        this.prevBodyRotationZ = this.bodyRotationZ;

        Entity entity = this.getControllingPassenger();
        if(entity != null && entity.equals(Minecraft.getInstance().player))
        {
            float lift = VehicleHelper.getLift();
            this.setLift(lift);
            PacketHandler.instance.sendToServer(new MessageLift(lift));
        }
    }

    @OnlyIn(Dist.CLIENT)
    protected void onPostClientUpdate()
    {
        this.bladeRotation += this.bladeSpeed;

        if(this.isFlying())
        {
            this.bodyRotationX = (float) (-this.motion.x * 30F);
            this.bodyRotationZ = (float) (this.motion.z * 30F);
        }
        else
        {
            this.bodyRotationX *= 0.5F;
            this.bodyRotationZ *= 0.5F;
        }
    }

    @Override
    public void addPassenger(Entity passenger)
    {
        super.addPassenger(passenger);
        //passenger.yRot = this.yRot;
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

    public boolean isFlying()
    {
        return !this.onGround;
    }

    @Override
    public boolean canChangeWheels()
    {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public float getBladeRotation(float partialTicks)
    {
        return this.prevBladeRotation + (this.bladeRotation - this.prevBladeRotation) * partialTicks;
    }

    @OnlyIn(Dist.CLIENT)
    public float getBodyRotationX(float partialTicks)
    {
        return this.prevBodyRotationX + (this.bodyRotationX - this.prevBodyRotationX) * partialTicks;
    }

    @OnlyIn(Dist.CLIENT)
    public float getBodyRotationY(float partialTicks)
    {
        return this.prevBodyRotationY + (this.bodyRotationY - this.prevBodyRotationY) * partialTicks;
    }

    @OnlyIn(Dist.CLIENT)
    public float getBodyRotationZ(float partialTicks)
    {
        return this.prevBodyRotationZ + (this.bodyRotationZ - this.prevBodyRotationZ) * partialTicks;
    }
}
