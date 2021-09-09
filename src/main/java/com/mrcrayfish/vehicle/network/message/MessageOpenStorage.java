package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.network.play.ServerPlayHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageOpenStorage implements IMessage<MessageOpenStorage>
{
    private int entityId;

    public MessageOpenStorage() {}

    public MessageOpenStorage(int entityId)
    {
        this.entityId = entityId;
    }

    @Override
    public void encode(MessageOpenStorage message, PacketBuffer buffer)
    {
        buffer.writeInt(message.entityId);
    }

    @Override
    public MessageOpenStorage decode(PacketBuffer buffer)
    {
        return new MessageOpenStorage(buffer.readInt());
    }

    @Override
    public void handle(MessageOpenStorage message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() ->
        {
            ServerPlayerEntity player = supplier.get().getSender();
            if(player != null)
            {
                ServerPlayHandler.handleOpenStorageMessage(player, message);
            }
        });
        supplier.get().setPacketHandled(true);
    }

    public int getEntityId()
    {
        return this.entityId;
    }
}
