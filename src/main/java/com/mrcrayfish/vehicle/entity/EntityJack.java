package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.tileentity.TileEntityJack;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

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
        super(worldIn);
        this.setSize(0F, 0F);
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
    public void onEntityUpdate()
    {
        super.onEntityUpdate();

        if(!world.isRemote && this.getPassengers().size() == 0)
        {
            this.setDead();
        }

        if(isDead)
            return;

        if(!activated && this.getPassengers().size() > 0)
        {
            activated = true;
        }

        if(activated)
        {
            if(liftProgress < 10)
            {
                liftProgress++;
            }
        }
        else if(liftProgress > 0)
        {
            liftProgress--;
        }

        TileEntity tileEntity = world.getTileEntity(new BlockPos(initialX, initialY, initialZ));
        if(tileEntity instanceof TileEntityJack)
        {
            TileEntityJack tileEntityJack = (TileEntityJack) tileEntity;
            this.setPosition(initialX, initialY + 0.5 * (tileEntityJack.liftProgress / (double) TileEntityJack.MAX_LIFT_PROGRESS), initialZ);
        }
    }

    @Override
    protected void addPassenger(Entity passenger)
    {
        super.addPassenger(passenger);
        if(this.getPassengers().contains(passenger))
        {
            passenger.prevPosX = posX;
            passenger.prevPosY = posY;
            passenger.prevPosZ = posZ;
            passenger.lastTickPosX = posX;
            passenger.lastTickPosY = posY;
            passenger.lastTickPosZ = posZ;
        }
    }

    @Override
    public void updatePassenger(Entity passenger)
    {
        if(passenger instanceof EntityVehicle)
        {
            EntityVehicle vehicle = (EntityVehicle) passenger;
            Vec3d heldOffset = vehicle.getProperties().getHeldOffset().rotateYaw(passenger.rotationYaw * 0.017453292F);
            vehicle.setPosition(posX - heldOffset.z * 0.0625, posY - heldOffset.y * 0.0625 - 2 * 0.0625, posZ - heldOffset.x * 0.0625);
        }
    }

    @Override
    protected void entityInit()
    {

    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound)
    {
        initialX = compound.getDouble("initialX");
        initialY = compound.getDouble("initialY");
        initialZ = compound.getDouble("initialZ");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound)
    {
        compound.setDouble("initialX", initialX);
        compound.setDouble("initialY", initialY);
        compound.setDouble("initialZ", initialZ);
    }

    @Override
    public void writeSpawnData(ByteBuf buffer)
    {
        buffer.writeDouble(initialX);
        buffer.writeDouble(initialY);
        buffer.writeDouble(initialZ);
    }

    @Override
    public void readSpawnData(ByteBuf additionalData)
    {
        initialX = additionalData.readDouble();
        initialY = additionalData.readDouble();
        initialZ = additionalData.readDouble();
        this.setLocationAndAngles(initialX, initialY, initialZ, rotationYaw, rotationPitch);
        prevPosX = initialX;
        prevPosY = initialY;
        prevPosZ = initialZ;
        lastTickPosX = initialX;
        lastTickPosY = initialY;
        lastTickPosZ = initialZ;
    }
}
