package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.network.play.ServerPlayHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageOpenMultiStorage implements IMessage<MessageOpenMultiStorage>
{
    private int entityId;
    private String key;

    public MessageOpenMultiStorage() {}

    public MessageOpenMultiStorage(int entityId, String key)
    {
        this.entityId = entityId;
        this.key = key;
    }

    @Override
    public void encode(MessageOpenMultiStorage message, PacketBuffer buffer)
    {
        buffer.writeInt(message.entityId);
        buffer.writeUtf(message.key);
    }

    @Override
    public MessageOpenMultiStorage decode(PacketBuffer buffer)
    {
        return new MessageOpenMultiStorage(buffer.readInt(), buffer.readUtf());
    }

    @Override
    public void handle(MessageOpenMultiStorage message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() ->
        {
            ServerPlayerEntity player = supplier.get().getSender();
            if(player != null)
            {
                ServerPlayHandler.handleOpenMultiStorageMessage(player, message);
            }
        });
        supplier.get().setPacketHandled(true);
    }

    public int getEntityId()
    {
        return this.entityId;
    }

    public String getKey()
    {
        return this.key;
    }
}
