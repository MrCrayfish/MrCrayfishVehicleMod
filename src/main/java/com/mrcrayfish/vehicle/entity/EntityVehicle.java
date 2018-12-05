package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.common.CommonEvents;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.vehicle.EntityTrailer;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.item.ItemSprayCan;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Author: MrCrayfish
 */
public abstract class EntityVehicle extends Entity implements IEntityAdditionalSpawnData
{
    public static final int[] DYE_TO_COLOR = new int[] {16383998, 16351261, 13061821, 3847130, 16701501, 8439583, 15961002, 4673362, 10329495, 1481884, 8991416, 3949738, 8606770, 6192150, 11546150, 1908001};

    protected static final DataParameter<Integer> COLOR = EntityDataManager.createKey(EntityVehicle.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> TIME_SINCE_HIT = EntityDataManager.createKey(EntityVehicle.class, DataSerializers.VARINT);
    private static final DataParameter<Float> DAMAGE_TAKEN = EntityDataManager.createKey(EntityVehicle.class, DataSerializers.FLOAT);

    private PartPosition bodyPosition;
    private Vec3d heldOffset = Vec3d.ZERO;
    private Vec3d trailerOffset = Vec3d.ZERO;
    private float axleOffset;
    private float wheelOffset;

    /**
     * ItemStack instances used for rendering
     */
    @SideOnly(Side.CLIENT)
    public ItemStack body, wheel;

    protected int lerpSteps;
    protected double lerpX;
    protected double lerpY;
    protected double lerpZ;
    protected double lerpYaw;
    protected double lerpPitch;

    public EntityVehicle(World worldIn)
    {
        super(worldIn);
    }

    @Override
    protected void entityInit()
    {
        this.dataManager.register(TIME_SINCE_HIT, 0);
        this.dataManager.register(DAMAGE_TAKEN, 0F);
        this.dataManager.register(COLOR, 16383998);

        if(this.world.isRemote)
        {
            this.onClientInit();
        }
    }

    @SideOnly(Side.CLIENT)
    public void onClientInit() {}

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
            if(this instanceof EntityLandVehicle)
            {
                int trailerId = player.getDataManager().get(CommonEvents.TRAILER);
                if(trailerId != -1)
                {
                    EntityLandVehicle landVehicle = (EntityLandVehicle) this;
                    if(landVehicle.getRidingEntity() == null && landVehicle.canTowTrailer() && landVehicle.getTrailer() == null)
                    {
                        Entity entity = world.getEntityByID(trailerId);
                        if(entity instanceof EntityTrailer)
                        {
                            EntityTrailer trailer = (EntityTrailer) entity;
                            landVehicle.setTrailer(trailer);
                            player.getDataManager().set(CommonEvents.TRAILER, -1);
                        }
                    }
                    return true;
                }
            }

            ItemStack heldItem = player.getHeldItem(hand);
            if(!heldItem.isEmpty() && heldItem.getItem() instanceof ItemSprayCan)
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
                            player.world.playSound(null, posX, posY, posZ, ModSounds.sprayCanSpray, SoundCategory.PLAYERS, 1.0F, 1.0F);
                            tagCompound.setInteger("remainingSprays", remainingSprays - 1);
                        }
                    }
                }
            }
            else if(this.canBeRidden(player))
            {
                player.startRiding(this);
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
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound)
    {
        compound.setIntArray("color", this.getColorRGB());
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key)
    {
        super.notifyDataManagerChange(key);
        if(world.isRemote)
        {
            if(COLOR.equals(key))
            {
                if(!body.hasTagCompound())
                {
                    body.setTagCompound(new NBTTagCompound());
                }
                body.getTagCompound().setInteger("color", this.dataManager.get(COLOR));
            }
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

        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        super.onUpdate();
        this.tickLerp();
        this.onUpdateVehicle();
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
                this.setTimeSinceHit(10);
                this.setDamageTaken(this.getDamageTaken() + amount * 10.0F);

                boolean isCreativeMode = trueSource instanceof EntityPlayer && ((EntityPlayer) trueSource).capabilities.isCreativeMode;
                if(isCreativeMode || this.getDamageTaken() > 40.0F)
                {
                    if(!isCreativeMode && this.world.getGameRules().getBoolean("doEntityDrops"))
                    {
                        //this.dropItemWithOffset(this.getItemBoat(), 1, 0.0F);
                    }
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

    protected void onVehicleDestroyed(EntityLivingBase entity) {}

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
    public abstract double getMountedYOffset();

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

    @Override
    public void updatePassenger(Entity passenger)
    {
        super.updatePassenger(passenger);
    }

    protected void applyYawToEntity(Entity entityToUpdate)
    {
        entityToUpdate.setRenderYawOffset(this.rotationYaw);
        float f = MathHelper.wrapDegrees(entityToUpdate.rotationYaw - this.rotationYaw);
        float f1 = MathHelper.clamp(f, -120.0F, 120.0F);
        entityToUpdate.prevRotationYaw += f1 - f;
        entityToUpdate.rotationYaw += f1 - f;
        entityToUpdate.setRotationYawHead(entityToUpdate.rotationYaw);
    }

    @SideOnly(Side.CLIENT)
    public void applyOrientationToEntity(Entity entityToUpdate)
    {
        this.applyYawToEntity(entityToUpdate);
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

    @SideOnly(Side.CLIENT)
    public void performHurtAnimation()
    {
        this.setTimeSinceHit(10);
        this.setDamageTaken(this.getDamageTaken() * 11.0F);
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

    public void setBodyPosition(PartPosition bodyPosition)
    {
        this.bodyPosition = bodyPosition;
    }

    public PartPosition getBodyPosition()
    {
        return bodyPosition;
    }

    public void setHeldOffset(Vec3d heldOffset)
    {
        this.heldOffset = heldOffset;
    }

    public Vec3d getHeldOffset()
    {
        return heldOffset;
    }

    public void setTrailerOffset(Vec3d trailerOffset)
    {
        this.trailerOffset = trailerOffset;
    }

    public Vec3d getTrailerOffset()
    {
        return trailerOffset;
    }

    public boolean canMountTrailer()
    {
        return true;
    }

    public void setAxleOffset(float axleOffset)
    {
        this.axleOffset = axleOffset;
    }

    public float getAxleOffset()
    {
        return axleOffset;
    }

    public void setWheelOffset(float wheelOffset)
    {
        this.wheelOffset = wheelOffset;
    }

    public float getWheelOffset()
    {
        return wheelOffset;
    }

    /**
     * Gets the absolute position of a part in the world
     *
     * @param position the position definition of the part
     * @return a Vec3d containing the exact location
     */
    protected Vec3d getPartPositionAbsoluteVec(PartPosition position)
    {
        Vec3d partVec = new Vec3d(position.getX() * 0.0625, position.getY() * 0.0625, position.getZ() * 0.0625);
        partVec = partVec.addVector(0, this.getWheelOffset() * 0.0625, 0);
        partVec = partVec.addVector(0, this.getAxleOffset() * 0.0625, 0);
        partVec = partVec.addVector(0, 0.5, 0);
        partVec = partVec.scale(bodyPosition.getScale());
        partVec = partVec.addVector(0, -0.5, 0);
        partVec = partVec.addVector(bodyPosition.getX(), bodyPosition.getY(), bodyPosition.getZ());
        partVec = partVec.rotateYaw(-this.rotationYaw * 0.017453292F);
        partVec = partVec.add(this.getPositionVector());
        return partVec;
    }

    protected static AxisAlignedBB createScaledBoundingBox(double x1, double y1, double z1, double x2, double y2, double z2, double scale)
    {
        return new AxisAlignedBB(x1 * scale, y1 * scale, z1 * scale, x2 * scale, y2 * scale, z2 * scale);
    }

    @Override
    public void writeSpawnData(ByteBuf buffer)
    {
        buffer.writeFloat(rotationYaw);
    }

    @Override
    public void readSpawnData(ByteBuf buffer)
    {
        rotationYaw = prevRotationYaw = buffer.readFloat();
    }
}
