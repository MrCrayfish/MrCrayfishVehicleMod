package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.network.play.ServerPlayHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageInteractKey implements IMessage<MessageInteractKey>
{
    private int entityId;

    public MessageInteractKey()
    {
    }

    public MessageInteractKey(Entity targetEntity)
    {
        this.entityId = targetEntity.getId();
    }

    private MessageInteractKey(int entityId)
    {
        this.entityId = entityId;
    }

    @Override
    public void encode(MessageInteractKey message, PacketBuffer buffer)
    {
        buffer.writeInt(message.entityId);
    }

    @Override
    public MessageInteractKey decode(PacketBuffer buffer)
    {
        return new MessageInteractKey(buffer.readInt());
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void handle(MessageInteractKey message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() ->
        {
            ServerPlayerEntity player = supplier.get().getSender();
            if(player != null)
            {
                ServerPlayHandler.handleInteractKeyMessage(player, message);
            }
        });
        supplier.get().setPacketHandled(true);
    }

    public int getEntityId()
    {
        return this.entityId;
    }
}
