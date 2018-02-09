package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageDrift;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public abstract class EntityVehicle extends Entity
{
    private static final DataParameter<Integer> TURN_DIRECTION = EntityDataManager.createKey(EntityVehicle.class, DataSerializers.VARINT);
    private static final DataParameter<Float> SPEED = EntityDataManager.createKey(EntityVehicle.class, DataSerializers.FLOAT);
    private static final DataParameter<Boolean> DRIFTING = EntityDataManager.createKey(EntityVehicle.class, DataSerializers.BOOLEAN);

    public static final double MAX_SPEED = 15;

    public float wheelAngle;
    public float prevWheelAngle;
    public float frontWheelRotation;
    public float prevFrontWheelRotation;
    public float rearWheelRotation;
    public float prevRearWheelRotation;

    public float deltaYaw;
    public float prevDeltaYaw;
    public float additionalYaw;
    public float prevAdditionalYaw;

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
    protected void entityInit()
    {
        this.dataManager.register(TURN_DIRECTION, TurnDirection.FORWARD.ordinal());
        this.dataManager.register(SPEED, 0F);
        this.dataManager.register(DRIFTING, false);
    }

    @Override
    public void onEntityUpdate()
    {
        super.onEntityUpdate();

        this.prevWheelAngle = this.wheelAngle;
        this.prevFrontWheelRotation = this.frontWheelRotation;
        this.prevRearWheelRotation = this.rearWheelRotation;
        this.prevAdditionalYaw = this.additionalYaw;

        if(world.isRemote)
        {
            this.onClientUpdate();
        }

        float speed = this.getSpeed();

        EntityLivingBase entity = (EntityLivingBase)this.getControllingPassenger();
        if(entity != null)
        {
            /* Handle the current speed of the vehicle based on rider's forward movement */
            if(entity.moveForward > 0)
            {
                speed += 0.5F;
                if(speed > MAX_SPEED)
                {
                    speed = (float) MAX_SPEED;
                }
            }
            else if(entity.moveForward < 0)
            {
                speed -= 4.0F;
                if(speed < -4.0F)
                {
                    speed = -4.0F;
                }
            }
            else
            {
                speed *= 0.85;
            }

            /* Determines the turn direction */
            if(entity.moveStrafing > 0)
            {
                this.setTurnDirection(TurnDirection.RIGHT);
            }
            else if(entity.moveStrafing < 0)
            {
                this.setTurnDirection(TurnDirection.LEFT);
            }
            else
            {
                this.setTurnDirection(TurnDirection.FORWARD);
            }

            TurnDirection turnDirection = getTurnDirection();
            if(this.isDrifting() && turnDirection != TurnDirection.FORWARD)
            {
                this.additionalYaw = MathHelper.clamp(this.additionalYaw + 5F * turnDirection.getDir(), -25F, 25F);
                speed *= 0.95F;
            }
            else
            {
                this.additionalYaw *= 0.85F;
            }

            if(speed != 0)
            {
                float speedPercent = (float) (speed / MAX_SPEED);

                float wheelRotation = 45F; //change to global var
                this.wheelAngle = turnDirection == TurnDirection.RIGHT ? wheelRotation : turnDirection == TurnDirection.LEFT ? -wheelRotation : wheelAngle * 0.5F;
                this.wheelAngle *= Math.max(0.25F, 1.0F - Math.abs(speedPercent));
                this.deltaYaw = wheelAngle * speedPercent / (this.isDrifting() ? 1 : 2);
                this.rotationYaw -= deltaYaw;

                if(entity.moveForward > 0)
                {
                    this.rearWheelRotation -= 68F * (1.0 - speedPercent);
                }
                this.frontWheelRotation -= (68F * speedPercent);
                this.rearWheelRotation -= (68F * speedPercent);

                this.moveRelative(0, 0, speed, 0.01F);

                this.motionY -= 0.08D;
                this.move(MoverType.SELF, this.motionX * Math.abs(speed), this.motionY, this.motionZ * Math.abs(speed));

                double blocksPerTick = Math.sqrt(Math.pow(this.posX - this.prevPosX, 2) + Math.pow(this.posZ - this.prevPosZ, 2));
                //System.out.println((int)((distance * 20) * 3.6));

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
            speed *= 0.85;
        }

        this.setSpeed(speed);
    }

    @SideOnly(Side.CLIENT)
    public void onClientUpdate()
    {
        EntityLivingBase entity = (EntityLivingBase) this.getControllingPassenger();
        if(entity != null && entity.equals(Minecraft.getMinecraft().player))
        {
            boolean drifting = Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown();
            this.setDrifting(drifting);
            PacketHandler.INSTANCE.sendToServer(new MessageDrift(drifting));
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
    public void updatePassenger(Entity passenger)
    {
        super.updatePassenger(passenger);
        //TODO change to config option
        //passenger.rotationYaw -= deltaYaw;
        //passenger.setRotationYawHead(this.rotationYaw);
    }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn) {}

    public boolean isMoving()
    {
        return getSpeed() != 0;
    }

    public float getNormalSpeed()
    {
        return (float) (getSpeed() / MAX_SPEED);
    }

    public void setSpeed(float speed)
    {
        this.dataManager.set(SPEED, speed);
    }

    public float getSpeed()
    {
        return this.dataManager.get(SPEED);
    }

    public void setTurnDirection(TurnDirection turnDirection)
    {
        this.dataManager.set(TURN_DIRECTION, turnDirection.ordinal());
    }

    public TurnDirection getTurnDirection()
    {
        return TurnDirection.values()[this.dataManager.get(TURN_DIRECTION)];
    }

    public void setDrifting(boolean drifting)
    {
        this.dataManager.set(DRIFTING, drifting);
    }

    public boolean isDrifting()
    {
        return this.dataManager.get(DRIFTING);
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
