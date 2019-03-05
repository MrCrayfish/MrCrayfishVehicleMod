package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageFlaps;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

/**
 * Author: MrCrayfish
 */
public abstract class EntityPlane extends EntityPoweredVehicle
{
    //TODO Create own data parameter system if problems continue to occur
    private static final DataParameter<Integer> FLAP_DIRECTION = EntityDataManager.createKey(EntityPlane.class, DataSerializers.VARINT);
    private static final DataParameter<Float> LIFT = EntityDataManager.createKey(EntityPlane.class, DataSerializers.FLOAT);

    private float lift;

    public float prevBodyRotationX;
    public float prevBodyRotationY;
    public float prevBodyRotationZ;

    public float bodyRotationX;
    public float bodyRotationY;
    public float bodyRotationZ;

    protected EntityPlane(World worldIn)
    {
        super(worldIn);
        this.setAccelerationSpeed(0.5F);
        this.setMaxSpeed(25F);
        this.setTurnSensitivity(5);
    }

    @Override
    public void entityInit()
    {
        super.entityInit();
        this.dataManager.register(FLAP_DIRECTION, FlapDirection.NONE.ordinal());
        this.dataManager.register(LIFT, 0F);
    }

    @Override
    public void updateVehicleMotion()
    {
        float f1 = MathHelper.sin(this.rotationYaw * 0.017453292F) / 20F; //Divide by 20 ticks
        float f2 = MathHelper.cos(this.rotationYaw * 0.017453292F) / 20F;

        this.updateLift();

        this.vehicleMotionX = (-currentSpeed * f1);
        this.vehicleMotionZ = (currentSpeed * f2);
        this.motionY += lift;
        this.motionY -= 0.05;
    }

    @Override
    public void onClientUpdate()
    {
        super.onClientUpdate();

        this.prevBodyRotationX = this.bodyRotationX;
        this.prevBodyRotationY = this.bodyRotationY;
        this.prevBodyRotationZ = this.bodyRotationZ;

        EntityLivingBase entity = (EntityLivingBase) this.getControllingPassenger();
        if(entity != null && entity.equals(Minecraft.getMinecraft().player))
        {
            FlapDirection flapDirection = VehicleMod.proxy.getFlapDirection();
            if(this.getFlapDirection() != flapDirection)
            {
                this.setFlapDirection(flapDirection);
                PacketHandler.INSTANCE.sendToServer(new MessageFlaps(flapDirection));
            }
        }

        if(this.isFlying())
        {
            this.bodyRotationX = (float) Math.toDegrees(Math.atan2(motionY, currentSpeed / 20F));
            this.bodyRotationZ = (this.turnAngle / (float) getMaxTurnAngle()) * 20F;
        }
        else
        {
            this.bodyRotationX *= 0.5F;
            this.bodyRotationZ *= 0.5F;
        }
    }

    @Override
    protected void updateSpeed()
    {
        lift = 0;
        currentSpeed = this.getSpeed();

        if(this.getControllingPassenger() != null)
        {
            AccelerationDirection acceleration = getAcceleration();
            if(this.canDrive() && acceleration == AccelerationDirection.FORWARD)
            {
                if(this.motionY < 0)
                {
                    this.motionY *= 0.95;
                }

                EngineTier engineTier = this.getEngineTier();
                float accelerationSpeed = this.getModifiedAccelerationSpeed() * engineTier.getAccelerationMultiplier();
                if(this.currentSpeed < this.getActualMaxSpeed())
                {
                    this.currentSpeed += accelerationSpeed;
                }
                this.lift = 0.051F * (Math.min(currentSpeed, 15F) / 15F);
            }
            else if(acceleration == AccelerationDirection.REVERSE)
            {
                if(this.isFlying())
                {
                    this.currentSpeed *= 0.95F;
                }
                else
                {
                    this.currentSpeed *= 0.9F;
                }
            }

            if(acceleration != AccelerationDirection.FORWARD)
            {
                if(this.isFlying())
                {
                    this.currentSpeed *= 0.995F;
                }
                else
                {
                    this.currentSpeed *= 0.98F;
                }
                this.lift = 0.04F * (Math.min(currentSpeed, 15F) / 15F);
            }
        }
        else
        {
            if(this.isFlying())
            {
                this.currentSpeed *= 0.98F;
            }
            else
            {
                this.currentSpeed *= 0.85F;
            }
        }
    }

    @Override
    protected void updateTurning()
    {
        TurnDirection direction = this.getTurnDirection();
        if(this.getControllingPassenger() != null && direction != TurnDirection.FORWARD)
        {
            this.turnAngle += direction.dir * getTurnSensitivity();
            if(Math.abs(this.turnAngle) > getMaxTurnAngle())
            {
                this.turnAngle = getMaxTurnAngle() * direction.dir;
            }
        }
        else
        {
            this.turnAngle *= 0.95;
        }

        if(this.isFlying())
        {
            this.wheelAngle = this.turnAngle * Math.max(0.25F, 1.0F - Math.abs(Math.min(currentSpeed, 30F) / 30F));
        }
        else
        {
            this.wheelAngle = this.turnAngle * Math.abs(Math.min(currentSpeed, 30F) / 30F);
        }

        this.deltaYaw = this.wheelAngle;

        if(this.isFlying())
        {
            this.deltaYaw *= 0.5;
        }
        else
        {
            this.deltaYaw *= 0.5 * (0.5 + 0.5 * (1.0F - Math.min(currentSpeed, 15F) / 15F));
        }
    }

    public void updateLift()
    {
        FlapDirection flapDirection = getFlapDirection();
        if(flapDirection == FlapDirection.UP)
        {
            this.lift += 0.04F * (Math.min(Math.max(currentSpeed - 5F, 0F), 15F) / 15F);
        }
        else if(flapDirection == FlapDirection.DOWN)
        {
            this.lift -= 0.06F * (Math.min(currentSpeed, 15F) / 15F);
        }
        this.setLift(this.lift);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setInteger("flapDirection", this.getFlapDirection().ordinal());
        compound.setFloat("lift", this.getLift());
        return compound;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        if(compound.hasKey("flapDirection", Constants.NBT.TAG_INT))
        {
            this.setFlapDirection(FlapDirection.values()[compound.getInteger("flapDirection")]);
        }
        if(compound.hasKey("lift", Constants.NBT.TAG_FLOAT))
        {
            this.setLift(compound.getFloat("lift"));
        }
    }

    public void setFlapDirection(FlapDirection flapDirection)
    {
        this.dataManager.set(FLAP_DIRECTION, flapDirection.ordinal());
    }

    public FlapDirection getFlapDirection()
    {
        return FlapDirection.values()[this.dataManager.get(FLAP_DIRECTION)];
    }

    public float getLift()
    {
        return this.dataManager.get(LIFT);
    }

    public void setLift(float lift)
    {
        this.dataManager.set(LIFT, lift);
    }

    public boolean isFlying()
    {
        return !this.onGround;
    }

    /*
     * Overridden to prevent players from taking fall damage when landing a plane
     */
    @Override
    public void fall(float distance, float damageMultiplier) {}

    @Override
    public boolean canChangeWheels()
    {
        return false;
    }

    public enum FlapDirection
    {
        UP, DOWN, NONE;

        public static FlapDirection fromInput(boolean up, boolean down)
        {
            return up && !down ? UP : down && !up ? DOWN : NONE;
        }
    }
}
