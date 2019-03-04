package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.entity.EntityPoweredVehicle;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageTurnAngle implements IMessage, IMessageHandler<MessageTurnAngle, IMessage>
{
	private float angle;

	public MessageTurnAngle() {}

	public MessageTurnAngle(float angle)
	{
		this.angle = angle;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeFloat(this.angle);
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.angle = buf.readFloat();
	}

	@Override
	public IMessage onMessage(MessageTurnAngle message, MessageContext ctx)
	{
	    FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() ->
        {
            Entity riding = ctx.getServerHandler().player.getRidingEntity();
            if(riding instanceof EntityPoweredVehicle)
            {
                ((EntityPoweredVehicle) riding).setTargetTurnAngle(message.angle);
            }
        });
		return null;
	}
}
