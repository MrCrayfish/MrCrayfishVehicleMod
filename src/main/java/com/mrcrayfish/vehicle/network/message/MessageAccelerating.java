package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageAccelerating implements IMessage<MessageAccelerating>
{
	private PoweredVehicleEntity.AccelerationDirection acceleration;

	public MessageAccelerating() {}

	public MessageAccelerating(PoweredVehicleEntity.AccelerationDirection acceleration)
	{
		this.acceleration = acceleration;
	}

	@Override
	public void encode(MessageAccelerating message, PacketBuffer buffer)
	{
		buffer.writeEnum(message.acceleration);
	}

	@Override
	public MessageAccelerating decode(PacketBuffer buffer)
	{
		return new MessageAccelerating(buffer.readEnum(PoweredVehicleEntity.AccelerationDirection.class));
	}

	@Override
	public void handle(MessageAccelerating message, Supplier<NetworkEvent.Context> supplier)
	{
		supplier.get().enqueueWork(() ->
		{
			ServerPlayerEntity player = supplier.get().getSender();
			if(player != null)
			{
				Entity riding = player.getVehicle();
				if(riding instanceof PoweredVehicleEntity)
				{
					((PoweredVehicleEntity) riding).setAcceleration(message.acceleration);
				}
			}
		});
		supplier.get().setPacketHandled(true);
	}
}
