package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageFuelVehicle implements IMessage<MessageFuelVehicle>
{
    protected int entityId;
    private Hand hand;

    public MessageFuelVehicle()
    {
    }

    public MessageFuelVehicle(int entityId, Hand hand)
    {
        this.entityId = entityId;
        this.hand = hand;
    }

    @Override
    public void encode(MessageFuelVehicle message, PacketBuffer buffer)
    {
        buffer.writeInt(message.entityId);
        buffer.writeEnum(message.hand);
    }

    @Override
    public MessageFuelVehicle decode(PacketBuffer buffer)
    {
        return new MessageFuelVehicle(buffer.readInt(), buffer.readEnum(Hand.class));
    }

    @Override
    public void handle(MessageFuelVehicle message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() -> {
            ServerPlayerEntity player = supplier.get().getSender();
            if(player != null)
            {
                Entity targetEntity = player.level.getEntity(message.entityId);
                if(targetEntity instanceof PoweredVehicleEntity)
                {
                    ((PoweredVehicleEntity) targetEntity).fuelVehicle(player, message.hand);
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}