package com.mrcrayfish.vehicle.common;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.mrcrayfish.vehicle.entity.EntityVehicle;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageSyncPlayerSeat;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.ByteBufUtils;

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
    private WeakReference<EntityVehicle> vehicleRef;

    public SeatTracker(EntityVehicle entity)
    {
        this.maxSeatSize = entity.getProperties().getSeats().size();
        this.vehicleRef = new WeakReference<>(entity);
    }

    public ImmutableMap<UUID, Integer> getPlayerSeatMap()
    {
        return ImmutableMap.copyOf(this.playerSeatMap);
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
        EntityVehicle vehicle = this.vehicleRef.get();
        if(vehicle != null && !vehicle.world.isRemote)
        {
            PacketHandler.INSTANCE.sendToAllTracking(new MessageSyncPlayerSeat(vehicle.getEntityId(), index, uuid), vehicle);
        }
    }

    public boolean isSeatAvailable(int index)
    {
        if(index < 0 || index >= this.maxSeatSize)
            return false;
        if(!this.playerSeatMap.inverse().containsKey(index))
            return true;
        EntityVehicle vehicle = this.vehicleRef.get();
        if(vehicle != null)
        {
            UUID uuid = this.playerSeatMap.inverse().get(index);
            return vehicle.getPassengers().stream().noneMatch(entity -> entity.getUniqueID().equals(uuid));
        }
        return false;
    }

    public int getNextAvailableSeat()
    {
        EntityVehicle vehicle = this.vehicleRef.get();
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

    public int getClosestAvailableSeatToPlayer(EntityPlayer player)
    {
        EntityVehicle vehicle = this.vehicleRef.get();
        if(vehicle != null && !vehicle.world.isRemote)
        {
            VehicleProperties properties = vehicle.getProperties();
            List<Seat> seats = properties.getSeats();

            /* If vehicle is full of passengers, no need to search */
            if(vehicle.getPassengers().size() == seats.size())
                return -1;

            int closestSeatIndex = -1;
            double closestDistance = 0;
            for(int i = 0; i < seats.size(); i++)
            {
                if(!this.isSeatAvailable(i))
                    continue;

                /* Get the real world distance to the seat and check if it's the closest */
                Seat seat = seats.get(i);
                Vec3d seatVec = seat.getPosition().addVector(0, properties.getAxleOffset() + properties.getWheelOffset(), 0).scale(properties.getBodyPosition().getScale()).scale(0.0625);
                seatVec = new Vec3d(-seatVec.x, seatVec.y, seatVec.z);
                seatVec = seatVec.rotateYaw(-(vehicle.getModifiedRotationYaw()) * 0.017453292F);
                seatVec = seatVec.add(vehicle.getPositionVector());
                double distance = player.getDistanceSq(seatVec.x, seatVec.y - player.height / 2F, seatVec.z); //TODO test
                if(closestSeatIndex == -1 || distance < closestDistance)
                {
                    closestSeatIndex = i;
                    closestDistance = distance;
                }
            }
            return closestSeatIndex;
        }
        return -1;
    }

    public NBTTagCompound write()
    {
        NBTTagCompound compound = new NBTTagCompound();
        NBTTagList list = new NBTTagList();
        this.playerSeatMap.forEach((uuid, seatIndex) -> {
            NBTTagCompound seatTag = new NBTTagCompound();
            seatTag.setUniqueId("UUID", uuid);
            seatTag.setInteger("SeatIndex", seatIndex);
            list.appendTag(seatTag);
        });
        compound.setTag("PlayerSeatMap", list);
        return compound;
    }

    public void read(NBTTagCompound compound)
    {
        if(compound.hasKey("PlayerSeatMap", Constants.NBT.TAG_LIST))
        {
            this.playerSeatMap.clear();
            NBTTagList list = compound.getTagList("PlayerSeatMap", Constants.NBT.TAG_COMPOUND);
            list.forEach(nbt -> {
                NBTTagCompound seatTag = (NBTTagCompound) nbt;
                UUID uuid = seatTag.getUniqueId("UUID");
                int seatIndex = seatTag.getInteger("SeatIndex");
                this.playerSeatMap.put(uuid, seatIndex);
            });
        }
    }

    public void write(ByteBuf buffer)
    {
        ByteBufUtils.writeVarInt(buffer, this.playerSeatMap.size(), 3);
        this.playerSeatMap.forEach((uuid, seatIndex) ->
        {
            buffer.writeLong(uuid.getMostSignificantBits());
            buffer.writeLong(uuid.getLeastSignificantBits());
            ByteBufUtils.writeVarInt(buffer, seatIndex, 3);
        });
    }

    public void read(ByteBuf buffer)
    {
        this.playerSeatMap.clear();
        int size = ByteBufUtils.readVarInt(buffer, 3);
        for(int i = 0; i < size; i++)
        {
            UUID uuid = new UUID(buffer.readLong(), buffer.readLong());
            int seatIndex = ByteBufUtils.readVarInt(buffer, 3);
            this.playerSeatMap.put(uuid, seatIndex);
        }
    }
}