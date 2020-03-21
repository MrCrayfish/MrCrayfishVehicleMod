package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.common.entity.SyncedPlayerData;
import com.mrcrayfish.vehicle.entity.TrailerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageAttachTrailer implements IMessage<MessageAttachTrailer>
{
    private int trailerId;
    private int entityId;

    public MessageAttachTrailer() {}

    public MessageAttachTrailer(int trailerId, int entityId)
    {
        this.trailerId = trailerId;
        this.entityId = entityId;
    }

    @Override
    public void encode(MessageAttachTrailer message, PacketBuffer buffer)
    {
        buffer.writeInt(message.trailerId);
        buffer.writeInt(message.entityId);
    }

    @Override
    public MessageAttachTrailer decode(PacketBuffer buffer)
    {
        return new MessageAttachTrailer(buffer.readInt(), buffer.readInt());
    }

    @Override
    public void handle(MessageAttachTrailer message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() ->
        {
            ServerPlayerEntity player = supplier.get().getSender();
            if(player != null)
            {
                World world = player.world;
                Entity trailerEntity = world.getEntityByID(message.trailerId);
                if(trailerEntity instanceof TrailerEntity)
                {
                    TrailerEntity trailer = (TrailerEntity) trailerEntity;
                    Entity entity = world.getEntityByID(message.entityId);
                    if(entity instanceof PlayerEntity && entity.getRidingEntity() == null)
                    {
                        trailer.setPullingEntity(entity);
                        SyncedPlayerData.setTrailer(player, message.trailerId);
                    }
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
