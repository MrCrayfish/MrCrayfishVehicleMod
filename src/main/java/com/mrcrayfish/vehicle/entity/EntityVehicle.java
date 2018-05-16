package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.client.ClientEvents;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageAccelerating;
import com.mrcrayfish.vehicle.network.message.MessageDrift;
import com.mrcrayfish.vehicle.network.message.MessageHorn;
import com.mrcrayfish.vehicle.network.message.MessageTurn;
import com.mrcrayfish.vehicle.proxy.ClientProxy;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
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
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
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
    private static final DataParameter<Float> ACCELERATION_SPEED = EntityDataManager.createKey(EntityVehicle.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> TURN_DIRECTION = EntityDataManager.createKey(EntityVehicle.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> TURN_SENSITIVITY = EntityDataManager.createKey(EntityVehicle.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> DRIFTING = EntityDataManager.createKey(EntityVehicle.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> ACCELERATION_DIRECTION = EntityDataManager.createKey(EntityVehicle.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> TIME_SINCE_HIT = EntityDataManager.createKey(EntityVehicle.class, DataSerializers.VARINT);
    private static final DataParameter<Float> DAMAGE_TAKEN = EntityDataManager.createKey(EntityVehicle.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> ENGINE_TYPE = EntityDataManager.createKey(EntityVehicle.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> HORN = EntityDataManager.createKey(EntityVehicle.class, DataSerializers.BOOLEAN);

    public float prevCurrentSpeed;
    public float currentSpeed;
    public float speedMultiplier;

    public int turnAngle;
    public int prevTurnAngle;

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

    @SideOnly(Side.CLIENT)
    public ItemStack engine;

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

    //TODO ability to change with nbt
    public SoundEvent getHornSound()
    {
        return ModSounds.HORN_MONO;
    }

    public SoundEvent getHornRidingSound()
    {
        return ModSounds.HORN_STEREO;
    }

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
    public void entityInit()
    {
        this.dataManager.register(CURRENT_SPEED, 0F);
        this.dataManager.register(MAX_SPEED, 10F);
        this.dataManager.register(ACCELERATION_SPEED, 0.5F);
        this.dataManager.register(TURN_DIRECTION, TurnDirection.FORWARD.ordinal());
        this.dataManager.register(TURN_SENSITIVITY, 10);
        this.dataManager.register(DRIFTING, false);
        this.dataManager.register(ACCELERATION_DIRECTION, AccelerationDirection.NONE.ordinal());
        this.dataManager.register(TIME_SINCE_HIT, 0);
        this.dataManager.register(DAMAGE_TAKEN, 0F);
        this.dataManager.register(ENGINE_TYPE, 0);
        this.dataManager.register(HORN, false);

        if(this.world.isRemote)
        {
            engine = new ItemStack(ModItems.ENGINE);
        }
    }

    @Override
    public void onUpdate()
    {
        if(this.getTimeSinceHit() > 0)
        {
            this.setTimeSinceHit(this.getTimeSinceHit() - 1);
        }

        if(this.getDamageTaken() > 0.0F)
        {
            this.setDamageTaken(this.getDamageTaken() - 1.0F);
        }

        super.onUpdate();

        if(this.getControllingPassenger() != null)
        {
            this.tickLerp();
        }
    }

    /**
     * Smooths the rendering on servers
     */
    private void tickLerp()
    {
        if(this.lerpSteps > 0 && !this.canPassengerSteer())
        {
            double d0 = this.posX + (this.lerpX - this.posX) / (double) this.lerpSteps;
            double d1 = this.posY + (this.lerpY - this.posY) / (double) this.lerpSteps;
            double d2 = this.posZ + (this.lerpZ - this.posZ) / (double) this.lerpSteps;
            double d3 = MathHelper.wrapDegrees(this.lerpYaw - (double) this.rotationYaw);
            this.rotationYaw = (float) ((double) this.rotationYaw + d3 / (double) this.lerpSteps);
            this.rotationPitch = (float) ((double) this.rotationPitch + (this.lerpPitch - (double) this.rotationPitch) / (double) this.lerpSteps);
            --this.lerpSteps;
            this.setPosition(d0, d1, d2);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        }
    }

    @Override
    public void onEntityUpdate()
    {
        super.onEntityUpdate();

        prevCurrentSpeed = currentSpeed;
        prevTurnAngle = turnAngle;
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
            currentSpeed = currentSpeed + (currentSpeed * speedMultiplier);
            setSpeed(currentSpeed);
            createParticles();
        }
        else
        {
            currentSpeed *= 0.5F;
            setSpeed(currentSpeed);
        }

        /* Handle the current speed of the vehicle based on rider's forward movement */
        updateSpeed();
        updateDrifting();
        updateWheels();

        motionY -= 0.08D;
        rotationYaw -= deltaYaw;

        moveRelative(0, 0, currentSpeed, 0.01F);
        move(MoverType.SELF, motionX * Math.abs(currentSpeed), motionY, motionZ * Math.abs(currentSpeed));

        motionX *= 0.8;
        motionY *= 0.9800000190734863D;
        motionZ *= 0.8;
        speedMultiplier *= 0.85;

        doBlockCollisions();
    }

    private void updateSpeed()
    {
        EngineType engineType = this.getEngineType();
        AccelerationDirection acceleration = this.getAcceleration();
        if(this.getControllingPassenger() != null)
        {
            if(acceleration == AccelerationDirection.FORWARD)
            {
                this.currentSpeed += this.getAccelerationSpeed() * engineType.getAccelerationMultiplier();
                if(this.currentSpeed > this.getMaxSpeed() + engineType.getAdditionalMaxSpeed())
                {
                    this.currentSpeed = this.getMaxSpeed() + engineType.getAdditionalMaxSpeed();
                }
            }
            else if(acceleration == AccelerationDirection.REVERSE)
            {
                this.currentSpeed -= this.getAccelerationSpeed() * engineType.getAccelerationMultiplier();
                if(this.currentSpeed < -(4.0F + engineType.getAdditionalMaxSpeed() / 2))
                {
                    this.currentSpeed = -(4.0F + engineType.getAdditionalMaxSpeed() / 2);
                }
            }
            else
            {
                this.currentSpeed *= 0.9;
            }
        }
        else
        {
            this.currentSpeed *= 0.9;
        }
    }

    private void updateDrifting()
    {
        TurnDirection turnDirection = this.getTurnDirection();
        if(this.getControllingPassenger() != null && this.isDrifting() && turnDirection != TurnDirection.FORWARD)
        {
            AccelerationDirection acceleration = this.getAcceleration();
            if(acceleration == AccelerationDirection.FORWARD)
            {
                this.currentSpeed *= 0.95F;
            }
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
        if(this.getControllingPassenger() != null && direction != TurnDirection.FORWARD)
        {
            this.turnAngle += direction.dir * getTurnSensitivity();
            if(Math.abs(this.turnAngle) > 45)
            {
                this.turnAngle = 45 * direction.dir;
            }
        }
        else
        {
            this.turnAngle *= 0.75;
        }

        float speedPercent = (float) (this.currentSpeed / this.getMaxSpeed());
        this.wheelAngle = this.turnAngle * Math.max(0.25F, 1.0F - Math.abs(speedPercent)); //TODO turn resistance
        this.deltaYaw = this.wheelAngle * speedPercent / (this.isDrifting() ? 1.5F : 2F);

        AccelerationDirection acceleration = this.getAcceleration();
        if(this.getControllingPassenger() != null && acceleration == AccelerationDirection.FORWARD)
        {
            this.rearWheelRotation -= 68F * (1.0 - speedPercent);
        }
        this.frontWheelRotation -= (68F * speedPercent);
        this.rearWheelRotation -= (68F * speedPercent);
    }

    private void createParticles()
    {
        int x = MathHelper.floor(this.posX);
        int y = MathHelper.floor(this.posY - 0.2D);
        int z = MathHelper.floor(this.posZ);
        BlockPos pos = new BlockPos(x, y, z);
        IBlockState state = this.world.getBlockState(pos);
        if(state.getMaterial() != Material.AIR && state.getMaterial().isToolNotRequired())
        {
            if(this.getAcceleration() == AccelerationDirection.FORWARD)
            {
                if(this.isDrifting())
                {
                    for(int i = 0; i < 3; i++)
                    {
                        this.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, this.posX + ((double) this.rand.nextFloat() - 0.5D) * (double) this.width, this.getEntityBoundingBox().minY + 0.1D, this.posZ + ((double) this.rand.nextFloat() - 0.5D) * (double) this.width, -this.motionX * 4.0D, 1.5D, -this.motionZ * 4.0D, Block.getStateId(state));
                    }
                }
                else
                {
                    this.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, this.posX + ((double) this.rand.nextFloat() - 0.5D) * (double) this.width, this.getEntityBoundingBox().minY + 0.1D, this.posZ + ((double) this.rand.nextFloat() - 0.5D) * (double) this.width, -this.motionX * 4.0D, 1.5D, -this.motionZ * 4.0D, Block.getStateId(state));
                }
            }
        }

        if(this.shouldShowEngineSmoke() && this.ticksExisted % 2 == 0)
        {
            Vec3d smokePosition = this.getEngineSmokePosition().rotateYaw(-(this.rotationYaw - this.additionalYaw) * 0.017453292F);
            this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX + smokePosition.x, this.posY + smokePosition.y, this.posZ + smokePosition.z, -this.motionX, 0.0D, -this.motionZ, Block.getStateId(state));
        }
    }

    @SideOnly(Side.CLIENT)
    public void onClientUpdate()
    {
        EntityLivingBase entity = (EntityLivingBase) this.getControllingPassenger();
        if(entity != null && entity.equals(Minecraft.getMinecraft().player))
        {
            AccelerationDirection acceleration = AccelerationDirection.fromEntity(entity);
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

            boolean horn = ClientProxy.KEY_HORN.isKeyDown();
            this.setHorn(horn);
            PacketHandler.INSTANCE.sendToServer(new MessageHorn(horn));

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
        if(this.isEntityInvulnerable(source))
        {
            return false;
        }
        else if(!this.world.isRemote && !this.isDead)
        {
            if(source instanceof EntityDamageSourceIndirect && source.getTrueSource() != null && this.isPassenger(source.getTrueSource()))
            {
                return false;
            }
            else
            {
                this.setTimeSinceHit(10);
                this.setDamageTaken(this.getDamageTaken() + amount * 10.0F);

                boolean flag = source.getTrueSource() instanceof EntityPlayer && ((EntityPlayer) source.getTrueSource()).capabilities.isCreativeMode;
                if(flag || this.getDamageTaken() > 40.0F)
                {
                    if(!flag && this.world.getGameRules().getBoolean("doEntityDrops"))
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
        if(compound.hasKey("engineType", Constants.NBT.TAG_INT))
        {
            this.setEngineType(EngineType.getType(compound.getInteger("engineType")));
        }
        if(compound.hasKey("maxSpeed", Constants.NBT.TAG_FLOAT))
        {
            this.setMaxSpeed(compound.getFloat("maxSpeed"));
        }
        if(compound.hasKey("accelerationSpeed", Constants.NBT.TAG_FLOAT))
        {
            this.setAccelerationSpeed(compound.getFloat("accelerationSpeed"));
        }
        if(compound.hasKey("turnSensitivity", Constants.NBT.TAG_INT))
        {
            this.setTurnSensitivity(compound.getInteger("turnSensitivity"));
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound)
    {
        compound.setInteger("engineType", this.getEngineType().ordinal());
        compound.setFloat("maxSpeed", this.getMaxSpeed());
        compound.setFloat("accelerationSpeed", this.getAccelerationSpeed());
        compound.setInteger("turnSensitivity", this.getTurnSensitivity());
    }

    @Nullable
    public Entity getControllingPassenger()
    {
        return this.getPassengers().isEmpty() ? null : (Entity) this.getPassengers().get(0);
    }

    @Override
    public void updatePassenger(Entity passenger)
    {
        super.updatePassenger(passenger);
        //TODO change to config option
        passenger.rotationYaw -= deltaYaw;
        passenger.setRotationYawHead(this.rotationYaw);
    }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn)
    {
    }

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

    public float getAccelerationSpeed()
    {
        return this.dataManager.get(ACCELERATION_SPEED);
    }

    public void setAccelerationSpeed(float speed)
    {
        this.dataManager.set(ACCELERATION_SPEED, speed);
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

    public void setAcceleration(AccelerationDirection direction)
    {
        this.dataManager.set(ACCELERATION_DIRECTION, direction.ordinal());
    }

    public AccelerationDirection getAcceleration()
    {
        return AccelerationDirection.values()[this.dataManager.get(ACCELERATION_DIRECTION)];
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

    public void setEngineType(EngineType engineType)
    {
        this.dataManager.set(ENGINE_TYPE, engineType.ordinal());
    }

    public EngineType getEngineType()
    {
        return EngineType.getType(this.dataManager.get(ENGINE_TYPE));
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldRenderEngine()
    {
        return true;
    }

    public Vec3d getEngineSmokePosition()
    {
        return new Vec3d(0, 0, 0);
    }

    public boolean shouldShowEngineSmoke()
    {
        return false;
    }

    public void setHorn(boolean activated)
    {
        this.dataManager.set(HORN, activated);
    }

    public boolean getHorn()
    {
        return this.dataManager.get(HORN);
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key)
    {
        super.notifyDataManagerChange(key);
        if(world.isRemote)
        {
            if(ENGINE_TYPE.equals(key))
            {
                EngineType type = EngineType.getType(this.dataManager.get(ENGINE_TYPE));
                engine.setItemDamage(type.ordinal());
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport)
    {
        this.lerpX = x;
        this.lerpY = y;
        this.lerpZ = z;
        this.lerpYaw = (double) yaw;
        this.lerpPitch = (double) pitch;
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
            this.rotationYaw = (float) this.lerpYaw;
            this.rotationPitch = (float) this.lerpPitch;
        }
        if(passenger instanceof EntityPlayer && world.isRemote)
        {
            VehicleMod.proxy.playVehicleSound((EntityPlayer) passenger, this);
        }
    }

    @Override
    protected void removePassenger(Entity passenger)
    {
        super.removePassenger(passenger);
        if(this.getControllingPassenger() == null)
        {
            this.rotationYaw -= this.additionalYaw;
            this.additionalYaw = 0;
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

    public enum AccelerationDirection
    {
        FORWARD, NONE, REVERSE;

        public static AccelerationDirection fromEntity(EntityLivingBase entity)
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
