package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.common.inventory.IStorage;
import com.mrcrayfish.vehicle.common.inventory.StorageInventory;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.network.play.ClientPlayHandler;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageSyncStorage implements IMessage<MessageSyncStorage>
{
    private int entityId;
    private String[] keys;
    private CompoundNBT[] tags;

    public MessageSyncStorage() {}

    public <T extends VehicleEntity & IStorage> MessageSyncStorage(T vehicle, String ... keys)
    {
        this.entityId = vehicle.getId();
        this.keys = keys;
        List<Pair<String, CompoundNBT>> tagList = new ArrayList<>();
        for(String key : keys)
        {
            StorageInventory inventory = vehicle.getStorageInventory(key);
            if(inventory != null)
            {
                CompoundNBT tag = new CompoundNBT();
                tag.put("Inventory", inventory.createTag());
                tagList.add(Pair.of(key, tag));
            }
        }
        this.keys = new String[tagList.size()];
        this.tags = new CompoundNBT[tagList.size()];
        for(int i = 0; i < tagList.size(); i++)
        {
            Pair<String, CompoundNBT> pair = tagList.get(i);
            this.keys[i] = pair.getLeft();
            this.tags[i] = pair.getRight();
        }
    }

    private MessageSyncStorage(int entityId, String[] keys, CompoundNBT[] tags)
    {
        this.entityId = entityId;
        this.keys = keys;
        this.tags = tags;
    }

    @Override
    public void encode(MessageSyncStorage message, PacketBuffer buffer)
    {
        buffer.writeInt(message.entityId);
        buffer.writeInt(message.keys.length);
        for(int i = 0; i < message.keys.length; i++)
        {
            buffer.writeUtf(message.keys[i]);
            buffer.writeNbt(message.tags[i]);
        }
    }

    @Override
    public MessageSyncStorage decode(PacketBuffer buffer)
    {
        int entityId = buffer.readInt();
        int keyLength = buffer.readInt();
        String[] keys = new String[keyLength];
        CompoundNBT[] tags = new CompoundNBT[keyLength];
        for(int i = 0; i < keyLength; i++)
        {
            keys[i] = buffer.readUtf();
            tags[i] = buffer.readNbt();
        }
        return new MessageSyncStorage(entityId, keys, tags);
    }

    @Override
    public void handle(MessageSyncStorage message, Supplier<NetworkEvent.Context> supplier)
    {
        IMessage.enqueueTask(supplier, () -> ClientPlayHandler.handleSyncStorage(message));
    }

    public int getEntityId()
    {
        return this.entityId;
    }

    public String[] getKeys()
    {
        return this.keys;
    }

    public CompoundNBT[] getTags()
    {
        return this.tags;
    }
}
