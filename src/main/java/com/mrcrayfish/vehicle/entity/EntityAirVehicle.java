package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageFlaps;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public abstract class EntityAirVehicle extends EntityVehicle
{
    private static final DataParameter<Integer> FLAP_DIRECTION = EntityDataManager.createKey(EntityAirVehicle.class, DataSerializers.VARINT);
    private static final DataParameter<Float> LIFT = EntityDataManager.createKey(EntityAirVehicle.class, DataSerializers.FLOAT);

    private float lift;

    public float prevBodyRotationX;
    public float prevBodyRotationY;
    public float prevBodyRotationZ;

    public float bodyRotationX;
    public float bodyRotationY;
    public float bodyRotationZ;

    protected EntityAirVehicle(World worldIn)
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
            boolean flapUp = Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown();
            boolean flapDown = Minecraft.getMinecraft().gameSettings.keyBindSprint.isKeyDown();

            FlapDirection flapDirection = FlapDirection.fromInput(flapUp, flapDown);
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
            if(acceleration == AccelerationDirection.FORWARD)
            {
                if(this.motionY < 0)
                {
                    this.motionY *= 0.95;
                }

                EngineType engineType = this.getEngineType();
                float accelerationSpeed = this.getAccelerationSpeed() * engineType.getAccelerationMultiplier();
                if(this.currentSpeed < getMaxSpeed())
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
            this.turnAngle *= 0.75;
        }
        this.wheelAngle = this.turnAngle * Math.max(0.25F, 1.0F - Math.abs(Math.min(currentSpeed, 30F) / 30F));
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
            this. lift -= 0.06F * (Math.min(currentSpeed, 15F) / 15F);
        }
        this.setLift(this.lift);
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

    public enum FlapDirection
    {
        UP, DOWN, NONE;

        public static FlapDirection fromInput(boolean up, boolean down)
        {
            return up && !down ? UP : down && !up ? DOWN : NONE;
        }
    }
}
