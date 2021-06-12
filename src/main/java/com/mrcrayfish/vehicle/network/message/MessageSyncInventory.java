package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.client.network.ClientPlayHandler;
import com.mrcrayfish.vehicle.common.inventory.StorageInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageSyncInventory implements IMessage<MessageSyncInventory>
{
    private int entityId;
    private CompoundNBT compound;

    public MessageSyncInventory() {}

    public MessageSyncInventory(int entityId, StorageInventory storageInventory)
    {
        this.entityId = entityId;
        CompoundNBT tag = new CompoundNBT();
        tag.put("Inventory", storageInventory.createTag());
        this.compound = tag;
    }

    private MessageSyncInventory(int entityId, CompoundNBT compound)
    {
        this.entityId = entityId;
        this.compound = compound;
    }

    @Override
    public void encode(MessageSyncInventory message, PacketBuffer buffer)
    {
        buffer.writeInt(message.entityId);
        buffer.writeNbt(message.compound);
    }

    @Override
    public MessageSyncInventory decode(PacketBuffer buffer)
    {
        return new MessageSyncInventory(buffer.readInt(), buffer.readNbt());
    }

    @Override
    public void handle(MessageSyncInventory message, Supplier<NetworkEvent.Context> supplier)
    {
        IMessage.enqueueTask(supplier, () -> ClientPlayHandler.handleSyncInventory(message));
    }

    public int getEntityId()
    {
        return this.entityId;
    }

    public CompoundNBT getCompound()
    {
        return this.compound;
    }
}
