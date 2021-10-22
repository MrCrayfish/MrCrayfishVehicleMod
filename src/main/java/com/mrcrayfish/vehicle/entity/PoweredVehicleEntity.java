package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.obfuscate.common.data.SyncedPlayerData;
import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.block.VehicleCrateBlock;
import com.mrcrayfish.vehicle.client.VehicleHelper;
import com.mrcrayfish.vehicle.common.SurfaceHelper;
import com.mrcrayfish.vehicle.common.entity.Transform;
import com.mrcrayfish.vehicle.entity.properties.PoweredProperties;
import com.mrcrayfish.vehicle.entity.properties.VehicleProperties;
import com.mrcrayfish.vehicle.init.ModDataKeys;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.inventory.container.EditVehicleContainer;
import com.mrcrayfish.vehicle.item.EngineItem;
import com.mrcrayfish.vehicle.item.JerryCanItem;
import com.mrcrayfish.vehicle.item.WheelItem;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.datasync.VehicleDataValue;
import com.mrcrayfish.vehicle.network.message.MessageHandbrake;
import com.mrcrayfish.vehicle.network.message.MessageHorn;
import com.mrcrayfish.vehicle.network.message.MessageThrottle;
import com.mrcrayfish.vehicle.network.message.MessageTurnAngle;
import com.mrcrayfish.vehicle.tileentity.GasPumpTankTileEntity;
import com.mrcrayfish.vehicle.tileentity.GasPumpTileEntity;
import com.mrcrayfish.vehicle.util.CommonUtils;
import com.mrcrayfish.vehicle.util.InventoryUtil;
import net.minecraft.block.BlockState;
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
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Author: MrCrayfish
 */
public abstract class PoweredVehicleEntity extends VehicleEntity implements IInventoryChangedListener, INamedContainerProvider
{
    protected static final int MAX_WHEELIE_TICKS = 10;

