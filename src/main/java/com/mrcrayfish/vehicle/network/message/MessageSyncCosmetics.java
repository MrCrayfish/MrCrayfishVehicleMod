package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.network.play.ClientPlayHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageSyncCosmetics implements IMessage<MessageSyncCosmetics>
{
    private int entityId;
    private List<Pair<ResourceLocation, ResourceLocation>> dirtyEntries;

    public MessageSyncCosmetics() {}

    public MessageSyncCosmetics(int entityId, List<Pair<ResourceLocation, ResourceLocation>> dirtyEntries)
    {
        this.entityId = entityId;
        this.dirtyEntries = dirtyEntries;
    }

    @Override
    public void encode(MessageSyncCosmetics message, PacketBuffer buffer)
    {
        buffer.writeInt(message.entityId);
        buffer.writeInt(message.dirtyEntries.size());
        message.dirtyEntries.forEach(pair -> {
            buffer.writeResourceLocation(pair.getLeft());
            buffer.writeResourceLocation(pair.getRight());
        });
    }

    @Override
    public MessageSyncCosmetics decode(PacketBuffer buffer)
    {
        int entityId = buffer.readInt();
        List<Pair<ResourceLocation, ResourceLocation>> dirtyEntries = new ArrayList<>();
        int size = buffer.readInt();
        for(int i = 0; i < size; i++)
        {
            ResourceLocation cosmeticId = buffer.readResourceLocation();
            ResourceLocation modelLocation = buffer.readResourceLocation();
            dirtyEntries.add(Pair.of(cosmeticId, modelLocation));
        }
        return new MessageSyncCosmetics(entityId, dirtyEntries);
    }

    @Override
    public void handle(MessageSyncCosmetics message, Supplier<NetworkEvent.Context> supplier)
    {
        if(supplier.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT)
        {
            IMessage.enqueueTask(supplier, () -> ClientPlayHandler.handleSyncCosmetics(message));
        }
    }

    public int getEntityId()
    {
        return this.entityId;
    }

    public List<Pair<ResourceLocation, ResourceLocation>> getDirtyEntries()
    {
        return this.dirtyEntries;
    }
}
