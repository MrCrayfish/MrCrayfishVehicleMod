package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.VehicleMod;
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
        buffer.writeUniqueId(message.uuid);
    }

    @Override
    public MessageSyncPlayerSeat decode(PacketBuffer buffer)
    {
        return new MessageSyncPlayerSeat(buffer.readVarInt(), buffer.readVarInt(), buffer.readUniqueId());
    }

    @Override
    public void handle(MessageSyncPlayerSeat message, Supplier<NetworkEvent.Context> supplier)
    {
        if(supplier.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT)
        {
            supplier.get().enqueueWork(() -> VehicleMod.PROXY.syncPlayerSeat(message.entityId, message.seatIndex, message.uuid));
            supplier.get().setPacketHandled(true);
        }
    }
}
