package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.client.render.Wheel;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageDrift;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;

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
    public void updateVehicle()
    {
        prevAdditionalYaw = additionalYaw;
        prevFrontWheelRotation = frontWheelRotation;
        prevRearWheelRotation = rearWheelRotation;

        this.updateDrifting();
        this.updateWheels();
    }

    @Override
    public void onClientUpdate()
    {
        super.onClientUpdate();
        EntityLivingBase entity = (EntityLivingBase) this.getControllingPassenger();
        if(entity != null && entity.equals(Minecraft.getMinecraft().player))
        {
            boolean drifting = Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown();
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
        TurnDirection direction = this.getTurnDirection();
        if(this.getControllingPassenger() != null && direction != TurnDirection.FORWARD)
        {
            float amount = direction.dir * getTurnSensitivity();
            this.turnAngle += this.isDrifting() ? amount * 0.45 : amount;
            if(Math.abs(this.turnAngle) > getMaxTurnAngle())
            {
                this.turnAngle = getMaxTurnAngle() * direction.dir;
            }
        }
        else if(this.isDrifting())
        {
            this.turnAngle *= 0.95;
        }
        else
        {
            this.turnAngle *= 0.75;
        }

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
                this.drifting = Math.min(1.0F, this.drifting + 0.05F);
            }
        }
        else
        {
            this.drifting *= 0.85F;
        }
        this.additionalYaw = 25F * drifting * (turnAngle / (float) this.getMaxTurnAngle()) * Math.min(this.getActualMaxSpeed(), this.getActualSpeed() * 2F);

        //Updates the delta yaw to consider drifting
        this.deltaYaw = this.wheelAngle * (currentSpeed / 30F) / (this.isDrifting() ? 1.5F : 2F);
    }

    public void updateWheels()
    {
        float speedPercent = this.getNormalSpeed();
        AccelerationDirection acceleration = this.getAcceleration();
        if(this.getControllingPassenger() != null && acceleration == AccelerationDirection.FORWARD)
        {
            this.rearWheelRotation -= 68F * (1.0 - speedPercent);
        }
        this.frontWheelRotation -= (68F * speedPercent);
        this.rearWheelRotation -= (68F * speedPercent);
    }

    @Override
    public void createParticles()
    {
        if(!this.canDrive())
            return;

        if(this.getAcceleration() == AccelerationDirection.FORWARD)
        {
            VehicleProperties properties = this.getProperties();
            for(Wheel wheel : properties.getWheels())
            {
                PartPosition bodyPosition = properties.getBodyPosition();
                double wheelX = bodyPosition.getX();
                double wheelY = bodyPosition.getY();
                double wheelZ = bodyPosition.getZ();

                double scale = bodyPosition.getScale();

                /* Applies axel and wheel offets */
                wheelY += 0.5 * scale;
                wheelY += (properties.getAxleOffset() * 0.0625F) * scale;
                wheelY += (properties.getWheelOffset() * 0.0625F) * scale;

                /* Compensate offsets */
                wheelY -= 0.5 * scale;
                wheelY -= (properties.getAxleOffset() * 0.0625F) * scale;

                /* Wheels Translations */
                wheelX += ((wheel.getOffsetX() * 0.0625) * wheel.getSide().getOffset()) * scale;
                wheelY += (wheel.getOffsetY() * 0.0625) * scale;
                wheelZ += (wheel.getOffsetZ() * 0.0625) * scale;
                wheelX += ((((wheel.getWidth() * wheel.getScale()) / 2) * 0.0625) * wheel.getSide().getOffset()) * scale;

                /* Offsets the position to the wheel contact on the ground */
                wheelY -= ((5 * 0.0625) / 2.0) * wheel.getScale();

                /* Gets the block under the wheel and spawns a particle */
                Vec3d wheelVec = new Vec3d(wheelX, wheelY, wheelZ).rotateYaw(-(this.rotationYaw - this.additionalYaw) * 0.017453292F);
                int x = MathHelper.floor(this.posX + wheelVec.x);
                int y = MathHelper.floor(this.posY + wheelVec.y - 0.2D);
                int z = MathHelper.floor(this.posZ + wheelVec.z);
                BlockPos pos = new BlockPos(x, y, z);
                IBlockState state = this.world.getBlockState(pos);
                if(state.getMaterial() != Material.AIR && state.getMaterial().isToolNotRequired())
                {
                    this.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, this.posX + wheelVec.x, this.posY + wheelVec.y, this.posZ + wheelVec.z, 0.0D, 0.0D, 0.0D, Block.getStateId(state));
                }
            }
        }

        if(this.shouldShowEngineSmoke() && this.ticksExisted % 2 == 0)
        {
            Vec3d smokePosition = this.getEngineSmokePosition().rotateYaw(-(this.rotationYaw - this.additionalYaw) * 0.017453292F);
            this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX + smokePosition.x, this.posY + smokePosition.y, this.posZ + smokePosition.z, -this.motionX, 0.0D, -this.motionZ);
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
}
