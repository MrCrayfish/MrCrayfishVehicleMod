package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.init.ModEntities;
import com.mrcrayfish.vehicle.tileentity.JackTileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

/**
 * Author: MrCrayfish
 */
public class EntityJack extends Entity implements IEntityAdditionalSpawnData
{
    private double initialX;
    private double initialY;
    private double initialZ;
    private boolean activated = false;
    private int liftProgress;

    public EntityJack(World worldIn)
    {
        super(ModEntities.JACK, worldIn);
        this.setNoGravity(true);
        this.noClip = true;
    }

    public EntityJack(World worldIn, BlockPos pos, double yOffset, float yaw)
    {
        this(worldIn);
        this.setPosition(pos.getX() + 0.5, pos.getY() + yOffset, pos.getZ() + 0.5);
        this.setRotation(yaw, 0F);
        this.initialX = pos.getX() + 0.5;
        this.initialY = pos.getY() + yOffset;
        this.initialZ = pos.getZ() + 0.5;
    }

    @Override
    protected void registerData()
    {

    }

    @Override
    public void tick()
    {
        super.tick();

        if(!world.isRemote && this.getPassengers().size() == 0)
        {
            this.remove();
        }

        if(!this.isAlive())
            return;

        if(!this.activated && this.getPassengers().size() > 0)
        {
            this.activated = true;
        }

        if(this.activated)
        {
            if(this.liftProgress < 10)
            {
                this.liftProgress++;
            }
        }
        else if(this.liftProgress > 0)
        {
            this.liftProgress--;
        }

        TileEntity tileEntity = this.world.getTileEntity(new BlockPos(this.initialX, this.initialY, this.initialZ));
        if(tileEntity instanceof JackTileEntity)
        {
            JackTileEntity jackTileEntity = (JackTileEntity) tileEntity;
            this.setPosition(this.initialX, this.initialY + 0.5 * (jackTileEntity.liftProgress / (double) JackTileEntity.MAX_LIFT_PROGRESS), this.initialZ);
        }
    }

    @Override
    protected void addPassenger(Entity passenger)
    {
        super.addPassenger(passenger);
        if(this.getPassengers().contains(passenger))
        {
            passenger.prevPosX = this.func_226277_ct_();
            passenger.prevPosY = this.func_226278_cu_();
            passenger.prevPosZ = this.func_226281_cx_();
            passenger.lastTickPosX = this.func_226277_ct_();
            passenger.lastTickPosY = this.func_226278_cu_();
            passenger.lastTickPosZ = this.func_226281_cx_();
        }
    }

    @Override
    public IPacket<?> createSpawnPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void updatePassenger(Entity passenger)
    {
        if(passenger instanceof VehicleEntity)
        {
            VehicleEntity vehicle = (VehicleEntity) passenger;
            Vec3d heldOffset = vehicle.getProperties().getHeldOffset().rotateYaw(passenger.rotationYaw * 0.017453292F);
            vehicle.setPosition(this.func_226277_ct_() - heldOffset.z * 0.0625, this.func_226278_cu_() - heldOffset.y * 0.0625 - 2 * 0.0625, this.func_226281_cx_() - heldOffset.x * 0.0625);
        }
    }

    @Override
    protected void readAdditional(CompoundNBT compound)
    {
        this.initialX = compound.getDouble("initialX");
        this.initialY = compound.getDouble("initialY");
        this.initialZ = compound.getDouble("initialZ");
    }

    @Override
    protected void writeAdditional(CompoundNBT compound)
    {
        compound.putDouble("initialX", this.initialX);
        compound.putDouble("initialY", this.initialY);
        compound.putDouble("initialZ", this.initialZ);
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer)
    {
        buffer.writeDouble(this.initialX);
        buffer.writeDouble(this.initialY);
        buffer.writeDouble(this.initialZ);
    }

    @Override
    public void readSpawnData(PacketBuffer buffer)
    {
        this.initialX = buffer.readDouble();
        this.initialY = buffer.readDouble();
        this.initialZ = buffer.readDouble();
        this.setLocationAndAngles(this.initialX, this.initialY, this.initialZ, this.rotationYaw, this.rotationPitch);
        this.prevPosX = this.initialX;
        this.prevPosY = this.initialY;
        this.prevPosZ = this.initialZ;
        this.lastTickPosX = this.initialX;
        this.lastTickPosY = this.initialY;
        this.lastTickPosZ = this.initialZ;
    }
}
