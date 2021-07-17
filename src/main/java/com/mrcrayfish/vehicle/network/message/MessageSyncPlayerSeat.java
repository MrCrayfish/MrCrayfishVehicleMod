package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.client.network.ClientPlayHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageSyncPlayerSeat implements IMessage<MessageSyncPlayerSeat>
{
    private int entityId;
    private int seatIndex;
    private UUID uuid;

    public MessageSyncPlayerSeat() {}

    public MessageSyncPlayerSeat(int entityId, int seatIndex, UUID uuid)
    {
        this.entityId = entityId;
        this.seatIndex = seatIndex;
        this.uuid = uuid;
    }

    @Override
    public void encode(MessageSyncPlayerSeat message, PacketBuffer buffer)
    {
        buffer.writeVarInt(message.entityId);
        buffer.writeVarInt(message.seatIndex);
        buffer.writeUUID(message.uuid);
    }

    @Override
    public MessageSyncPlayerSeat decode(PacketBuffer buffer)
    {
        return new MessageSyncPlayerSeat(buffer.readVarInt(), buffer.readVarInt(), buffer.readUUID());
    }

    @Override
    public void handle(MessageSyncPlayerSeat message, Supplier<NetworkEvent.Context> supplier)
    {
        if(supplier.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT)
        {
            IMessage.enqueueTask(supplier, () -> ClientPlayHandler.handleSyncPlayerSeat(message));
        }
    }

    public int getEntityId()
    {
        return this.entityId;
    }

    public int getSeatIndex()
    {
        return this.seatIndex;
    }

    public UUID getUuid()
    {
        return this.uuid;
    }
}
