package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.Config;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public abstract class TrailerEntity extends VehicleEntity
{
    public static final DataParameter<Integer> PULLING_ENTITY = EntityDataManager.createKey(TrailerEntity.class, DataSerializers.VARINT);

    private Entity pullingEntity;

    public float wheelRotation;
    public float prevWheelRotation;

    public TrailerEntity(EntityType<?> entityType, World worldIn)
    {
        super(entityType, worldIn);
        this.stepHeight = 1.0F;
    }

    @Override
    protected void registerData()
    {
        super.registerData();
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
        this.prevWheelRotation = this.wheelRotation;

        Vec3d motion = this.getMotion();
        this.setMotion(motion.getX(), motion.getY() - 0.08, motion.getZ());

        if(this.pullingEntity != null && !this.world.isRemote)
        {
            if(this.pullingEntity.getDistance(this) > Config.SERVER.trailerDetachThreshold.get())
            {
                this.world.playSound(null, this.pullingEntity.getPosition(), SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 1.0F, 1.0F);
                this.pullingEntity = null;
                return;
            }
        }

        if(this.pullingEntity != null)
        {
            if(!this.pullingEntity.isAlive() || (this.pullingEntity instanceof VehicleEntity && ((VehicleEntity) this.pullingEntity).getTrailer() != this))
            {
                this.pullingEntity = null;
                return;
            }
            this.updatePullingMotion();
        }
        else if(!world.isRemote)
        {
            motion = this.getMotion();
            this.move(MoverType.SELF, new Vec3d(motion.getX() * 0.75, motion.getY(), motion.getZ() * 0.75));
        }

        this.doBlockCollisions();

        float speed = (float) (Math.sqrt(Math.pow(this.func_226277_ct_() - this.prevPosX, 2) + Math.pow(this.func_226278_cu_() - this.prevPosY, 2) + Math.pow(this.func_226281_cx_() - this.prevPosZ, 2)) * 20);
        wheelRotation -= 90F * (speed / 10F);
    }

    private void updatePullingMotion()
    {
        Vec3d towBar = pullingEntity.getPositionVector();
        if(pullingEntity instanceof VehicleEntity)
        {
            VehicleEntity vehicle = (VehicleEntity) pullingEntity;
            Vec3d towBarVec = vehicle.getProperties().getTowBarPosition();
            towBarVec = new Vec3d(towBarVec.x * 0.0625, towBarVec.y * 0.0625, towBarVec.z * 0.0625 + vehicle.getProperties().getBodyPosition().getZ());
            if(vehicle instanceof LandVehicleEntity)
            {
                LandVehicleEntity landVehicle = (LandVehicleEntity) vehicle;
                towBar = towBar.add(towBarVec.rotateYaw((float) Math.toRadians(-vehicle.rotationYaw + landVehicle.additionalYaw)));
            }
            else
            {
                towBar = towBar.add(towBarVec.rotateYaw((float) Math.toRadians(-vehicle.rotationYaw)));
            }
        }

        this.rotationYaw = (float) Math.toDegrees(Math.atan2(towBar.z - this.func_226281_cx_(), towBar.x - this.func_226277_ct_()) - Math.toRadians(90F));
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
        Vec3d motion = this.getMotion();
        this.setMotion(vec.x - this.func_226277_ct_(), motion.getY(), vec.z - this.func_226281_cx_());
        this.move(MoverType.SELF, this.getMotion());
    }

    @Override
    public double getMountedYOffset()
    {
        return 0.0;
    }

    public boolean setPullingEntity(Entity pullingEntity)
    {
        if(pullingEntity instanceof PlayerEntity || (pullingEntity instanceof VehicleEntity && pullingEntity.getRidingEntity() == null && ((VehicleEntity) pullingEntity).canTowTrailer()))
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
    @OnlyIn(Dist.CLIENT)
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

    @Override
    public void notifyDataManagerChange(DataParameter<?> key)
    {
        super.notifyDataManagerChange(key);
        if(world.isRemote)
        {
            if(PULLING_ENTITY.equals(key))
            {
                int entityId = this.dataManager.get(PULLING_ENTITY);
                if(entityId != -1)
                {
                    Entity entity = world.getEntityByID(this.dataManager.get(PULLING_ENTITY));
                    if(entity instanceof PlayerEntity || (entity instanceof VehicleEntity && ((VehicleEntity) entity).canTowTrailer()))
                    {
                        pullingEntity = entity;
                    }
                    else
                    {
                        pullingEntity = null;
                    }
                }
                else
                {
                    pullingEntity = null;
                }
            }
        }
    }

    public abstract double getHitchOffset();
}
