package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.entity.vehicle.EntityTrailer;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageDrift;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Author: MrCrayfish
 */
public abstract class EntityLandVehicle extends EntityPoweredVehicle
{
    private static final DataParameter<Boolean> DRIFTING = EntityDataManager.createKey(EntityPoweredVehicle.class, DataSerializers.BOOLEAN);

    public float drifting;
    public float additionalYaw;
    public float prevAdditionalYaw;

    public float frontWheelRotation;
    public float prevFrontWheelRotation;
    public float rearWheelRotation;
    public float prevRearWheelRotation;

    private EntityTrailer trailer = null;
    private Vec3d towBarVec = Vec3d.ZERO;

    @SideOnly(Side.CLIENT)
    public ItemStack towBar;

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
    public void onClientInit()
    {
        super.onClientInit();
        towBar = new ItemStack(ModItems.TOW_BAR);
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
    public void onUpdateVehicle()
    {
        super.onUpdateVehicle();

        if(trailer != null && (trailer.isDead || trailer.getPullingEntity() != this))
        {
            trailer = null;
        }
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
        float f1 = MathHelper.sin(this.rotationYaw * 0.017453292F) / 20F; //Divide by 20 ticks
        float f2 = MathHelper.cos(this.rotationYaw * 0.017453292F) / 20F;
        this.vehicleMotionX = (-currentSpeed * f1);
        this.motionY -= 0.08D;
        this.vehicleMotionZ = (currentSpeed * f2);
    }

    private void updateDrifting()
    {
        TurnDirection turnDirection = this.getTurnDirection();
        if(this.getControllingPassenger() != null && this.isDrifting() && turnDirection != TurnDirection.FORWARD)
        {
            AccelerationDirection acceleration = this.getAcceleration();
            if(acceleration == AccelerationDirection.FORWARD)
            {
                this.currentSpeed *= 0.95F;
            }
            this.drifting = Math.min(1.0F, this.drifting + (1.0F / 8.0F));
        }
        else
        {
            this.drifting *= 0.85F;
        }
        this.additionalYaw = 35F * (turnAngle / 45F) * drifting;

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
        int x = MathHelper.floor(this.posX);
        int y = MathHelper.floor(this.posY - 0.2D);
        int z = MathHelper.floor(this.posZ);
        BlockPos pos = new BlockPos(x, y, z);
        IBlockState state = this.world.getBlockState(pos);
        if(state.getMaterial() != Material.AIR && state.getMaterial().isToolNotRequired())
        {
            if(this.getAcceleration() == AccelerationDirection.FORWARD)
            {
                if(this.isDrifting())
                {
                    for(int i = 0; i < 3; i++)
                    {
                        this.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, this.posX + ((double) this.rand.nextFloat() - 0.5D) * (double) this.width, this.getEntityBoundingBox().minY + 0.1D, this.posZ + ((double) this.rand.nextFloat() - 0.5D) * (double) this.width, -this.motionX * 4.0D, 1.5D, -this.motionZ * 4.0D, Block.getStateId(state));
                    }
                }
                else
                {
                    this.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, this.posX + ((double) this.rand.nextFloat() - 0.5D) * (double) this.width, this.getEntityBoundingBox().minY + 0.1D, this.posZ + ((double) this.rand.nextFloat() - 0.5D) * (double) this.width, -this.motionX * 4.0D, 1.5D, -this.motionZ * 4.0D, Block.getStateId(state));
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

    public void setDrifting(boolean drifting)
    {
        this.dataManager.set(DRIFTING, drifting);
    }

    public boolean isDrifting()
    {
        return this.dataManager.get(DRIFTING);
    }

    public void setTowBarPosition(Vec3d towBarVec)
    {
        this.towBarVec = towBarVec;
    }

    public Vec3d getTowBarVec()
    {
        return towBarVec;
    }

    public boolean canTowTrailer()
    {
        return false;
    }

    public void setTrailer(EntityTrailer trailer)
    {
        this.trailer = trailer;
        trailer.setPullingEntity(this);
    }

    public EntityTrailer getTrailer()
    {
        return trailer;
    }
}
