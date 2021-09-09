package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.network.play.ServerPlayHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageThrowVehicle implements IMessage<MessageThrowVehicle>
{
    @Override
    public void encode(MessageThrowVehicle message, PacketBuffer buffer) {}

    @Override
    public MessageThrowVehicle decode(PacketBuffer buffer)
    {
        return new MessageThrowVehicle();
    }

    @Override
    public void handle(MessageThrowVehicle message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() ->
        {
            ServerPlayerEntity player = supplier.get().getSender();
            if(player != null)
            {
                ServerPlayHandler.handleThrowVehicle(player, message);
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
