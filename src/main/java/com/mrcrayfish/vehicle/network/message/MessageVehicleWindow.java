package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.VehicleMod;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageVehicleWindow implements IMessage<MessageVehicleWindow>
{
    private int windowId;
    private int entityId;

    public MessageVehicleWindow() {}

    public MessageVehicleWindow(int windowId, int entityId)
    {
        this.windowId = windowId;
        this.entityId = entityId;
    }

    @Override
    public void encode(MessageVehicleWindow message, PacketBuffer buffer)
    {
        buffer.writeInt(message.windowId);
        buffer.writeInt(message.entityId);
    }

    @Override
    public MessageVehicleWindow decode(PacketBuffer buffer)
    {
        return new MessageVehicleWindow(buffer.readInt(), buffer.readInt());
    }

    @Override
    public void handle(MessageVehicleWindow message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() -> VehicleMod.PROXY.openVehicleEditWindow(message.entityId, message.windowId));
        supplier.get().setPacketHandled(true);
    }
}
