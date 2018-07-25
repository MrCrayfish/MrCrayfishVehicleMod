package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.entity.EntityPoweredVehicle;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageTurn implements IMessage, IMessageHandler<MessageTurn, IMessage>
{
	private EntityPoweredVehicle.TurnDirection direction;

	public MessageTurn() {}

	public MessageTurn(EntityPoweredVehicle.TurnDirection direction)
	{
		this.direction = direction;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(direction.ordinal());
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.direction = EntityPoweredVehicle.TurnDirection.values()[buf.readInt()];
	}

	@Override
	public IMessage onMessage(MessageTurn message, MessageContext ctx)
	{
		EntityPlayerMP player = ctx.getServerHandler().player;
		Entity riding = player.getRidingEntity();
		if(riding instanceof EntityPoweredVehicle)
		{
			((EntityPoweredVehicle) riding).setTurnDirection(message.direction);
		}
		return null;
	}
}
