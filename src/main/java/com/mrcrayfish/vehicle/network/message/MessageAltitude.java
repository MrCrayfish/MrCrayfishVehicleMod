package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.entity.HelicopterEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageAltitude implements IMessage<MessageAltitude>
{
	private HelicopterEntity.AltitudeChange altitudeChange;

	public MessageAltitude() {}

	public MessageAltitude(HelicopterEntity.AltitudeChange altitudeChange)
	{
		this.altitudeChange = altitudeChange;
	}

	@Override
	public void encode(MessageAltitude message, PacketBuffer buffer)
	{
		buffer.writeEnum(message.altitudeChange);
	}

	@Override
	public MessageAltitude decode(PacketBuffer buffer)
	{
		return new MessageAltitude(buffer.readEnum(HelicopterEntity.AltitudeChange.class));
	}

	@Override
	public void handle(MessageAltitude message, Supplier<NetworkEvent.Context> supplier)
	{
		supplier.get().enqueueWork(() ->
		{
			ServerPlayerEntity player = supplier.get().getSender();
			if(player != null)
			{
				Entity riding = player.getVehicle();
				if(riding instanceof HelicopterEntity)
				{
					((HelicopterEntity) riding).setAltitudeChange(message.altitudeChange);
				}
			}
		});
		supplier.get().setPacketHandled(true);
	}
}
