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
public class EntityAirVehicle extends EntityVehicle
{
    private static final DataParameter<Integer> FLAP_DIRECTION = EntityDataManager.createKey(EntityAirVehicle.class, DataSerializers.VARINT);

    private float lift;
    public float speed;

    public float prevBodyRotationX;
    public float prevBodyRotationY;
    public float prevBodyRotationZ;

    public float bodyRotationX;
    public float bodyRotationY;
    public float bodyRotationZ;

    private AccelerationDirection prevAcceleration;

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
    }

    @Override
    public SoundEvent getMovingSound()
    {
        return null;
    }

    @Override
    public SoundEvent getRidingSound()
    {
        return null;
    }

    @Override
    public void updateVehicleMotion()
    {
        float f1 = MathHelper.sin(this.rotationYaw * 0.017453292F) / 20F; //Divide by 20 ticks
        float f2 = MathHelper.cos(this.rotationYaw * 0.017453292F) / 20F;

        this.lift = 0;

        AccelerationDirection acceleration = getAcceleration();
        if(acceleration == AccelerationDirection.FORWARD)
        {
            if(this.motionY < 0)
            {
                this.motionY *= 0.95;
            }

            EngineType engineType = this.getEngineType();
            float accelerationSpeed = this.getAccelerationSpeed() * engineType.getAccelerationMultiplier();
            if(this.speed < 30F)
            {
                this.speed += accelerationSpeed;
            }
            this.lift = 0.051F * (Math.min(speed, 15F) / 15F);
        }
        else if(acceleration == AccelerationDirection.REVERSE)
        {
            if(this.onGround)
            {
                this.speed *= 0.9F;
            }
            else
            {
                this.speed *= 0.95F;
            }
        }

        if(acceleration != AccelerationDirection.FORWARD)
        {
            if(this.onGround)
            {
                this.speed *= 0.98F;
            }
            else
            {
                this.speed *= 0.99F;
            }
            this.lift = 0.04F * (Math.min(speed, 15F) / 15F);
        }

        FlapDirection flapDirection = getFlapDirection();
        if(flapDirection == FlapDirection.UP)
        {
            this.lift += 0.04F * (Math.min(Math.max(speed - 5F, 0F), 15F) / 15F);
        }
        else if(flapDirection == FlapDirection.DOWN)
        {
            this.lift -= 0.05F * (Math.min(speed, 15F) / 15F);
        }

        this.vehicleMotionX = (-speed * f1);
        this.vehicleMotionZ = (speed * f2);
        this.motionY += lift;
        this.motionY -= 0.05;

        this.prevAcceleration = acceleration;
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

        this.bodyRotationX = (float) Math.toDegrees(Math.atan2(motionY, speed / 20F));
        this.bodyRotationZ = (this.turnAngle / (float) getMaxTurnAngle()) * 20F;
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
        this.wheelAngle = this.turnAngle * Math.max(0.25F, 1.0F - Math.abs(Math.min(speed, 30F) / 30F));
        this.deltaYaw = this.wheelAngle;

        if(!this.onGround)
        {
            this.deltaYaw *= 0.5;
        }
        else
        {
            this.deltaYaw *= 0.5 * (0.5 + 0.5 * (1.0F - Math.min(speed, 15F) / 15F));
        }
    }

    @Override
    public double getMountedYOffset()
    {
        return 6 * 0.0625;
    }

    public void setFlapDirection(FlapDirection flapDirection)
    {
        this.dataManager.set(FLAP_DIRECTION, flapDirection.ordinal());
    }

    public FlapDirection getFlapDirection()
    {
        return FlapDirection.values()[this.dataManager.get(FLAP_DIRECTION)];
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
