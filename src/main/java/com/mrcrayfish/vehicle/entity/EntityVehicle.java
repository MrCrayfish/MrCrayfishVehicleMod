package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public abstract class EntityVehicle extends Entity
{
    public static final double MAX_SPEED = 15;
    public double currentSpeed;
    public boolean accelerating;

    public TurnDirection turnDirection = TurnDirection.FORWARD;
    public float wheelAngle;
    public float prevWheelAngle;
    public float frontWheelRotation;
    public float prevFrontWheelRotation;
    public float rearWheelRotation;
    public float prevRearWheelRotation;

    public int soundLoop;

    protected EntityVehicle(World worldIn)
    {
        super(worldIn);
        this.setSize(1F, 1F);
        this.stepHeight = 1.0F;
    }

    public EntityVehicle(World worldIn, double posX, double posY, double posZ)
    {
        this(worldIn);
        this.setPosition(posX, posY, posZ);
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return true;
    }

    @Override
    protected void entityInit() {}

    @Override
    public void onEntityUpdate()
    {
        super.onEntityUpdate();

        this.prevWheelAngle = this.wheelAngle;
        this.prevFrontWheelRotation = this.frontWheelRotation;
        this.prevRearWheelRotation = this.rearWheelRotation;

        EntityLivingBase entity = (EntityLivingBase)this.getControllingPassenger();
        if(entity != null)
        {
            /* Handle the current speed of the vehicle based on rider's forward movement */
            if(entity.moveForward > 0)
            {
                currentSpeed += 0.5;
                if(currentSpeed > MAX_SPEED)
                {
                    currentSpeed = MAX_SPEED;
                }
            }
            else if(entity.moveForward < 0)
            {
                currentSpeed -= 2.0;
                if(currentSpeed < -2.0)
                {
                    currentSpeed = -2.0;
                }
            }
            else
            {
                currentSpeed *= 0.85;
            }

            /* Determines the turn direction */
            if(entity.moveStrafing > 0)
            {
                turnDirection = TurnDirection.RIGHT;
            }
            else if(entity.moveStrafing < 0)
            {
                turnDirection = TurnDirection.LEFT;
            }
            else
            {
                turnDirection = TurnDirection.FORWARD;
            }

            this.accelerating = entity.moveForward != 0;

            System.out.println(entity.moveForward);

            if(currentSpeed != 0)
            {
                float speedPercent = (float) (currentSpeed / MAX_SPEED);

                float turnRotation = 10F * speedPercent;
                this.rotationYaw += entity.moveStrafing > 0 ? -turnRotation : entity.moveStrafing < 0 ? turnRotation : 0;

                float wheelRotation = 45F * Math.abs(speedPercent);
                this.wheelAngle = entity.moveStrafing > 0 ? wheelRotation : entity.moveStrafing < 0 ? -wheelRotation : wheelAngle * 0.65F;
                this.frontWheelRotation -= (68F * speedPercent);
                this.rearWheelRotation -= (68F * speedPercent);

                if(entity.moveForward > 0)
                {
                    this.rearWheelRotation -= 68F * (1.0 - speedPercent);
                }

                float originalYaw = this.rotationYaw;
                this.rotationYaw -= (wheelAngle / 2);
                this.moveRelative(0, 0, (float) currentSpeed, 0.01F);
                this.rotationYaw = originalYaw;

                this.motionY -= 0.08D;

                this.move(MoverType.SELF, this.motionX * Math.abs(currentSpeed), this.motionY, this.motionZ * Math.abs(currentSpeed));

                double distance = Math.sqrt(Math.pow(this.posX - this.prevPosX, 2) + Math.pow(this.posZ - this.prevPosZ, 2));
                System.out.println((int)((distance * 20) * 3.6));

                this.motionY *= 0.9800000190734863D;
                this.motionX *= 0.8;
                this.motionZ *= 0.8;

                this.doBlockCollisions();

                if(entity.moveForward > 0)
                {
                    this.createRunningParticles();
                }
                if(entity.moveForward > 0 && soundLoop % 4 == 0)
                {
                    world.playSound(posX, posY, posZ, ModSounds.DRIVING, SoundCategory.BLOCKS, 0.5F, 1.0F, false);
                }
            }

            if(entity.moveForward <= 0 && soundLoop % 4 == 0)
            {
                world.playSound(posX, posY, posZ, ModSounds.IDLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
            }
            soundLoop++;
        }
        else
        {
            currentSpeed *= 0.85;
        }
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
    {
        if(!world.isRemote)
        {
            player.startRiding(this);
        }
        return true;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        this.setDead();
        return true;
    }

    @Override
    public double getMountedYOffset()
    {
        return 9 * 0.0625;
    }

    @Override
    protected boolean canBeRidden(Entity entityIn)
    {
        return true;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound)
    {

    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound)
    {

    }

    @Nullable
    public Entity getControllingPassenger()
    {
        return this.getPassengers().isEmpty() ? null : (Entity)this.getPassengers().get(0);
    }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn) {}

    public boolean isMoving()
    {
        return currentSpeed != 0;
    }

    public float getNormalSpeed()
    {
        return (float) (currentSpeed / MAX_SPEED);
    }

    public boolean isAccelerating()
    {
        return accelerating;
    }

    public TurnDirection getTurnDirection()
    {
        return turnDirection;
    }

    public enum TurnDirection
    {
        LEFT(-1), FORWARD(0), RIGHT(1);

        final int dir;

        TurnDirection(int dir)
        {
            this.dir = dir;
        }

        public int getDir()
        {
            return dir;
        }
    }
}
