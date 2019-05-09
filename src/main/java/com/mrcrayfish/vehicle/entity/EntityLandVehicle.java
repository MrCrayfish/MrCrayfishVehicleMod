package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.client.render.Wheel;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageDrift;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public abstract class EntityLandVehicle extends EntityPoweredVehicle
{
    private static final DataParameter<Boolean> DRIFTING = EntityDataManager.createKey(EntityLandVehicle.class, DataSerializers.BOOLEAN);

    public float drifting;
    public float additionalYaw;
    public float prevAdditionalYaw;

    public float frontWheelRotation;
    public float prevFrontWheelRotation;
    public float rearWheelRotation;
    public float prevRearWheelRotation;

    public EntityLandVehicle(World worldIn)
    {
        super(worldIn);
    }

    @Override
    public void entityInit()
    {
        super.entityInit();
        this.dataManager.register(DRIFTING, false);
    }

    @Override
    public void onUpdateVehicle()
    {
        super.onUpdateVehicle();
        this.updateWheels();
    }

    @Override
    public void updateVehicle()
    {
        prevAdditionalYaw = additionalYaw;
        prevFrontWheelRotation = frontWheelRotation;
        prevRearWheelRotation = rearWheelRotation;

        this.updateDrifting();
    }

    @Override
    public void onClientUpdate()
    {
        super.onClientUpdate();
        EntityLivingBase entity = (EntityLivingBase) this.getControllingPassenger();
        if(entity != null && entity.equals(Minecraft.getMinecraft().player))
        {
            boolean drifting = VehicleMod.proxy.isDrifting();
            if(this.isDrifting() != drifting)
            {
                this.setDrifting(drifting);
                PacketHandler.INSTANCE.sendToServer(new MessageDrift(drifting));
            }
        }
    }

    @Override
    public void updateVehicleMotion()
    {
        float currentSpeed = this.currentSpeed;

        if(speedMultiplier > 1.0F)
        {
            speedMultiplier = 1.0F;
        }

        /* Applies the speed multiplier to the current speed */
        currentSpeed = currentSpeed + (currentSpeed * speedMultiplier);

        float f1 = MathHelper.sin(this.rotationYaw * 0.017453292F) / 20F; //Divide by 20 ticks
        float f2 = MathHelper.cos(this.rotationYaw * 0.017453292F) / 20F;
        this.vehicleMotionX = (-currentSpeed * f1);
        if(!launching)
        {
            this.motionY -= 0.08D;
        }
        this.vehicleMotionZ = (currentSpeed * f2);
    }

    @Override
    protected void updateTurning()
    {
        this.turnAngle = VehicleMod.proxy.getTargetTurnAngle(this, this.isDrifting());
        this.wheelAngle = this.turnAngle * Math.max(0.25F, 1.0F - Math.abs(currentSpeed / 30F));
        this.deltaYaw = this.wheelAngle * (currentSpeed / 30F) / 2F;

        if(world.isRemote)
        {
            this.targetWheelAngle = this.isDrifting() ? -35F * (this.turnAngle / (float) this.getMaxTurnAngle()) * this.getNormalSpeed() : this.wheelAngle - 35F * (this.turnAngle / (float) this.getMaxTurnAngle()) * drifting;
            this.renderWheelAngle = this.renderWheelAngle + (this.targetWheelAngle - this.renderWheelAngle) * (this.isDrifting() ? 0.35F : 0.5F);
        }
    }

    private void updateDrifting()
    {
        TurnDirection turnDirection = this.getTurnDirection();
        if(this.getControllingPassenger() != null && this.isDrifting())
        {
            if(turnDirection != TurnDirection.FORWARD)
            {
                AccelerationDirection acceleration = this.getAcceleration();
                if(acceleration == AccelerationDirection.FORWARD)
                {
                    this.currentSpeed *= 0.975F;
                }
                this.drifting = Math.min(1.0F, this.drifting + 0.025F);
            }
        }
        else
        {
            this.drifting *= 0.95F;
        }
        this.additionalYaw = 25F * drifting * (turnAngle / (float) this.getMaxTurnAngle()) * Math.min(this.getActualMaxSpeed(), this.getActualSpeed() * 2F);

        //Updates the delta yaw to consider drifting
        this.deltaYaw = this.wheelAngle * (currentSpeed / 30F) / (this.isDrifting() ? 1.5F : 2F);
    }

    public void updateWheels()
    {
        VehicleProperties properties = VehicleProperties.getProperties(this.getClass());
        double wheelCircumference = 16.0;
        double vehicleScale = properties.getBodyPosition().getScale();
        double speed = this.getSpeed();

        Wheel frontWheel = properties.getFirstFrontWheel();
        if(frontWheel != null)
        {
            double frontWheelCircumference = wheelCircumference * vehicleScale * frontWheel.getScaleY();
            double rotation = (speed * 16) / frontWheelCircumference;
            this.frontWheelRotation -= rotation * 20F;
        }

        Wheel rearWheel = properties.getFirstRearWheel();
        if(rearWheel != null)
        {
            double rearWheelCircumference = wheelCircumference * vehicleScale * rearWheel.getScaleY();
            double rotation = (speed * 16) / rearWheelCircumference;
            this.rearWheelRotation -= rotation * 20F;
        }
    }

    @Override
    public void createParticles()
    {
        if(this.canDrive())
        {
            super.createParticles();
        }
    }

    @Override
    protected void removePassenger(Entity passenger)
    {
        super.removePassenger(passenger);
        if(this.getControllingPassenger() == null)
        {
            this.rotationYaw -= this.additionalYaw;
            this.additionalYaw = 0;
            this.drifting = 0;
        }
    }

    @Override
    protected void applyYawToEntity(Entity entityToUpdate)
    {
        entityToUpdate.setRenderYawOffset(this.rotationYaw - this.additionalYaw);
        float f = MathHelper.wrapDegrees(entityToUpdate.rotationYaw - this.rotationYaw);
        float f1 = MathHelper.clamp(f, -120.0F, 120.0F);
        entityToUpdate.prevRotationYaw += f1 - f;
        entityToUpdate.rotationYaw += f1 - f;
        entityToUpdate.setRotationYawHead(entityToUpdate.rotationYaw);
    }

    public void setDrifting(boolean drifting)
    {
        this.dataManager.set(DRIFTING, drifting);
    }

    public boolean isDrifting()
    {
        return this.dataManager.get(DRIFTING);
    }

    @Override
    protected float getModifiedAccelerationSpeed()
    {
        if(trailer != null)
        {
            if(trailer.getPassengers().size() > 0)
            {
                return super.getModifiedAccelerationSpeed() * 0.5F;
            }
            else
            {
                return super.getModifiedAccelerationSpeed() * 0.8F;
            }
        }
        return super.getModifiedAccelerationSpeed();
    }

    @Override
    public float getModifiedRotationYaw()
    {
        return this.rotationYaw - this.additionalYaw;
    }
}
