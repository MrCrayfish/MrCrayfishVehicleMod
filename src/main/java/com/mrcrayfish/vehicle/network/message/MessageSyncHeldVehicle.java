package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.client.network.ClientPlayHandler;
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
        buffer.writeNbt(message.vehicleTag);
    }

    @Override
    public MessageSyncHeldVehicle decode(PacketBuffer buffer)
    {
        return new MessageSyncHeldVehicle(buffer.readVarInt(), buffer.readNbt());
    }

    @Override
    public void handle(MessageSyncHeldVehicle message, Supplier<NetworkEvent.Context> supplier)
    {
        if(supplier.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT)
        {
            IMessage.enqueueTask(supplier, () -> ClientPlayHandler.handleSyncHeldVehicle(message));
        }
    }

    public int getEntityId()
    {
        return this.entityId;
    }

    public CompoundNBT getVehicleTag()
    {
        return this.vehicleTag;
    }
}
