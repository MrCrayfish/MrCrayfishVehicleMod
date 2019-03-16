package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.entity.EntityPoweredVehicle;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageTurnDirection implements IMessage, IMessageHandler<MessageTurnDirection, IMessage>
{
	private EntityPoweredVehicle.TurnDirection direction;

	public MessageTurnDirection() {}

	public MessageTurnDirection(EntityPoweredVehicle.TurnDirection direction)
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
	public IMessage onMessage(MessageTurnDirection message, MessageContext ctx)
	{
	    FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() ->
        {
            Entity riding = ctx.getServerHandler().player.getRidingEntity();
            if(riding instanceof EntityPoweredVehicle)
            {
                ((EntityPoweredVehicle) riding).setTurnDirection(message.direction);
            }
        });
		return null;
	}
}
