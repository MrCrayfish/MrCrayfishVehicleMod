package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.network.play.ServerPlayHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageThrottle implements IMessage<MessageThrottle>
{
	private float power;

	public MessageThrottle() {}

	public MessageThrottle(float power)
	{
		this.power = power;
	}

	@Override
	public void encode(MessageThrottle message, PacketBuffer buffer)
	{
		buffer.writeFloat(message.power);
	}

	@Override
	public MessageThrottle decode(PacketBuffer buffer)
	{
		return new MessageThrottle(buffer.readFloat());
	}

	@Override
	public void handle(MessageThrottle message, Supplier<NetworkEvent.Context> supplier)
	{
		supplier.get().enqueueWork(() ->
		{
			ServerPlayerEntity player = supplier.get().getSender();
			if(player != null)
			{
				ServerPlayHandler.handleThrottleMessage(player, message);
			}
		});
		supplier.get().setPacketHandled(true);
	}

	public float getPower()
	{
		return this.power;
	}
}
