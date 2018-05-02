package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageAccelerating;
import com.mrcrayfish.vehicle.network.message.MessageDrift;
import com.mrcrayfish.vehicle.network.message.MessageTurn;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public abstract class EntityVehicle extends Entity
{
    private static final DataParameter<Float> CURRENT_SPEED = EntityDataManager.createKey(EntityVehicle.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> MAX_SPEED = EntityDataManager.createKey(EntityVehicle.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> TURN_DIRECTION = EntityDataManager.createKey(EntityVehicle.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> TURN_SENSITIVITY = EntityDataManager.createKey(EntityVehicle.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> DRIFTING = EntityDataManager.createKey(EntityVehicle.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> ACCELERATION_DIRECTION = EntityDataManager.createKey(EntityVehicle.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> TIME_SINCE_HIT = EntityDataManager.createKey(EntityVehicle.class, DataSerializers.VARINT);
    private static final DataParameter<Float> DAMAGE_TAKEN = EntityDataManager.createKey(EntityVehicle.class, DataSerializers.FLOAT);

    public float currentSpeed;
    public float speedMultiplier;

    public int turnAngle;

    public float wheelAngle;
    public float prevWheelAngle;
    public float frontWheelRotation;
    public float prevFrontWheelRotation;
    public float rearWheelRotation;
    public float prevRearWheelRotation;

    public float deltaYaw;
    public float additionalYaw;
    public float prevAdditionalYaw;

    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYaw;
    private double lerpPitch;

    /**
     * ItemStack instances used for rendering
     */
    @SideOnly(Side.CLIENT)
    public ItemStack body, wheel;

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

    public abstract SoundEvent getMovingSound();

    public abstract SoundEvent getRidingSound();

    public float getMinEnginePitch()
    {
        return 0.5F;
    }

    public float getMaxEnginePitch()
    {
        return 1.2F;
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return true;
    }

    @Override
    protected void entityInit()
    {
        this.dataManager.register(CURRENT_SPEED, 0F);
        this.dataManager.register(MAX_SPEED, 10F);
        this.dataManager.register(TURN_DIRECTION, TurnDirection.FORWARD.ordinal());
        this.dataManager.register(TURN_SENSITIVITY, 10);
        this.dataManager.register(DRIFTING, false);
        this.dataManager.register(ACCELERATION_DIRECTION, Acceleration.NONE.ordinal());
        this.dataManager.register(TIME_SINCE_HIT, 0);
        this.dataManager.register(DAMAGE_TAKEN, 0F);
    }

    @Override
    public void onUpdate()
    {
        if (this.getTimeSinceHit() > 0)
        {
            this.setTimeSinceHit(this.getTimeSinceHit() - 1);
        }

        if (this.getDamageTaken() > 0.0F)
        {
            this.setDamageTaken(this.getDamageTaken() - 1.0F);
        }

        super.onUpdate();
        this.tickLerp();
    }

    /**
     * Smooths the rendering on servers
     */
    private void tickLerp()
    {
        if (this.lerpSteps > 0 && !this.canPassengerSteer())
        {
            double d0 = this.posX + (this.lerpX - this.posX) / (double)this.lerpSteps;
            double d1 = this.posY + (this.lerpY - this.posY) / (double)this.lerpSteps;
            double d2 = this.posZ + (this.lerpZ - this.posZ) / (double)this.lerpSteps;
            double d3 = MathHelper.wrapDegrees(this.lerpYaw - (double)this.rotationYaw);
            this.rotationYaw = (float)((double)this.rotationYaw + d3 / (double)this.lerpSteps);
            this.rotationPitch = (float)((double)this.rotationPitch + (this.lerpPitch - (double)this.rotationPitch) / (double)this.lerpSteps);
            --this.lerpSteps;
            this.setPosition(d0, d1, d2);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        }
    }

    @Override
    public void onEntityUpdate()
    {
        super.onEntityUpdate();

        prevWheelAngle = wheelAngle;
        prevFrontWheelRotation = frontWheelRotation;
        prevRearWheelRotation = rearWheelRotation;
        prevAdditionalYaw = additionalYaw;

        if(world.isRemote)
        {
            onClientUpdate();
        }

        currentSpeed = getSpeed();

        EntityLivingBase entity = (EntityLivingBase) getControllingPassenger();
        if(entity != null)
        {
            /* Handle the current speed of the vehicle based on rider's forward movement */
            updateSpeed();
            updateDrifting();
            updateWheels();

            currentSpeed = currentSpeed + (currentSpeed * speedMultiplier);
            setSpeed(currentSpeed);

            rotationYaw -= deltaYaw;
            motionY -= 0.08D;

            moveRelative(0, 0, currentSpeed, 0.01F);
            move(MoverType.SELF, motionX * Math.abs(currentSpeed), motionY, motionZ * Math.abs(currentSpeed));

            motionX *= 0.8;
            motionY *= 0.9800000190734863D;
            motionZ *= 0.8;
            speedMultiplier *= 0.85;

            doBlockCollisions();
            createParticles();
        }
        else
        {
            currentSpeed = 0F;
            setSpeed(currentSpeed);
        }
    }

    private void updateSpeed()
    {
        Acceleration acceleration = this.getAcceleration();
        if(acceleration == Acceleration.FORWARD)
        {
            this.currentSpeed += 0.5F;
            if(this.currentSpeed > this.getMaxSpeed())
            {
                this.currentSpeed = this.getMaxSpeed();
            }
        }
        else if(acceleration == Acceleration.REVERSE)
        {
            this.currentSpeed -= 4.0F;
            if(this.currentSpeed < -4.0F)
            {
                this.currentSpeed = -4.0F;
            }
        }
        else
        {
            this.currentSpeed *= 0.85;
        }

        TurnDirection turnDirection = this.getTurnDirection();
        if(this.isDrifting() && acceleration == Acceleration.FORWARD && turnDirection != TurnDirection.FORWARD)
        {
            this.currentSpeed *= 0.95F;
        }
    }

    private void updateDrifting()
    {
        TurnDirection turnDirection = this.getTurnDirection();
        if(this.isDrifting() && turnDirection != TurnDirection.FORWARD)
        {
            this.additionalYaw = MathHelper.clamp(this.additionalYaw + 5F * turnDirection.getDir(), -35F, 35F);
        }
        else
        {
            this.additionalYaw *= 0.85F;
        }
    }

    private void updateWheels()
    {
        TurnDirection direction = this.getTurnDirection();
        if(direction != TurnDirection.FORWARD)
        {
            if(Math.abs(this.turnAngle + direction.dir * getTurnSensitivity()) < 45)
            {
                this.turnAngle += direction.dir * 10;
                if(this.turnAngle > 45)
                {
                    this.turnAngle = 45;
                }
            }
        }
        else
        {
            this.turnAngle *= 0.75;
        }

        float speedPercent = (float) (this.currentSpeed / this.getMaxSpeed());
        this.wheelAngle = this.turnAngle * Math.max(0.25F, 1.0F - Math.abs(speedPercent)); //TODO turn resistance
        this.deltaYaw = this.wheelAngle * speedPercent / (this.isDrifting() ? 1.5F : 2F);

        Acceleration acceleration = this.getAcceleration();
        if(acceleration == Acceleration.FORWARD)
        {
            this.rearWheelRotation -= 68F * (1.0 - speedPercent);
        }
        this.frontWheelRotation -= (68F * speedPercent);
        this.rearWheelRotation -= (68F * speedPercent);
    }

    private void createParticles()
    {
        if(this.getAcceleration() == Acceleration.FORWARD)
        {
            if(this.isDrifting())
            {
                for(int i = 0; i < 3; i++)
                {
                    this.createRunningParticles();
                }
            }
            else
            {
                this.createRunningParticles();
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void onClientUpdate()
    {
        EntityLivingBase entity = (EntityLivingBase) this.getControllingPassenger();
        if(entity != null && entity.equals(Minecraft.getMinecraft().player))
        {
            Acceleration acceleration = Acceleration.fromEntity(entity);
            if(this.getAcceleration() != acceleration)
            {
                this.setAcceleration(acceleration);
                PacketHandler.INSTANCE.sendToServer(new MessageAccelerating(acceleration));
            }

            boolean drifting = Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown();
            if(this.isDrifting() != drifting)
            {
                this.setDrifting(drifting);
                PacketHandler.INSTANCE.sendToServer(new MessageDrift(drifting));
            }

            TurnDirection direction = TurnDirection.FORWARD;
            if(entity.moveStrafing < 0)
            {
                direction = TurnDirection.RIGHT;
            }
            else if(entity.moveStrafing > 0)
            {
                direction = TurnDirection.LEFT;
            }
            if(this.getTurnDirection() != direction)
            {
                this.setTurnDirection(direction);
                PacketHandler.INSTANCE.sendToServer(new MessageTurn(direction));
            }
        }
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
    {
        if(!world.isRemote && !player.isSneaking())
        {
            player.startRiding(this);
        }
        return true;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (this.isEntityInvulnerable(source))
        {
            return false;
        }
        else if (!this.world.isRemote && !this.isDead)
        {
            if (source instanceof EntityDamageSourceIndirect && source.getTrueSource() != null && this.isPassenger(source.getTrueSource()))
            {
                return false;
            }
            else
            {
                this.setTimeSinceHit(10);
                this.setDamageTaken(this.getDamageTaken() + amount * 10.0F);

                boolean flag = source.getTrueSource() instanceof EntityPlayer && ((EntityPlayer)source.getTrueSource()).capabilities.isCreativeMode;
                if (flag || this.getDamageTaken() > 40.0F)
                {
                    if (!flag && this.world.getGameRules().getBoolean("doEntityDrops"))
                    {
                        //this.dropItemWithOffset(this.getItemBoat(), 1, 0.0F);
                    }

                    this.setDead();
                }

                return true;
            }
        }
        else
        {
            return true;
        }
    }

    @Override
    public abstract double getMountedYOffset();

    @Override
    protected boolean canBeRidden(Entity entityIn)
    {
        return true;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound)
    {
        if(compound.hasKey("maxSpeed", Constants.NBT.TAG_FLOAT))
        {
            this.setMaxSpeed(compound.getFloat("maxSpeed"));
        }
        if(compound.hasKey("turnSensitivity", Constants.NBT.TAG_INT))
        {
            this.setTurnSensitivity(compound.getInteger("turnSensitivity"));
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound)
    {
        compound.setFloat("maxSpeed", this.getMaxSpeed());
        compound.setInteger("turnSensitivity", this.getTurnSensitivity());
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

    @SideOnly(Side.CLIENT)
    public void performHurtAnimation()
    {
        this.setTimeSinceHit(10);
        this.setDamageTaken(this.getDamageTaken() * 11.0F);
    }

    public boolean isMoving()
    {
        return this.currentSpeed != 0;
    }

    public void setMaxSpeed(float maxSpeed)
    {
        this.dataManager.set(MAX_SPEED, maxSpeed);
    }

    public float getMaxSpeed()
    {
        return this.dataManager.get(MAX_SPEED);
    }

    public void setSpeed(float speed)
    {
        this.dataManager.set(CURRENT_SPEED, speed);
    }

    public float getSpeed()
    {
        return this.currentSpeed;
    }

    public float getNormalSpeed()
    {
        return this.currentSpeed / this.getMaxSpeed();
    }

    public double getKilometersPreHour()
    {
        return Math.sqrt(Math.pow(this.posX - this.prevPosX, 2) + Math.pow(this.posZ - this.prevPosZ, 2)) * 3.6;
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

    public void setAcceleration(Acceleration direction)
    {
        this.dataManager.set(ACCELERATION_DIRECTION, direction.ordinal());
    }

    public Acceleration getAcceleration()
    {
        return Acceleration.values()[this.dataManager.get(ACCELERATION_DIRECTION)];
    }

    public void setTurnSensitivity(int sensitivity)
    {
        this.dataManager.set(TURN_SENSITIVITY, sensitivity);
    }

    public int getTurnSensitivity()
    {
        return this.dataManager.get(TURN_SENSITIVITY);
    }

    /**
     * Sets the time to count down from since the last time entity was hit.
     */
    public void setTimeSinceHit(int timeSinceHit)
    {
        this.dataManager.set(TIME_SINCE_HIT, timeSinceHit);
    }

    /**
     * Gets the time since the last hit.
     */
    public int getTimeSinceHit()
    {
        return this.dataManager.get(TIME_SINCE_HIT);
    }

    /**
     * Sets the damage taken from the last hit.
     */
    public void setDamageTaken(float damageTaken)
    {
        this.dataManager.set(DAMAGE_TAKEN, damageTaken);
    }

    /**
     * Gets the damage taken from the last hit.
     */
    public float getDamageTaken()
    {
        return this.dataManager.get(DAMAGE_TAKEN);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport)
    {
        this.lerpX = x;
        this.lerpY = y;
        this.lerpZ = z;
        this.lerpYaw = (double)yaw;
        this.lerpPitch = (double)pitch;
        this.lerpSteps = 10;
    }

    @Override
    protected void addPassenger(Entity passenger)
    {
        super.addPassenger(passenger);
        if(this.canPassengerSteer() && this.lerpSteps > 0)
        {
            this.lerpSteps = 0;
            this.posX = this.lerpX;
            this.posY = this.lerpY;
            this.posZ = this.lerpZ;
            this.rotationYaw = (float)this.lerpYaw;
            this.rotationPitch = (float)this.lerpPitch;
        }
        if(passenger instanceof EntityPlayer)
        {
            VehicleMod.proxy.playVehicleSound((EntityPlayer) passenger, this);
        }
    }

    public enum TurnDirection
    {
        LEFT(1), FORWARD(0), RIGHT(-1);

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

    public enum Acceleration
    {
        FORWARD, NONE, REVERSE;

        public static Acceleration fromEntity(EntityLivingBase entity)
        {
            if(entity.moveForward > 0)
            {
                return FORWARD;
            }
            else if(entity.moveForward < 0)
            {
                return REVERSE;
            }
            return NONE;
        }
    }
}
