package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.VehicleMod;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageSyncHeldVehicle implements IMessage<MessageSyncHeldVehicle>
{
    private int entityId;
    private CompoundNBT vehicleTag;

    public MessageSyncHeldVehicle() {}

    public MessageSyncHeldVehicle(int entityId, CompoundNBT vehicleTag)
    {
        this.entityId = entityId;
        this.vehicleTag = vehicleTag;
    }

    @Override
    public void encode(MessageSyncHeldVehicle message, PacketBuffer buffer)
    {
        buffer.writeVarInt(message.entityId);
        buffer.writeCompoundTag(message.vehicleTag);
    }

    @Override
    public MessageSyncHeldVehicle decode(PacketBuffer buffer)
    {
        return new MessageSyncHeldVehicle(buffer.readVarInt(), buffer.readCompoundTag());
    }

    @Override
    public void handle(MessageSyncHeldVehicle message, Supplier<NetworkEvent.Context> supplier)
    {
        if(supplier.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT)
        {
            supplier.get().enqueueWork(() -> VehicleMod.PROXY.syncHeldVehicle(message.entityId, message.vehicleTag));
        }
    }
}