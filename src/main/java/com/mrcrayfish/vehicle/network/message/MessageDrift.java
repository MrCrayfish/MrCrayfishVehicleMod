package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.entity.EntityLandVehicle;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageDrift implements IMessage, IMessageHandler<MessageDrift, IMessage>
{
	private boolean drifting;

	public MessageDrift() {}

	public MessageDrift(boolean drifting)
	{
		this.drifting = drifting;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeBoolean(drifting);
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.drifting = buf.readBoolean();
	}

	@Override
	public IMessage onMessage(MessageDrift message, MessageContext ctx)
	{
	    FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() ->
        {
            Entity riding = ctx.getServerHandler().player.getRidingEntity();
            if(riding instanceof EntityLandVehicle)
            {
                ((EntityLandVehicle) riding).setDrifting(message.drifting);
            }
        });
		return null;
	}
}
