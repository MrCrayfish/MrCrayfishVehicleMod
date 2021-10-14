package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.network.play.ServerPlayHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageInteractCosmetic implements IMessage<MessageInteractCosmetic>
{
    private int entityId;
    private ResourceLocation cosmeticId;

    public MessageInteractCosmetic() {}

    public MessageInteractCosmetic(int entityId, ResourceLocation cosmeticId)
    {
        this.entityId = entityId;
        this.cosmeticId = cosmeticId;
    }

    @Override
    public void encode(MessageInteractCosmetic message, PacketBuffer buffer)
    {
        buffer.writeInt(message.entityId);
        buffer.writeResourceLocation(message.cosmeticId);
    }

    @Override
    public MessageInteractCosmetic decode(PacketBuffer buffer)
    {
        return new MessageInteractCosmetic(buffer.readInt(), buffer.readResourceLocation());
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void handle(MessageInteractCosmetic message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() ->
        {
            ServerPlayerEntity player = supplier.get().getSender();
            if(player != null)
            {
                ServerPlayHandler.handleInteractCosmeticMessage(player, message);
            }
        });
        supplier.get().setPacketHandled(true);
    }

    public int getEntityId()
    {
        return this.entityId;
    }

    public ResourceLocation getCosmeticId()
    {
        return this.cosmeticId;
    }
}
