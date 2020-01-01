package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.VehicleMod;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageStorageWindow implements IMessage<MessageStorageWindow>
{
    private int windowId;
    private int entityId;

    public MessageStorageWindow() {}

    public MessageStorageWindow(int windowId, int entityId)
    {
        this.windowId = windowId;
        this.entityId = entityId;
    }

    @Override
    public void encode(MessageStorageWindow message, PacketBuffer buffer)
    {
        buffer.writeInt(message.windowId);
        buffer.writeInt(message.entityId);
    }

    @Override
    public MessageStorageWindow decode(PacketBuffer buffer)
    {
        return new MessageStorageWindow(buffer.readInt(), buffer.readInt());
    }

    @Override
    public void handle(MessageStorageWindow message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() -> VehicleMod.PROXY.openStorageWindow(message.entityId, message.windowId));
        supplier.get().setPacketHandled(true);
    }
}
