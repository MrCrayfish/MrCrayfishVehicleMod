package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.util.FluidUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.TileFluidHandler;

/**
 * Author: MrCrayfish
 */
public class TileEntityFuelDrum extends TileFluidHandler
{
    public TileEntityFuelDrum()
    {
        tank = new FluidTank(0)
        {
            @Override
            protected void onContentsChanged()
            {
                syncToClient();
            }
        };
    }

    public TileEntityFuelDrum(int capacity)
    {
        this();
        this.setCapacity(capacity);
    }

    public FluidTank getFluidTank()
    {
        return tank;
    }

    public boolean hasFluid()
    {
        return tank.getFluid() != null;
    }

    public int getAmount()
    {
        return tank.getFluidAmount();
    }

    public int getCapacity()
    {
        return tank.getCapacity();
    }

    private void setCapacity(int capacity)
    {
        tank.setCapacity(capacity);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        FluidUtils.fixEmptyTag(tag);
        super.readFromNBT(tag);
        if(tag.hasKey("capacity", Constants.NBT.TAG_INT))
        {
            this.setCapacity(tag.getInteger("capacity"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);
        tag.setInteger("capacity", tank.getCapacity());
        return tag;
    }

    private void syncToClient()
    {
        if(!world.isRemote)
        {
            if(world instanceof WorldServer)
            {
                WorldServer server = (WorldServer) world;
                PlayerChunkMapEntry entry = server.getPlayerChunkMap().getEntry(pos.getX() >> 4, pos.getZ() >> 4);
                if(entry != null)
                {
                    SPacketUpdateTileEntity packet = getUpdatePacket();
                    if(packet != null)
                    {
                        entry.sendPacket(packet);
                    }
                }
            }
        }
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(final NetworkManager net, final SPacketUpdateTileEntity pkt)
    {
        readFromNBT(pkt.getNbtCompound());
    }
}
