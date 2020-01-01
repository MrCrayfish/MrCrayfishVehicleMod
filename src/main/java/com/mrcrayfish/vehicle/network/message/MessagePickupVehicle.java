package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.common.CommonEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessagePickupVehicle implements IMessage<MessagePickupVehicle>
{
    private int entityId;

    public MessagePickupVehicle()
    {
    }

    public MessagePickupVehicle(Entity targetEntity)
    {
        this.entityId = targetEntity.getEntityId();
    }

    public MessagePickupVehicle(int entityId)
    {
        this.entityId = entityId;
    }

    @Override
    public void encode(MessagePickupVehicle message, PacketBuffer buffer)
    {
        buffer.writeInt(message.entityId);
    }

    @Override
    public MessagePickupVehicle decode(PacketBuffer buffer)
    {
        return new MessagePickupVehicle(buffer.readInt());
    }

    @Override
    public void handle(MessagePickupVehicle message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() -> {
            ServerPlayerEntity player = supplier.get().getSender();
            if(player != null && player.isCrouching())
            {
                Entity targetEntity = player.world.getEntityByID(message.entityId);
                if(targetEntity != null)
                {
                    CommonEvents.pickUpVehicle(player.world, player, Hand.MAIN_HAND, targetEntity);
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}