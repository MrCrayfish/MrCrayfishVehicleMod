package com.mrcrayfish.vehicle.tileentity;

import com.google.common.base.Optional;
import com.mrcrayfish.vehicle.VehicleConfig;
import com.mrcrayfish.vehicle.client.util.HermiteInterpolator;
import com.mrcrayfish.vehicle.common.CommonEvents;
import com.mrcrayfish.vehicle.common.entity.SyncedPlayerData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class TileEntityGasPump extends TileEntitySynced implements ITickable
{
    private int fuelingEntityId;
    private EntityPlayer fuelingEntity;

    private HermiteInterpolator cachedSpline;
    private boolean recentlyUsed;

    public HermiteInterpolator getCachedSpline()
    {
        return cachedSpline;
    }

    public void setCachedSpline(HermiteInterpolator cachedSpline)
    {
        this.cachedSpline = cachedSpline;
    }

    public boolean isRecentlyUsed()
    {
        return recentlyUsed;
    }

    public void setRecentlyUsed(boolean recentlyUsed)
    {
        this.recentlyUsed = recentlyUsed;
    }

    @Nullable
    public FluidTank getTank()
    {
        TileEntity tileEntity = world.getTileEntity(pos.down());
        if(tileEntity instanceof TileEntityGasPumpTank)
        {
            return ((TileEntityGasPumpTank) tileEntity).getFluidTank();
        }
        return null;
    }

    public EntityPlayer getFuelingEntity()
    {
        return fuelingEntity;
    }

    public void setFuelingEntity(@Nullable EntityPlayer entity)
    {
        if(!world.isRemote)
        {
            if(fuelingEntity != null)
            {
                SyncedPlayerData.setGasPumpPos(this.fuelingEntity, Optional.absent());
            }
            this.fuelingEntity = null;
            this.fuelingEntityId = -1;
            if(entity != null)
            {
                this.fuelingEntityId = entity.getEntityId();
                SyncedPlayerData.setGasPumpPos(entity, Optional.fromNullable(this.getPos()));
            }
            this.syncToClient();
        }
    }

    @Override
    public void update()
    {
        if(fuelingEntityId != -1)
        {
            if(fuelingEntity == null)
            {
                Entity entity = world.getEntityByID(fuelingEntityId);
                if(entity instanceof EntityPlayer)
                {
                    fuelingEntity = (EntityPlayer) entity;
                }
                else if(!world.isRemote)
                {
                    fuelingEntityId = -1;
                    this.syncToClient();
                }
            }
        }
        else if(world.isRemote && fuelingEntity != null)
        {
            fuelingEntity = null;
        }

        if(!world.isRemote && fuelingEntity != null)
        {
            if(Math.sqrt(fuelingEntity.getDistanceSq(this.getPos())) > VehicleConfig.SERVER.maxHoseDistance || fuelingEntity.isDead)
            {
                if(!fuelingEntity.isDead)
                {
                    world.playSound(null, fuelingEntity.getPosition(), SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 1.0F, 1.0F);
                }
                SyncedPlayerData.setGasPumpPos(this.fuelingEntity, Optional.absent());
                fuelingEntityId = -1;
                fuelingEntity = null;
                this.syncToClient();
                //TODO add breaking sound like when a trailer disconnects
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        if(compound.hasKey("fuelingEntityId", Constants.NBT.TAG_INT))
        {
            this.fuelingEntityId = compound.getInteger("fuelingEntityId");
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        return super.writeToNBT(compound);
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        NBTTagCompound tagCompound = super.writeToNBT(new NBTTagCompound());
        tagCompound.setInteger("fuelingEntityId", this.fuelingEntityId);
        return tagCompound;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return INFINITE_EXTENT_AABB;
    }

    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared()
    {
        return 65536.0D;
    }
}
