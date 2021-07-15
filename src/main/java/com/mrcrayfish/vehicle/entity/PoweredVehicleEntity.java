package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.obfuscate.common.data.SyncedPlayerData;
import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.block.VehicleCrateBlock;
import com.mrcrayfish.vehicle.client.VehicleHelper;
import com.mrcrayfish.vehicle.client.model.ISpecialModel;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.render.Wheel;
import com.mrcrayfish.vehicle.common.ItemLookup;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.vehicle.BumperCarEntity;
import com.mrcrayfish.vehicle.init.ModDataKeys;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.inventory.container.EditVehicleContainer;
import com.mrcrayfish.vehicle.item.EngineItem;
import com.mrcrayfish.vehicle.item.JerryCanItem;
import com.mrcrayfish.vehicle.item.WheelItem;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageAccelerating;
import com.mrcrayfish.vehicle.network.message.MessageHorn;
import com.mrcrayfish.vehicle.network.message.MessagePower;
import com.mrcrayfish.vehicle.network.message.MessageTurnAngle;
import com.mrcrayfish.vehicle.network.message.MessageTurnDirection;
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
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Author: MrCrayfish
 */
public abstract class PoweredVehicleEntity extends VehicleEntity implements IInventoryChangedListener, INamedContainerProvider
{
    protected static final DataParameter<Float> CURRENT_SPEED = EntityDataManager.defineId(PoweredVehicleEntity.class, DataSerializers.FLOAT);
    protected static final DataParameter<Float> MAX_SPEED = EntityDataManager.defineId(PoweredVehicleEntity.class, DataSerializers.FLOAT);
    protected static final DataParameter<Float> ACCELERATION_SPEED = EntityDataManager.defineId(PoweredVehicleEntity.class, DataSerializers.FLOAT);
    protected static final DataParameter<Float> POWER = EntityDataManager.defineId(PoweredVehicleEntity.class, DataSerializers.FLOAT);
    protected static final DataParameter<Integer> TURN_DIRECTION = EntityDataManager.defineId(PoweredVehicleEntity.class, DataSerializers.INT);
    protected static final DataParameter<Float> TARGET_TURN_ANGLE = EntityDataManager.defineId(PoweredVehicleEntity.class, DataSerializers.FLOAT);
    protected static final DataParameter<Integer> TURN_SENSITIVITY = EntityDataManager.defineId(PoweredVehicleEntity.class, DataSerializers.INT);
    protected static final DataParameter<Integer> MAX_TURN_ANGLE = EntityDataManager.defineId(PoweredVehicleEntity.class, DataSerializers.INT);
    protected static final DataParameter<Integer> ACCELERATION_DIRECTION = EntityDataManager.defineId(PoweredVehicleEntity.class, DataSerializers.INT);
    protected static final DataParameter<Boolean> HAS_ENGINE = EntityDataManager.defineId(PoweredVehicleEntity.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Integer> ENGINE_TIER = EntityDataManager.defineId(PoweredVehicleEntity.class, DataSerializers.INT);
    protected static final DataParameter<Boolean> HORN = EntityDataManager.defineId(PoweredVehicleEntity.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Boolean> REQUIRES_FUEL = EntityDataManager.defineId(PoweredVehicleEntity.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Float> CURRENT_FUEL = EntityDataManager.defineId(PoweredVehicleEntity.class, DataSerializers.FLOAT);
    protected static final DataParameter<Float> FUEL_CAPACITY = EntityDataManager.defineId(PoweredVehicleEntity.class, DataSerializers.FLOAT);
    protected static final DataParameter<Boolean> NEEDS_KEY = EntityDataManager.defineId(PoweredVehicleEntity.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<ItemStack> KEY_STACK = EntityDataManager.defineId(PoweredVehicleEntity.class, DataSerializers.ITEM_STACK);
    protected static final DataParameter<ItemStack> WHEEL_STACK = EntityDataManager.defineId(PoweredVehicleEntity.class, DataSerializers.ITEM_STACK);

    public float prevCurrentSpeed;
    public float currentSpeed;
    public float speedMultiplier;
    public boolean boosting;
    public int boostTimer;
    public boolean launching;
    public int launchingTimer;
    public boolean disableFallDamage;
    public float fuelConsumption = 0.25F;
    protected boolean charging;
    protected AccelerationDirection prevAcceleration;

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
    @OnlyIn(Dist.CLIENT)
    public int wheelieCount;
    @OnlyIn(Dist.CLIENT)
    public int prevWheelieCount;

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
        this.maxUpStep = 1.0F;
    }

    public PoweredVehicleEntity(EntityType<?> entityType, World worldIn, double posX, double posY, double posZ)
    {
        this(entityType, worldIn);
        this.setPos(posX, posY, posZ);
    }

    @Override
    public void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(CURRENT_SPEED, 0F);
        this.entityData.define(MAX_SPEED, 10F);
        this.entityData.define(ACCELERATION_SPEED, 0.5F);
        this.entityData.define(POWER, 1.0F);
        this.entityData.define(TURN_DIRECTION, TurnDirection.FORWARD.ordinal());
        this.entityData.define(TARGET_TURN_ANGLE, 0F);
        this.entityData.define(TURN_SENSITIVITY, 6);
        this.entityData.define(MAX_TURN_ANGLE, 45);
        this.entityData.define(ACCELERATION_DIRECTION, AccelerationDirection.NONE.ordinal());
        this.entityData.define(HAS_ENGINE, false);
        this.entityData.define(ENGINE_TIER, 0);
        this.entityData.define(HORN, false);
        this.entityData.define(REQUIRES_FUEL, Config.SERVER.fuelEnabled.get());
        this.entityData.define(CURRENT_FUEL, 0F);
        this.entityData.define(FUEL_CAPACITY, 15000F);
        this.entityData.define(NEEDS_KEY, false);
        this.entityData.define(KEY_STACK, ItemStack.EMPTY);
        this.entityData.define(WHEEL_STACK, ItemStack.EMPTY);

        List<Wheel> wheels = this.getProperties().getWheels();
        if(wheels != null && wheels.size() > 0)
        {
            this.wheelPositions = new double[wheels.size() * 3];
        }
    }

    public abstract SoundEvent getEngineSound();

    //TODO ability to change with nbt
    public SoundEvent getHornSound()
    {
        return ModSounds.ENTITY_VEHICLE_HORN.get();
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
    public boolean isPickable()
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
        if(SyncedPlayerData.instance().get(player, ModDataKeys.GAS_PUMP).isPresent())
        {
            BlockPos pos = SyncedPlayerData.instance().get(player, ModDataKeys.GAS_PUMP).get();
            TileEntity tileEntity = this.level.getBlockEntity(pos);
            if(!(tileEntity instanceof GasPumpTileEntity))
                return;

            tileEntity = this.level.getBlockEntity(pos.below());
            if(!(tileEntity instanceof GasPumpTankTileEntity))
                return;

            GasPumpTankTileEntity gasPumpTank = (GasPumpTankTileEntity) tileEntity;
            FluidTank tank = gasPumpTank.getFluidTank();
            FluidStack stack = tank.getFluid();
            if(stack.isEmpty() || !Config.SERVER.validFuels.get().contains(stack.getFluid().getRegistryName().toString()))
                return;

            stack = tank.drain(200, IFluidHandler.FluidAction.EXECUTE);
            if(stack.isEmpty())
                return;

            stack.setAmount(this.addFuel(stack.getAmount()));
            if(stack.getAmount() <= 0)
                return;

            gasPumpTank.getFluidTank().fill(stack, IFluidHandler.FluidAction.EXECUTE);
            return;
        }

        ItemStack stack = player.getItemInHand(hand);
        if(!(stack.getItem() instanceof JerryCanItem))
            return;

        JerryCanItem jerryCan = (JerryCanItem) stack.getItem();
        Optional<IFluidHandlerItem> optional = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).resolve();
        if(!optional.isPresent())
            return;

        IFluidHandlerItem handler = optional.get();
        FluidStack fluidStack = handler.getFluidInTank(0);
        if(fluidStack.isEmpty() || !Config.SERVER.validFuels.get().contains(fluidStack.getFluid().getRegistryName().toString()))
            return;

        int transferAmount = Math.min(handler.getFluidInTank(0).getAmount(), jerryCan.getFillRate());
        transferAmount = (int) Math.min(Math.floor(this.getFuelCapacity() - this.getCurrentFuel()), transferAmount);
        handler.drain(transferAmount, IFluidHandler.FluidAction.EXECUTE);
        this.addFuel(transferAmount);
    }

    @Override
    public ActionResultType interact(PlayerEntity player, Hand hand)
    {
        ItemStack stack = player.getItemInHand(hand);
        if(!level.isClientSide)
        {
            /* If no owner is set, make the owner the person adding the key. It is used because
             * owner will not be set if the vehicle was summoned through a command */
            if(this.owner == null)
            {
                this.owner = player.getUUID();
            }

            if(stack.getItem() == ModItems.KEY.get())
            {
                if(!this.owner.equals(player.getUUID()))
                {
                    CommonUtils.sendInfoMessage(player, "vehicle.status.invalid_owner");
                    return ActionResultType.FAIL;
                }

                if(this.isLockable())
                {
                    CompoundNBT tag = CommonUtils.getOrCreateStackTag(stack);
                    if(!tag.hasUUID("VehicleId") || this.getUUID().equals(tag.getUUID("VehicleId")))
                    {
                        tag.putUUID("VehicleId", this.getUUID());
                        if(!this.isKeyNeeded())
                        {
                            this.setKeyNeeded(true);
                            CommonUtils.sendInfoMessage(player, "vehicle.status.key_added");
                        }
                        else
                        {
                            CommonUtils.sendInfoMessage(player, "vehicle.status.key_created");
                        }
                        return ActionResultType.SUCCESS;
                    }
                }
                else
                {
                    CommonUtils.sendInfoMessage(player, "vehicle.status.not_lockable");
                    return ActionResultType.FAIL;
                }
            }
            else if(stack.getItem() == ModItems.WRENCH.get() && this.getVehicle() instanceof EntityJack)
            {
                if(player.getUUID().equals(owner))
                {
                    this.openEditInventory(player);
                }
                else
                {
                    CommonUtils.sendInfoMessage(player, "vehicle.status.invalid_owner");
                }
                return ActionResultType.SUCCESS;
            }
        }
        return super.interact(player, hand);
    }

    @Override
    public void onUpdateVehicle()
    {
        this.prevCurrentSpeed = this.currentSpeed;
        this.prevTurnAngle = this.turnAngle;
        this.prevWheelAngle = this.wheelAngle;

        if(this.level.isClientSide)
        {
            this.onClientUpdate();
        }

        Entity controllingPassenger = this.getControllingPassenger();

        /* If there driver, create particles */
        if(controllingPassenger != null)
        {
            this.createParticles();
        }

        /* Makes the vehicle boost slightly from charging up */
        if(this.charging && this.prevAcceleration == AccelerationDirection.CHARGING && this.getAcceleration() != this.prevAcceleration && this.getRealSpeed() > 0.95F)
        {
            this.releaseCharge();
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
            this.yRot -= this.deltaYaw;
        }

        /* Updates the vehicle motion and applies it on top of the normal motion */
        this.updateVehicleMotion();

        this.setRot(this.yRot, this.xRot);
        double deltaRot = (double) (this.yRotO - this.yRot);
        if (deltaRot < -180.0D)
        {
            this.yRotO += 360.0F;
        }
        else if (deltaRot >= 180.0D)
        {
            this.yRotO -= 360.0F;
        }
        this.updateWheelPositions();

        this.move(MoverType.SELF, this.getDeltaMovement().add(this.vehicleMotionX, this.vehicleMotionY, this.vehicleMotionZ));

        /* Reduces the motion and speed multiplier */
        if(this.onGround)
        {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.8, 0.98, 0.8));
        }
        else
        {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.98, 0.98, 0.98));
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
        this.checkInsideBlocks();

        /* Checks for collisions with any other vehicles */
        List<Entity> list = this.level.getEntities(this, this.getBoundingBox(), entity -> entity instanceof BumperCarEntity);
        if (!list.isEmpty())
        {
            for(Entity entity : list)
            {
                this.push(entity);
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

        this.prevAcceleration = this.getAcceleration();
    }

    public void updateVehicle() {}

    public abstract void updateVehicleMotion();

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

        /* Reset charging to false if acceleration is not charging */
        if(acceleration != AccelerationDirection.CHARGING)
        {
            this.charging = false;
        }

        if(this.getControllingPassenger() != null)
        {
            if(this.canDrive())
            {
                boolean charging = this.canCharge() && acceleration == AccelerationDirection.CHARGING && Math.abs(this.currentSpeed) < 0.5F;
                if(acceleration == AccelerationDirection.FORWARD || (charging || this.charging))
                {
                    if(!this.charging)
                    {
                        this.charging = charging;
                    }
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

        if(level.isClientSide)
        {
            this.renderWheelAngle = this.wheelAngle;
        }
    }

    public void createParticles()
    {
        if(this.getAcceleration() == AccelerationDirection.FORWARD || this.charging)
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
                    int x = MathHelper.floor(this.getX() + wheelX);
                    int y = MathHelper.floor(this.getY() + wheelY - 0.2D);
                    int z = MathHelper.floor(this.getZ() + wheelZ);
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = this.level.getBlockState(pos);
                    if(state.getMaterial() != Material.AIR && state.getMaterial().isSolid())
                    {
                        Vector3d dirVec = this.calculateViewVector(this.xRot, this.getModifiedRotationYaw() + 180F).add(0, 0.5, 0);
                        if(this.charging)
                        {
                            dirVec = dirVec.scale(this.currentSpeed / 3F);
                        }
                        if(this.level.isClientSide())
                        {
                            VehicleHelper.spawnWheelParticle(pos, state, this.getX() + wheelX, this.getY() + wheelY, this.getZ() + wheelZ, dirVec);
                        }
                    }
                }
            }
        }

        if(this.shouldShowEngineSmoke()&& this.canDrive() && this.tickCount % 2 == 0)
        {
            Vector3d smokePosition = this.getEngineSmokePosition().yRot(-this.getModifiedRotationYaw() * 0.017453292F);
            this.level.addParticle(ParticleTypes.SMOKE, this.getX() + smokePosition.x, this.getY() + smokePosition.y, this.getZ() + smokePosition.z, -this.getDeltaMovement().x, 0.0D, -this.getDeltaMovement().z);
            if(this.charging && this.getRealSpeed() > 0.95F)
            {
                this.level.addParticle(ParticleTypes.CRIT, this.getX() + smokePosition.x, this.getY() + smokePosition.y, this.getZ() + smokePosition.z, -this.getDeltaMovement().x, 0.0D, -this.getDeltaMovement().z);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void onClientUpdate()
    {
        this.prevRenderWheelAngle = this.renderWheelAngle;
        this.prevWheelieCount = this.wheelieCount;

        Entity entity = this.getControllingPassenger();
        if(entity instanceof LivingEntity && entity.equals(Minecraft.getInstance().player))
        {
            LivingEntity livingEntity = (LivingEntity) entity;
            float power = VehicleHelper.getPower(this);
            if(power != this.getPower())
            {
                this.setPower(power);
                PacketHandler.instance.sendToServer(new MessagePower(power));
            }

            AccelerationDirection acceleration = VehicleHelper.getAccelerationDirection(livingEntity);
            if(this.getAcceleration() != acceleration)
            {
                this.setAcceleration(acceleration);
                PacketHandler.instance.sendToServer(new MessageAccelerating(acceleration));
            }

            boolean horn = VehicleHelper.isHonking();
            this.setHorn(horn);
            PacketHandler.instance.sendToServer(new MessageHorn(horn));

            TurnDirection direction = VehicleHelper.getTurnDirection(livingEntity);
            if(this.getTurnDirection() != direction)
            {
                this.setTurnDirection(direction);
                PacketHandler.instance.sendToServer(new MessageTurnDirection(direction));
            }

            float targetTurnAngle = VehicleHelper.getTargetTurnAngle(this, false);
            this.setTargetTurnAngle(targetTurnAngle);
            PacketHandler.instance.sendToServer(new MessageTurnAngle(targetTurnAngle));
        }

        if(this.isBoosting() && this.getControllingPassenger() != null)
        {
            if(this.wheelieCount < 4)
            {
                this.wheelieCount++;
            }
        }
        else if(this.wheelieCount > 0)
        {
            this.wheelieCount--;
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT compound)
    {
        super.readAdditionalSaveData(compound);
        if(compound.contains("Owner", Constants.NBT.TAG_COMPOUND))
        {
            this.owner = compound.getUUID("Owner");
        }
        if(compound.contains("HasEngine", Constants.NBT.TAG_BYTE))
        {
            this.setEngine(compound.getBoolean("HasEngine"));
        }
        if(compound.contains("EngineTier", Constants.NBT.TAG_INT))
        {
            this.setEngineTier(EngineTier.getType(compound.getInt("EngineTier")));
        }
        if(compound.contains("WheelStack", Constants.NBT.TAG_COMPOUND))
        {
            this.setWheelStack(ItemStack.of(compound.getCompound("WheelStack")));
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
            this.maxUpStep = compound.getFloat("StepHeight");
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
    protected void addAdditionalSaveData(CompoundNBT compound)
    {
        super.addAdditionalSaveData(compound);
        if(this.owner != null)
        {
            compound.putUUID("Owner", this.owner);
        }
        compound.putBoolean("HasEngine", this.hasEngine());
        compound.putInt("EngineTier", this.getEngineTier().ordinal());
        CommonUtils.writeItemStackToTag(compound, "WheelStack", this.getWheelStack());
        compound.putFloat("MaxSpeed", this.getMaxSpeed());
        compound.putFloat("AccelerationSpeed", this.getAccelerationSpeed());
        compound.putInt("TurnSensitivity", this.getTurnSensitivity());
        compound.putInt("MaxTurnAngle", this.getMaxTurnAngle());
        compound.putFloat("StepHeight", this.maxUpStep);
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
            int seatIndex = this.getSeatTracker().getSeatIndex(passenger.getUUID());
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
        if(this.hasPassenger(passenger))
        {
            int seatIndex = this.getSeatTracker().getSeatIndex(passenger.getUUID());
            if(seatIndex != -1)
            {
                VehicleProperties properties = this.getProperties();
                if(seatIndex >= 0 && seatIndex < properties.getSeats().size())
                {
                    Seat seat = properties.getSeats().get(seatIndex);
                    Vector3d seatVec = seat.getPosition().add(0, properties.getAxleOffset() + properties.getWheelOffset(), 0).scale(properties.getBodyPosition().getScale()).multiply(-1, 1, 1).scale(0.0625).yRot(-(this.getModifiedRotationYaw() + 180) * 0.017453292F);
                    //Vector3d seatVec = Vector3d.ZERO;
                    passenger.setPos(this.getX() - seatVec.x, this.getY() + seatVec.y + passenger.getMyRidingOffset(), this.getZ() - seatVec.z);
                    if(this.level.isClientSide() && VehicleHelper.canApplyVehicleYaw(passenger))
                    {
                        passenger.yRot -= this.deltaYaw;
                        passenger.setYHeadRot(passenger.yRot);
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
        this.entityData.set(MAX_SPEED, maxSpeed);
    }

    public float getMaxSpeed()
    {
        return this.entityData.get(MAX_SPEED);
    }

    public float getActualMaxSpeed()
    {
        return this.entityData.get(MAX_SPEED) + this.getEngineTier().getAdditionalMaxSpeed();
    }

    public float getRealSpeed()
    {
        return this.currentSpeed / (this.getActualMaxSpeed() * this.getWheelModifier() * this.getPower());
    }

    public void setSpeed(float speed)
    {
        this.entityData.set(CURRENT_SPEED, speed);
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
        this.entityData.set(ACCELERATION_SPEED, speed);
    }

    public float getAccelerationSpeed()
    {
        return this.entityData.get(ACCELERATION_SPEED);
    }

    protected float getModifiedAccelerationSpeed()
    {
        return this.entityData.get(ACCELERATION_SPEED);
    }

    public double getKilometersPreHour()
    {
        return Math.sqrt(Math.pow(this.getX() - this.xo, 2) + Math.pow(this.getY() - this.yo, 2) + Math.pow(this.getZ() - this.zo, 2)) * 20;
    }

    public void setTurnDirection(TurnDirection turnDirection)
    {
        this.entityData.set(TURN_DIRECTION, turnDirection.ordinal());
    }

    public TurnDirection getTurnDirection()
    {
        return TurnDirection.values()[this.entityData.get(TURN_DIRECTION)];
    }

    public void setTargetTurnAngle(float targetTurnAngle)
    {
        this.entityData.set(TARGET_TURN_ANGLE, targetTurnAngle);
    }

    public float getTargetTurnAngle()
    {
        return this.entityData.get(TARGET_TURN_ANGLE);
    }

    public void setAcceleration(AccelerationDirection direction)
    {
        this.entityData.set(ACCELERATION_DIRECTION, direction.ordinal());
    }

    public AccelerationDirection getAcceleration()
    {
        return AccelerationDirection.values()[this.entityData.get(ACCELERATION_DIRECTION)];
    }

    public void setPower(float power)
    {
        this.entityData.set(POWER, MathHelper.clamp(power, 0.0F, 1.0F));
    }

    public float getPower()
    {
        return this.entityData.get(POWER);
    }

    public void setTurnSensitivity(int sensitivity)
    {
        this.entityData.set(TURN_SENSITIVITY, sensitivity);
    }

    public int getTurnSensitivity()
    {
        return this.entityData.get(TURN_SENSITIVITY);
    }

    public void setMaxTurnAngle(int turnAngle)
    {
        this.entityData.set(MAX_TURN_ANGLE, turnAngle);
    }

    public int getMaxTurnAngle()
    {
        return this.entityData.get(MAX_TURN_ANGLE);
    }

    public boolean hasEngine()
    {
        return this.entityData.get(HAS_ENGINE);
    }

    public void setEngine(boolean hasEngine)
    {
        this.entityData.set(HAS_ENGINE, hasEngine);
    }

    public void setEngineTier(EngineTier engineTier)
    {
        this.entityData.set(ENGINE_TIER, engineTier.ordinal());
    }

    public EngineTier getEngineTier()
    {
        return EngineTier.getType(this.entityData.get(ENGINE_TIER));
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

    public Vector3d getEngineSmokePosition()
    {
        return new Vector3d(0, 0, 0);
    }

    public boolean shouldShowEngineSmoke()
    {
        return false;
    }

    public void setHorn(boolean activated)
    {
        this.entityData.set(HORN, activated);
    }

    public boolean getHorn()
    {
        return this.entityData.get(HORN);
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
        return Config.SERVER.fuelEnabled.get() && this.entityData.get(REQUIRES_FUEL);
    }

    public void setRequiresFuel(boolean requiresFuel)
    {
        this.entityData.set(REQUIRES_FUEL, Config.SERVER.fuelEnabled.get() && requiresFuel);
    }

    public boolean isFueled()
    {
        return !this.requiresFuel() || this.isControllingPassengerCreative() || this.getCurrentFuel() > 0F;
    }

    public void setCurrentFuel(float fuel)
    {
        this.entityData.set(CURRENT_FUEL, fuel);
    }

    public float getCurrentFuel()
    {
        return this.entityData.get(CURRENT_FUEL);
    }

    public void setFuelCapacity(float capacity)
    {
        this.entityData.set(FUEL_CAPACITY, capacity);
    }

    public float getFuelCapacity()
    {
        return this.entityData.get(FUEL_CAPACITY);
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
        this.entityData.set(NEEDS_KEY, needsKey);
    }

    public boolean isKeyNeeded()
    {
        return this.entityData.get(NEEDS_KEY);
    }

    public void setKeyStack(ItemStack stack)
    {
        this.entityData.set(KEY_STACK, stack);
    }

    public ItemStack getKeyStack()
    {
        return this.entityData.get(KEY_STACK);
    }

    public void ejectKey()
    {
        if(!this.getKeyStack().isEmpty())
        {
            Vector3d keyHole = this.getPartPositionAbsoluteVec(this.getProperties().getKeyPortPosition(), 1F);
            this.level.addFreshEntity(new ItemEntity(this.level, keyHole.x, keyHole.y, keyHole.z, this.getKeyStack()));
            this.setKeyStack(ItemStack.EMPTY);
        }
    }

    public boolean isLockable()
    {
        return true;
    }

    public boolean isEnginePowered()
    {
        return ((this.getProperties().getEngineType() == EngineType.NONE || this.hasEngine()) && (this.isControllingPassengerCreative() || this.isFueled()) && this.getDestroyedStage() < 9) && (!this.isKeyNeeded() || !this.getKeyStack().isEmpty());
    }

    public boolean canDrive()
    {
        return (!this.canChangeWheels() || this.hasWheelStack()) && this.isEnginePowered();
    }

    public boolean isOwner(PlayerEntity player)
    {
        return owner == null || player.getUUID().equals(owner);
    }

    public void setOwner(UUID owner)
    {
        this.owner = owner;
    }

    public boolean hasWheelStack()
    {
        return !this.getWheelStack().isEmpty();
    }

    public void setWheelStack(ItemStack wheels)
    {
        this.entityData.set(WHEEL_STACK, wheels);
    }

    public ItemStack getWheelStack()
    {
        return this.entityData.get(WHEEL_STACK);
    }

    public Optional<IWheelType> getWheelType()
    {
        return IWheelType.fromStack(this.entityData.get(WHEEL_STACK));
    }

    @Override
    public void onSyncedDataUpdated(DataParameter<?> key)
    {
        super.onSyncedDataUpdated(key);
        if(level.isClientSide)
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
        if(passenger instanceof PlayerEntity && this.level.isClientSide())
        {
            VehicleHelper.playVehicleSound((PlayerEntity) passenger, this);
        }
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier)
    {
        if(!this.disableFallDamage)
        {
            super.causeFallDamage(distance, damageMultiplier);
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
            NetworkHooks.openGui((ServerPlayerEntity) player, this, buffer -> buffer.writeInt(this.getId()));
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
        if(this.getProperties().getEngineType() != EngineType.NONE & !engine.isEmpty())
        {
            this.vehicleInventory.setItem(0, engine);
        }

        ItemStack wheel = this.getWheelStack();
        if(this.canChangeWheels() && !wheel.isEmpty())
        {
            this.vehicleInventory.setItem(1, wheel.copy());
        }

        this.vehicleInventory.addListener(this);
    }

    private void updateSlots()
    {
        if (!this.level.isClientSide())
        {
            ItemStack engine = this.vehicleInventory.getItem(0);
            if(engine.getItem() instanceof EngineItem)
            {
                EngineItem item = (EngineItem) engine.getItem();
                if(item.getEngineType() == this.getProperties().getEngineType())
                {
                    this.setEngine(true);
                    this.setEngineTier(item.getEngineTier());
                }
                else
                {
                    this.setEngine(false);
                }
            }
            else if(this.getProperties().getEngineType() != EngineType.NONE)
            {
                this.setEngine(false);
            }

            ItemStack wheel = this.vehicleInventory.getItem(1);
            if(this.canChangeWheels())
            {
                if(wheel.getItem() instanceof WheelItem)
                {
                    if(!this.hasWheelStack())
                    {
                        WheelItem wheelItem = (WheelItem) wheel.getItem();
                        this.level.playSound(null, this.blockPosition(), ModSounds.BLOCK_JACK_AIR_WRENCH_GUN.get(), SoundCategory.BLOCKS, 1.0F, 1.1F);
                        this.setWheelStack(wheel.copy());
                    }
                }
                else
                {
                    this.level.playSound(null, this.blockPosition(), ModSounds.BLOCK_JACK_AIR_WRENCH_GUN.get(), SoundCategory.BLOCKS, 1.0F, 0.8F);
                    this.setWheelStack(ItemStack.EMPTY);
                }
            }
        }
    }

    @Override
    public void containerChanged(IInventory inventory)
    {
        this.updateSlots();
    }

    @Override
    protected void onVehicleDestroyed(LivingEntity entity)
    {
        super.onVehicleDestroyed(entity);
        boolean isCreativeMode = entity instanceof PlayerEntity && ((PlayerEntity) entity).isCreative();
        if(!isCreativeMode && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS))
        {
            // Spawns the engine if the vehicle has one
            ItemStack engine = ItemLookup.getEngine(this);
            if(this.getProperties().getEngineType() != EngineType.NONE && !engine.isEmpty())
            {
                InventoryUtil.spawnItemStack(this.level, this.getX(), this.getY(), this.getZ(), engine);
            }

            // Spawns the key and removes the associated vehicle uuid
            ItemStack key = this.getKeyStack().copy();
            if(!key.isEmpty())
            {
                CommonUtils.getOrCreateStackTag(key).remove("VehicleId");
                InventoryUtil.spawnItemStack(this.level, this.getX(), this.getY(), this.getZ(), key);
            }

            // Spawns wheels if the vehicle has any
            ItemStack wheel = this.getWheelStack();
            if(this.canChangeWheels() && !wheel.isEmpty())
            {
                InventoryUtil.spawnItemStack(this.level, this.getX(), this.getY(), this.getZ(), wheel.copy());
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
                wheelY -= ((8 * 0.0625) / 2.0) * scale * wheel.getScaleY();

                /* Update the wheel position */
                Vector3d wheelVec = new Vector3d(wheelX, wheelY, wheelZ).yRot(-this.getModifiedRotationYaw() * 0.017453292F);
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
        if(this.hasWheelStack() && wheels != null)
        {
            Optional<IWheelType> optional = this.getWheelType();
            if(optional.isPresent())
            {
                int wheelCount = 0;
                IWheelType wheelType = optional.get();
                for(int i = 0; i < wheels.size(); i++)
                {
                    double wheelX = this.wheelPositions[i * 3];
                    double wheelY = this.wheelPositions[i * 3 + 1];
                    double wheelZ = this.wheelPositions[i * 3 + 2];
                    int x = MathHelper.floor(this.getX() + wheelX);
                    int y = MathHelper.floor(this.getY() + wheelY - 0.2D);
                    int z = MathHelper.floor(this.getZ() + wheelZ);
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = this.level.getBlockState(pos);
                    if(state.getMaterial() != Material.AIR)
                    {
                        if(state.getMaterial() == Material.TOP_SNOW || state.getMaterial() == Material.SNOW || (state.getBlock() == Blocks.GRASS_BLOCK && state.getValue(GrassBlock.SNOWY)))
                        {
                            wheelModifier += (1.0F - wheelType.getSnowMultiplier());
                        }
                        else if(!state.getMaterial().isSolid())
                        {
                            wheelModifier += (1.0F - wheelType.getRoadMultiplier());
                        }
                        else
                        {
                            wheelModifier += (1.0F - wheelType.getDirtMultiplier());
                        }
                        wheelCount++;
                    }
                }
                if(wheelCount > 0)
                {
                    wheelModifier /= (float) wheelCount;
                }
            }
        }
        return 1.0F - wheelModifier;
    }

    protected void updateGroundState()
    {
        if(this.hasWheelStack())
        {
            VehicleProperties properties = this.getProperties();
            List<Wheel> wheels = properties.getWheels();
            if(this.hasWheelStack() && wheels != null)
            {
                for(int i = 0; i < wheels.size(); i++)
                {
                    double wheelX = this.wheelPositions[i * 3];
                    double wheelY = this.wheelPositions[i * 3 + 1];
                    double wheelZ = this.wheelPositions[i * 3 + 2];
                    int x = MathHelper.floor(this.getX() + wheelX);
                    int y = MathHelper.floor(this.getY() + wheelY - 0.2D);
                    int z = MathHelper.floor(this.getZ() + wheelZ);
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = this.level.getBlockState(pos);
                    if(!state.getCollisionShape(this.level, pos).isEmpty())
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

    protected boolean canCharge()
    {
        return false;
    }

    protected void releaseCharge()
    {
        this.boosting = true;
        this.boostTimer = 20;
        this.speedMultiplier = 0.5F;
    }

    @Override
    public ItemStack getPickedResult(RayTraceResult target)
    {
        EngineTier engineTier = null;
        if(this.hasEngine())
        {
            engineTier = this.getEngineTier();
        }

        ItemStack wheel = ItemStack.EMPTY;
        if(this.hasWheelStack())
        {
            wheel = this.getWheelStack();
        }

        ResourceLocation entityId = this.getType().getRegistryName();
        if(entityId != null)
        {
            return VehicleCrateBlock.create(entityId, this.getColor(), engineTier, wheel);
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
        FORWARD, NONE, REVERSE,
        CHARGING;

        public static AccelerationDirection fromEntity(LivingEntity entity)
        {
            if(entity.zza > 0)
            {
                return FORWARD;
            }
            else if(entity.zza < 0)
            {
                return REVERSE;
            }
            return NONE;
        }
    }

    public enum FuelPortType
    {
        DEFAULT(SpecialModels.FUEL_DOOR_CLOSED, SpecialModels.FUEL_DOOR_OPEN, ModSounds.ENTITY_VEHICLE_FUEL_PORT_LARGE_OPEN.get(), 0.25F, 0.6F, ModSounds.ENTITY_VEHICLE_FUEL_PORT_LARGE_CLOSE.get(), 0.12F, 0.6F),
        SMALL(SpecialModels.SMALL_FUEL_DOOR_CLOSED, SpecialModels.SMALL_FUEL_DOOR_OPEN, ModSounds.ENTITY_VEHICLE_FUEL_PORT_SMALL_OPEN.get(), 0.4F, 0.6F, ModSounds.ENTITY_VEHICLE_FUEL_PORT_SMALL_CLOSE.get(), 0.3F, 0.6F);

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
            VehicleHelper.playSound(this.openSound, this.openVolume, this.openPitch);
        }

        @OnlyIn(Dist.CLIENT)
        public void playCloseSound()
        {
            VehicleHelper.playSound(this.closeSound, this.closeVolume, this.closePitch);
        }
    }
}
