package com.mrcrayfish.vehicle.common;

import com.google.common.collect.HashBiMap;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageSyncPlayerSeat;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.PacketDistributor;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.UUID;

/**
 * Author: MrCrayfish
 */
public class SeatTracker
{
    private final int maxSeatSize;
    private HashBiMap<UUID, Integer> playerSeatMap = HashBiMap.create();
    private WeakReference<VehicleEntity> vehicleRef;

    public SeatTracker(VehicleEntity entity)
    {
        this.maxSeatSize = entity.getProperties().getSeats().size();
        this.vehicleRef = new WeakReference<>(entity);
    }

    public int getSeatIndex(UUID uuid)
    {
        if(this.playerSeatMap.containsKey(uuid))
        {
            return this.playerSeatMap.getOrDefault(uuid, -1);
        }
        return -1;
    }

    /**
     * Sets the seat index for the corresponding player uuid. If the uuid already exists
     * in the seating map, it will automatically be updated to the new index.
     *
     * @param index the index of the seat
     * @param uuid the uuid of the player
     */
    public void setSeatIndex(int index, UUID uuid)
    {
        if(index < 0 || index >= this.maxSeatSize)
            return;
        this.playerSeatMap.forcePut(uuid, index);
        VehicleEntity vehicle = this.vehicleRef.get();
        if(vehicle != null && !vehicle.world.isRemote)
        {
            PacketHandler.instance.send(PacketDistributor.TRACKING_ENTITY.with(() -> vehicle), new MessageSyncPlayerSeat(vehicle.getEntityId(), index, uuid));
        }
    }

    public boolean isSeatAvailable(int index)
    {
        if(index < 0 || index >= this.maxSeatSize)
            return false;
        if(!this.playerSeatMap.inverse().containsKey(index))
            return true;
        VehicleEntity vehicle = this.vehicleRef.get();
        if(vehicle != null)
        {
            UUID uuid = this.playerSeatMap.inverse().get(index);
            return vehicle.getPassengers().stream().noneMatch(entity -> entity.getUniqueID().equals(uuid));
        }
        return false;
    }

    public void remove(UUID uuid)
    {
        this.playerSeatMap.remove(uuid);
    }

    public int getNextAvailableSeat()
    {
        VehicleEntity vehicle = this.vehicleRef.get();
        if(vehicle != null && !vehicle.world.isRemote)
        {
            VehicleProperties properties = vehicle.getProperties();
            List<Seat> seats = properties.getSeats();
            for(int i = 0; i < seats.size(); i++)
            {
                if(!this.playerSeatMap.values().contains(i))
                {
                    return i;
                }
                UUID uuid = this.playerSeatMap.inverse().get(i);
                if(vehicle.getPassengers().stream().noneMatch(entity -> entity.getUniqueID().equals(uuid)))
                {
                    this.playerSeatMap.remove(uuid);
                    return i;
                }
            }
        }
        return -1;
    }
}
