package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.entity.EntityPoweredVehicle;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessagePower implements IMessage, IMessageHandler<MessagePower, IMessage>
{
	private float power;

	public MessagePower() {}

	public MessagePower(float power)
	{
		this.power = power;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeFloat(power);
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.power = buf.readFloat();
	}

	@Override
	public IMessage onMessage(MessagePower message, MessageContext ctx)
	{
	    FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() ->
        {
            Entity riding = ctx.getServerHandler().player.getRidingEntity();
            if(riding instanceof EntityPoweredVehicle)
            {
                ((EntityPoweredVehicle) riding).setPower(message.power);
            }
        });
		return null;
	}
}
