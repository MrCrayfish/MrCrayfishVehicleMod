package com.mrcrayfish.vehicle.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.capability.TileFluidHandler;

public class TileFluidHandlerSynced extends TileFluidHandler
{
    protected void syncToClient()
    {
        markDirty();
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