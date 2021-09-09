package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.network.play.ServerPlayHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageHelicopterInput implements IMessage<MessageHelicopterInput>
{
	private float lift;
	private float forward;
	private float side;

	public MessageHelicopterInput() {}

	public MessageHelicopterInput(float lift, float forward, float side)
	{
		this.lift = lift;
		this.forward = forward;
		this.side = side;
	}

	@Override
	public void encode(MessageHelicopterInput message, PacketBuffer buffer)
	{
		buffer.writeFloat(message.lift);
		buffer.writeFloat(message.forward);
		buffer.writeFloat(message.side);
	}

	@Override
	public MessageHelicopterInput decode(PacketBuffer buffer)
	{
		return new MessageHelicopterInput(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
	}

	@Override
	public void handle(MessageHelicopterInput message, Supplier<NetworkEvent.Context> supplier)
	{
		supplier.get().enqueueWork(() ->
		{
			ServerPlayerEntity player = supplier.get().getSender();
			if(player != null)
			{
				ServerPlayHandler.handleHelicopterInputMessage(player, message);
			}
		});
		supplier.get().setPacketHandled(true);
	}

	public float getLift()
	{
		return this.lift;
	}

	public float getForward()
	{
		return this.forward;
	}

	public float getSide()
	{
		return this.side;
	}
}
