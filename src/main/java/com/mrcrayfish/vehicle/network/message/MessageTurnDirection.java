package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageTurnDirection implements IMessage<MessageTurnDirection>
{
	private PoweredVehicleEntity.TurnDirection direction;

	public MessageTurnDirection() {}

	public MessageTurnDirection(PoweredVehicleEntity.TurnDirection direction)
	{
		this.direction = direction;
	}

	@Override
	public void encode(MessageTurnDirection message, PacketBuffer buffer)
	{
		buffer.writeEnum(message.direction);
	}

	@Override
	public MessageTurnDirection decode(PacketBuffer buffer)
	{
		return new MessageTurnDirection(buffer.readEnum(PoweredVehicleEntity.TurnDirection.class));
	}

	@Override
	public void handle(MessageTurnDirection message, Supplier<NetworkEvent.Context> supplier)
	{
		supplier.get().enqueueWork(() ->
		{
			ServerPlayerEntity player = supplier.get().getSender();
			if(player != null)
			{
				Entity riding = player.getVehicle();
				if(riding instanceof PoweredVehicleEntity)
				{
					((PoweredVehicleEntity) riding).setTurnDirection(message.direction);
				}
			}
		});
		supplier.get().setPacketHandled(true);
	}
}
