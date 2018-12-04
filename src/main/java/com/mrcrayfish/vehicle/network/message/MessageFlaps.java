package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.entity.EntityPlane;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageFlaps implements IMessage, IMessageHandler<MessageFlaps, IMessage>
{
	private EntityPlane.FlapDirection flapDirection;

	public MessageFlaps() {}

	public MessageFlaps(EntityPlane.FlapDirection flapDirection)
	{
		this.flapDirection = flapDirection;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(flapDirection.ordinal());
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.flapDirection = EntityPlane.FlapDirection.values()[buf.readInt()];
	}

	@Override
	public IMessage onMessage(MessageFlaps message, MessageContext ctx)
	{
	    FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() ->
        {
            Entity riding = ctx.getServerHandler().player.getRidingEntity();
            if(riding instanceof EntityPlane)
            {
                ((EntityPlane) riding).setFlapDirection(message.flapDirection);
            }
        });
		return null;
	}
}
