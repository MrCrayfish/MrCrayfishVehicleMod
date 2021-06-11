package com.mrcrayfish.vehicle.network.message;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public interface IMessage<T>
{
    void encode(T message, PacketBuffer buffer);

    T decode(PacketBuffer buffer);

    void handle(T message, Supplier<NetworkEvent.Context> supplier);

    static void enqueueTask(Supplier<NetworkEvent.Context> supplier, Runnable runnable)
    {
        supplier.get().enqueueWork(runnable);
        supplier.get().setPacketHandled(true);
    }
}
