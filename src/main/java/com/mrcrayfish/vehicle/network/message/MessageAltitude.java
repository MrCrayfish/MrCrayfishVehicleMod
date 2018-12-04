package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.entity.EntityHelicopter;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageAltitude implements IMessage, IMessageHandler<MessageAltitude, IMessage>
{
	private EntityHelicopter.AltitudeChange altitudeChange;

	public MessageAltitude() {}

	public MessageAltitude(EntityHelicopter.AltitudeChange altitudeChange)
	{
		this.altitudeChange = altitudeChange;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(altitudeChange.ordinal());
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.altitudeChange = EntityHelicopter.AltitudeChange.values()[buf.readInt()];
	}

	@Override
	public IMessage onMessage(MessageAltitude message, MessageContext ctx)
	{
	    FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() ->
        {
            Entity riding = ctx.getServerHandler().player.getRidingEntity();
            if(riding instanceof EntityHelicopter)
            {
                ((EntityHelicopter) riding).setAltitudeChange(message.altitudeChange);
            }
        });
		return null;
	}
}
