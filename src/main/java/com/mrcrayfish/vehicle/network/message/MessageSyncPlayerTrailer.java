package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.VehicleMod;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageSyncPlayerTrailer implements IMessage<MessageSyncPlayerTrailer>
{
    private int entityId;
    private int trailerId;

    public MessageSyncPlayerTrailer() {}

    public MessageSyncPlayerTrailer(int entityId, int trailerId)
    {
        this.entityId = entityId;
        this.trailerId = trailerId;
    }

    @Override
    public void encode(MessageSyncPlayerTrailer message, PacketBuffer buffer)
    {
        buffer.writeVarInt(message.entityId);
        buffer.writeVarInt(message.trailerId);
    }

    @Override
    public MessageSyncPlayerTrailer decode(PacketBuffer buffer)
    {
        return new MessageSyncPlayerTrailer(buffer.readVarInt(), buffer.readVarInt());
    }

    @Override
    public void handle(MessageSyncPlayerTrailer message, Supplier<NetworkEvent.Context> supplier)
    {
        if(supplier.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT)
        {
            supplier.get().enqueueWork(() -> VehicleMod.PROXY.syncTrailer(message.entityId, message.trailerId));
        }
    }
}
