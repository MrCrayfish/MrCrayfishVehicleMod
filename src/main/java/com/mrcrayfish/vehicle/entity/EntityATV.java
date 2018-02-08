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
public class EntityATV extends Entity
{
    public static final double MAX_SPEED = 5;
    public double currentSpeed;

    public float wheelAngle;
    public float prevWheelAngle;
    public float wheelRotation;
    public float prevWheelRotation;

    public int soundLoop;

    public EntityATV(World worldIn)
    {
        super(worldIn);
        this.setSize(1F, 1F);
        this.stepHeight = 1.0F;
    }

    public EntityATV(World worldIn, double posX, double posY, double posZ)
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
        this.prevWheelRotation = this.wheelRotation;

        EntityLivingBase entity = (EntityLivingBase)this.getControllingPassenger();
        if(entity != null)
        {
            /* Handle the current speed of the vehicle based on rider's forward movement */
            if(entity.moveForward > 0)
            {
                if(currentSpeed + 1.0 < MAX_SPEED)
                {
                    currentSpeed += 0.25;
                }
            }
            else if(entity.moveForward < 0)
            {
                if(currentSpeed > -2.0)
                {
                    currentSpeed -= 0.25;
                }
            }
            else
            {
                currentSpeed *= 0.85;
            }

            if(currentSpeed != 0)
            {
                float speedPercent = (float) (currentSpeed / MAX_SPEED);

                float turnRotation = 10F * speedPercent;
                this.rotationYaw += entity.moveStrafing > 0 ? -turnRotation : entity.moveStrafing < 0 ? turnRotation : 0;

                float wheelRotation = 45F * Math.abs(speedPercent);
                this.wheelAngle = entity.moveStrafing > 0 ? wheelRotation : entity.moveStrafing < 0 ? -wheelRotation : wheelAngle * 0.65F;
                this.wheelRotation -= (40F * speedPercent) * Math.abs(entity.moveForward);

                this.moveRelative(0, 0, entity.moveForward, 0.02F);

                this.motionY -= 0.08D;

                this.move(MoverType.SELF, this.motionX * Math.abs(currentSpeed), this.motionY, this.motionZ * Math.abs(currentSpeed));

                this.motionY *= 0.9800000190734863D;
                this.motionX *= 0.8;
                this.motionZ *= 0.8;

                this.doBlockCollisions();

                if(currentSpeed > 2.0)
                {
                    this.createRunningParticles();
                }
                if(currentSpeed > 0.75 && soundLoop % 4 == 0)
                {
                    world.playSound(posX, posY, posZ, ModSounds.DRIVING, SoundCategory.BLOCKS, 0.5F, 1.0F, false);
                }
            }

            if(currentSpeed <= 0.75 && soundLoop % 4 == 0)
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
        return 6 * 0.0625;
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
}
