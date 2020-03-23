package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.VehicleMod;
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
        this.compound = storageInventory.write();
    }

    private MessageSyncInventory(int entityId, CompoundNBT compound)
    {
        this.entityId = entityId;
        this.compound = compound;
    }

    @Override
    public void encode(MessageSyncInventory message, PacketBuffer buffer)
    {
        buffer.writeVarInt(message.entityId);
        buffer.writeCompoundTag(message.compound);
    }

    @Override
    public MessageSyncInventory decode(PacketBuffer buffer)
    {
        return new MessageSyncInventory(buffer.readVarInt(), buffer.readCompoundTag());
    }

    @Override
    public void handle(MessageSyncInventory message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() -> VehicleMod.PROXY.syncStorageInventory(message.entityId, message.compound));
        supplier.get().setPacketHandled(true);
    }
}
