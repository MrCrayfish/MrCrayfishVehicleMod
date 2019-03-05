package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.entity.EntityHelicopter;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageTravelProperties implements IMessage, IMessageHandler<MessageTravelProperties, IMessage>
{
	private float travelSpeed;
	private float travelDirection;

	public MessageTravelProperties() {}

	public MessageTravelProperties(float travelSpeed, float travelDirection)
	{
		this.travelSpeed = travelSpeed;
		this.travelDirection = travelDirection;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeFloat(this.travelSpeed);
		buf.writeFloat(this.travelDirection);
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.travelSpeed = buf.readFloat();
		this.travelDirection = buf.readFloat();
	}

	@Override
	public IMessage onMessage(MessageTravelProperties message, MessageContext ctx)
	{
	    FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() ->
        {
            Entity riding = ctx.getServerHandler().player.getRidingEntity();
            if(riding instanceof EntityHelicopter)
            {
				EntityHelicopter helicopter = (EntityHelicopter) riding;
				helicopter.setTravelSpeed(message.travelSpeed);
				helicopter.setTravelDirection(message.travelDirection);
            }
        });
		return null;
	}
}
