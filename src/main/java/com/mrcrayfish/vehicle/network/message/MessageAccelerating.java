package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.entity.EntityVehicle;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageAccelerating implements IMessage, IMessageHandler<MessageAccelerating, IMessage>
{
	private EntityVehicle.AccelerationDirection acceleration;

	public MessageAccelerating() {}

	public MessageAccelerating(EntityVehicle.AccelerationDirection acceleration)
	{
		this.acceleration = acceleration;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(acceleration.ordinal());
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.acceleration = EntityVehicle.AccelerationDirection.values()[buf.readInt()];
	}

	@Override
	public IMessage onMessage(MessageAccelerating message, MessageContext ctx)
	{
		EntityPlayerMP player = ctx.getServerHandler().player;
		Entity riding = player.getRidingEntity();
		if(riding instanceof EntityVehicle)
		{
			((EntityVehicle) riding).setAcceleration(message.acceleration);
		}
		return null;
	}
}
