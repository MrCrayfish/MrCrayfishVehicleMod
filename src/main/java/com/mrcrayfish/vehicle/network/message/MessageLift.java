package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.entity.HelicopterEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageLift implements IMessage<MessageLift>
{
	private float lift;

	public MessageLift() {}

	public MessageLift(float lift)
	{
		this.lift = lift;
	}

	@Override
	public void encode(MessageLift message, PacketBuffer buffer)
	{
		buffer.writeFloat(this.lift);
	}

	@Override
	public MessageLift decode(PacketBuffer buffer)
	{
		return new MessageLift(buffer.readFloat());
	}

	@Override
	public void handle(MessageLift message, Supplier<NetworkEvent.Context> supplier)
	{
		supplier.get().enqueueWork(() ->
		{
			ServerPlayerEntity player = supplier.get().getSender();
			if(player != null)
			{
				Entity riding = player.getVehicle();
				if(riding instanceof HelicopterEntity)
				{
					((HelicopterEntity) riding).setLift(message.lift);
				}
			}
		});
		supplier.get().setPacketHandled(true);
	}
}
