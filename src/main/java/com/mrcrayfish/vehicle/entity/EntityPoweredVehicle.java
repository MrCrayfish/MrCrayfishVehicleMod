package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.entity.vehicle.EntityBumperCar;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.item.ItemJerryCan;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageAccelerating;
import com.mrcrayfish.vehicle.network.message.MessageHorn;
import com.mrcrayfish.vehicle.network.message.MessageTurn;
import com.mrcrayfish.vehicle.proxy.ClientProxy;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

import java.awt.Color;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public abstract class EntityPoweredVehicle extends EntityVehicle
{
    private static final DataParameter<Float> CURRENT_SPEED = EntityDataManager.createKey(EntityPoweredVehicle.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> MAX_SPEED = EntityDataManager.createKey(EntityPoweredVehicle.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> ACCELERATION_SPEED = EntityDataManager.createKey(EntityPoweredVehicle.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> TURN_DIRECTION = EntityDataManager.createKey(EntityPoweredVehicle.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> TURN_SENSITIVITY = EntityDataManager.createKey(EntityPoweredVehicle.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> MAX_TURN_ANGLE = EntityDataManager.createKey(EntityPoweredVehicle.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> ACCELERATION_DIRECTION = EntityDataManager.createKey(EntityPoweredVehicle.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> ENGINE_TYPE = EntityDataManager.createKey(EntityPoweredVehicle.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> HORN = EntityDataManager.createKey(EntityPoweredVehicle.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Float> CURRENT_FUEL = EntityDataManager.createKey(EntityPoweredVehicle.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> FUEL_CAPACITY = EntityDataManager.createKey(EntityPoweredVehicle.class, DataSerializers.FLOAT);

    public float prevCurrentSpeed;
    public float currentSpeed;
    public float speedMultiplier;
    public boolean boosting;
    public int boostTimer;
    public boolean launching;
    public int launchingTimer;
    public boolean disableFallDamage;
    public float fuelConsumption = 1F;

    public int turnAngle;
    public int prevTurnAngle;

    public float deltaYaw;
    public float wheelAngle;
    public float prevWheelAngle;

    public float vehicleMotionX;
    public float vehicleMotionY;
    public float vehicleMotionZ;

    @SideOnly(Side.CLIENT)
    public ItemStack engine;

    @SideOnly(Side.CLIENT)
    private FuelPort fuelPort;

    @SideOnly(Side.CLIENT)
    public ItemStack fuelPortClosed;

    @SideOnly(Side.CLIENT)
    public ItemStack fuelPortBody;

    @SideOnly(Side.CLIENT)
    public ItemStack fuelPortLid;

    private boolean fueling;

    protected EntityPoweredVehicle(World worldIn)
    {
        super(worldIn);
        this.setSize(1F, 1F);
        this.stepHeight = 1.0F;
    }

    public EntityPoweredVehicle(World worldIn, double posX, double posY, double posZ)
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

    public void playFuelPortOpenSound()
    {
        if (!fueling)
        {
            fuelPort.playOpenSound();
            fueling = true;
        }
    }

    public void playFuelPortCloseSound()
    {
        if (fueling)
        {
            fuelPort.playCloseSound();
            fueling = false;
        }
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
        super.entityInit();
        this.dataManager.register(CURRENT_SPEED, 0F);
        this.dataManager.register(MAX_SPEED, 10F);
        this.dataManager.register(ACCELERATION_SPEED, 0.5F);
        this.dataManager.register(TURN_DIRECTION, TurnDirection.FORWARD.ordinal());
        this.dataManager.register(TURN_SENSITIVITY, 10);
        this.dataManager.register(MAX_TURN_ANGLE, 45);
        this.dataManager.register(ACCELERATION_DIRECTION, AccelerationDirection.NONE.ordinal());
        this.dataManager.register(ENGINE_TYPE, 0);
        this.dataManager.register(HORN, false);
        this.dataManager.register(CURRENT_FUEL, 0F);
        this.dataManager.register(FUEL_CAPACITY, 15000F);
    }

    @Override
    public void onClientInit()
    {
        engine = new ItemStack(ModItems.ENGINE);
        setFuelPort(FuelPort.LID);
    }

    protected void setFuelPort(FuelPort fuelPort)
    {
        this.fuelPort = fuelPort;
        fuelPortClosed = new ItemStack(fuelPort.getClosed());
        fuelPortBody = new ItemStack(fuelPort.getBody());
        fuelPortLid = new ItemStack(fuelPort.getLid());
    }

    public void fuelVehicle(EntityPlayer player, EnumHand hand)
    {
        ItemStack stack = player.getHeldItem(hand);
        if(!stack.isEmpty() && stack.getItem() instanceof ItemJerryCan)
        {
            ItemJerryCan jerryCan = (ItemJerryCan) stack.getItem();
            int rate = jerryCan.getFillRate(stack);
            int drained = jerryCan.drain(stack, rate);
            int remaining = this.addFuel(drained);
            jerryCan.fill(stack, remaining);
        }
    }

    @Override
    public void onUpdateVehicle()
    {
        prevCurrentSpeed = currentSpeed;
        prevTurnAngle = turnAngle;
        prevWheelAngle = wheelAngle;

        if(world.isRemote)
        {
            this.onClientUpdate();
        }

        Entity controllingPassenger = this.getControllingPassenger();

        /* If there driver, create particles */
        if(controllingPassenger != null)
        {
            this.createParticles();
        }

        /* Handle the current speed of the vehicle based on rider's forward movement */
        this.updateSpeed();
        this.updateTurning();
        this.updateVehicle();
        this.setSpeed(currentSpeed);

        /* Updates the direction of the vehicle */
        rotationYaw -= deltaYaw;

        /* Updates the vehicle motion and applies it on top of the normal motion */
        this.updateVehicleMotion();
        move(MoverType.SELF, motionX + vehicleMotionX, motionY + vehicleMotionY, motionZ + vehicleMotionZ);

        /* Reduces the motion and speed multiplier */
        if(this.onGround)
        {
            motionX *= 0.8;
            motionY *= 0.98D;
            motionZ *= 0.8;
        }
        else
        {
            motionX *= 0.98;
            motionY *= 0.98D;
            motionZ *= 0.98;
        }

        if(boostTimer > 0)
        {
            boostTimer--;
        }
        else
        {
            boosting = false;
            speedMultiplier *= 0.85;
        }

        if(launchingTimer > 0)
        {
            //Ensures fall damage is disabled while launching
            disableFallDamage = true;
            launchingTimer--;
        }
        else
        {
            launching = false;
        }

        /* Checks for block collisions */
        this.doBlockCollisions();

        /* Checks for collisions with any other vehicles */
        List<Entity> list = this.world.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox(), entity -> entity instanceof EntityBumperCar);
        if (!list.isEmpty())
        {
            for(Entity entity : list)
            {
                this.applyEntityCollision(entity);
            }
        }

        if(controllingPassenger != null && controllingPassenger instanceof EntityPlayer && !((EntityPlayer) controllingPassenger).isCreative())
        {
            float currentSpeed = Math.abs(Math.min(this.getSpeed(), this.getMaxSpeed()));
            float normalSpeed = Math.max(0.05F, currentSpeed / this.getMaxSpeed());
            float currentFuel = this.getCurrentFuel();
            currentFuel -= fuelConsumption * normalSpeed;
            if(currentFuel < 0F) currentFuel = 0F;
            this.setCurrentFuel(currentFuel);
        }
    }

    public void updateVehicle() {}

    public abstract void updateVehicleMotion();

    protected void updateSpeed()
    {
        currentSpeed = this.getSpeed();

        EngineType engineType = this.getEngineType();
        AccelerationDirection acceleration = this.getAcceleration();
        if(this.getControllingPassenger() != null)
        {
            if(this.hasFuel())
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
        else
        {
            this.currentSpeed *= 0.5;
        }
    }

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
        this.wheelAngle = this.turnAngle * Math.max(0.25F, 1.0F - Math.abs(currentSpeed / 30F));
        this.deltaYaw = this.wheelAngle * (currentSpeed / 30F) / 2F;
    }

    public void createParticles()
    {
        if(this.shouldShowEngineSmoke()&& this.hasFuel() && this.ticksExisted % 2 == 0)
        {
            Vec3d smokePosition = this.getEngineSmokePosition().rotateYaw(-this.rotationYaw * 0.017453292F);
            this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX + smokePosition.x, this.posY + smokePosition.y, this.posZ + smokePosition.z, -this.motionX, 0.0D, -this.motionZ);
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
    protected void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
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
        if(compound.hasKey("maxTurnAngle", Constants.NBT.TAG_INT))
        {
            this.setMaxTurnAngle(compound.getInteger("maxTurnAngle"));
        }
        if(compound.hasKey("stepHeight", Constants.NBT.TAG_FLOAT))
        {
            this.stepHeight = compound.getFloat("stepHeight");
        }
        if(compound.hasKey("currentFuel", Constants.NBT.TAG_INT))
        {
            this.setCurrentFuel(compound.getInteger("currentFuel"));
        }
        if(compound.hasKey("fuelCapacity", Constants.NBT.TAG_INT))
        {
            this.setFuelCapacity(compound.getInteger("fuelCapacity"));
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        compound.setInteger("engineType", this.getEngineType().ordinal());
        compound.setFloat("maxSpeed", this.getMaxSpeed());
        compound.setFloat("accelerationSpeed", this.getAccelerationSpeed());
        compound.setInteger("turnSensitivity", this.getTurnSensitivity());
        compound.setInteger("maxTurnAngle", this.getMaxTurnAngle());
        compound.setFloat("stepHeight", this.stepHeight);
        compound.setFloat("currentFuel", this.getCurrentFuel());
        compound.setFloat("fuelCapacity", this.getFuelCapacity());
    }

    @Nullable
    public Entity getControllingPassenger()
    {
        return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
    }

    @Override
    public void updatePassenger(Entity passenger)
    {
        super.updatePassenger(passenger);
        //TODO change to config option
        passenger.rotationYaw -= deltaYaw;
        passenger.setRotationYawHead(passenger.rotationYaw);
        super.applyYawToEntity(passenger);
    }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn) {}

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

    public float getActualMaxSpeed()
    {
        return this.dataManager.get(MAX_SPEED) + this.getEngineType().getAdditionalMaxSpeed();
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

    public float getActualSpeed()
    {
        return (this.currentSpeed + this.currentSpeed * this.speedMultiplier) / this.getActualMaxSpeed();
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
        return Math.sqrt(Math.pow(this.posX - this.prevPosX, 2) + Math.pow(this.posY - this.prevPosY, 2) + Math.pow(this.posZ - this.prevPosZ, 2)) * 20;
    }

    public void setTurnDirection(TurnDirection turnDirection)
    {
        this.dataManager.set(TURN_DIRECTION, turnDirection.ordinal());
    }

    public TurnDirection getTurnDirection()
    {
        return TurnDirection.values()[this.dataManager.get(TURN_DIRECTION)];
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

    public void setMaxTurnAngle(int turnAngle)
    {
        this.dataManager.set(MAX_TURN_ANGLE, turnAngle);
    }

    public int getMaxTurnAngle()
    {
        return this.dataManager.get(MAX_TURN_ANGLE);
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
        return false;
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldRenderFuelPort()
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

    public void setBoosting(boolean boosting)
    {
        this.boosting = boosting;
        this.boostTimer = 10;
    }

    public boolean isBoosting()
    {
        return boosting;
    }

    public void setLaunching(int hold)
    {
        this.launching = true;
        this.launchingTimer = hold;
        this.disableFallDamage = true;
    }

    public boolean isLaunching()
    {
        return launching;
    }

    public boolean hasFuel()
    {
        Entity entity = this.getControllingPassenger();
        if(entity instanceof EntityPlayer)
        {
            if(((EntityPlayer) entity).isCreative())
            {
                return true;
            }
        }
        return this.getCurrentFuel() > 0F;
    }

    public void setCurrentFuel(float fuel)
    {
        this.dataManager.set(CURRENT_FUEL, fuel);
    }

    public float getCurrentFuel()
    {
        return this.dataManager.get(CURRENT_FUEL);
    }

    public void setFuelCapacity(float capacity)
    {
        this.dataManager.set(FUEL_CAPACITY, capacity);
    }

    public float getFuelCapacity()
    {
        return this.dataManager.get(FUEL_CAPACITY);
    }

    public void setFuelConsumption(float consumption)
    {
        this.fuelConsumption = consumption;
    }

    public float getFuelConsumption()
    {
        return fuelConsumption;
    }

    public int addFuel(int fuel)
    {
        float currentFuel = this.getCurrentFuel();
        currentFuel += fuel;
        int remaining = Math.max(0, Math.round(currentFuel - this.getFuelCapacity()));
        currentFuel = Math.min(currentFuel, this.getFuelCapacity());
        this.setCurrentFuel(currentFuel);
        return remaining;
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
            if(COLOR.equals(key))
            {
                if(!fuelPortBody.hasTagCompound())
                {
                    fuelPortClosed.setTagCompound(new NBTTagCompound());
                    fuelPortBody.setTagCompound(new NBTTagCompound());
                    fuelPortLid.setTagCompound(new NBTTagCompound());
                }
                Color color = new Color(this.dataManager.get(COLOR));
                int colorInt = (Math.sqrt(color.getRed() * color.getRed() * 0.241
                        + color.getGreen() * color.getGreen() * 0.691
                        + color.getBlue() * color.getBlue() * 0.068) > 127 ? color.darker() : color.brighter()).getRGB();
                fuelPortClosed.getTagCompound().setInteger("color", colorInt);
                fuelPortBody.getTagCompound().setInteger("color", colorInt);
                fuelPortLid.getTagCompound().setInteger("color", colorInt);
            }
        }
    }

    @Override
    public void addPassenger(Entity passenger)
    {
        super.addPassenger(passenger);
        if(passenger instanceof EntityPlayer && world.isRemote)
        {
            VehicleMod.proxy.playVehicleSound((EntityPlayer) passenger, this);
        }
    }

    @Override
    public void fall(float distance, float damageMultiplier)
    {
        if(!disableFallDamage)
        {
            super.fall(distance, damageMultiplier);
        }
        if(launchingTimer <= 0 && distance > 3)
        {
            disableFallDamage = false;
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

    public enum FuelPort
    {
        LID(ModItems.FUEL_PORT_CLOSED, ModItems.FUEL_PORT_BODY, ModItems.FUEL_PORT_LID, ModSounds.FUEL_PORT_OPEN, 0.25F, 0.6F, ModSounds.FUEL_PORT_CLOSE, 0.12F, 0.6F),
        CAP(ModItems.FUEL_PORT_2_CLOSED, ModItems.FUEL_PORT_2_PIPE, null, ModSounds.FUEL_PORT_2_OPEN, 0.4F, 0.6F, ModSounds.FUEL_PORT_2_CLOSE, 0.3F, 0.6F);

        private Item closed, body, lid;
        private SoundEvent soundOpen, soundClose;
        private float volumeOpen, volumeClose;
        private float pitchOpen, pitchClose;

        private FuelPort(Item closed, Item body, Item lid, SoundEvent soundOpen, float volumeOpen, float pitchOpen, SoundEvent soundClose, float volumeClose, float pitchClose)
        {
            this.closed = closed;
            this.body = body;
            this.lid = lid;
            this.soundOpen = soundOpen;
            this.volumeOpen = volumeOpen;
            this.pitchOpen = pitchOpen;
            this.soundClose = soundClose;
            this.volumeClose = volumeClose;
            this.pitchClose = pitchClose;
        }

        public Item getClosed()
        {
            return closed;
        }

        public Item getBody()
        {
            return body;
        }

        public Item getLid()
        {
            return lid;
        }

        public void playOpenSound()
        {
            Minecraft.getMinecraft().player.playSound(soundOpen, volumeOpen, pitchOpen);
        }

        public void playCloseSound()
        {
            Minecraft.getMinecraft().player.playSound(soundClose, volumeClose, pitchClose);
        }

    }
}
