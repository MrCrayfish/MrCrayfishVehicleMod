package com.mrcrayfish.vehicle.network.message;

import java.util.UUID;

import com.mrcrayfish.vehicle.common.CommonEvents;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Author: MrCrayfish
 */
public class MessagePickupVehicle implements IMessage, IMessageHandler<MessagePickupVehicle, IMessage>
{
    private UUID targetEntityID;

    public MessagePickupVehicle() {}

    public MessagePickupVehicle(Entity targetEntity)
    {
        targetEntityID = targetEntity.getUniqueID();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, targetEntityID.toString());
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        targetEntityID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
    }

    @Override
    public IMessage onMessage(MessagePickupVehicle message, MessageContext ctx)
    {
        EntityPlayer player = ctx.getServerHandler().player;
        MinecraftServer server = player.world.getMinecraftServer();
        if(server != null && player.isSneaking())
        {
            server.addScheduledTask(() ->
            {
                Entity targetEntity = server.getEntityFromUuid(message.targetEntityID);
                if (targetEntity != null)
                {
                    CommonEvents.pickUpVehicle(player.world, player, EnumHand.MAIN_HAND, targetEntity);
                }
            });
        }
        return null;
    }
}