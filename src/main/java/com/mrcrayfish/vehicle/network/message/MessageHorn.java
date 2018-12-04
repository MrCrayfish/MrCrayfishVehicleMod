package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.entity.EntityPoweredVehicle;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageHorn implements IMessage, IMessageHandler<MessageHorn, IMessage>
{
	private boolean horn;

	public MessageHorn() {}

	public MessageHorn(boolean horn)
	{
		this.horn = horn;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeBoolean(horn);
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.horn = buf.readBoolean();
	}

	@Override
	public IMessage onMessage(MessageHorn message, MessageContext ctx)
	{
	    FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() ->
        {
            Entity riding = ctx.getServerHandler().player.getRidingEntity();
            if(riding instanceof EntityPoweredVehicle)
            {
                ((EntityPoweredVehicle) riding).setHorn(message.horn);
            }
        });
		return null;
	}
}
