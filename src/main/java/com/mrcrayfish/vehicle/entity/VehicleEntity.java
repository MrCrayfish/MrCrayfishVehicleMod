package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.block.BlockVehicleCrate;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.common.SeatTracker;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.common.entity.SyncedPlayerData;
import com.mrcrayfish.vehicle.crafting.VehicleRecipe;
import com.mrcrayfish.vehicle.crafting.VehicleRecipes;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.item.SprayCanItem;
import com.mrcrayfish.vehicle.util.InventoryUtil;
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
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Author: MrCrayfish
 */
public abstract class VehicleEntity extends Entity implements IEntityAdditionalSpawnData
{
    public static final int[] DYE_TO_COLOR = new int[] {16383998, 16351261, 13061821, 3847130, 16701501, 8439583, 15961002, 4673362, 10329495, 1481884, 8991416, 3949738, 8606770, 6192150, 11546150, 1908001};

    protected static final DataParameter<Integer> COLOR = EntityDataManager.createKey(VehicleEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> TIME_SINCE_HIT = EntityDataManager.createKey(VehicleEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Float> MAX_HEALTH = EntityDataManager.createKey(VehicleEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> HEALTH = EntityDataManager.createKey(VehicleEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> TRAILER = EntityDataManager.createKey(VehicleEntity.class, DataSerializers.VARINT);

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
    protected void registerData()
    {
        this.dataManager.register(TIME_SINCE_HIT, 0);
        this.dataManager.register(MAX_HEALTH, 100F);
        this.dataManager.register(HEALTH, 100F);
        this.dataManager.register(COLOR, 16383998);
        this.dataManager.register(TRAILER, -1);

        if(this.world.isRemote)
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
    public AxisAlignedBB getRenderBoundingBox()
    {
        return this.getBoundingBox().grow(1);
    }

    @Override
    public boolean processInitialInteract(PlayerEntity player, Hand hand)
    {
        if(!world.isRemote && !player.isSneaking())
        {
            int trailerId = SyncedPlayerData.getTrailer(player);
            if(trailerId != -1)
            {
                if(this.getRidingEntity() == null && this.canTowTrailer() && this.getTrailer() == null)
                {
                    Entity entity = world.getEntityByID(trailerId);
                    if(entity instanceof TrailerEntity && entity != this)
                    {
                        TrailerEntity trailer = (TrailerEntity) entity;
                        this.setTrailer(trailer);
                        SyncedPlayerData.setTrailer(player, -1);
                    }
                }
                return true;
            }

            ItemStack heldItem = player.getHeldItem(hand);
            if(heldItem.getItem() instanceof SprayCanItem)
            {
                if(this.canBeColored())
                {
                    CompoundNBT compound = heldItem.getTag();
                    if(compound != null)
                    {
                        int remainingSprays = compound.getInt("RemainingSprays");
                        if(compound.contains("Color", Constants.NBT.TAG_INT) && remainingSprays > 0)
                        {
                            int color = compound.getInt("Color");
                            if(this.getColor() != color)
                            {
                                this.setColor(compound.getInt("Color"));
                                player.world.playSound(null, this.posX, this.posY, this.posZ, ModSounds.SPRAY_CAN_SPRAY.get(), SoundCategory.PLAYERS, 1.0F, 1.0F);
                                compound.putInt("RemainingSprays", remainingSprays - 1);
                            }
                        }
                    }
                }
                return true;
            }
            else if(heldItem.getItem() == ModItems.HAMMER.get() && this.getRidingEntity() instanceof EntityJack)
            {
                if(this.getHealth() < this.getMaxHealth())
                {
                    heldItem.damageItem(1, player, playerEntity -> player.sendBreakAnimation(hand));
                    this.setHealth(this.getHealth() + 5F);
                    this.world.playSound(null, this.posX, this.posY, this.posZ, ModSounds.VEHICLE_THUD.get(), SoundCategory.PLAYERS, 1.0F, 0.8F + 0.4F * rand.nextFloat());
                    player.swingArm(hand);
                    if(player instanceof ServerPlayerEntity)
                    {
                        ((ServerPlayerEntity) player).connection.sendPacket(new SAnimateHandPacket(player, hand == Hand.MAIN_HAND ? 0 : 3));
                    }
                    if(this.getHealth() == this.getMaxHealth())
                    {
                        if(world instanceof ServerWorld)
                        {
                            //TODO send as single packet instead of multiple
                            int count = (int) (50 * (this.getWidth() * this.getHeight()));
                            for(int i = 0; i < count; i++)
                            {
                                double width = this.getWidth() * 2;
                                double height = this.getHeight() * 1.5;

                                Vec3d heldOffset = this.getProperties().getHeldOffset().rotateYaw((float) Math.toRadians(-this.rotationYaw));
                                double x = this.posX + width * rand.nextFloat() - width / 2 + heldOffset.z * 0.0625;
                                double y = this.posY + height * rand.nextFloat();
                                double z = this.posZ + width * rand.nextFloat() - width / 2 + heldOffset.x * 0.0625;

                                double d0 = rand.nextGaussian() * 0.02D;
                                double d1 = rand.nextGaussian() * 0.02D;
                                double d2 = rand.nextGaussian() * 0.02D;
                                ((ServerWorld) this.world).spawnParticle(ParticleTypes.HAPPY_VILLAGER, x, y, z, 1, d0, d1, d2, 1.0);
                            }
                        }
                        this.world.playSound(null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1.0F, 1.5F);
                    }
                }
                return true;
            }
            else if(this.canBeRidden(player))
            {
                int seatIndex = this.seatTracker.getClosestAvailableSeatToPlayer(player);
                if(seatIndex != -1)
                {
                    if(player.startRiding(this))
                    {
                        this.getSeatTracker().setSeatIndex(seatIndex, player.getUniqueID());
                    }
                }
                return true;
            }
        }
        return true;
    }

    @Override
    protected void readAdditional(CompoundNBT compound)
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
        if(compound.hasUniqueId("Trailer"))
        {
            this.trailerId = compound.getUniqueId("Trailer");
        }
        if(compound.contains("SeatTracker", Constants.NBT.TAG_COMPOUND))
        {
            this.seatTracker.read(compound.getCompound("SeatTracker"));
        }
    }

    @Override
    protected void writeAdditional(CompoundNBT compound)
    {
        compound.putIntArray("Color", this.getColorRGB());
        compound.putFloat("MaxHealth", this.getMaxHealth());
        compound.putFloat("Health", this.getHealth());

        //TODO make it save the entity
        if(this.trailerId != null)
        {
            compound.putUniqueId("Trailer", this.trailerId);
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

        /*this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;*/

        if(!this.world.isRemote)
        {
            if(this.searchDelay <= 0)
            {
                if(this.trailer != null)
                {
                    /* Updates periodically to ensure the client knows the vehicle/trailer connection.
                     * There is often problems on loading worlds that it doesn't sync correctly, so this
                     * is the fix. */
                    this.dataManager.set(TRAILER, trailer.getEntityId());
                    this.trailer.getDataManager().set(TrailerEntity.PULLING_ENTITY, this.getEntityId());
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

        if(this.world.isRemote)
        {
            int entityId = this.dataManager.get(TRAILER);
            if(entityId != -1)
            {
                Entity entity = this.world.getEntityByID(this.dataManager.get(TRAILER));
                if(entity instanceof TrailerEntity)
                {
                    this.trailer = (TrailerEntity) entity;
                    this.trailerId = trailer.getUniqueID();
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

        if(!this.world.isRemote && this.trailer != null && (!this.trailer.isAlive() || this.trailer.getPullingEntity() != this))
        {
            this.setTrailer(null);
        }

        super.tick();
        this.tickLerp();
        this.onUpdateVehicle();
    }

    private void findTrailer()
    {
        if(!this.world.isRemote && this.trailerId != null && this.trailer == null)
        {
            ServerWorld server = (ServerWorld) this.world;
            Entity entity = server.getEntityByUuid(this.trailerId);
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
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if(this.isInvulnerableTo(source))
        {
            return false;
        }
        else if(!this.world.isRemote && this.isAlive())
        {
            Entity trueSource = source.getTrueSource();
            if(source instanceof IndirectEntityDamageSource && trueSource != null && this.isPassenger(trueSource))
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
    public void fall(float distance, float damageMultiplier)
    {
        if(Config.SERVER.vehicleDamage.get() && distance >= 4F && this.getMotion().getY() < -1.0F)
        {
            float damage = distance / 2F;
            this.attackEntityFrom(DamageSource.FALL, damage);
            this.world.playSound(null, this.posX, this.posY, this.posZ, ModSounds.VEHICLE_IMPACT.get(), SoundCategory.AMBIENT, 1.0F, 1.0F);
        }
    }

    protected void onVehicleDestroyed(LivingEntity entity)
    {
        this.world.playSound(null, this.posX, this.posY, this.posZ, ModSounds.VEHICLE_DESTROYED.get(), SoundCategory.AMBIENT, 1.0F, 0.5F);

        boolean isCreativeMode = entity instanceof PlayerEntity && ((PlayerEntity) entity).isCreative();
        if(!isCreativeMode && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS))
        {
            VehicleRecipe recipe = VehicleRecipes.getRecipe(this.getType(), this.world);
            if(recipe != null)
            {
                List<ItemStack> materials = recipe.getMaterials();
                for(ItemStack stack : materials)
                {
                    ItemStack copy = stack.copy();
                    int shrink = copy.getCount() / 2;
                    if(shrink > 0)
                        copy.shrink(this.rand.nextInt(shrink + 1));
                    InventoryUtil.spawnItemStack(this.world, this.posX, this.posY, this.posZ, copy);
                }
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
        if(this.lerpSteps > 0)
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
    protected boolean canBeRidden(Entity entityIn)
    {
        return true;
    }

    @Override
    public void addPassenger(Entity passenger)
    {
        super.addPassenger(passenger);
        if(this.canPassengerSteer() && this.lerpSteps > 0)
        {
            this.lerpSteps = 0;
            this.setPosition(this.lerpX, this.lerpY, this.lerpZ);
            this.rotationYaw = (float) this.lerpYaw;
            this.rotationPitch = (float) this.lerpPitch;
        }
    }

    protected void applyYawToEntity(Entity passenger)
    {
        int seatIndex = this.getSeatTracker().getSeatIndex(passenger.getUniqueID());
        if(seatIndex != -1)
        {
            VehicleProperties properties = this.getProperties();
            Seat seat = properties.getSeats().get(seatIndex);
            passenger.setRenderYawOffset(this.getModifiedRotationYaw() + seat.getYawOffset());
            float f = MathHelper.wrapDegrees(passenger.rotationYaw - this.getModifiedRotationYaw() + seat.getYawOffset());
            float f1 = MathHelper.clamp(f, -120.0F, 120.0F);
            passenger.prevRotationYaw += f1 - f;
            passenger.rotationYaw += f1 - f;
            passenger.setRotationYawHead(passenger.rotationYaw);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void applyOrientationToEntity(Entity entityToUpdate)
    {
        this.applyYawToEntity(entityToUpdate);
    }

    @Override
    public void addVelocity(double x, double y, double z) {}

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
     * Sets the max health of the vehicle.
     */
    public void setMaxHealth(float maxHealth)
    {
        this.dataManager.set(MAX_HEALTH, maxHealth);
    }

    /**
     * Gets the max health of the vehicle.
     */
    public float getMaxHealth()
    {
        return this.dataManager.get(MAX_HEALTH);
    }

    /**
     * Sets the current health of the vehicle.
     */
    public void setHealth(float health)
    {
        this.dataManager.set(HEALTH, Math.min(this.getMaxHealth(), health));
    }

    /**
     * Gets the current health of the vehicle.
     */
    public float getHealth()
    {
        return this.dataManager.get(HEALTH);
    }

    //TODO look into this and why its here. May have to send vanilla event to client
    @Override
    @OnlyIn(Dist.CLIENT)
    public void performHurtAnimation()
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
            this.dataManager.set(COLOR, color);
        }
    }

    public void setColorRGB(int r, int g, int b)
    {
        int color = ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF));
        this.dataManager.set(COLOR, color);
    }

    public int getColor()
    {
        return this.dataManager.get(COLOR);
    }

    public int[] getColorRGB()
    {
        int color = this.dataManager.get(COLOR);
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
     * @return a Vec3d containing the exact location
     */
    public Vec3d getPartPositionAbsoluteVec(PartPosition position, float partialTicks)
    {
        VehicleProperties properties = this.getProperties();
        PartPosition bodyPosition = properties.getBodyPosition();
        Vec3d partVec = Vec3d.ZERO;
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
        partVec = partVec.rotateYaw(-(this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * partialTicks) * 0.017453292F);
        partVec = partVec.add(this.prevPosX + (this.posX - this.prevPosX) * partialTicks, 0, 0);
        partVec = partVec.add(0, this.prevPosY + (this.posY - this.prevPosY) * partialTicks, 0);
        partVec = partVec.add(0, 0, this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks);
        return partVec;
    }

    protected static AxisAlignedBB createScaledBoundingBox(double x1, double y1, double z1, double x2, double y2, double z2, double scale)
    {
        return new AxisAlignedBB(x1 * scale, y1 * scale, z1 * scale, x2 * scale, y2 * scale, z2 * scale);
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer)
    {
        buffer.writeFloat(this.rotationYaw);
        this.seatTracker.write(buffer);
    }

    @Override
    public void readSpawnData(PacketBuffer buffer)
    {
        this.rotationYaw = this.prevRotationYaw = buffer.readFloat();
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
            this.trailerId = trailer.getUniqueID();
            trailer.setPullingEntity(this);
            this.dataManager.set(TRAILER, trailer.getEntityId());
        }
        else
        {
            if(this.trailer != null && this.trailer.getPullingEntity() == this)
            {
                this.trailer.setPullingEntity(null);
            }
            this.trailer = null;
            this.trailerId = null;
            this.dataManager.set(TRAILER, -1);
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

    public VehicleProperties getProperties()
    {
        return VehicleProperties.getProperties(this.getType());
    }

    public float getModifiedRotationYaw()
    {
        return this.rotationYaw;
    }

    @Override
    public ItemStack getPickedResult(RayTraceResult target)
    {
        ResourceLocation entityId = this.getType().getRegistryName();
        if(entityId != null)
        {
            return BlockVehicleCrate.create(entityId, this.getColor(), null, null, -1);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public IPacket<?> createSpawnPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public SeatTracker getSeatTracker()
    {
        return this.seatTracker;
    }

    @Override
    protected boolean canFitPassenger(Entity passenger)
    {
        return this.getPassengers().size() < this.getProperties().getSeats().size();
    }

    @Override
    public void updatePassenger(Entity passenger)
    {
        super.updatePassenger(passenger);
        this.updatePassengerPosition(passenger);
    }

    protected void updatePassengerPosition(Entity passenger)
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
                    Vec3d seatVec = seat.getPosition().add(0, properties.getAxleOffset() + properties.getWheelOffset(), 0).scale(properties.getBodyPosition().getScale()).rotateYaw(-this.getModifiedRotationYaw() * 0.017453292F - ((float) Math.PI / 2F));
                    passenger.setPosition(this.posX + seatVec.x, this.posY + seatVec.y, this.posZ + seatVec.z);
                    this.applyYawToEntity(passenger);
                }
            }
        }
    }
}
