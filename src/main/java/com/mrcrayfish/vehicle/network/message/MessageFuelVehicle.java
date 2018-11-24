package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.entity.EntityPoweredVehicle;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageFuelVehicle extends MessageVehicleInteract implements IMessageHandler<MessageFuelVehicle, IMessage>
{
    private EnumHand hand;

    public MessageFuelVehicle() {}

    public MessageFuelVehicle(EntityPlayer player, EnumHand hand, Entity targetEntity)
    {
        super(targetEntity);
        this.hand = hand;
        fuelVehicle(player, hand, targetEntity);
    }

    private void fuelVehicle(EntityPlayer player, EnumHand hand, Entity targetEntity)
    {
        if (targetEntity instanceof EntityPoweredVehicle)
        {
            ((EntityPoweredVehicle) targetEntity).fuelVehicle(player, hand);
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        super.toBytes(buf);
        buf.writeInt(hand.ordinal());
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        super.fromBytes(buf);
        hand = EnumHand.values()[buf.readInt()];
    }

    @Override
    public IMessage onMessage(MessageFuelVehicle message, MessageContext ctx)
    {
        FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() ->
        {
            EntityPlayer player = ctx.getServerHandler().player;
            MinecraftServer server = player.world.getMinecraftServer();
            if(server != null)
            {
                Entity targetEntity = server.getEntityFromUuid(message.targetEntityID);
                if (targetEntity != null)
                {
                    fuelVehicle(player, message.hand, targetEntity);
                }
            }
        });
        return null;
    }
}