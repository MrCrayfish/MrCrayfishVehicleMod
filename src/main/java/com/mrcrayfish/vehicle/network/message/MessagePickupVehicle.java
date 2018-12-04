package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.common.CommonEvents;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Author: MrCrayfish
 */
public class MessagePickupVehicle extends MessageVehicleInteract implements IMessageHandler<MessagePickupVehicle, IMessage>
{
    public MessagePickupVehicle() {}

    public MessagePickupVehicle(Entity targetEntity)
    {
        super(targetEntity);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        super.toBytes(buf);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        super.fromBytes(buf);
    }

    @Override
    public IMessage onMessage(MessagePickupVehicle message, MessageContext ctx)
    {
        FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() ->
        {
            EntityPlayer player = ctx.getServerHandler().player;
            MinecraftServer server = player.world.getMinecraftServer();
            if(server != null && player.isSneaking())
            {
                Entity targetEntity = server.getEntityFromUuid(message.targetEntityID);
                if (targetEntity != null)
                {
                    CommonEvents.pickUpVehicle(player.world, player, EnumHand.MAIN_HAND, targetEntity);
                }
            }
        });
        return null;
    }
}