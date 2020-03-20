package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.block.BlockVehicleCrate;
import com.mrcrayfish.vehicle.client.ISpecialModel;
import com.mrcrayfish.vehicle.client.SpecialModels;
import com.mrcrayfish.vehicle.client.render.Wheel;
import com.mrcrayfish.vehicle.common.CustomDataParameters;
import com.mrcrayfish.vehicle.common.ItemLookup;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.vehicle.BumperCarEntity;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.inventory.container.EditVehicleContainer;
import com.mrcrayfish.vehicle.item.EngineItem;
import com.mrcrayfish.vehicle.item.JerryCanItem;
import com.mrcrayfish.vehicle.item.WheelItem;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.*;
import com.mrcrayfish.vehicle.tileentity.GasPumpTankTileEntity;
import com.mrcrayfish.vehicle.tileentity.GasPumpTileEntity;
import com.mrcrayfish.vehicle.util.CommonUtils;
import com.mrcrayfish.vehicle.util.InventoryUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.GrassBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Author: MrCrayfish
 */
public abstract class PoweredVehicleEntity extends VehicleEntity implements IInventoryChangedListener, INamedContainerProvider
{
    protected static final DataParameter<Float> CURRENT_SPEED = EntityDataManager.createKey(PoweredVehicleEntity.class, DataSerializers.FLOAT);
    protected static final DataParameter<Float> MAX_SPEED = EntityDataManager.createKey(PoweredVehicleEntity.class, DataSerializers.FLOAT);
    protected static final DataParameter<Float> ACCELERATION_SPEED = EntityDataManager.createKey(PoweredVehicleEntity.class, DataSerializers.FLOAT);
    protected static final DataParameter<Float> POWER = EntityDataManager.createKey(PoweredVehicleEntity.class, DataSerializers.FLOAT);
    protected static final DataParameter<Integer> TURN_DIRECTION = EntityDataManager.createKey(PoweredVehicleEntity.class, DataSerializers.VARINT);
    protected static final DataParameter<Float> TARGET_TURN_ANGLE = EntityDataManager.createKey(PoweredVehicleEntity.class, DataSerializers.FLOAT);
    protected static final DataParameter<Integer> TURN_SENSITIVITY = EntityDataManager.createKey(PoweredVehicleEntity.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> MAX_TURN_ANGLE = EntityDataManager.createKey(PoweredVehicleEntity.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> ACCELERATION_DIRECTION = EntityDataManager.createKey(PoweredVehicleEntity.class, DataSerializers.VARINT);
    protected static final DataParameter<Boolean> HAS_ENGINE = EntityDataManager.createKey(PoweredVehicleEntity.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Integer> ENGINE_TIER = EntityDataManager.createKey(PoweredVehicleEntity.class, DataSerializers.VARINT);
    protected static final DataParameter<Boolean> HORN = EntityDataManager.createKey(PoweredVehicleEntity.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Boolean> REQUIRES_FUEL = EntityDataManager.createKey(PoweredVehicleEntity.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Float> CURRENT_FUEL = EntityDataManager.createKey(PoweredVehicleEntity.class, DataSerializers.FLOAT);
    protected static final DataParameter<Float> FUEL_CAPACITY = EntityDataManager.createKey(PoweredVehicleEntity.class, DataSerializers.FLOAT);
    protected static final DataParameter<Boolean> NEEDS_KEY = EntityDataManager.createKey(PoweredVehicleEntity.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<ItemStack> KEY_STACK = EntityDataManager.createKey(PoweredVehicleEntity.class, DataSerializers.ITEMSTACK);
    protected static final DataParameter<Boolean> HAS_WHEELS = EntityDataManager.createKey(PoweredVehicleEntity.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Integer> WHEEL_TYPE = EntityDataManager.createKey(PoweredVehicleEntity.class, DataSerializers.VARINT);
    public static final DataParameter<Integer> WHEEL_COLOR = EntityDataManager.createKey(PoweredVehicleEntity.class, DataSerializers.VARINT);

    public float prevCurrentSpeed;
    public float currentSpeed;
    public float speedMultiplier;
    public boolean boosting;
    public int boostTimer;
    public boolean launching;
    public int launchingTimer;
    public boolean disableFallDamage;
    public float fuelConsumption = 0.25F;

    protected double[] wheelPositions;
    protected boolean wheelsOnGround = true;
    public float turnAngle;
    public float prevTurnAngle;

    public float deltaYaw;
    public float wheelAngle;
    public float prevWheelAngle; //TODO can remove use render wheel angle instead

    @OnlyIn(Dist.CLIENT)
    public float targetWheelAngle;
    @OnlyIn(Dist.CLIENT)
    public float renderWheelAngle;
    @OnlyIn(Dist.CLIENT)
    public float prevRenderWheelAngle;

    public float vehicleMotionX;
    public float vehicleMotionY;
    public float vehicleMotionZ;

    private UUID owner;

    private Inventory vehicleInventory;

    private FuelPortType fuelPortType;
    private boolean fueling;

    protected PoweredVehicleEntity(EntityType<?> entityType, World worldIn)
    {
        super(entityType, worldIn);
        this.stepHeight = 1.0F;
    }

    public PoweredVehicleEntity(EntityType<?> entityType, World worldIn, double posX, double posY, double posZ)
    {
        this(entityType, worldIn);
        this.setPosition(posX, posY, posZ);
    }

    @Override
    public void registerData()
    {
        super.registerData();
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
        this.dataManager.register(REQUIRES_FUEL, Config.SERVER.fuelEnabled.get());
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
        return ModSounds.HORN_MONO.get();
    }

    public SoundEvent getHornRidingSound()
    {
        return ModSounds.HORN_STEREO.get();
    }

    public void playFuelPortOpenSound()
    {
        if(!this.fueling)
        {
            this.fuelPortType.playOpenSound();
            this.fueling = true;
        }
    }

    public void playFuelPortCloseSound()
    {
        if(this.fueling)
        {
            this.fuelPortType.playCloseSound();
            this.fueling = false;
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
        this.setFuelPortType(FuelPortType.DEFAULT);
    }

    protected void setFuelPortType(FuelPortType fuelPortType)
    {
        this.fuelPortType = fuelPortType;
    }

    public void fuelVehicle(PlayerEntity player, Hand hand)
    {
        if(player.getDataManager().get(CustomDataParameters.GAS_PUMP).isPresent())
        {
            BlockPos pos = player.getDataManager().get(CustomDataParameters.GAS_PUMP).get();
            TileEntity tileEntity = world.getTileEntity(pos);
            if(tileEntity instanceof GasPumpTileEntity)
            {
                tileEntity = world.getTileEntity(pos.down());
                if(tileEntity instanceof GasPumpTankTileEntity)
                {
                    GasPumpTankTileEntity gasPumpTank = (GasPumpTankTileEntity) tileEntity;
                    FluidStack stack = gasPumpTank.getFluidTank().drain(200, IFluidHandler.FluidAction.EXECUTE);
                    if(!stack.isEmpty())
                    {
                        stack.setAmount(this.addFuel(stack.getAmount()));
                        if(stack.getAmount() > 0)
                        {
                            gasPumpTank.getFluidTank().fill(stack, IFluidHandler.FluidAction.EXECUTE);
                        }
                    }
                }
            }
            return;
        }

        ItemStack stack = player.getHeldItem(hand);
        if(!stack.isEmpty() && stack.getItem() instanceof JerryCanItem)
        {
            JerryCanItem jerryCan = (JerryCanItem) stack.getItem();
            int rate = jerryCan.getFillRate(stack);
            int drained = jerryCan.drain(stack, rate);
            int remaining = this.addFuel(drained);
            jerryCan.fill(stack, remaining);
        }
    }

    @Override
    public boolean processInitialInteract(PlayerEntity player, Hand hand)
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

            if(stack.getItem() == ModItems.KEY.get())
            {
                if(!this.owner.equals(player.getUniqueID()))
                {
                    CommonUtils.sendInfoMessage(player, "vehicle.status.invalid_owner");
                    return false;
                }

                if(this.isLockable())
                {
                    CompoundNBT tag = CommonUtils.getOrCreateStackTag(stack);
                    if(!tag.hasUniqueId("VehicleId") || this.getUniqueID().equals(tag.getUniqueId("VehicleId")))
                    {
                        tag.putUniqueId("VehicleId", this.getUniqueID());
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
            else if(stack.getItem() == ModItems.WRENCH.get() && this.getRidingEntity() instanceof EntityJack)
            {
                if(player.getUniqueID().equals(owner))
                {
                    this.openEditInventory(player);
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
        this.prevCurrentSpeed = this.currentSpeed;
        this.prevTurnAngle = this.turnAngle;
        this.prevWheelAngle = this.wheelAngle;

        if(this.world.isRemote)
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
        this.setSpeed(this.currentSpeed);

        /* Updates the direction of the vehicle */
        VehicleProperties properties = this.getProperties();
        if(properties.getFrontAxelVec() == null || properties.getRearAxelVec() == null)
        {
            this.rotationYaw -= this.deltaYaw;
        }

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

        this.move(MoverType.SELF, this.getMotion().add(this.vehicleMotionX, this.vehicleMotionY, this.vehicleMotionZ));

        /* Reduces the motion and speed multiplier */
        if(this.onGround)
        {
            this.setMotion(this.getMotion().mul(0.8, 0.98, 0.8));
        }
        else
        {
            this.setMotion(this.getMotion().mul(0.98, 0.98, 0.98));
        }

        if(this.boostTimer > 0)
        {
            this.boostTimer--;
        }
        else
        {
            this.boosting = false;
            this.speedMultiplier *= 0.85;
        }

        if(this.launchingTimer > 0)
        {
            //Ensures fall damage is disabled while launching
            this.disableFallDamage = true;
            this.launchingTimer--;
        }
        else
        {
            this.launching = false;
        }

        /* Checks for block collisions */
        this.doBlockCollisions();

        /* Checks for collisions with any other vehicles */
        List<Entity> list = this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox(), entity -> entity instanceof BumperCarEntity);
        if (!list.isEmpty())
        {
            for(Entity entity : list)
            {
                this.applyEntityCollision(entity);
            }
        }

        if(this.requiresFuel() && controllingPassenger instanceof PlayerEntity && !((PlayerEntity) controllingPassenger).isCreative() && this.isEnginePowered())
        {
            float currentSpeed = Math.abs(Math.min(this.getSpeed(), this.getMaxSpeed()));
            float normalSpeed = Math.max(0.05F, currentSpeed / this.getMaxSpeed());
            float currentFuel = this.getCurrentFuel();
            currentFuel -= this.fuelConsumption * normalSpeed * Config.SERVER.fuelConsumptionFactor.get();
            if(currentFuel < 0F) currentFuel = 0F;
            this.setCurrentFuel(currentFuel);
        }
    }

    public void updateVehicle() {}

    public abstract void updateVehicleMotion();

    public abstract EngineType getEngineType();

    public FuelPortType getFuelPortType()
    {
        return FuelPortType.DEFAULT;
    }

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
                    int x = MathHelper.floor(this.getPosX() + wheelX);
                    int y = MathHelper.floor(this.getPosY() + wheelY - 0.2D);
                    int z = MathHelper.floor(this.getPosZ() + wheelZ);
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = this.world.getBlockState(pos);
                    if(state.getMaterial() != Material.AIR && state.getMaterial().isToolNotRequired())
                    {
                        Vec3d dirVec = this.getVectorForRotation(this.rotationPitch, this.getModifiedRotationYaw() + 180F);
                        this.world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, state), this.getPosX() + wheelX, this.getPosY() + wheelY, this.getPosZ() + wheelZ, dirVec.x, dirVec.y, dirVec.z);
                    }
                }
            }
        }

        if(this.shouldShowEngineSmoke()&& this.canDrive() && this.ticksExisted % 2 == 0)
        {
            Vec3d smokePosition = this.getEngineSmokePosition().rotateYaw(-this.getModifiedRotationYaw() * 0.017453292F);
            this.world.addParticle(ParticleTypes.SMOKE, this.getPosX() + smokePosition.x, this.getPosY() + smokePosition.y, this.getPosZ() + smokePosition.z, -this.getMotion().x, 0.0D, -this.getMotion().z);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void onClientUpdate()
    {
        this.prevRenderWheelAngle = this.renderWheelAngle;

        Entity entity = this.getControllingPassenger();
        if(entity instanceof LivingEntity && entity.equals(Minecraft.getInstance().player))
        {
            LivingEntity livingEntity = (LivingEntity) entity;
            float power = VehicleMod.PROXY.getPower(this);
            if(power != this.getPower())
            {
                this.setPower(power);
                PacketHandler.instance.sendToServer(new MessagePower(power));
            }

            AccelerationDirection acceleration = VehicleMod.PROXY.getAccelerationDirection(livingEntity);
            if(this.getAcceleration() != acceleration)
            {
                this.setAcceleration(acceleration);
                PacketHandler.instance.sendToServer(new MessageAccelerating(acceleration));
            }

            boolean horn = VehicleMod.PROXY.isHonking();
            this.setHorn(horn);
            PacketHandler.instance.sendToServer(new MessageHorn(horn));

            TurnDirection direction = VehicleMod.PROXY.getTurnDirection(livingEntity);
            if(this.getTurnDirection() != direction)
            {
                this.setTurnDirection(direction);
                PacketHandler.instance.sendToServer(new MessageTurnDirection(direction));
            }

            float targetTurnAngle = VehicleMod.PROXY.getTargetTurnAngle(this, false);
            this.setTargetTurnAngle(targetTurnAngle);
            PacketHandler.instance.sendToServer(new MessageTurnAngle(targetTurnAngle));
        }
    }

    @Override
    protected void readAdditional(CompoundNBT compound)
    {
        super.readAdditional(compound);
        if(compound.contains("Owner", Constants.NBT.TAG_COMPOUND))
        {
            this.owner = compound.getUniqueId("Owner");
        }
        if(compound.contains("HasEngine", Constants.NBT.TAG_BYTE))
        {
            this.setEngine(compound.getBoolean("HasEngine"));
        }
        if(compound.contains("EngineTier", Constants.NBT.TAG_INT))
        {
            this.setEngineTier(EngineTier.getType(compound.getInt("EngineTier")));
        }
        if(compound.contains("HasWheels", Constants.NBT.TAG_BYTE))
        {
            this.setWheels(compound.getBoolean("HasWheels"));
        }
        if(compound.contains("WheelType", Constants.NBT.TAG_INT))
        {
            this.setWheelType(WheelType.getType(compound.getInt("WheelType")));
        }
        if(compound.contains("WheelColor", Constants.NBT.TAG_INT))
        {
            this.setWheelColor(compound.getInt("WheelColor"));
        }
        if(compound.contains("MaxSpeed", Constants.NBT.TAG_FLOAT))
        {
            this.setMaxSpeed(compound.getFloat("MaxSpeed"));
        }
        if(compound.contains("AccelerationSpeed", Constants.NBT.TAG_FLOAT))
        {
            this.setAccelerationSpeed(compound.getFloat("AccelerationSpeed"));
        }
        if(compound.contains("TurnSensitivity", Constants.NBT.TAG_INT))
        {
            this.setTurnSensitivity(compound.getInt("TurnSensitivity"));
        }
        if(compound.contains("MaxTurnAngle", Constants.NBT.TAG_INT))
        {
            this.setMaxTurnAngle(compound.getInt("MaxTurnAngle"));
        }
        if(compound.contains("StepHeight", Constants.NBT.TAG_FLOAT))
        {
            this.stepHeight = compound.getFloat("StepHeight");
        }
        if(compound.contains("RequiresFuel", Constants.NBT.TAG_BYTE))
        {
            this.setRequiresFuel(compound.getBoolean("RequiresFuel"));
        }
        if(compound.contains("CurrentFuel", Constants.NBT.TAG_FLOAT))
        {
            this.setCurrentFuel(compound.getFloat("CurrentFuel"));
        }
        if(compound.contains("FuelCapacity", Constants.NBT.TAG_INT))
        {
            this.setFuelCapacity(compound.getInt("FuelCapacity"));
        }
        if(compound.contains("KeyNeeded", Constants.NBT.TAG_BYTE))
        {
            this.setKeyNeeded(compound.getBoolean("KeyNeeded"));
        }
        this.setKeyStack(CommonUtils.readItemStackFromTag(compound, "KeyStack"));
    }

    @Override
    protected void writeAdditional(CompoundNBT compound)
    {
        super.writeAdditional(compound);
        if(this.owner != null)
        {
            compound.putUniqueId("Owner", this.owner);
        }
        compound.putBoolean("HasEngine", this.hasEngine());
        compound.putInt("EngineTier", this.getEngineTier().ordinal());
        compound.putBoolean("HasWheels", this.hasWheels());
        compound.putInt("WheelType", this.getWheelType().ordinal());
        compound.putInt("WheelColor", this.getWheelColor());
        compound.putFloat("MaxSpeed", this.getMaxSpeed());
        compound.putFloat("AccelerationSpeed", this.getAccelerationSpeed());
        compound.putInt("TurnSensitivity", this.getTurnSensitivity());
        compound.putInt("MaxTurnAngle", this.getMaxTurnAngle());
        compound.putFloat("StepHeight", this.stepHeight);
        compound.putBoolean("RequiresFuel", this.requiresFuel());
        compound.putFloat("CurrentFuel", this.getCurrentFuel());
        compound.putFloat("FuelCapacity", this.getFuelCapacity());
        compound.putBoolean("KeyNeeded", this.isKeyNeeded());
        CommonUtils.writeItemStackToTag(compound, "KeyStack", this.getKeyStack());
    }

    @Nullable
    public Entity getControllingPassenger()
    {
        if(this.getPassengers().isEmpty())
        {
            return null;
        }
        VehicleProperties properties = this.getProperties();
        for(Entity passenger : this.getPassengers())
        {
            int seatIndex = this.getSeatTracker().getSeatIndex(passenger.getUniqueID());
            if(seatIndex != -1 && properties.getSeats().get(seatIndex).isDriverSeat())
            {
                return passenger;
            }
        }
        return null;
    }

    @Override
    public void updatePassengerPosition(Entity passenger)
    {
        if(this.isPassenger(passenger))
        {
            int seatIndex = this.getSeatTracker().getSeatIndex(passenger.getUniqueID());
            if(seatIndex != -1)
            {
                VehicleProperties properties = this.getProperties();
                if(seatIndex >= 0 && seatIndex < properties.getSeats().size())
                {
                    Seat seat = properties.getSeats().get(seatIndex);
                    Vec3d seatVec = seat.getPosition().add(0, properties.getAxleOffset() + properties.getWheelOffset(), 0).scale(properties.getBodyPosition().getScale()).mul(-1, 1, 1).scale(0.0625).rotateYaw(-(this.getModifiedRotationYaw() + 180) * 0.017453292F);
                    //Vec3d seatVec = Vec3d.ZERO;
                    passenger.setPosition(this.getPosX() - seatVec.x, this.getPosY() + seatVec.y + passenger.getYOffset(), this.getPosZ() - seatVec.z);
                    if(VehicleMod.PROXY.canApplyVehicleYaw(passenger))
                    {
                        passenger.rotationYaw -= this.deltaYaw;
                        passenger.setRotationYawHead(passenger.rotationYaw);
                    }
                    this.applyYawToEntity(passenger);
                }
            }
        }
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
        return Math.sqrt(Math.pow(this.getPosX() - this.prevPosX, 2) + Math.pow(this.getPosY() - this.prevPosY, 2) + Math.pow(this.getPosZ() - this.prevPosZ, 2)) * 20;
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
        this.dataManager.set(POWER, MathHelper.clamp(power, 0.0F, 1.0F));
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

    @OnlyIn(Dist.CLIENT)
    public boolean shouldRenderEngine()
    {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
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
        return Config.SERVER.fuelEnabled.get() && this.dataManager.get(REQUIRES_FUEL);
    }

    public void setRequiresFuel(boolean requiresFuel)
    {
        this.dataManager.set(REQUIRES_FUEL, Config.SERVER.fuelEnabled.get() && requiresFuel);
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
            this.world.addEntity(new ItemEntity(this.world, keyHole.x, keyHole.y, keyHole.z, this.getKeyStack()));
            this.setKeyStack(ItemStack.EMPTY);
        }
    }

    public boolean isLockable()
    {
        return true;
    }

    public boolean isEnginePowered()
    {
        return ((this.getEngineType() == EngineType.NONE || this.hasEngine()) && (this.isControllingPassengerCreative() || this.isFueled()) && this.getDestroyedStage() < 9) && (!this.isKeyNeeded() || !this.getKeyStack().isEmpty());
    }

    public boolean canDrive()
    {
        return (!this.canChangeWheels() || this.hasWheels()) && this.isEnginePowered();
    }

    public boolean isOwner(PlayerEntity player)
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
            if(COLOR.equals(key))
            {
                /*Color color = new Color(this.dataManager.get(COLOR)); //TODO move this code to renderer to make fuel port darker or lighter
                int colorInt = (Math.sqrt(color.getRed() * color.getRed() * 0.241
                        + color.getGreen() * color.getGreen() * 0.691
                        + color.getBlue() * color.getBlue() * 0.068) > 127 ? color.darker() : color.brighter()).getRGB();*/
            }
        }
    }

    @Override
    public void addPassenger(Entity passenger)
    {
        super.addPassenger(passenger);
        if(passenger instanceof PlayerEntity && world.isRemote)
        {
            VehicleMod.PROXY.playVehicleSound((PlayerEntity) passenger, this);
        }
    }

    @Override
    public boolean func_225503_b_(float distance, float damageMultiplier)
    {
        if(!this.disableFallDamage)
        {
            super.func_225503_b_(distance, damageMultiplier);
        }
        if(this.launchingTimer <= 0 && distance > 3)
        {
            this.disableFallDamage = false;
        }
        return true;
    }

    private boolean isControllingPassengerCreative()
    {
        Entity entity = this.getControllingPassenger();
        if(entity instanceof PlayerEntity)
        {
            return ((PlayerEntity) entity).isCreative();
        }
        return false;
    }

    private void openEditInventory(PlayerEntity player)
    {
        if(player instanceof ServerPlayerEntity)
        {
            NetworkHooks.openGui((ServerPlayerEntity) player, this, buffer -> buffer.writeInt(this.getEntityId()));
            /*ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
            serverPlayerEntity.getNextWindowId();
            serverPlayerEntity.openContainer = new EditVehicleContainer(serverPlayerEntity.currentWindowId, this.getVehicleInventory(), this, player, player.inventory);
            serverPlayerEntity.openContainer.addListener(serverPlayerEntity);
            PacketHandler.instance.send(PacketDistributor.PLAYER.with(() -> serverPlayerEntity), new MessageVehicleWindow(serverPlayerEntity.currentWindowId, this.getEntityId()));*/
        }
    }

    public Inventory getVehicleInventory()
    {
        if(this.vehicleInventory == null)
        {
            this.initVehicleInventory();
        }
        return this.vehicleInventory;
    }

    protected void initVehicleInventory()
    {
        this.vehicleInventory = new Inventory(2);

        ItemStack engine = ItemLookup.getEngine(this);
        if(this.getEngineType() != EngineType.NONE & !engine.isEmpty())
        {
            this.vehicleInventory.setInventorySlotContents(0, engine);
        }

        ItemStack wheel = ItemLookup.getWheel(this);
        if(this.canChangeWheels() && !wheel.isEmpty())
        {
            this.vehicleInventory.setInventorySlotContents(1, wheel);
        }

        this.vehicleInventory.addListener(this);
    }

    private void updateSlots()
    {
        if (!this.world.isRemote)
        {
            ItemStack engine = this.vehicleInventory.getStackInSlot(0);
            if(engine.getItem() instanceof EngineItem)
            {
                EngineItem item = (EngineItem) engine.getItem();
                if(item.getEngineType() == this.getEngineType())
                {
                    this.setEngine(true);
                    this.setEngineTier(item.getEngineTier());
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
                if(wheel.getItem() instanceof WheelItem)
                {
                    if(!this.hasWheels())
                    {
                        WheelItem wheelItem = (WheelItem) wheel.getItem();
                        this.world.playSound(null, getPosition(), ModSounds.AIR_WRENCH_GUN.get(), SoundCategory.BLOCKS, 1.0F, 1.1F);
                        this.setWheels(true);
                        this.setWheelType(wheelItem.getWheelType());
                        if(wheelItem.hasColor(wheel))
                        {
                            this.setWheelColor(wheelItem.getColor(wheel));
                        }
                    }
                }
                else
                {
                    this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), ModSounds.AIR_WRENCH_GUN.get(), SoundCategory.BLOCKS, 1.0F, 0.8F);
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
    protected void onVehicleDestroyed(LivingEntity entity)
    {
        super.onVehicleDestroyed(entity);
        boolean isCreativeMode = entity instanceof PlayerEntity && ((PlayerEntity) entity).isCreative();
        if(!isCreativeMode && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS))
        {
            // Spawns the engine if the vehicle has one
            ItemStack engine = ItemLookup.getEngine(this);
            if(this.getEngineType() != EngineType.NONE && !engine.isEmpty())
            {
                InventoryUtil.spawnItemStack(this.world, this.getPosX(), this.getPosY(), this.getPosZ(), engine);
            }

            // Spawns the key and removes the associated vehicle uuid
            ItemStack key = this.getKeyStack().copy();
            if(!key.isEmpty())
            {
                CommonUtils.getOrCreateStackTag(key).remove("VehicleId");
                InventoryUtil.spawnItemStack(this.world, this.getPosX(), this.getPosY(), this.getPosZ(), key);
            }

            // Spawns wheels if the vehicle has any
            ItemStack wheel = ItemLookup.getWheel(this);
            if(this.canChangeWheels() && !wheel.isEmpty())
            {
                InventoryUtil.spawnItemStack(this.world, this.getPosX(), this.getPosY(), this.getPosZ(), wheel);
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
                int x = MathHelper.floor(this.getPosX() + wheelX);
                int y = MathHelper.floor(this.getPosY() + wheelY - 0.2D);
                int z = MathHelper.floor(this.getPosZ() + wheelZ);
                BlockPos pos = new BlockPos(x, y, z);
                BlockState state = this.world.getBlockState(pos);
                if(state.getMaterial() != Material.AIR)
                {
                    if(state.getMaterial() == Material.SNOW || state.getMaterial() == Material.SNOW_BLOCK || (state.getBlock() == Blocks.GRASS_BLOCK && state.get(GrassBlock.SNOWY)))
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
                    int x = MathHelper.floor(this.getPosX() + wheelX);
                    int y = MathHelper.floor(this.getPosY() + wheelY - 0.2D);
                    int z = MathHelper.floor(this.getPosZ() + wheelZ);
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = this.world.getBlockState(pos);
                    if(!state.getCollisionShape(this.world, pos).isEmpty())
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

        ResourceLocation entityId = this.getType().getRegistryName();
        if(entityId != null)
        {
            return BlockVehicleCrate.create(entityId, this.getColor(), engineTier, wheelType, wheelColor);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return this.getName();
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity)
    {
        return new EditVehicleContainer(windowId, this.getVehicleInventory(), this, playerEntity, playerInventory);
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

        public static AccelerationDirection fromEntity(LivingEntity entity)
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

    public enum FuelPortType
    {
        DEFAULT(SpecialModels.FUEL_DOOR_CLOSED, SpecialModels.FUEL_DOOR_OPEN, ModSounds.FUEL_PORT_OPEN.get(), 0.25F, 0.6F, ModSounds.FUEL_PORT_CLOSE.get(), 0.12F, 0.6F),
        SMALL(SpecialModels.SMALL_FUEL_DOOR_CLOSED, SpecialModels.SMALL_FUEL_DOOR_OPEN, ModSounds.FUEL_PORT_2_OPEN.get(), 0.4F, 0.6F, ModSounds.FUEL_PORT_2_CLOSE.get(), 0.3F, 0.6F);

        private ISpecialModel closed;
        private ISpecialModel open;
        private SoundEvent openSound;
        private SoundEvent closeSound;
        private float openVolume;
        private float closeVolume;
        private float openPitch;
        private float closePitch;

        FuelPortType(ISpecialModel closed, ISpecialModel open, SoundEvent openSound, float openVolume, float openPitch, SoundEvent closeCount, float closeVolume, float closePitch)
        {
            this.closed = closed;
            this.open = open;
            this.openSound = openSound;
            this.openVolume = openVolume;
            this.openPitch = openPitch;
            this.closeSound = closeCount;
            this.closeVolume = closeVolume;
            this.closePitch = closePitch;
        }

        public ISpecialModel getClosedModel()
        {
            return closed;
        }

        public ISpecialModel getOpenModel()
        {
            return open;
        }

        @OnlyIn(Dist.CLIENT)
        public void playOpenSound()
        {
            VehicleMod.PROXY.playSound(this.openSound, this.openVolume, this.openPitch);
        }

        @OnlyIn(Dist.CLIENT)
        public void playCloseSound()
        {
            VehicleMod.PROXY.playSound(this.closeSound, this.closeVolume, this.closePitch);
        }
    }
}
