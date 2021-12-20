package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.network.play.ServerPlayHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageSetSeat implements IMessage<MessageSetSeat>
{
    private int index;

    public MessageSetSeat() {}

    public MessageSetSeat(int index)
    {
        this.index = index;
    }

    @Override
    public void encode(MessageSetSeat message, PacketBuffer buffer)
    {
        buffer.writeInt(message.index);
    }

    @Override
    public MessageSetSeat decode(PacketBuffer buffer)
    {
        return new MessageSetSeat(buffer.readInt());
    }

    @Override
    public void handle(MessageSetSeat message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() ->
        {
            ServerPlayerEntity player = supplier.get().getSender();
            if(player != null)
            {
                ServerPlayHandler.handleSetSeatMessage(player, message);
            }
        });
        supplier.get().setPacketHandled(true);
    }

    public int getIndex()
    {
        return this.index;
    }
}
