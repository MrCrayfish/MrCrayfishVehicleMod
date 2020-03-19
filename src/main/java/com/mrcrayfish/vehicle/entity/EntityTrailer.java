package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.VehicleConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public abstract class EntityTrailer extends EntityVehicle
{
    public static final DataParameter<Integer> PULLING_ENTITY = EntityDataManager.createKey(EntityTrailer.class, DataSerializers.VARINT);

    private Entity pullingEntity;

    public float wheelRotation;
    public float prevWheelRotation;

    public EntityTrailer(World worldIn)
    {
        super(worldIn);
        this.setSize(1.5F, 1.5F);
        this.stepHeight = 1.0F;
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        this.dataManager.register(PULLING_ENTITY, -1);
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return true;
    }

    @Override
    public void onUpdateVehicle()
    {
        prevWheelRotation = wheelRotation;

        this.motionY -= 0.08;

        if(this.world.isRemote)
        {
            int entityId = this.dataManager.get(PULLING_ENTITY);
            if(entityId != -1)
            {
                Entity entity = world.getEntityByID(this.dataManager.get(PULLING_ENTITY));
                if(entity instanceof EntityPlayer || (entity instanceof EntityVehicle && ((EntityVehicle) entity).canTowTrailer()))
                {
                    this.pullingEntity = entity;
                }
                else if(this.pullingEntity != null)
                {
                    this.pullingEntity = null;
                }
            }
            else if(this.pullingEntity != null)
            {
                this.pullingEntity = null;
            }
        }

        if(this.pullingEntity != null && !world.isRemote)
        {
            if(this.pullingEntity.getDistance(this) > VehicleConfig.SERVER.trailerDetachThreshold)
            {
                world.playSound(null, pullingEntity.getPosition(), SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 1.0F, 1.0F);
                this.pullingEntity = null;
                return;
            }
        }

        if(this.pullingEntity != null)
        {
            if(this.pullingEntity.isDead || (this.pullingEntity instanceof EntityVehicle && ((EntityVehicle) this.pullingEntity).getTrailer() != this))
            {
                this.pullingEntity = null;
                return;
            }
            this.updatePullingMotion();
        }
        else if(!world.isRemote)
        {
            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
            
            /* Reduces the motion and speed multiplier */
            if(this.onGround)
            {
                this.motionX *= 0.65;
                this.motionZ *= 0.65;
            }
            else
            {
                this.motionX *= 0.98;
                this.motionZ *= 0.98;
            }
        }

        this.doBlockCollisions();

        float speed = (float) (Math.sqrt(Math.pow(this.posX - this.prevPosX, 2) + Math.pow(this.posY - this.prevPosY, 2) + Math.pow(this.posZ - this.prevPosZ, 2)) * 20);
        wheelRotation -= 90F * (speed / 10F);
    }

    private void updatePullingMotion()
    {
        Vec3d towBar = pullingEntity.getPositionVector();
        if(pullingEntity instanceof EntityVehicle)
        {
            EntityVehicle vehicle = (EntityVehicle) pullingEntity;
            Vec3d towBarVec = vehicle.getProperties().getTowBarPosition();
            towBarVec = new Vec3d(towBarVec.x * 0.0625, towBarVec.y * 0.0625, towBarVec.z * 0.0625 + vehicle.getProperties().getBodyPosition().getZ());
            if(vehicle instanceof EntityLandVehicle)
            {
                EntityLandVehicle landVehicle = (EntityLandVehicle) vehicle;
                towBar = towBar.add(towBarVec.rotateYaw((float) Math.toRadians(-vehicle.rotationYaw + landVehicle.additionalYaw)));
            }
            else
            {
                towBar = towBar.add(towBarVec.rotateYaw((float) Math.toRadians(-vehicle.rotationYaw)));
            }
        }

        this.rotationYaw = (float) Math.toDegrees(Math.atan2(towBar.z - this.posZ, towBar.x - this.posX) - Math.toRadians(90F));
        double deltaRot = (double) (this.prevRotationYaw - this.rotationYaw);
        if (deltaRot < -180.0D)
        {
            this.prevRotationYaw += 360.0F;
        }
        else if (deltaRot >= 180.0D)
        {
            this.prevRotationYaw -= 360.0F;
        }

        Vec3d vec = new Vec3d(0, 0, this.getHitchOffset() * 0.0625).rotateYaw((float) Math.toRadians(-this.rotationYaw)).add(towBar);
        this.motionX = vec.x - this.posX;
        this.motionZ = vec.z - this.posZ;
        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
    }

    @Override
    public double getMountedYOffset()
    {
        return 0.0;
    }

    public boolean setPullingEntity(Entity pullingEntity)
    {
        if(pullingEntity instanceof EntityPlayer || (pullingEntity instanceof EntityVehicle && pullingEntity.getRidingEntity() == null && ((EntityVehicle) pullingEntity).canTowTrailer()))
        {
            this.pullingEntity = pullingEntity;
            this.dataManager.set(PULLING_ENTITY, pullingEntity.getEntityId());
            return true;
        }
        else
        {
            this.pullingEntity = null;
            this.dataManager.set(PULLING_ENTITY, -1);
            return false;
        }
    }

    @Nullable
    public Entity getPullingEntity()
    {
        return pullingEntity;
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
        this.lerpSteps = 1;
    }

    @Override
    public boolean canMountTrailer()
    {
        return false;
    }

    public abstract double getHitchOffset();
}
