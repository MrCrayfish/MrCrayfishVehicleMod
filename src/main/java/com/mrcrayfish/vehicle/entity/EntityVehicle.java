package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.VehicleConfig;
import com.mrcrayfish.vehicle.block.BlockVehicleCrate;
import com.mrcrayfish.vehicle.common.CommonEvents;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.common.SeatTracker;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.common.entity.SyncedPlayerData;
import com.mrcrayfish.vehicle.crafting.VehicleRecipes;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.item.ItemSprayCan;
import com.mrcrayfish.vehicle.util.InventoryUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Author: MrCrayfish
 */
public abstract class EntityVehicle extends Entity implements IEntityAdditionalSpawnData
{
    public static final int[] DYE_TO_COLOR = new int[] {16383998, 16351261, 13061821, 3847130, 16701501, 8439583, 15961002, 4673362, 10329495, 1481884, 8991416, 3949738, 8606770, 6192150, 11546150, 1908001};

    protected static final DataParameter<Integer> COLOR = EntityDataManager.createKey(EntityVehicle.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> TIME_SINCE_HIT = EntityDataManager.createKey(EntityVehicle.class, DataSerializers.VARINT);
    private static final DataParameter<Float> MAX_HEALTH = EntityDataManager.createKey(EntityVehicle.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> HEALTH = EntityDataManager.createKey(EntityVehicle.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> TRAILER = EntityDataManager.createKey(EntityVehicle.class, DataSerializers.VARINT);

    protected UUID trailerId;
    protected EntityTrailer trailer = null;
    private int searchDelay = 0;

    protected int lerpSteps;
    protected double lerpX;
    protected double lerpY;
    protected double lerpZ;
    protected double lerpYaw;
    protected double lerpPitch;

    protected SeatTracker seatTracker;

    public EntityVehicle(World worldIn)
    {
        super(worldIn);
        this.seatTracker = new SeatTracker(this);
    }

    @Override
    protected void entityInit()
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

    @SideOnly(Side.CLIENT)
    public void onClientInit() {}

    /* Overridden to prevent odd step sound when driving vehicles. Ain't no subclasses getting
     * the ability to override this. */
    @Override
    protected final void playStepSound(BlockPos pos, Block blockIn) {}

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return this.getEntityBoundingBox().grow(1);
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
    {
        if(!world.isRemote && !player.isSneaking())
        {
            int trailerId = SyncedPlayerData.getTrailer(player);
            if(trailerId != -1)
            {
                if(this.getRidingEntity() == null && this.canTowTrailer() && this.getTrailer() == null)
                {
                    Entity entity = world.getEntityByID(trailerId);
                    if(entity instanceof EntityTrailer && entity != this)
                    {
                        EntityTrailer trailer = (EntityTrailer) entity;
                        this.setTrailer(trailer);
                        SyncedPlayerData.setTrailer(player, -1);
                    }
                }
                return true;
            }

            ItemStack heldItem = player.getHeldItem(hand);
            if(heldItem.getItem() == ModItems.SPRAY_CAN)
            {
                if(canBeColored())
                {
                    NBTTagCompound tagCompound = ItemSprayCan.createTagCompound(heldItem);
                    int remainingSprays = tagCompound.getInteger("remainingSprays");
                    if(tagCompound.hasKey("color", Constants.NBT.TAG_INT) && remainingSprays > 0)
                    {
                        int color = tagCompound.getInteger("color");
                        if(this.getColor() != color)
                        {
                            this.setColor(tagCompound.getInteger("color"));
                            player.world.playSound(null, posX, posY, posZ, ModSounds.SPRAY_CAN_SPRAY, SoundCategory.PLAYERS, 1.0F, 1.0F);
                            tagCompound.setInteger("remainingSprays", remainingSprays - 1);
                        }
                    }
                }
            }
            else if(heldItem.getItem() == ModItems.HAMMER && this.getRidingEntity() instanceof EntityJack)
            {
                if(this.getHealth() < this.getMaxHealth())
                {
                    heldItem.damageItem(1, player);
                    this.setHealth(this.getHealth() + 5F);
                    world.playSound(null, posX, posY, posZ, ModSounds.VEHICLE_THUD, SoundCategory.PLAYERS, 1.0F, 0.8F + 0.4F * rand.nextFloat());
                    player.swingArm(hand);
                    if(player instanceof EntityPlayerMP)
                    {
                        ((EntityPlayerMP) player).connection.sendPacket(new SPacketAnimation(player, hand == EnumHand.MAIN_HAND ? 0 : 3));
                    }
                    if(this.getHealth() == this.getMaxHealth())
                    {
                        if(world instanceof WorldServer)
                        {
                            //TODO send as single packet instead of multiple
                            int count = (int) (50 * (this.width * this.height));
                            for(int i = 0; i < count; i++)
                            {
                                double width = this.width * 2;
                                double height = this.height * 1.5;

                                Vec3d heldOffset = this.getProperties().getHeldOffset().rotateYaw((float) Math.toRadians(-this.rotationYaw));
                                double x = posX + width * rand.nextFloat() - width / 2 + heldOffset.z * 0.0625;
                                double y = posY + height * rand.nextFloat();
                                double z = posZ + width * rand.nextFloat() - width / 2 + heldOffset.x * 0.0625;

                                double d0 = rand.nextGaussian() * 0.02D;
                                double d1 = rand.nextGaussian() * 0.02D;
                                double d2 = rand.nextGaussian() * 0.02D;
                                ((WorldServer) world).spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, x, y, z, 1, d0, d1, d2, 1.0);
                            }
                        }
                        world.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1.0F, 1.5F);
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
    protected void readEntityFromNBT(NBTTagCompound compound)
    {
        if(compound.hasKey("color", Constants.NBT.TAG_INT_ARRAY))
        {
            int[] c = compound.getIntArray("color");
            if(c.length == 3)
            {
                int color = ((c[0] & 0xFF) << 16) | ((c[1] & 0xFF) << 8) | ((c[2] & 0xFF));
                this.setColor(color);
            }
        }
        else if(compound.hasKey("color", Constants.NBT.TAG_INT))
        {
            int index = compound.getInteger("color");
            if(index >= 0 && index < DYE_TO_COLOR.length)
            {
                this.setColor(DYE_TO_COLOR[index]);
            }
            compound.removeTag("color");
        }
        if(compound.hasKey("maxHealth", Constants.NBT.TAG_FLOAT))
        {
            this.setMaxHealth(compound.getFloat("maxHealth"));
        }
        if(compound.hasKey("health", Constants.NBT.TAG_FLOAT))
        {
            this.setHealth(compound.getFloat("health"));
        }
        if(compound.hasUniqueId("trailer"))
        {
            this.trailerId = compound.getUniqueId("trailer");
        }
        if(compound.hasKey("SeatTracker", Constants.NBT.TAG_COMPOUND))
        {
            this.seatTracker.read(compound.getCompoundTag("SeatTracker"));
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound)
    {
        compound.setIntArray("color", this.getColorRGB());
        compound.setFloat("maxHealth", this.getMaxHealth());
        compound.setFloat("health", this.getHealth());

        //TODO make it save the entity
        if(trailerId != null)
        {
            compound.setUniqueId("trailer", trailerId);
        }

        compound.setTag("SeatTracker", this.seatTracker.write());
    }

    @Override
    public void onUpdate()
    {
        if(this.getTimeSinceHit() > 0)
        {
            this.setTimeSinceHit(this.getTimeSinceHit() - 1);
        }

        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        if(!world.isRemote)
        {
            if(this.searchDelay <= 0)
            {
                if(trailer != null)
                {
                    /* Updates periodically to ensure the client knows the vehicle/trailer connection.
                     * There is often problems on loading worlds that it doesn't sync correctly, so this
                     * is the fix. */
                    this.dataManager.setDirty(TRAILER);
                    this.trailer.getDataManager().setDirty(EntityTrailer.PULLING_ENTITY);
                    this.searchDelay = VehicleConfig.SERVER.trailerSyncCooldown;
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
                Entity entity = world.getEntityByID(this.dataManager.get(TRAILER));
                if(entity instanceof EntityTrailer)
                {
                    this.trailer = (EntityTrailer) entity;
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

        if(!this.world.isRemote && trailer != null && (trailer.isDead || trailer.getPullingEntity() != this))
        {
            this.setTrailer(null);
        }

        super.onUpdate();
        this.tickLerp();
        this.onUpdateVehicle();
    }

    private void findTrailer()
    {
        if(!world.isRemote && trailerId != null && trailer == null)
        {
            WorldServer server = (WorldServer) world;
            Entity entity = server.getEntityFromUuid(trailerId);
            if(entity instanceof EntityTrailer)
            {
                this.setTrailer((EntityTrailer) entity);
                return;
            }
            trailerId = null;
        }
    }

    protected abstract void onUpdateVehicle();

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if(this.isEntityInvulnerable(source))
        {
            return false;
        }
        else if(!this.world.isRemote && !this.isDead)
        {
            Entity trueSource = source.getTrueSource();
            if(source instanceof EntityDamageSourceIndirect && trueSource != null && this.isPassenger(trueSource))
            {
                return false;
            }
            else
            {
                if(VehicleConfig.SERVER.vehicleDamage)
                {
                    this.setTimeSinceHit(10);
                    this.setHealth(this.getHealth() - amount);
                }
                boolean isCreativeMode = trueSource instanceof EntityPlayer && ((EntityPlayer) trueSource).capabilities.isCreativeMode;
                if(isCreativeMode || this.getHealth() < 0.0F)
                {
                    this.onVehicleDestroyed((EntityLivingBase) trueSource);
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
    public void fall(float distance, float damageMultiplier)
    {
        if(VehicleConfig.SERVER.vehicleDamage && distance >= 4F && this.motionY < -1.0F)
        {
            float damage = distance / 2F;
            this.attackEntityFrom(DamageSource.FALL, damage);
            world.playSound(null, posX, posY, posZ, ModSounds.VEHICLE_IMPACT, SoundCategory.AMBIENT, 1.0F, 1.0F);
        }
    }

    protected void onVehicleDestroyed(EntityLivingBase entity)
    {
        world.playSound(null, posX, posY, posZ, ModSounds.VEHICLE_DESTROYED, SoundCategory.AMBIENT, 1.0F, 0.5F);

        boolean isCreativeMode = entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isCreativeMode;
        if(!isCreativeMode && this.world.getGameRules().getBoolean("doEntityDrops"))
        {
            VehicleRecipes.VehicleRecipe recipe = VehicleRecipes.getRecipe(this.getClass());
            if(recipe != null)
            {
                List<ItemStack> materials = recipe.getMaterials();
                for(ItemStack stack : materials)
                {
                    ItemStack copy = stack.copy();
                    int shrink = copy.getCount() / 2;
                    if(shrink > 0)
                        copy.shrink(rand.nextInt(shrink + 1));
                    InventoryUtil.spawnItemStack(world, posX, posY, posZ, copy);
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
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport)
    {
        //super.setPositionAndRotationDirect(x, y, z, yaw, pitch, posRotationIncrements, teleport);
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
            this.posX = this.lerpX;
            this.posY = this.lerpY;
            this.posZ = this.lerpZ;
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
    @SideOnly(Side.CLIENT)
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
    @SideOnly(Side.CLIENT)
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
        partVec = partVec.addVector(0, 0.5, 0);
        partVec = partVec.scale(position.getScale());
        partVec = partVec.addVector(0, -0.5, 0);
        partVec = partVec.addVector(position.getX() * 0.0625, position.getY() * 0.0625, position.getZ() * 0.0625);
        partVec = partVec.addVector(0, properties.getWheelOffset() * 0.0625, 0);
        partVec = partVec.addVector(0, properties.getAxleOffset() * 0.0625, 0);
        partVec = partVec.addVector(0, 0.5, 0);
        partVec = partVec.scale(bodyPosition.getScale());
        partVec = partVec.addVector(0, -0.5, 0);
        partVec = partVec.addVector(0, 0.5, 0);
        partVec = partVec.addVector(bodyPosition.getX(), bodyPosition.getY(), bodyPosition.getZ());
        partVec = partVec.rotateYaw(-(this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * partialTicks) * 0.017453292F);
        partVec = partVec.addVector(this.prevPosX + (this.posX - this.prevPosX) * partialTicks, 0, 0);
        partVec = partVec.addVector(0, this.prevPosY + (this.posY - this.prevPosY) * partialTicks, 0);
        partVec = partVec.addVector(0, 0, this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks);
        return partVec;
    }

    protected static AxisAlignedBB createScaledBoundingBox(double x1, double y1, double z1, double x2, double y2, double z2, double scale)
    {
        return new AxisAlignedBB(x1 * scale, y1 * scale, z1 * scale, x2 * scale, y2 * scale, z2 * scale);
    }

    @Override
    public void writeSpawnData(ByteBuf buffer)
    {
        buffer.writeFloat(this.rotationYaw);
        this.seatTracker.write(buffer);
    }

    @Override
    public void readSpawnData(ByteBuf buffer)
    {
        this.rotationYaw = this.prevRotationYaw = buffer.readFloat();
        this.seatTracker.read(buffer);
    }

    public boolean canTowTrailer()
    {
        return false;
    }

    public void setTrailer(EntityTrailer trailer)
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
    public EntityTrailer getTrailer()
    {
        return trailer;
    }

    public VehicleProperties getProperties()
    {
        return VehicleProperties.getProperties(this.getClass());
    }

    public float getModifiedRotationYaw()
    {
        return this.rotationYaw;
    }

    @Override
    public ItemStack getPickedResult(RayTraceResult target)
    {
        ResourceLocation entityId = EntityList.getKey(this);
        return BlockVehicleCrate.create(entityId, this.getColor(), null, null, -1);
    }

    public SeatTracker getSeatTracker()
    {
        return seatTracker;
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
                    Vec3d seatVec = seat.getPosition().addVector(0, properties.getAxleOffset() + properties.getWheelOffset(), 0).scale(properties.getBodyPosition().getScale()).rotateYaw(-this.getModifiedRotationYaw() * 0.017453292F - ((float) Math.PI / 2F));
                    passenger.setPosition(this.posX + seatVec.x, this.posY + seatVec.y, this.posZ + seatVec.z);
                    this.applyYawToEntity(passenger);
                }
            }
        }
    }
}
