package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.network.play.ClientPlayHandler;
import net.minecraft.nbt.CompoundNBT;
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
public class MessageSyncActionData implements IMessage<MessageSyncActionData>
{
    private int entityId;
    private ResourceLocation cosmeticId;
    private List<Pair<ResourceLocation, CompoundNBT>> actionData;

    public MessageSyncActionData() {}

    public MessageSyncActionData(int entityId, ResourceLocation cosmeticId, List<Pair<ResourceLocation, CompoundNBT>> actionData)
    {
        this.entityId = entityId;
        this.cosmeticId = cosmeticId;
        this.actionData = actionData;
    }

    @Override
    public void encode(MessageSyncActionData message, PacketBuffer buffer)
    {
        buffer.writeInt(message.entityId);
        buffer.writeResourceLocation(message.cosmeticId);
        buffer.writeInt(message.actionData.size());
        message.actionData.forEach(pair -> {
            buffer.writeResourceLocation(pair.getLeft());
            buffer.writeNbt(pair.getRight());
        });
    }

    @Override
    public MessageSyncActionData decode(PacketBuffer buffer)
    {
        int entityId = buffer.readInt();
        ResourceLocation cosmeticId = buffer.readResourceLocation();
        List<Pair<ResourceLocation, CompoundNBT>> actionData = new ArrayList<>();
        int size = buffer.readInt();
        for(int i = 0; i < size; i++)
        {
            ResourceLocation actionId = buffer.readResourceLocation();
            CompoundNBT data = buffer.readNbt();
            actionData.add(Pair.of(actionId, data));
        }
        return new MessageSyncActionData(entityId, cosmeticId, actionData);
    }

    @Override
    public void handle(MessageSyncActionData message, Supplier<NetworkEvent.Context> supplier)
    {
        if(supplier.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT)
        {
            IMessage.enqueueTask(supplier, () -> ClientPlayHandler.handleSyncActionData(message));
        }
    }

    public int getEntityId()
    {
        return this.entityId;
    }

    public ResourceLocation getCosmeticId()
    {
        return this.cosmeticId;
    }

    public List<Pair<ResourceLocation, CompoundNBT>> getActionData()
    {
        return this.actionData;
    }
}
