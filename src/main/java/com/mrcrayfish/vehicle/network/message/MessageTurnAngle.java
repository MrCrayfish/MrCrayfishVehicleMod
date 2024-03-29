package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.network.play.ServerPlayHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageTurnAngle implements IMessage<MessageTurnAngle>
{
	private float angle;

	public MessageTurnAngle() {}

	public MessageTurnAngle(float angle)
	{
		this.angle = angle;
	}

	@Override
	public void encode(MessageTurnAngle message, PacketBuffer buffer)
	{
		buffer.writeFloat(message.angle);
	}

	@Override
	public MessageTurnAngle decode(PacketBuffer buffer)
	{
		return new MessageTurnAngle(buffer.readFloat());
	}

	@Override
	public void handle(MessageTurnAngle message, Supplier<NetworkEvent.Context> supplier)
	{
		supplier.get().enqueueWork(() ->
		{
			ServerPlayerEntity player = supplier.get().getSender();
			if(player != null)
			{
				ServerPlayHandler.handleTurnAngleMessage(player, message);
			}
		});
		supplier.get().setPacketHandled(true);
	}

	public float getAngle()
	{
		return this.angle;
	}
}
