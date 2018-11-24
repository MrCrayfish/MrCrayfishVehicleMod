package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.entity.EntityPoweredVehicle;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageAccelerating implements IMessage, IMessageHandler<MessageAccelerating, IMessage>
{
	private EntityPoweredVehicle.AccelerationDirection acceleration;

	public MessageAccelerating() {}

	public MessageAccelerating(EntityPoweredVehicle.AccelerationDirection acceleration)
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
		this.acceleration = EntityPoweredVehicle.AccelerationDirection.values()[buf.readInt()];
	}

	@Override
	public IMessage onMessage(MessageAccelerating message, MessageContext ctx)
	{
	    FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() ->
        {
            Entity riding = ctx.getServerHandler().player.getRidingEntity();
            if(riding instanceof EntityPoweredVehicle)
            {
                ((EntityPoweredVehicle) riding).setAcceleration(message.acceleration);
            }
        });
		return null;
	}
}
