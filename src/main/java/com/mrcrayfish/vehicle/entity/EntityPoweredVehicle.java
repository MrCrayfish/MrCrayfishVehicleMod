package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.VehicleConfig;
import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.block.BlockVehicleCrate;
import com.mrcrayfish.vehicle.client.render.Wheel;
import com.mrcrayfish.vehicle.common.CommonEvents;
import com.mrcrayfish.vehicle.common.container.ContainerVehicle;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.vehicle.EntityBumperCar;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.item.ItemEngine;
import com.mrcrayfish.vehicle.item.ItemJerryCan;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.*;
import com.mrcrayfish.vehicle.proxy.ClientProxy;
import com.mrcrayfish.vehicle.tileentity.TileEntityGasPump;
import com.mrcrayfish.vehicle.tileentity.TileEntityGasPumpTank;
import com.mrcrayfish.vehicle.util.CommonUtils;
import com.mrcrayfish.vehicle.util.InventoryUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.UUID;

/**
 * Author: MrCrayfish
 */
public abstract class EntityPoweredVehicle extends EntityVehicle implements IInventoryChangedListener
{
    protected static final DataParameter<Float> CURRENT_SPEED = EntityDataManager.createKey(EntityPoweredVehicle.class, DataSerializers.FLOAT);
    protected static final DataParameter<Float> MAX_SPEED = EntityDataManager.createKey(EntityPoweredVehicle.class, DataSerializers.FLOAT);
    protected static final DataParameter<Float> ACCELERATION_SPEED = EntityDataManager.createKey(EntityPoweredVehicle.class, DataSerializers.FLOAT);
    protected static final DataParameter<Float> POWER = EntityDataManager.createKey(EntityPoweredVehicle.class, DataSerializers.FLOAT);
    protected static final DataParameter<Integer> TURN_DIRECTION = EntityDataManager.createKey(EntityPoweredVehicle.class, DataSerializers.VARINT);
    protected static final DataParameter<Float> TARGET_TURN_ANGLE = EntityDataManager.createKey(EntityPoweredVehicle.class, DataSerializers.FLOAT);
    protected static final DataParameter<Integer> TURN_SENSITIVITY = EntityDataManager.createKey(EntityPoweredVehicle.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> MAX_TURN_ANGLE = EntityDataManager.createKey(EntityPoweredVehicle.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> ACCELERATION_DIRECTION = EntityDataManager.createKey(EntityPoweredVehicle.class, DataSerializers.VARINT);
    protected static final DataParameter<Boolean> HAS_ENGINE = EntityDataManager.createKey(EntityPoweredVehicle.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Integer> ENGINE_TIER = EntityDataManager.createKey(EntityPoweredVehicle.class, DataSerializers.VARINT);
    protected static final DataParameter<Boolean> HORN = EntityDataManager.createKey(EntityPoweredVehicle.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Boolean> REQUIRES_FUEL = EntityDataManager.createKey(EntityPoweredVehicle.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Float> CURRENT_FUEL = EntityDataManager.createKey(EntityPoweredVehicle.class, DataSerializers.FLOAT);
    protected static final DataParameter<Float> FUEL_CAPACITY = EntityDataManager.createKey(EntityPoweredVehicle.class, DataSerializers.FLOAT);
    protected static final DataParameter<Boolean> NEEDS_KEY = EntityDataManager.createKey(EntityPoweredVehicle.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<ItemStack> KEY_STACK = EntityDataManager.createKey(EntityPoweredVehicle.class, DataSerializers.ITEM_STACK);
    protected static final DataParameter<Boolean> HAS_WHEELS = EntityDataManager.createKey(EntityPoweredVehicle.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Integer> WHEEL_TYPE = EntityDataManager.createKey(EntityPoweredVehicle.class, DataSerializers.VARINT);
    public  static final DataParameter<Integer> WHEEL_COLOR = EntityDataManager.createKey(EntityPoweredVehicle.class, DataSerializers.VARINT);

    public float prevCurrentSpeed;
    public float currentSpeed;
    public float speedMultiplier;
    public boolean boosting;
    public int boostTimer;
    public boolean launching;
    public int launchingTimer;
    public boolean disableFallDamage;
    public float fuelConsumption = 1F;

    protected double[] wheelPositions;
    protected boolean wheelsOnGround = true;
    public float turnAngle;
    public float prevTurnAngle;

    public float deltaYaw;
    public float wheelAngle;
    public float prevWheelAngle; //TODO can remove use render wheel angle instead

    @SideOnly(Side.CLIENT)
    public float targetWheelAngle;
    @SideOnly(Side.CLIENT)
    public float renderWheelAngle;
    @SideOnly(Side.CLIENT)
    public float prevRenderWheelAngle;

    public float vehicleMotionX;
    public float vehicleMotionY;
    public float vehicleMotionZ;

    private UUID owner;

    private InventoryBasic vehicleInventory;

    @SideOnly(Side.CLIENT)
    public ItemStack engine;

    @SideOnly(Side.CLIENT)
    public ItemStack keyPort;

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

    @Override
    public void entityInit()
    {
        super.entityInit();
        this.dataManager.register(CURRENT_SPEED, 0F);
        this.dataManager.register(MAX_SPEED, 10F);
        this.dataManager.register(ACCELERATION_SPEED, 0.5F);
        this.dataManager.register(POWER, 1.0F);
        this.dataManager.register(TURN_DIRECTION, TurnDirection.FORWARD.ordinal());
        this.dataManager.register(TARGET_TURN_ANGLE, 0F);
        this.dataManager.register(TURN_SENSITIVITY, 6);
        this.dataManager.register(MAX_TURN_ANGLE, 45);
        this.dataManager.register(ACCELERATION_DIRECTION, AccelerationDirection.NONE.ordinal());
        this.dataManager.register(HAS_ENGINE, false);
        this.dataManager.register(ENGINE_TIER, 0);
        this.dataManager.register(HORN, false);
        this.dataManager.register(REQUIRES_FUEL, VehicleConfig.SERVER.fuelEnabled);
        this.dataManager.register(CURRENT_FUEL, 0F);
        this.dataManager.register(FUEL_CAPACITY, 15000F);
        this.dataManager.register(NEEDS_KEY, false);
        this.dataManager.register(KEY_STACK, ItemStack.EMPTY);
        this.dataManager.register(HAS_WHEELS, true);
        this.dataManager.register(WHEEL_TYPE, WheelType.STANDARD.ordinal());
        this.dataManager.register(WHEEL_COLOR, -1);

        List<Wheel> wheels = this.getProperties().getWheels();
        if(wheels != null && wheels.size() > 0)
        {
            this.wheelPositions = new double[wheels.size() * 3];
        }
    }

    public abstract SoundEvent getMovingSound();

    public abstract SoundEvent getRidingSound();

    //TODO ability to change with nbt
    public SoundEvent getHornSound()
    {
        return ModSounds.hornMono;
    }

    public SoundEvent getHornRidingSound()
    {
        return ModSounds.hornStereo;
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
    public void onClientInit()
    {
        super.onClientInit();
        engine = new ItemStack(ModItems.SMALL_ENGINE);
        keyPort = new ItemStack(ModItems.KEY_PORT);
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
        if(player.getDataManager().get(CommonEvents.GAS_PUMP).isPresent())
        {
            BlockPos pos = player.getDataManager().get(CommonEvents.GAS_PUMP).get();
            TileEntity tileEntity = world.getTileEntity(pos);
            if(tileEntity instanceof TileEntityGasPump)
            {
                tileEntity = world.getTileEntity(pos.down());
                if(tileEntity instanceof TileEntityGasPumpTank)
                {
                    TileEntityGasPumpTank gasPumpTank = (TileEntityGasPumpTank) tileEntity;
                    FluidStack stack = gasPumpTank.getFluidTank().drain(200, true);
                    if(stack != null)
                    {
                        stack.amount = this.addFuel(stack.amount);
                        if(stack.amount > 0)
                        {
                            gasPumpTank.getFluidTank().fill(stack, true);
                        }
                    }
                }
            }
            return;
        }

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
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
    {
        ItemStack stack = player.getHeldItem(hand);
        if(!world.isRemote)
        {
            /* If no owner is set, make the owner the person adding the key. It is used because
             * owner will not be set if the vehicle was summoned through a command */
            if(this.owner == null)
            {
                this.owner = player.getUniqueID();
            }

            if(stack.getItem() == ModItems.KEY)
            {
                if(!this.owner.equals(player.getUniqueID()))
                {
                    CommonUtils.sendInfoMessage(player, "vehicle.status.invalid_owner");
                    return false;
                }

                if(this.isLockable())
                {
                    NBTTagCompound tag = CommonUtils.getItemTagCompound(stack);
                    if(!tag.hasUniqueId("vehicleId") || this.getUniqueID().equals(tag.getUniqueId("vehicleId")))
                    {
                        tag.setUniqueId("vehicleId", this.getUniqueID());
                        if(!this.isKeyNeeded())
                        {
                            this.setKeyNeeded(true);
                            CommonUtils.sendInfoMessage(player, "vehicle.status.key_added");
                        }
                        else
                        {
                            CommonUtils.sendInfoMessage(player, "vehicle.status.key_created");
                        }
                        return true;
                    }
                }
                else
                {
                    CommonUtils.sendInfoMessage(player, "vehicle.status.not_lockable");
                    return false;
                }
            }
            else if(stack.getItem() == ModItems.WRENCH && this.getRidingEntity() instanceof EntityJack)
            {
                if(player.getUniqueID().equals(owner))
                {
                    this.openInventory(player);
                }
                else
                {
                    CommonUtils.sendInfoMessage(player, "vehicle.status.invalid_owner");
                }
                return true;
            }
        }
        return super.processInitialInteract(player, hand);
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
        this.updateGroundState();
        this.updateSpeed();
        this.updateTurning();
        this.updateVehicle();
        this.setSpeed(currentSpeed);

        /* Updates the direction of the vehicle */
        rotationYaw -= deltaYaw;

        /* Updates the vehicle motion and applies it on top of the normal motion */
        this.updateVehicleMotion();

        this.setRotation(this.rotationYaw, this.rotationPitch);
        double deltaRot = (double) (this.prevRotationYaw - this.rotationYaw);
        if (deltaRot < -180.0D)
        {
            this.prevRotationYaw += 360.0F;
        }
        else if (deltaRot >= 180.0D)
        {
            this.prevRotationYaw -= 360.0F;
        }
        this.updateWheelPositions();

        this.move(MoverType.SELF, motionX + vehicleMotionX, motionY + vehicleMotionY, motionZ + vehicleMotionZ);

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

        if(this.requiresFuel() && controllingPassenger != null && controllingPassenger instanceof EntityPlayer && !((EntityPlayer) controllingPassenger).isCreative())
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

    public abstract EngineType getEngineType();

    protected void updateSpeed()
    {
        float wheelModifier = this.getWheelModifier();
        this.currentSpeed = this.getSpeed();

        EngineTier engineTier = this.getEngineTier();
        AccelerationDirection acceleration = this.getAcceleration();
        if(this.getControllingPassenger() != null)
        {
            if(this.canDrive())
            {
                if(acceleration == AccelerationDirection.FORWARD)
                {
                    if(this.wheelsOnGround || this.canAccelerateInAir())
                    {
                        float maxSpeed = this.getActualMaxSpeed() * wheelModifier * this.getPower();
                        if(this.currentSpeed < maxSpeed)
                        {
                            this.currentSpeed += this.getModifiedAccelerationSpeed() * engineTier.getAccelerationMultiplier();
                            if(this.currentSpeed > maxSpeed)
                            {
                                this.currentSpeed = maxSpeed;
                            }
                        }
                        if(this.currentSpeed > maxSpeed)
                        {
                            this.currentSpeed *= 0.975F;
                        }
                        return;
                    }
                }
                else if(acceleration == AccelerationDirection.REVERSE)
                {
                    if(this.wheelsOnGround || this.canAccelerateInAir())
                    {
                        float maxSpeed = -(4.0F + engineTier.getAdditionalMaxSpeed() / 2) * wheelModifier * this.getPower();;
                        if(this.currentSpeed > maxSpeed)
                        {
                            this.currentSpeed -= this.getModifiedAccelerationSpeed() * engineTier.getAccelerationMultiplier();
                            if(this.currentSpeed < maxSpeed)
                            {
                                this.currentSpeed = maxSpeed;
                            }
                        }
                        if(this.currentSpeed < maxSpeed)
                        {
                            this.currentSpeed *= 0.975F;
                        }
                        return;
                    }
                }
            }

            if(this.wheelsOnGround || this.canAccelerateInAir())
            {
                this.currentSpeed *= 0.9;
            }
            else
            {
                this.currentSpeed *= 0.98;
            }
        }
        else if(this.wheelsOnGround)
        {
            this.currentSpeed *= 0.85;
        }
        else
        {
            this.currentSpeed *= 0.98;
        }
    }

    protected void updateTurning()
    {
        this.turnAngle = this.getTargetTurnAngle();
        this.wheelAngle = this.turnAngle * Math.max(0.25F, 1.0F - Math.abs(currentSpeed / 30F));
        this.deltaYaw = this.wheelAngle * (currentSpeed / 30F) / 2F;

        if(world.isRemote)
        {
            this.renderWheelAngle = this.wheelAngle;
        }
    }

    public void createParticles()
    {
        if(this.getAcceleration() == AccelerationDirection.FORWARD)
        {
            /* Uses the same logic when rendering wheels to determine the position, then spawns
             * particles at the contact of the wheel and the ground. */
            VehicleProperties properties = this.getProperties();
            if(properties.getWheels() != null)
            {
                List<Wheel> wheels = properties.getWheels();
                for(int i = 0; i < wheels.size(); i++)
                {
                    Wheel wheel = wheels.get(i);
                    if(!wheel.shouldSpawnParticles())
                        continue;
                    /* Gets the block under the wheel and spawns a particle */
                    double wheelX = this.wheelPositions[i * 3];
                    double wheelY = this.wheelPositions[i * 3 + 1];
                    double wheelZ = this.wheelPositions[i * 3 + 2];
                    int x = MathHelper.floor(this.posX + wheelX);
                    int y = MathHelper.floor(this.posY + wheelY - 0.2D);
                    int z = MathHelper.floor(this.posZ + wheelZ);
                    BlockPos pos = new BlockPos(x, y, z);
                    IBlockState state = this.world.getBlockState(pos);
                    if(state.getMaterial() != Material.AIR && state.getMaterial().isToolNotRequired())
                    {
                        Vec3d dirVec = this.getVectorForRotation(this.rotationPitch, this.getModifiedRotationYaw() + 180F);
                        this.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, this.posX + wheelX, this.posY + wheelY, this.posZ + wheelZ, dirVec.x, dirVec.y, dirVec.z, Block.getStateId(state));
                    }
                }
            }
        }

        if(this.shouldShowEngineSmoke()&& this.canDrive() && this.ticksExisted % 2 == 0)
        {
            Vec3d smokePosition = this.getEngineSmokePosition().rotateYaw(-this.getModifiedRotationYaw() * 0.017453292F);
            this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX + smokePosition.x, this.posY + smokePosition.y, this.posZ + smokePosition.z, -this.motionX, 0.0D, -this.motionZ);
        }
    }

    @SideOnly(Side.CLIENT)
    public void onClientUpdate()
    {
        prevRenderWheelAngle = renderWheelAngle;

        EntityLivingBase entity = (EntityLivingBase) this.getControllingPassenger();
        if(entity != null && entity.equals(Minecraft.getMinecraft().player))
        {
            float power = VehicleMod.proxy.getPower(this);
            if(power != this.getPower())
            {
                this.setPower(power);
                PacketHandler.INSTANCE.sendToServer(new MessagePower(power));
            }

            AccelerationDirection acceleration = VehicleMod.proxy.getAccelerationDirection(entity);
            if(this.getAcceleration() != acceleration)
            {
                this.setAcceleration(acceleration);
                PacketHandler.INSTANCE.sendToServer(new MessageAccelerating(acceleration));
            }

            boolean horn = VehicleMod.proxy.isHonking();
            this.setHorn(horn);
            PacketHandler.INSTANCE.sendToServer(new MessageHorn(horn));

            TurnDirection direction = VehicleMod.proxy.getTurnDirection(entity);
            if(this.getTurnDirection() != direction)
            {
                this.setTurnDirection(direction);
                PacketHandler.INSTANCE.sendToServer(new MessageTurnDirection(direction));
            }

            float targetTurnAngle = VehicleMod.proxy.getTargetTurnAngle(this, false);
            this.setTargetTurnAngle(targetTurnAngle);
            PacketHandler.INSTANCE.sendToServer(new MessageTurnAngle(targetTurnAngle));
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        if(compound.hasKey("owner", Constants.NBT.TAG_COMPOUND))
        {
            this.owner = NBTUtil.getUUIDFromTag(compound.getCompoundTag("owner"));
        }
        if(compound.hasKey("hasEngine", Constants.NBT.TAG_BYTE))
        {
            this.setEngine(compound.getBoolean("hasEngine"));
        }
        if(compound.hasKey("engineType", Constants.NBT.TAG_INT)) //TODO: Remove after release
        {
            this.setEngine(true);
            this.setEngineTier(EngineTier.getType(compound.getInteger("engineTier")));
        }
        if(compound.hasKey("engineTier", Constants.NBT.TAG_INT))
        {
            this.setEngineTier(EngineTier.getType(compound.getInteger("engineTier")));
        }
        if(compound.hasKey("hasWheels", Constants.NBT.TAG_BYTE))
        {
            this.setWheels(compound.getBoolean("hasWheels"));
        }
        if(compound.hasKey("wheelType", Constants.NBT.TAG_INT))
        {
            this.setWheelType(WheelType.getType(compound.getInteger("wheelType")));
        }
        if(compound.hasKey("wheelColor", Constants.NBT.TAG_INT))
        {
            this.setWheelColor(compound.getInteger("wheelColor"));
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
        if(compound.hasKey("requiresFuel", Constants.NBT.TAG_BYTE))
        {
            this.setRequiresFuel(compound.getBoolean("requiresFuel"));
        }
        if(compound.hasKey("currentFuel", Constants.NBT.TAG_FLOAT))
        {
            this.setCurrentFuel(compound.getFloat("currentFuel"));
        }
        if(compound.hasKey("fuelCapacity", Constants.NBT.TAG_INT))
        {
            this.setFuelCapacity(compound.getInteger("fuelCapacity"));
        }
        if(compound.hasKey("keyNeeded", Constants.NBT.TAG_BYTE))
        {
            this.setKeyNeeded(compound.getBoolean("keyNeeded"));
        }
        this.setKeyStack(CommonUtils.readItemStackFromTag(compound, "keyStack"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        if(owner != null)
        {
            compound.setTag("owner", NBTUtil.createUUIDTag(owner));
        }
        compound.setBoolean("hasEngine", this.hasEngine());
        compound.setInteger("engineTier", this.getEngineTier().ordinal());
        compound.setBoolean("hasWheels", this.hasWheels());
        compound.setInteger("wheelType", this.getWheelType().ordinal());
        compound.setInteger("wheelColor", this.getWheelColor());
        compound.setFloat("maxSpeed", this.getMaxSpeed());
        compound.setFloat("accelerationSpeed", this.getAccelerationSpeed());
        compound.setInteger("turnSensitivity", this.getTurnSensitivity());
        compound.setInteger("maxTurnAngle", this.getMaxTurnAngle());
        compound.setFloat("stepHeight", this.stepHeight);
        compound.setBoolean("requiresFuel", this.requiresFuel());
        compound.setFloat("currentFuel", this.getCurrentFuel());
        compound.setFloat("fuelCapacity", this.getFuelCapacity());
        compound.setBoolean("keyNeeded", this.isKeyNeeded());
        CommonUtils.writeItemStackToTag(compound, "keyStack", this.getKeyStack());
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
        return this.dataManager.get(MAX_SPEED) + this.getEngineTier().getAdditionalMaxSpeed();
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

    public void setAccelerationSpeed(float speed)
    {
        this.dataManager.set(ACCELERATION_SPEED, speed);
    }

    public float getAccelerationSpeed()
    {
        return this.dataManager.get(ACCELERATION_SPEED);
    }

    protected float getModifiedAccelerationSpeed()
    {
        return this.dataManager.get(ACCELERATION_SPEED);
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

    public void setTargetTurnAngle(float targetTurnAngle)
    {
        this.dataManager.set(TARGET_TURN_ANGLE, targetTurnAngle);
    }

    public float getTargetTurnAngle()
    {
        return this.dataManager.get(TARGET_TURN_ANGLE);
    }

    public void setAcceleration(AccelerationDirection direction)
    {
        this.dataManager.set(ACCELERATION_DIRECTION, direction.ordinal());
    }

    public AccelerationDirection getAcceleration()
    {
        return AccelerationDirection.values()[this.dataManager.get(ACCELERATION_DIRECTION)];
    }

    public void setPower(float power)
    {
        this.dataManager.set(POWER, power);
    }

    public float getPower()
    {
        return this.dataManager.get(POWER);
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

    public boolean hasEngine()
    {
        return this.dataManager.get(HAS_ENGINE);
    }

    public void setEngine(boolean hasEngine)
    {
        this.dataManager.set(HAS_ENGINE, hasEngine);
    }

    public void setEngineTier(EngineTier engineTier)
    {
        this.dataManager.set(ENGINE_TIER, engineTier.ordinal());
    }

    public EngineTier getEngineTier()
    {
        return EngineTier.getType(this.dataManager.get(ENGINE_TIER));
    }

    public ItemStack getEngineStack()
    {
        if(this.hasEngine())
        {
            switch(this.getEngineType())
            {
                case SMALL_MOTOR:
                    return new ItemStack(ModItems.SMALL_ENGINE, 1, this.getEngineTier().ordinal());
                case LARGE_MOTOR:
                    return new ItemStack(ModItems.LARGE_ENGINE, 1, this.getEngineTier().ordinal());
                case ELECTRIC_MOTOR:
                    return new ItemStack(ModItems.ELECTRIC_ENGINE, 1, this.getEngineTier().ordinal());
            }
        }
        return ItemStack.EMPTY;
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

    public boolean requiresFuel()
    {
        return VehicleConfig.SERVER.fuelEnabled && this.dataManager.get(REQUIRES_FUEL);
    }

    public void setRequiresFuel(boolean requiresFuel)
    {
        this.dataManager.set(REQUIRES_FUEL, VehicleConfig.SERVER.fuelEnabled && requiresFuel);
    }

    public boolean isFueled()
    {
        return !this.requiresFuel() || this.isControllingPassengerCreative() || this.getCurrentFuel() > 0F;
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
        if(!this.requiresFuel())
            return fuel;
        float currentFuel = this.getCurrentFuel();
        currentFuel += fuel;
        int remaining = Math.max(0, Math.round(currentFuel - this.getFuelCapacity()));
        currentFuel = Math.min(currentFuel, this.getFuelCapacity());
        this.setCurrentFuel(currentFuel);
        return remaining;
    }

    public void setKeyNeeded(boolean needsKey)
    {
        this.dataManager.set(NEEDS_KEY, needsKey);
    }

    public boolean isKeyNeeded()
    {
        return this.dataManager.get(NEEDS_KEY);
    }

    public void setKeyStack(ItemStack stack)
    {
        this.dataManager.set(KEY_STACK, stack);
    }

    public ItemStack getKeyStack()
    {
        return this.dataManager.get(KEY_STACK);
    }

    public void ejectKey()
    {
        if(!this.getKeyStack().isEmpty())
        {
            Vec3d keyHole = this.getPartPositionAbsoluteVec(this.getProperties().getKeyPortPosition(), 1F);
            world.spawnEntity(new EntityItem(world, keyHole.x, keyHole.y, keyHole.z, this.getKeyStack()));
            this.setKeyStack(ItemStack.EMPTY);
        }
    }

    public boolean isLockable()
    {
        return true;
    }

    public boolean isEnginePowered()
    {
        return (this.getEngineType() == EngineType.NONE || this.hasEngine() && (this.isControllingPassengerCreative() || this.isFueled()) && this.getDestroyedStage() < 9) && (!this.isKeyNeeded() || !this.getKeyStack().isEmpty());
    }

    public boolean canDrive()
    {
        return (!this.canChangeWheels() || this.hasWheels()) && this.isEnginePowered();
    }

    public boolean isOwner(EntityPlayer player)
    {
        return owner == null || player.getUniqueID().equals(owner);
    }

    public void setOwner(UUID owner)
    {
        this.owner = owner;
    }

    public boolean hasWheels()
    {
        return this.dataManager.get(HAS_WHEELS);
    }

    public void setWheels(boolean hasWheels)
    {
        this.dataManager.set(HAS_WHEELS, hasWheels);
    }

    public void setWheelType(WheelType wheelType)
    {
        this.dataManager.set(WHEEL_TYPE, wheelType.ordinal());
    }

    public WheelType getWheelType()
    {
        return WheelType.values()[this.dataManager.get(WHEEL_TYPE)];
    }

    public ItemStack getWheelStack()
    {
        if(this.hasWheels())
        {
            ItemStack stack = new ItemStack(ModItems.WHEEL, 1, this.getWheelType().ordinal());
            if(this.getWheelColor() != -1)
            {
                CommonUtils.getItemTagCompound(stack).setInteger("color", this.getWheelColor());
            }
            return stack;
        }
        return ItemStack.EMPTY;
    }

    public void setWheelColor(int color)
    {
        this.dataManager.set(WHEEL_COLOR, color);
    }

    public int getWheelColor()
    {
        return this.dataManager.get(WHEEL_COLOR);
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key)
    {
        super.notifyDataManagerChange(key);
        if(world.isRemote)
        {
            if(ENGINE_TIER.equals(key))
            {
                EngineTier tier = EngineTier.getType(this.dataManager.get(ENGINE_TIER));
                engine.setItemDamage(tier.ordinal());
            }
            if(WHEEL_TYPE.equals(key))
            {
                WheelType type = this.getWheelType();
                wheel.setItemDamage(type.ordinal());
            }
            if(COLOR.equals(key))
            {
                Color color = new Color(this.dataManager.get(COLOR));
                int colorInt = (Math.sqrt(color.getRed() * color.getRed() * 0.241
                        + color.getGreen() * color.getGreen() * 0.691
                        + color.getBlue() * color.getBlue() * 0.068) > 127 ? color.darker() : color.brighter()).getRGB();
                CommonUtils.getItemTagCompound(fuelPortClosed).setInteger("color", colorInt);
                CommonUtils.getItemTagCompound(fuelPortBody).setInteger("color", colorInt);
                CommonUtils.getItemTagCompound(fuelPortLid).setInteger("color", colorInt);
                CommonUtils.getItemTagCompound(keyPort).setInteger("color", colorInt);
            }
            if(WHEEL_COLOR.equals(key))
            {
                int color = this.dataManager.get(WHEEL_COLOR);
                CommonUtils.getItemTagCompound(wheel).setInteger("color", color != -1 ? color : 16383998);
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

    public boolean isControllingPassengerCreative()
    {
        Entity entity = this.getControllingPassenger();
        if(entity instanceof EntityPlayer)
        {
            return ((EntityPlayer) entity).isCreative();
        }
        return false;
    }

    private void openInventory(EntityPlayer player)
    {
        if(player instanceof EntityPlayerMP)
        {
            EntityPlayerMP entityPlayerMP = (EntityPlayerMP) player;
            entityPlayerMP.getNextWindowId();
            entityPlayerMP.openContainer = new ContainerVehicle(this.getVehicleInventory(), this, entityPlayerMP);
            entityPlayerMP.openContainer.windowId = entityPlayerMP.currentWindowId;
            entityPlayerMP.openContainer.addListener(entityPlayerMP);
            PacketHandler.INSTANCE.sendTo(new MessageVehicleWindow(player.openContainer.windowId, this.getEntityId()), entityPlayerMP);
        }
    }

    public InventoryBasic getVehicleInventory()
    {
        if(this.vehicleInventory == null)
        {
            this.initVehicleInventory();
        }
        return this.vehicleInventory;
    }

    protected void initVehicleInventory()
    {
        this.vehicleInventory = new InventoryBasic(this.getName(), false, 2);

        ItemStack engine = this.getEngineStack();
        if(this.getEngineType() != EngineType.NONE & !engine.isEmpty())
        {
            this.vehicleInventory.setInventorySlotContents(0, engine);
        }

        ItemStack wheel = this.getWheelStack();
        if(this.canChangeWheels() && !wheel.isEmpty())
        {
            this.vehicleInventory.setInventorySlotContents(1, wheel);
        }

        this.vehicleInventory.addInventoryChangeListener(this);
    }

    private void updateSlots()
    {
        if (!this.world.isRemote)
        {
            ItemStack engine = this.vehicleInventory.getStackInSlot(0);
            if(engine.getItem() instanceof ItemEngine)
            {
                ItemEngine item = (ItemEngine) engine.getItem();
                if(item.getEngineType() == this.getEngineType())
                {
                    this.setEngine(true);
                    this.setEngineTier(EngineTier.getType(engine.getMetadata()));
                }
                else
                {
                    this.setEngine(false);
                }
            }
            else if(this.getEngineType() != EngineType.NONE)
            {
                this.setEngine(false);
            }

            ItemStack wheel = this.vehicleInventory.getStackInSlot(1);
            if(this.canChangeWheels())
            {
                if(wheel.getItem() == ModItems.WHEEL)
                {
                    if(!this.hasWheels())
                    {
                        world.playSound(null, getPosition(), ModSounds.airWrenchGun, SoundCategory.BLOCKS, 1.0F, 1.1F);
                        this.setWheels(true);
                        this.setWheelType(WheelType.values()[wheel.getMetadata()]);

                        NBTTagCompound tagCompound = CommonUtils.getItemTagCompound(wheel);
                        if(tagCompound.hasKey("color", Constants.NBT.TAG_INT))
                        {
                            this.setWheelColor(tagCompound.getInteger("color"));
                        }
                        else
                        {
                            this.setWheelColor(-1);
                        }
                    }
                }
                else
                {
                    world.playSound(null, posX, posY, posZ, ModSounds.airWrenchGun, SoundCategory.BLOCKS, 1.0F, 0.8F);
                    this.setWheels(false);
                    this.setWheelColor(-1);
                }
            }
        }
    }

    @Override
    public void onInventoryChanged(IInventory inventory)
    {
        this.updateSlots();
    }

    @Override
    protected void onVehicleDestroyed(EntityLivingBase entity)
    {
        super.onVehicleDestroyed(entity);
        boolean isCreativeMode = entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isCreativeMode;
        if(!isCreativeMode && this.world.getGameRules().getBoolean("doEntityDrops"))
        {
            // Spawns the engine if the vehicle has one
            ItemStack engine = this.getEngineStack();
            if(this.getEngineType() != EngineType.NONE && !engine.isEmpty())
            {
                InventoryUtil.spawnItemStack(world, posX, posY, posZ, engine);
            }

            // Spawns the key and removes the associated vehicle uuid
            ItemStack key = this.getKeyStack().copy();
            if(!key.isEmpty())
            {
                CommonUtils.getItemTagCompound(key).removeTag("vehicleId");
                InventoryUtil.spawnItemStack(world, posX, posY, posZ, key);
            }

            // Spawns wheels if the vehicle has any
            ItemStack wheel = this.getWheelStack();
            if(this.canChangeWheels() && !wheel.isEmpty())
            {
                InventoryUtil.spawnItemStack(world, posX, posY, posZ, wheel);
            }
        }
    }

    public boolean canChangeWheels()
    {
        return true;
    }

    private void updateWheelPositions()
    {
        VehicleProperties properties = this.getProperties();
        if(properties.getWheels() != null)
        {
            List<Wheel> wheels = properties.getWheels();
            for(int i = 0; i < wheels.size(); i++)
            {
                Wheel wheel = wheels.get(i);

                PartPosition bodyPosition = properties.getBodyPosition();
                double wheelX = bodyPosition.getX();
                double wheelY = bodyPosition.getY();
                double wheelZ = bodyPosition.getZ();

                double scale = bodyPosition.getScale();

                /* Applies axel and wheel offets */
                wheelY += (properties.getWheelOffset() * 0.0625F) * scale;

                /* Wheels Translations */
                wheelX += ((wheel.getOffsetX() * 0.0625) * wheel.getSide().getOffset()) * scale;
                wheelY += (wheel.getOffsetY() * 0.0625) * scale;
                wheelZ += (wheel.getOffsetZ() * 0.0625) * scale;
                wheelX += ((((wheel.getWidth() * wheel.getScaleX()) / 2) * 0.0625) * wheel.getSide().getOffset()) * scale;

                /* Offsets the position to the wheel contact on the ground */
                wheelY -= ((5 * 0.0625) / 2.0) * wheel.getScaleY();

                /* Update the wheel position */
                Vec3d wheelVec = new Vec3d(wheelX, wheelY, wheelZ).rotateYaw(-this.getModifiedRotationYaw() * 0.017453292F);
                wheelPositions[i * 3] = wheelVec.x;
                wheelPositions[i * 3 + 1] = wheelVec.y;
                wheelPositions[i * 3 + 2] = wheelVec.z;
            }
        }
    }

    public float getWheelModifier()
    {
        float wheelModifier = 0F;
        VehicleProperties properties = this.getProperties();
        List<Wheel> wheels = properties.getWheels();
        if(this.hasWheels() && wheels != null)
        {
            int wheelCount = 0;
            WheelType type = this.getWheelType();
            for(int i = 0; i < wheels.size(); i++)
            {
                double wheelX = this.wheelPositions[i * 3];
                double wheelY = this.wheelPositions[i * 3 + 1];
                double wheelZ = this.wheelPositions[i * 3 + 2];
                int x = MathHelper.floor(this.posX + wheelX);
                int y = MathHelper.floor(this.posY + wheelY - 0.2D);
                int z = MathHelper.floor(this.posZ + wheelZ);
                BlockPos pos = new BlockPos(x, y, z);
                IBlockState state = this.world.getBlockState(pos);
                if(state.getMaterial() != Material.AIR)
                {
                    if(state.getMaterial() == Material.SNOW || state.getMaterial() == Material.CRAFTED_SNOW || (state.getBlock() == Blocks.GRASS && state.getValue(BlockGrass.SNOWY)))
                    {
                        wheelModifier += (1.0F - type.snowMultiplier);
                    }
                    else if(!state.getMaterial().isToolNotRequired())
                    {
                        wheelModifier += (1.0F - type.roadMultiplier);
                    }
                    else
                    {
                        wheelModifier += (1.0F - type.dirtMultiplier);
                    }
                    wheelCount++;
                }
            }
            if(wheelCount > 0)
            {
                wheelModifier /= (float) wheelCount;
            }
        }
        return 1.0F - wheelModifier;
    }

    protected void updateGroundState()
    {
        if(this.hasWheels())
        {
            VehicleProperties properties = this.getProperties();
            List<Wheel> wheels = properties.getWheels();
            if(this.hasWheels() && wheels != null)
            {
                for(int i = 0; i < wheels.size(); i++)
                {
                    double wheelX = this.wheelPositions[i * 3];
                    double wheelY = this.wheelPositions[i * 3 + 1];
                    double wheelZ = this.wheelPositions[i * 3 + 2];
                    int x = MathHelper.floor(this.posX + wheelX);
                    int y = MathHelper.floor(this.posY + wheelY - 0.2D);
                    int z = MathHelper.floor(this.posZ + wheelZ);
                    BlockPos pos = new BlockPos(x, y, z);
                    IBlockState state = this.world.getBlockState(pos);
                    if(state.getCollisionBoundingBox(world, pos) != Block.NULL_AABB)
                    {
                        wheelsOnGround = true;
                        return;
                    }
                }
            }
            wheelsOnGround = false;
        }
    }

    protected boolean canAccelerateInAir()
    {
        return false;
    }

    @Override
    public ItemStack getPickedResult(RayTraceResult target)
    {
        EngineTier engineTier = null;
        if(this.hasEngine())
        {
            engineTier = this.getEngineTier();
        }

        int wheelColor = -1;
        WheelType wheelType = null;
        if(this.hasWheels())
        {
            wheelType = this.getWheelType();
            wheelColor = this.getWheelColor();
        }

        ResourceLocation entityId = EntityList.getKey(this);
        if(entityId != null)
        {
            return BlockVehicleCrate.create(entityId, this.getColor(), engineTier, wheelType, wheelColor);
        }
        return ItemStack.EMPTY;
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
        LID(ModItems.FUEL_PORT_CLOSED, ModItems.FUEL_PORT_BODY, ModItems.FUEL_PORT_LID, ModSounds.fuelPortOpen, 0.25F, 0.6F, ModSounds.fuelPortClose, 0.12F, 0.6F),
        CAP(ModItems.FUEL_PORT_2_CLOSED, ModItems.FUEL_PORT_2_PIPE, null, ModSounds.fuelPort2Open, 0.4F, 0.6F, ModSounds.fuelPort2Close, 0.3F, 0.6F);

        private Item closed, body, lid;
        private SoundEvent soundOpen, soundClose;
        private float volumeOpen, volumeClose;
        private float pitchOpen, pitchClose;

        FuelPort(Item closed, Item body, Item lid, SoundEvent soundOpen, float volumeOpen, float pitchOpen, SoundEvent soundClose, float volumeClose, float pitchClose)
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
