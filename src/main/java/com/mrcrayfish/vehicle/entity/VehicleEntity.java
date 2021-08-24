package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.obfuscate.common.data.SyncedPlayerData;
import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.block.VehicleCrateBlock;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.common.SeatTracker;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.crafting.WorkstationRecipe;
import com.mrcrayfish.vehicle.crafting.WorkstationRecipes;
import com.mrcrayfish.vehicle.init.ModDataKeys;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.item.SprayCanItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SAnimateHandPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Author: MrCrayfish
 */
public abstract class VehicleEntity extends Entity implements IEntityAdditionalSpawnData, EntityRayTracer.IEntityRayTraceable
{
    public static final int[] DYE_TO_COLOR = new int[] {16383998, 16351261, 13061821, 3847130, 16701501, 8439583, 15961002, 4673362, 10329495, 1481884, 8991416, 3949738, 8606770, 6192150, 11546150, 1908001};

    protected static final DataParameter<Integer> COLOR = EntityDataManager.defineId(VehicleEntity.class, DataSerializers.INT);
    private static final DataParameter<Integer> TIME_SINCE_HIT = EntityDataManager.defineId(VehicleEntity.class, DataSerializers.INT);
    private static final DataParameter<Float> MAX_HEALTH = EntityDataManager.defineId(VehicleEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> HEALTH = EntityDataManager.defineId(VehicleEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> TRAILER = EntityDataManager.defineId(VehicleEntity.class, DataSerializers.INT);

    protected UUID trailerId;
    protected TrailerEntity trailer = null;
    private int searchDelay = 0;

    protected int lerpSteps;
    protected double lerpX;
    protected double lerpY;
    protected double lerpZ;
    protected double lerpYaw;
    protected double lerpPitch;

    protected SeatTracker seatTracker;

    public VehicleEntity(EntityType<?> entityType, World worldIn)
    {
        super(entityType, worldIn);
        this.seatTracker = new SeatTracker(this);
    }

    @Override
    protected void defineSynchedData()
    {
        this.entityData.define(TIME_SINCE_HIT, 0);
        this.entityData.define(MAX_HEALTH, 100F);
        this.entityData.define(HEALTH, 100F);
        this.entityData.define(COLOR, 16383998);
        this.entityData.define(TRAILER, -1);

        if(this.level.isClientSide)
        {
            this.onClientInit();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void onClientInit() {}

    /* Overridden to prevent odd step sound when driving vehicles. Ain't no subclasses getting
     * the ability to override this. */
    @Override
    protected final void playStepSound(BlockPos pos, BlockState blockIn) {}

    @Override //TODO hmmmmmmmm
    public AxisAlignedBB getBoundingBoxForCulling()
    {
        return this.getBoundingBox().inflate(1);
    }

    @Override
    public ActionResultType interact(PlayerEntity player, Hand hand)
    {
        if(!level.isClientSide && !player.isCrouching())
        {
            int trailerId = SyncedPlayerData.instance().get(player, ModDataKeys.TRAILER);
            if(trailerId != -1)
            {
                if(this.getVehicle() == null && this.canTowTrailer() && this.getTrailer() == null)
                {
                    Entity entity = level.getEntity(trailerId);
                    if(entity instanceof TrailerEntity && entity != this)
                    {
                        TrailerEntity trailer = (TrailerEntity) entity;
                        this.setTrailer(trailer);
                        SyncedPlayerData.instance().set(player, ModDataKeys.TRAILER, -1);
                    }
                }
                return ActionResultType.SUCCESS;
            }

            ItemStack heldItem = player.getItemInHand(hand);
            if(heldItem.getItem() instanceof SprayCanItem)
            {
                if(this.canBeColored())
                {
                    CompoundNBT compound = heldItem.getTag();
                    if(compound != null)
                    {
                        if(!compound.contains("RemainingSprays", Constants.NBT.TAG_INT))
                        {
                            compound.putInt("RemainingSprays", ModItems.SPRAY_CAN.get().getCapacity(heldItem));
                        }
                        int remainingSprays = compound.getInt("RemainingSprays");
                        if(compound.contains("Color", Constants.NBT.TAG_INT) && remainingSprays > 0)
                        {
                            int color = compound.getInt("Color");
                            if(this.getColor() != color)
                            {
                                this.setColor(compound.getInt("Color"));
                                player.level.playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.ITEM_SPRAY_CAN_SPRAY.get(), SoundCategory.PLAYERS, 1.0F, 1.0F);
                                compound.putInt("RemainingSprays", remainingSprays - 1);
                            }
                        }
                    }
                }
                return ActionResultType.SUCCESS;
            }
            else if(heldItem.getItem() == ModItems.HAMMER.get() && this.getVehicle() instanceof EntityJack)
            {
                if(this.getHealth() < this.getMaxHealth())
                {
                    heldItem.hurtAndBreak(1, player, playerEntity -> player.broadcastBreakEvent(hand));
                    this.setHealth(this.getHealth() + 5F);
                    this.level.playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.ENTITY_VEHICLE_THUD.get(), SoundCategory.PLAYERS, 1.0F, 0.8F + 0.4F * random.nextFloat());
                    player.swing(hand);
                    if(player instanceof ServerPlayerEntity)
                    {
                        ((ServerPlayerEntity) player).connection.send(new SAnimateHandPacket(player, hand == Hand.MAIN_HAND ? 0 : 3));
                    }
                    if(this.getHealth() == this.getMaxHealth())
                    {
                        if(level instanceof ServerWorld)
                        {
                            //TODO send as single packet instead of multiple
                            int count = (int) (50 * (this.getBbWidth() * this.getBbHeight()));
                            for(int i = 0; i < count; i++)
                            {
                                double width = this.getBbWidth() * 2;
                                double height = this.getBbHeight() * 1.5;

                                Vector3d heldOffset = this.getProperties().getHeldOffset().yRot((float) Math.toRadians(-this.yRot));
                                double x = this.getX() + width * random.nextFloat() - width / 2 + heldOffset.z * 0.0625;
                                double y = this.getY() + height * random.nextFloat();
                                double z = this.getZ() + width * random.nextFloat() - width / 2 + heldOffset.x * 0.0625;

                                double d0 = random.nextGaussian() * 0.02D;
                                double d1 = random.nextGaussian() * 0.02D;
                                double d2 = random.nextGaussian() * 0.02D;
                                ((ServerWorld) this.level).sendParticles(ParticleTypes.HAPPY_VILLAGER, x, y, z, 1, d0, d1, d2, 1.0);
                            }
                        }
                        this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_LEVELUP, SoundCategory.PLAYERS, 1.0F, 1.5F);
                    }
                }
                return ActionResultType.SUCCESS;
            }
            else if(this.canRide(player))
            {
                int seatIndex = this.seatTracker.getClosestAvailableSeatToPlayer(player);
                if(seatIndex != -1)
                {
                    if(player.startRiding(this))
                    {
                        this.getSeatTracker().setSeatIndex(seatIndex, player.getUUID());
                    }
                }
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT compound)
    {
        if(compound.contains("Color", Constants.NBT.TAG_INT_ARRAY))
        {
            int[] c = compound.getIntArray("Color");
            if(c.length == 3)
            {
                int color = ((c[0] & 0xFF) << 16) | ((c[1] & 0xFF) << 8) | ((c[2] & 0xFF));
                this.setColor(color);
            }
        }
        else if(compound.contains("Color", Constants.NBT.TAG_INT))
        {
            int index = compound.getInt("Color");
            if(index >= 0 && index < DYE_TO_COLOR.length)
            {
                this.setColor(DYE_TO_COLOR[index]);
            }
            compound.remove("Color");
        }
        if(compound.contains("MaxHealth", Constants.NBT.TAG_FLOAT))
        {
            this.setMaxHealth(compound.getFloat("MaxHealth"));
        }
        if(compound.contains("Health", Constants.NBT.TAG_FLOAT))
        {
            this.setHealth(compound.getFloat("Health"));
        }
        if(compound.hasUUID("Trailer"))
        {
            this.trailerId = compound.getUUID("Trailer");
        }
        if(compound.contains("SeatTracker", Constants.NBT.TAG_COMPOUND))
        {
            this.seatTracker.read(compound.getCompound("SeatTracker"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compound)
    {
        compound.putIntArray("Color", this.getColorRGB());
        compound.putFloat("MaxHealth", this.getMaxHealth());
        compound.putFloat("Health", this.getHealth());

        //TODO make it save the entity
        if(this.trailerId != null)
        {
            compound.putUUID("Trailer", this.trailerId);
        }

        compound.put("SeatTracker", this.seatTracker.write());
    }

    @Override
    public void tick()
    {
        if(this.getTimeSinceHit() > 0)
        {
            this.setTimeSinceHit(this.getTimeSinceHit() - 1);
        }

        /*this.prevPosX = this.getPosX();
        this.prevPosY = this.getPosY();
        this.prevPosZ = this.getPosZ();*/

        if(!this.level.isClientSide)
        {
            if(this.searchDelay <= 0)
            {
                if(this.trailer != null)
                {
                    /* Updates periodically to ensure the client knows the vehicle/trailer connection.
                     * There is often problems on loading worlds that it doesn't sync correctly, so this
                     * is the fix. */
                    this.entityData.set(TRAILER, trailer.getId());
                    this.trailer.getEntityData().set(TrailerEntity.PULLING_ENTITY, this.getId());
                    this.searchDelay = Config.SERVER.trailerSyncCooldown.get();
                }
                else
                {
                    this.findTrailer();
                }
            }
            else
            {
                this.searchDelay--;
            }
        }

        if(this.level.isClientSide)
        {
            int entityId = this.entityData.get(TRAILER);
            if(entityId != -1)
            {
                Entity entity = this.level.getEntity(this.entityData.get(TRAILER));
                if(entity instanceof TrailerEntity)
                {
                    this.trailer = (TrailerEntity) entity;
                    this.trailerId = trailer.getUUID();
                }
                else if(this.trailer != null)
                {
                    this.trailer = null;
                    this.trailerId = null;
                }
            }
            else if(this.trailer != null)
            {
                this.trailer = null;
                this.trailerId = null;
            }
        }

        if(!this.level.isClientSide && this.trailer != null && (!this.trailer.isAlive() || this.trailer.getPullingEntity() != this))
        {
            this.setTrailer(null);
        }

        super.tick();
        this.tickLerp();
        this.onUpdateVehicle();
    }

    private void findTrailer()
    {
        if(!this.level.isClientSide && this.trailerId != null && this.trailer == null)
        {
            ServerWorld server = (ServerWorld) this.level;
            Entity entity = server.getEntity(this.trailerId);
            if(entity instanceof TrailerEntity)
            {
                this.setTrailer((TrailerEntity) entity);
                return;
            }
            this.trailerId = null;
        }
    }

    protected abstract void onUpdateVehicle();

    @Override
    public boolean hurt(DamageSource source, float amount)
    {
        if(this.isInvulnerableTo(source))
        {
            return false;
        }
        else if(!this.level.isClientSide && this.isAlive())
        {
            Entity trueSource = source.getEntity();
            if(source instanceof IndirectEntityDamageSource && trueSource != null && this.hasPassenger(trueSource))
            {
                return false;
            }
            else
            {
                if(Config.SERVER.vehicleDamage.get())
                {
                    this.setTimeSinceHit(10);
                    this.setHealth(this.getHealth() - amount);
                }
                boolean isCreativeMode = trueSource instanceof PlayerEntity && ((PlayerEntity) trueSource).isCreative();
                if(isCreativeMode || this.getHealth() < 0.0F)
                {
                    this.onVehicleDestroyed((LivingEntity) trueSource);
                    this.remove();
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
    public boolean causeFallDamage(float distance, float damageMultiplier)
    {
        if(Config.SERVER.vehicleDamage.get() && distance >= 4F && this.getDeltaMovement().y() < -1.0F)
        {
            float damage = distance / 2F;
            this.hurt(DamageSource.FALL, damage);
            this.level.playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.ENTITY_VEHICLE_IMPACT.get(), SoundCategory.AMBIENT, 1.0F, 1.0F);
        }
        return true;
    }

    protected void onVehicleDestroyed(LivingEntity entity)
    {
        this.level.playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.ENTITY_VEHICLE_DESTROYED.get(), SoundCategory.AMBIENT, 1.0F, 0.5F);

        boolean isCreativeMode = entity instanceof PlayerEntity && ((PlayerEntity) entity).isCreative();
        if(!isCreativeMode && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS))
        {
            WorkstationRecipe recipe = WorkstationRecipes.getRecipe(this.getType(), this.level);
            if(recipe != null)
            {
                //TODO make vehicle inoperable instead of destroying
                /*List<ItemStack> materials = recipe.getMaterials();
                for(ItemStack stack : materials)
                {
                    ItemStack copy = stack.copy();
                    int shrink = copy.getCount() / 2;
                    if(shrink > 0)
                        copy.shrink(this.random.nextInt(shrink + 1));
                    InventoryUtil.spawnItemStack(this.level, this.getX(), this.getY(), this.getZ(), copy);
                }*/
            }
        }
    }

    public int getDestroyedStage()
    {
        return 10 - (int) Math.max(1.0F, (int) Math.ceil(10.0F * (this.getHealth() / this.getMaxHealth())));
    }

    /**
     * Smooths the rendering on servers
     */
    private void tickLerp()
    {
        if(this.isControlledByLocalInstance())
        {
            this.lerpSteps = 0;
            this.setPacketCoordinates(this.getX(), this.getY(), this.getZ());
        }

        if(this.lerpSteps > 0)
        {
            double d0 = this.getX() + (this.lerpX - this.getX()) / (double) this.lerpSteps;
            double d1 = this.getY() + (this.lerpY - this.getY()) / (double) this.lerpSteps;
            double d2 = this.getZ() + (this.lerpZ - this.getZ()) / (double) this.lerpSteps;
            double d3 = MathHelper.wrapDegrees(this.lerpYaw - (double) this.yRot);
            this.yRot = (float) ((double) this.yRot + d3 / (double) this.lerpSteps);
            this.xRot = (float) ((double) this.xRot + (this.lerpPitch - (double) this.xRot) / (double) this.lerpSteps);
            --this.lerpSteps;
            this.setPos(d0, d1, d2);
            this.setRot(this.yRot, this.xRot);
        }
    }


    @Override
    public void lerpTo(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport)
    {
        this.lerpX = x;
        this.lerpY = y;
        this.lerpZ = z;
        this.lerpYaw = (double) yaw;
        this.lerpPitch = (double) pitch;
        this.lerpSteps = 10;
    }

    @Override
    protected boolean canRide(Entity entityIn)
    {
        return true;
    }

    @Override
    public void addPassenger(Entity passenger)
    {
        super.addPassenger(passenger);
        if(this.isControlledByLocalInstance() && this.lerpSteps > 0)
        {
            this.lerpSteps = 0;
            this.setPos(this.lerpX, this.lerpY, this.lerpZ);
            this.yRot = (float) this.lerpYaw;
            this.xRot = (float) this.lerpPitch;
        }
    }

    protected void applyYawToEntity(Entity passenger)
    {
        int seatIndex = this.getSeatTracker().getSeatIndex(passenger.getUUID());
        if(seatIndex != -1)
        {
            VehicleProperties properties = this.getProperties();
            Seat seat = properties.getSeats().get(seatIndex);
            passenger.setYBodyRot(this.getModifiedRotationYaw() + seat.getYawOffset());
            float f = MathHelper.wrapDegrees(passenger.yRot - this.getModifiedRotationYaw() + seat.getYawOffset());
            float f1 = MathHelper.clamp(f, -120.0F, 120.0F);
            passenger.yRotO += f1 - f;
            passenger.yRot += f1 - f;
            passenger.setYHeadRot(passenger.yRot);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onPassengerTurned(Entity entityToUpdate)
    {
        this.applyYawToEntity(entityToUpdate);
    }

    @Override
    public void push(double x, double y, double z) {}

    /**
     * Sets the time to count down from since the last time entity was hit.
     */
    public void setTimeSinceHit(int timeSinceHit)
    {
        this.entityData.set(TIME_SINCE_HIT, timeSinceHit);
    }

    /**
     * Gets the time since the last hit.
     */
    public int getTimeSinceHit()
    {
        return this.entityData.get(TIME_SINCE_HIT);
    }

    /**
     * Sets the max health of the vehicle.
     */
    public void setMaxHealth(float maxHealth)
    {
        this.entityData.set(MAX_HEALTH, maxHealth);
    }

    /**
     * Gets the max health of the vehicle.
     */
    public float getMaxHealth()
    {
        return this.entityData.get(MAX_HEALTH);
    }

    /**
     * Sets the current health of the vehicle.
     */
    public void setHealth(float health)
    {
        this.entityData.set(HEALTH, Math.min(this.getMaxHealth(), health));
    }

    /**
     * Gets the current health of the vehicle.
     */
    public float getHealth()
    {
        return this.entityData.get(HEALTH);
    }

    //TODO look into this and why its here. May have to send vanilla event to client
    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateHurt()
    {
        this.setTimeSinceHit(10);
    }

    public boolean canBeColored()
    {
        return false;
    }

    public void setColor(int color)
    {
        if(this.canBeColored())
        {
            this.entityData.set(COLOR, color);
        }
    }

    public void setColorRGB(int r, int g, int b)
    {
        int color = ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF));
        this.entityData.set(COLOR, color);
    }

    public int getColor()
    {
        return this.entityData.get(COLOR);
    }

    public int[] getColorRGB()
    {
        int color = this.entityData.get(COLOR);
        return new int[]{ (color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF };
    }

    public boolean canMountTrailer()
    {
        return true;
    }

    /**
     * Gets the absolute position of a part in the world
     *
     * @param position the position definition of the part
     * @return a Vector3d containing the exact location
     */
    public Vector3d getPartPositionAbsoluteVec(PartPosition position, float partialTicks)
    {
        VehicleProperties properties = this.getProperties();
        PartPosition bodyPosition = properties.getBodyPosition();
        Vector3d partVec = Vector3d.ZERO;
        partVec = partVec.add(0, 0.5, 0);
        partVec = partVec.scale(position.getScale());
        partVec = partVec.add(0, -0.5, 0);
        partVec = partVec.add(position.getX() * 0.0625, position.getY() * 0.0625, position.getZ() * 0.0625);
        partVec = partVec.add(0, properties.getWheelOffset() * 0.0625, 0);
        partVec = partVec.add(0, properties.getAxleOffset() * 0.0625, 0);
        partVec = partVec.add(0, 0.5, 0);
        partVec = partVec.scale(bodyPosition.getScale());
        partVec = partVec.add(0, -0.5, 0);
        partVec = partVec.add(0, 0.5, 0);
        partVec = partVec.add(bodyPosition.getX(), bodyPosition.getY(), bodyPosition.getZ());
        partVec = partVec.yRot(-(this.yRotO + (this.yRot - this.yRotO) * partialTicks) * 0.017453292F);
        partVec = partVec.add(this.xo + (this.getX() - this.xo) * partialTicks, 0, 0);
        partVec = partVec.add(0, this.yo + (this.getY() - this.yo) * partialTicks, 0);
        partVec = partVec.add(0, 0, this.zo + (this.getZ() - this.zo) * partialTicks);
        return partVec;
    }

    protected static AxisAlignedBB createScaledBoundingBox(double x1, double y1, double z1, double x2, double y2, double z2, double scale)
    {
        return new AxisAlignedBB(x1 * scale, y1 * scale, z1 * scale, x2 * scale, y2 * scale, z2 * scale);
    }

    protected static AxisAlignedBB createBoxScaled(double x1, double y1, double z1, double x2, double y2, double z2, double scale)
    {
        return new AxisAlignedBB(x1 * 0.0625 * scale, y1 * 0.0625 * scale, z1 * 0.0625 * scale, x2 * 0.0625 * scale, y2 * 0.0625 * scale, z2 * 0.0625 * scale);
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer)
    {
        buffer.writeFloat(this.yRot);
        this.seatTracker.write(buffer);
    }

    @Override
    public void readSpawnData(PacketBuffer buffer)
    {
        this.yRot = this.yRotO = buffer.readFloat();
        this.seatTracker.read(buffer);
    }

    public boolean canTowTrailer()
    {
        return false;
    }

    public void setTrailer(TrailerEntity trailer)
    {
        if(trailer != null)
        {
            this.trailer = trailer;
            this.trailerId = trailer.getUUID();
            trailer.setPullingEntity(this);
            this.entityData.set(TRAILER, trailer.getId());
        }
        else
        {
            if(this.trailer != null && this.trailer.getPullingEntity() == this)
            {
                this.trailer.setPullingEntity(null);
            }
            this.trailer = null;
            this.trailerId = null;
            this.entityData.set(TRAILER, -1);
        }
    }

    @Nullable
    public UUID getTrailerId()
    {
        return trailerId;
    }

    @Nullable
    public TrailerEntity getTrailer()
    {
        return trailer;
    }

    public final VehicleProperties getProperties()
    {
        return VehicleProperties.get(this.getType());
    }

    public float getModifiedRotationYaw()
    {
        return this.yRot;
    }

    @Override
    public ItemStack getPickedResult(RayTraceResult target)
    {
        ResourceLocation entityId = this.getType().getRegistryName();
        if(entityId != null)
        {
            return VehicleCrateBlock.create(entityId, this.getColor(), null, ItemStack.EMPTY);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public IPacket<?> getAddEntityPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public SeatTracker getSeatTracker()
    {
        return this.seatTracker;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger)
    {
        return this.getPassengers().size() < this.getProperties().getSeats().size();
    }

    @Override
    public void positionRider(Entity passenger)
    {
        super.positionRider(passenger);
        this.updatePassengerPosition(passenger);
    }

    protected void updatePassengerPosition(Entity passenger)
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
                    Vector3d seatVec = seat.getPosition().add(0, properties.getAxleOffset() + properties.getWheelOffset(), 0).scale(properties.getBodyPosition().getScale()).yRot(-this.getModifiedRotationYaw() * 0.017453292F - ((float) Math.PI / 2F));
                    passenger.setPos(this.getX() + seatVec.x, this.getY() + seatVec.y, this.getZ() + seatVec.z);
                    this.applyYawToEntity(passenger);
                }
            }
        }
    }
}