    protected static final DataParameter<Float> THROTTLE = EntityDataManager.defineId(PoweredVehicleEntity.class, DataSerializers.FLOAT);
    protected static final DataParameter<Boolean> HANDBRAKE = EntityDataManager.defineId(PoweredVehicleEntity.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Float> STEERING_ANGLE = EntityDataManager.defineId(PoweredVehicleEntity.class, DataSerializers.FLOAT);
    protected static final DataParameter<Boolean> HORN = EntityDataManager.defineId(PoweredVehicleEntity.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Float> CURRENT_FUEL = EntityDataManager.defineId(PoweredVehicleEntity.class, DataSerializers.FLOAT);
    protected static final DataParameter<Boolean> NEEDS_KEY = EntityDataManager.defineId(PoweredVehicleEntity.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<ItemStack> KEY_STACK = EntityDataManager.defineId(PoweredVehicleEntity.class, DataSerializers.ITEM_STACK);
    protected static final DataParameter<ItemStack> ENGINE_STACK = EntityDataManager.defineId(PoweredVehicleEntity.class, DataSerializers.ITEM_STACK);

    // Sensitive variables used for physics
    private final VehicleDataValue<Float> throttle = new VehicleDataValue<>(this, THROTTLE);
    private final VehicleDataValue<Boolean> handbrake = new VehicleDataValue<>(this, HANDBRAKE);
    private final VehicleDataValue<Float> steeringAngle = new VehicleDataValue<>(this, STEERING_ANGLE);

    protected UUID owner;
    protected float speedMultiplier;
    protected boolean boosting;
    protected int boostTimer;
    protected float boostStrength;
    protected boolean launching;
    protected int launchingTimer;
    protected boolean disableFallDamage;
    protected boolean charging;
    protected float chargingAmount;
    private double[] wheelPositions;
    private boolean fueling;
    protected Vector3d motion = Vector3d.ZERO;
    private Inventory vehicleInventory;

    @OnlyIn(Dist.CLIENT)
    protected float renderWheelAngle;
    @OnlyIn(Dist.CLIENT)
    protected float prevRenderWheelAngle;
    @OnlyIn(Dist.CLIENT)
    protected float enginePitch;
    @OnlyIn(Dist.CLIENT)
    protected float engineVolume;

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
        this.entityData.define(THROTTLE, 0F);
        this.entityData.define(HANDBRAKE, false);
        this.entityData.define(STEERING_ANGLE, 0F);
        this.entityData.define(HORN, false);
        this.entityData.define(CURRENT_FUEL, 0F);
        this.entityData.define(NEEDS_KEY, false);
        this.entityData.define(KEY_STACK, ItemStack.EMPTY);
        this.entityData.define(ENGINE_STACK, ItemStack.EMPTY);
    }

    public final SoundEvent getEngineSound()
    {
        return ForgeRegistries.SOUND_EVENTS.getValue(this.getPoweredProperties().getEngineSound());
    }

    public final SoundEvent getHornSound()
    {
        return ForgeRegistries.SOUND_EVENTS.getValue(this.getPoweredProperties().getHornSound());
    }

    public void playFuelPortOpenSound()
    {
        if(!this.fueling)
        {
            this.getFuelFillerType().playOpenSound();
            this.fueling = true;
        }
    }

    public void playFuelPortCloseSound()
    {
        if(this.fueling)
        {
            this.getFuelFillerType().playCloseSound();
            this.fueling = false;
        }
    }

    public final float getMinEnginePitch()
    {
        return this.getPoweredProperties().getMinEnginePitch();
    }

    public final float getMaxEnginePitch()
    {
        return this.getPoweredProperties().getMaxEnginePitch();
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

            stack.setAmount(this.addEnergy(stack.getAmount()));
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
        transferAmount = (int) Math.min(Math.floor(this.getEnergyCapacity() - this.getCurrentEnergy()), transferAmount);
        handler.drain(transferAmount, IFluidHandler.FluidAction.EXECUTE);
        this.addEnergy(transferAmount);
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
        if(this.level.isClientSide())
        {
            this.onClientUpdate();
        }

        Entity controllingPassenger = this.getControllingPassenger();

        /* If there driver, create particles */
        if(controllingPassenger != null)
        {
            this.createParticles();
        }
        else
        {
            this.setThrottle(0F);
            this.steeringAngle.set(this, this.steeringAngle.get(this) * 0.85F);
        }

        /* Handle the current speed of the vehicle based on rider's forward movement */
        this.updateTurning();
        this.onVehicleTick();

        /* Updates the vehicle motion */
        this.updateVehicleMotion();

        /* Updates the rotation and fixes the old rotation */
        this.setRot(this.yRot, this.xRot);
        double deltaRot = this.yRotO - this.yRot;
        this.yRotO += (deltaRot < -180) ? 360F : (deltaRot >= 180) ? -360F : 0F;

        this.updateWheelPositions();

        // Move vehicle
        this.move(MoverType.SELF, this.getDeltaMovement().add(this.motion));

        /* Reduces the motion and speed multiplier */
        if(this.onGround)
        {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.75, 0.0, 0.75));
        }
        else
        {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.98, 1.0, 0.98));
        }

        if(this.boostTimer > 0 && this.getThrottle() > 0)
        {
            this.boostTimer--;
        }
        else
        {
            this.boostTimer = 0;
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

        //TODO improve fuel consumption logic
        if(this.requiresEnergy() && controllingPassenger instanceof PlayerEntity && !((PlayerEntity) controllingPassenger).isCreative() && this.isEnginePowered())
        {
            float currentFuel = this.getCurrentEnergy();
            currentFuel -= this.getEnergyConsumptionPerTick() * Config.SERVER.energyConsumptionFactor.get();
            if(currentFuel < 0F) currentFuel = 0F;
            this.setCurrentEnergy(currentFuel);
        }

        if(this.level.isClientSide())
        {
            this.updateEngineSound();
        }
    }

    protected void onVehicleTick() {}

    protected abstract void updateVehicleMotion();

    public final FuelFillerType getFuelFillerType()
    {
        return this.getPoweredProperties().getFuelFillerType();
    }

    protected void updateTurning() {}

    protected boolean showWheelParticles()
    {
        return this.getThrottle() > 0 || this.charging || this.boosting;
    }

    protected boolean showTyreSmokeParticles()
    {
        return this.charging || this.boosting;
    }

    public void createParticles()
    {
        if(this.showWheelParticles())
        {
            /* Uses the same logic when rendering wheels to determine the position, then spawns
             * particles at the contact of the wheel and the ground. */
            VehicleProperties properties = this.getProperties();
            if(properties.getWheels() != null)
            {
                double[] wheelPositions = this.getWheelPositions();
                List<Wheel> wheels = properties.getWheels();
                for(int i = 0; i < wheels.size(); i++)
                {
                    Wheel wheel = wheels.get(i);
                    if(!wheel.shouldSpawnParticles())
                        continue;
                    /* Gets the block under the wheel and spawns a particle */
                    double wheelX = wheelPositions[i * 3];
                    double wheelY = wheelPositions[i * 3 + 1];
                    double wheelZ = wheelPositions[i * 3 + 2];
                    int x = MathHelper.floor(this.getX() + wheelX);
                    int y = MathHelper.floor(this.getY() + wheelY - 0.2D);
                    int z = MathHelper.floor(this.getZ() + wheelZ);
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = this.level.getBlockState(pos);
                    if(state.getMaterial() != Material.AIR && state.getMaterial().isSolid())
                    {
                        Vector3d dirVec = this.calculateViewVector(this.xRot, this.yRot + 180F).add(0, this.charging ? 0.5 : 1.0, 0);
                        if(this.charging)
                        {
                            dirVec = dirVec.scale(this.chargingAmount * this.getEnginePower() / 3F);
                        }
                        if(this.level.isClientSide())
                        {
                            double wheelWorldX = this.getX() + wheelX;
                            double wheelWorldY = this.getY() + wheelY;
                            double wheelWorldZ = this.getZ() + wheelZ;
                            VehicleHelper.spawnWheelParticle(pos, state, wheelWorldX, wheelWorldY, wheelWorldZ, dirVec);
                            if(this.showTyreSmokeParticles() && SurfaceHelper.getSurfaceTypeForMaterial(state.getMaterial()) == SurfaceHelper.SurfaceType.SOLID)
                            {
                                VehicleHelper.spawnSmokeParticle(wheelWorldX, wheelWorldY, wheelWorldZ, dirVec.multiply(0.03 * this.random.nextFloat(), 0.03, 0.03 * this.random.nextFloat()));
                            }
                        }
                    }
                }
            }
        }

        if(this.shouldShowExhaustFumes() && this.canDrive() && this.tickCount % 2 == 0)
        {
            //TODO maybe add more control of this
            Vector3d fumePosition = this.getExhaustFumesPosition().scale(0.0625).yRot(-this.yRot * 0.017453292F);
            this.level.addParticle(ParticleTypes.SMOKE, this.getX() + fumePosition.x, this.getY() + fumePosition.y, this.getZ() + fumePosition.z, -this.getDeltaMovement().x, 0.0D, -this.getDeltaMovement().z);
            if(this.charging && this.isMoving())
            {
                this.level.addParticle(ParticleTypes.CRIT, this.getX() + fumePosition.x, this.getY() + fumePosition.y, this.getZ() + fumePosition.z, -this.getDeltaMovement().x, 0.0D, -this.getDeltaMovement().z);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void onClientUpdate()
    {
        this.prevRenderWheelAngle = this.renderWheelAngle;

        Entity entity = this.getControllingPassenger();
        if(entity instanceof LivingEntity && entity.equals(Minecraft.getInstance().player))
        {
            float throttle = VehicleHelper.getThrottle((LivingEntity) entity);
            if(throttle != this.getThrottle())
            {
                this.setThrottle(throttle);
                PacketHandler.getPlayChannel().sendToServer(new MessageThrottle(throttle));
            }

            boolean handbraking = VehicleHelper.isHandbraking();
            if(this.isHandbraking() != handbraking)
            {
                this.setHandbraking(handbraking);
                PacketHandler.getPlayChannel().sendToServer(new MessageHandbrake(handbraking));
            }

            if(this.hasHorn())
            {
                boolean horn = VehicleHelper.isHonking();
                this.setHorn(horn);
                PacketHandler.getPlayChannel().sendToServer(new MessageHorn(horn));
            }

            float steeringAngle = VehicleHelper.getSteeringAngle(this);
            this.setSteeringAngle(steeringAngle);
            PacketHandler.getPlayChannel().sendToServer(new MessageTurnAngle(steeringAngle));
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
        if(compound.contains("EngineStack", Constants.NBT.TAG_COMPOUND))
        {
            this.setEngineStack(ItemStack.of(compound.getCompound("EngineStack")));
        }
        if(compound.contains("StepHeight", Constants.NBT.TAG_FLOAT))
        {
            this.maxUpStep = compound.getFloat("StepHeight");
        }
        if(compound.contains("CurrentFuel", Constants.NBT.TAG_FLOAT))
        {
            this.setCurrentEnergy(compound.getFloat("CurrentFuel"));
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
        CommonUtils.writeItemStackToTag(compound, "EngineStack", this.getEngineStack());
        compound.putFloat("AccelerationSpeed", this.getAccelerationSpeed());
        compound.putFloat("MaxSteeringAngle", this.getMaxSteeringAngle());
        compound.putFloat("StepHeight", this.maxUpStep);
        compound.putBoolean("RequiresFuel", this.requiresEnergy());
        compound.putFloat("CurrentFuel", this.getCurrentEnergy());
        compound.putFloat("FuelCapacity", this.getEnergyCapacity());
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
            if(seatIndex != -1 && properties.getSeats().get(seatIndex).isDriver())
            {
                return passenger;
            }
        }
        return null;
    }

    //TODO test
    public boolean isMoving()
    {
        return this.motion.length() != 0;
    }

    //TODO remove these
    public float getAccelerationSpeed()
    {
        return this.entityData.get(THROTTLE);
    }

    public double getSpeed()
    {
        return Math.sqrt(Math.pow(this.motion.x, 2) + Math.pow(this.motion.z, 2)) * 20;
    }

    public void setSteeringAngle(float steeringAngle)
    {
        this.steeringAngle.set(this, steeringAngle);
    }

    public float getSteeringAngle()
    {
        return this.steeringAngle.get(this);
    }

    public void setThrottle(float power)
    {
        this.throttle.set(this, MathHelper.clamp(power, -1.0F, 1.0F));
    }

    public float getThrottle()
    {
        return this.throttle.get(this);
    }

    public final float getMaxSteeringAngle()
    {
        return this.getPoweredProperties().getMaxSteeringAngle();
    }

    public boolean hasEngine()
    {
        return !this.getEngineStack().isEmpty();
    }

    public void setEngineStack(ItemStack engine)
    {
        this.entityData.set(ENGINE_STACK, engine);
    }

    public ItemStack getEngineStack()
    {
        return this.entityData.get(ENGINE_STACK);
    }

    public Optional<IEngineTier> getEngineTier()
    {
        return IEngineTier.fromStack(this.getEngineStack());
    }

    @OnlyIn(Dist.CLIENT)
    public final boolean shouldRenderEngine()
    {
        return this.getPoweredProperties().isRenderEngine();
    }

    @OnlyIn(Dist.CLIENT)
    public boolean shouldRenderFuelPort()
    {
        return true;
    }

    public final Vector3d getExhaustFumesPosition()
    {
        return this.getPoweredProperties().getExhaustFumesPosition();
    }

    public final boolean shouldShowExhaustFumes()
    {
        return this.getPoweredProperties().showExhaustFumes();
    }

    public void setHorn(boolean activated)
    {
        if(this.hasHorn())
        {
            this.entityData.set(HORN, activated);
        }
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

    public final boolean requiresEnergy()
    {
        return this.getPoweredProperties().requiresEnergy() && Config.SERVER.fuelEnabled.get();
    }

    public boolean isFueled()
    {
        return !this.requiresEnergy() || this.isControllingPassengerCreative() || this.getCurrentEnergy() > 0F;
    }

    public void setCurrentEnergy(float fuel)
    {
        this.entityData.set(CURRENT_FUEL, fuel);
    }

    public float getCurrentEnergy()
    {
        return this.entityData.get(CURRENT_FUEL);
    }

    public final float getEnergyCapacity()
    {
        return this.getPoweredProperties().getEnergyCapacity();
    }

    public final float getEnergyConsumptionPerTick()
    {
        return this.getPoweredProperties().getEnergyConsumptionPerTick();
    }

    public int addEnergy(int amount)
    {
        if(!this.requiresEnergy())
            return amount;
        float currentEnergy = this.getCurrentEnergy();
        currentEnergy += amount;
        int remaining = Math.max(0, Math.round(currentEnergy - this.getEnergyCapacity()));
        currentEnergy = Math.min(currentEnergy, this.getEnergyCapacity());
        this.setCurrentEnergy(currentEnergy);
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
            Vector3d keyHole = this.getWorldPosition(this.getIgnitionTransform(), 1.0F);
            this.level.addFreshEntity(new ItemEntity(this.level, keyHole.x, keyHole.y, keyHole.z, this.getKeyStack()));
            this.setKeyStack(ItemStack.EMPTY);
        }
    }

    public final boolean isLockable()
    {
        return this.getPoweredProperties().canLockWithKey();
    }

    public boolean isEnginePowered()
    {
        return ((this.getEngineType() == EngineType.NONE || this.hasEngine()) && (this.isControllingPassengerCreative() || this.isFueled()) && this.getDestroyedStage() < 9) && (!this.isKeyNeeded() || !this.getKeyStack().isEmpty());
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

    public void setHandbraking(boolean handbraking)
    {
        this.handbrake.set(this, handbraking);
    }

    public boolean isHandbraking()
    {
        return this.handbrake.get(this);
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

        ItemStack engine = this.getEngineStack();
        if(this.getEngineType() != EngineType.NONE & !engine.isEmpty())
        {
            this.vehicleInventory.setItem(0, engine.copy());
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
        if(!this.level.isClientSide())
        {
            ItemStack engine = this.vehicleInventory.getItem(0);
            if(engine.getItem() instanceof EngineItem)
            {
                EngineItem item = (EngineItem) engine.getItem();
                if(item.getEngineType() == this.getEngineType())
                {
                    this.setEngineStack(engine.copy());
                }
                else
                {
                    this.setEngineStack(ItemStack.EMPTY);
                }
            }
            else if(this.getEngineType() != EngineType.NONE)
            {
                this.setEngineStack(ItemStack.EMPTY);
            }

            ItemStack wheel = this.vehicleInventory.getItem(1);
            if(this.canChangeWheels())
            {
                if(wheel.getItem() instanceof WheelItem)
                {
                    if(!this.hasWheelStack())
                    {
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
            ItemStack engine = this.getEngineStack();
            if(this.getEngineType() != EngineType.NONE && !engine.isEmpty())
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

    private void updateWheelPositions()
    {
        VehicleProperties properties = this.getProperties();
        if(properties.getWheels() != null)
        {
            List<Wheel> wheels = properties.getWheels();

            // Fixes game crashing if adding wheels when reloading vehicle properties json
           /* if(this.wheelPositions.length != wheels.size() * 3)
            {
                this.wheelPositions = new double[wheels.size() * 3];
            }*/

            double[] wheelPositions = this.getWheelPositions();
            for(int i = 0; i < wheels.size(); i++)
            {
                Wheel wheel = wheels.get(i);

                Transform bodyPosition = properties.getBodyTransform();
                double wheelX = bodyPosition.getX();
                double wheelY = bodyPosition.getY();
                double wheelZ = bodyPosition.getZ();

                double scale = bodyPosition.getScale();

                /* Applies axle and wheel offsets */
                wheelY += (properties.getWheelOffset() * 0.0625F) * scale;

                /* Wheels Translations */
                wheelX += ((wheel.getOffsetX() * 0.0625) * wheel.getSide().getOffset()) * scale;
                wheelY += (wheel.getOffsetY() * 0.0625) * scale;
                wheelZ += (wheel.getOffsetZ() * 0.0625) * scale;
                wheelX += ((((wheel.getWidth() * wheel.getScaleX()) / 2) * 0.0625) * wheel.getSide().getOffset()) * scale;

                /* Offsets the position to the wheel contact on the ground */
                wheelY -= ((8 * 0.0625) / 2.0) * scale * wheel.getScaleY();

                /* Update the wheel position */
                Vector3d wheelVec = new Vector3d(wheelX, wheelY, wheelZ).yRot(-this.yRot * 0.017453292F);
                wheelPositions[i * 3] = wheelVec.x;
                wheelPositions[i * 3 + 1] = wheelVec.y;
                wheelPositions[i * 3 + 2] = wheelVec.z;
            }
        }
    }

    protected void releaseCharge(float strength)
    {
        this.boosting = true;
        this.boostStrength = MathHelper.clamp(strength, 0.0F, 1.0F);
        this.boostTimer = (int) (20 * this.boostStrength);
        this.speedMultiplier = 0.5F * this.boostStrength;
    }

    @Override
    public ItemStack getPickedResult(RayTraceResult target)
    {
        ItemStack engine = ItemStack.EMPTY;
        if(this.hasEngine())
        {
            engine = this.getEngineStack();
        }

        ItemStack wheel = ItemStack.EMPTY;
        if(this.hasWheelStack())
        {
            wheel = this.getWheelStack();
        }

        ResourceLocation entityId = this.getType().getRegistryName();
        if(entityId != null)
        {
            return VehicleCrateBlock.create(entityId, this.getColor(), engine, wheel);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return this.getName();
    }

    public float getSpeedMultiplier()
    {
        return speedMultiplier;
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity)
    {
        return new EditVehicleContainer(windowId, this.getVehicleInventory(), this, playerEntity, playerInventory);
    }

    public double[] getWheelPositions()
    {
        /* Updates the wheel positions as reloading vehicle properties
         * could cause a crash if wheels are added or removed. */
        if(this.wheelPositions == null || this.wheelPositions.length != this.getProperties().getWheels().size() * 3)
        {
            this.wheelPositions = new double[this.getProperties().getWheels().size() * 3];
        }
        return this.wheelPositions;
    }

    public float getBoostStrength()
    {
        return this.boostStrength;
    }

    public void setSpeedMultiplier(float speedMultiplier)
    {
        this.speedMultiplier = speedMultiplier;
    }

    public final IEngineType getEngineType()
    {
        return this.getPoweredProperties().getEngineType();
    }

    public final float getEnginePower()
    {
        return this.getPoweredProperties().getEnginePower();
    }

    public final Transform getIgnitionTransform()
    {
        return this.getPoweredProperties().getIgnitionTransform();
    }

    public final Vector3d getFrontAxleOffset()
    {
        return this.getPoweredProperties().getFrontAxleOffset();
    }

    public final Vector3d getRearAxleOffset()
    {
        return this.getPoweredProperties().getRearAxleOffset();
    }

    public final boolean hasHorn()
    {
        return this.getPoweredProperties().hasHorn();
    }

    protected final PoweredProperties getPoweredProperties()
    {
        return this.getProperties().getExtended(PoweredProperties.class);
    }

    @OnlyIn(Dist.CLIENT)
    protected void updateEngineSound()
    {
        if(this.charging)
        {
            this.enginePitch = this.getMinEnginePitch() + (this.getMaxEnginePitch() - this.getMinEnginePitch()) * 0.75F * this.chargingAmount;
            return;
        }

        this.enginePitch = this.getMinEnginePitch() + (this.getMaxEnginePitch() - this.getMinEnginePitch()) * (float) Math.abs(this.getSpeed() / 25F);
        this.engineVolume = this.getControllingPassenger() != null && this.isEnginePowered() ? 1.0F : 0.001F;
    }

    @OnlyIn(Dist.CLIENT)
    public float getEnginePitch()
    {
        return this.enginePitch;
    }

    @OnlyIn(Dist.CLIENT)
    public float getEngineVolume()
    {
        return this.engineVolume;
    }

    @OnlyIn(Dist.CLIENT)
    public float getRenderWheelAngle(float partialTicks)
    {
        return this.prevRenderWheelAngle + (this.renderWheelAngle - this.prevRenderWheelAngle) * partialTicks;
    }

}
