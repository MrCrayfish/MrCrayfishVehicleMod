package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.entity.PlaneEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageFlaps implements IMessage<MessageFlaps>
{
	private PlaneEntity.FlapDirection flapDirection;

	public MessageFlaps() {}

	public MessageFlaps(PlaneEntity.FlapDirection flapDirection)
	{
		this.flapDirection = flapDirection;
	}

	@Override
	public void encode(MessageFlaps message, PacketBuffer buffer)
	{
		buffer.writeEnum(message.flapDirection);
	}

	@Override
	public MessageFlaps decode(PacketBuffer buffer)
	{
		return new MessageFlaps(buffer.readEnum(PlaneEntity.FlapDirection.class));
	}

	@Override
	public void handle(MessageFlaps message, Supplier<NetworkEvent.Context> supplier)
	{
		supplier.get().enqueueWork(() ->
		{
			ServerPlayerEntity player = supplier.get().getSender();
			if(player != null)
			{
				Entity riding = player.getVehicle();
				if(riding instanceof PlaneEntity)
				{
					((PlaneEntity) riding).setFlapDirection(message.flapDirection);
				}
			}
		});
		supplier.get().setPacketHandled(true);
	}
}
