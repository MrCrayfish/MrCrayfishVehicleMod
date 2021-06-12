package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.entity.HelicopterEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageTravelProperties implements IMessage<MessageTravelProperties>
{
	private float travelSpeed;
	private float travelDirection;

	public MessageTravelProperties() {}

	public MessageTravelProperties(float travelSpeed, float travelDirection)
	{
		this.travelSpeed = travelSpeed;
		this.travelDirection = travelDirection;
	}

	@Override
	public void encode(MessageTravelProperties message, PacketBuffer buffer)
	{
		buffer.writeFloat(message.travelSpeed);
		buffer.writeFloat(message.travelDirection);
	}

	@Override
	public MessageTravelProperties decode(PacketBuffer buffer)
	{
		return new MessageTravelProperties(buffer.readFloat(), buffer.readFloat());
	}

	@Override
	public void handle(MessageTravelProperties message, Supplier<NetworkEvent.Context> supplier)
	{
		supplier.get().enqueueWork(() ->
		{
			ServerPlayerEntity player = supplier.get().getSender();
			if(player != null)
			{
				Entity riding = player.getVehicle();
				if(riding instanceof HelicopterEntity)
				{
					HelicopterEntity helicopter = (HelicopterEntity) riding;
					helicopter.setTravelSpeed(message.travelSpeed);
					helicopter.setTravelDirection(message.travelDirection);
				}
			}
		});
		supplier.get().setPacketHandled(true);
	}
}
