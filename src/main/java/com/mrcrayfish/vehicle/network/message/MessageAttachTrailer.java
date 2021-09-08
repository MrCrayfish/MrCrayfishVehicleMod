package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.obfuscate.common.data.SyncedPlayerData;
import com.mrcrayfish.vehicle.entity.TrailerEntity;
import com.mrcrayfish.vehicle.init.ModDataKeys;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageAttachTrailer implements IMessage<MessageAttachTrailer>
{
    private int trailerId;

    public MessageAttachTrailer() {}

    public MessageAttachTrailer(int trailerId)
    {
        this.trailerId = trailerId;
    }

    @Override
    public void encode(MessageAttachTrailer message, PacketBuffer buffer)
    {
        buffer.writeInt(message.trailerId);
    }

    @Override
    public MessageAttachTrailer decode(PacketBuffer buffer)
    {
        return new MessageAttachTrailer(buffer.readInt());
    }

    @Override
    public void handle(MessageAttachTrailer message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() ->
        {
            ServerPlayerEntity player = supplier.get().getSender();
            if(player != null)
            {
                Entity trailerEntity = player.level.getEntity(message.trailerId);
                if(trailerEntity instanceof TrailerEntity)
                {
                    TrailerEntity trailer = (TrailerEntity) trailerEntity;
                    if(player.getVehicle() == null)
                    {
                        trailer.setPullingEntity(player);
                        SyncedPlayerData.instance().set(player, ModDataKeys.TRAILER, message.trailerId);
                    }
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
