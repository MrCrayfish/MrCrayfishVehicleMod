package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessagePower implements IMessage<MessagePower>
{
	private float power;

	public MessagePower() {}

	public MessagePower(float power)
	{
		this.power = power;
	}

	@Override
	public void encode(MessagePower message, PacketBuffer buffer)
	{
		buffer.writeFloat(message.power);
	}

	@Override
	public MessagePower decode(PacketBuffer buffer)
	{
		return new MessagePower(buffer.readFloat());
	}

	@Override
	public void handle(MessagePower message, Supplier<NetworkEvent.Context> supplier)
	{
		supplier.get().enqueueWork(() ->
		{
			ServerPlayerEntity player = supplier.get().getSender();
			if(player != null)
			{
				Entity riding = player.getVehicle();
				if(riding instanceof PoweredVehicleEntity)
				{
					((PoweredVehicleEntity) riding).setPower(message.power);
				}
			}
		});
		supplier.get().setPacketHandled(true);
	}
}
