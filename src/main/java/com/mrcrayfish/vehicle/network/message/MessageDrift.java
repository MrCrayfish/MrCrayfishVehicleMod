package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageDrift implements IMessage<MessageDrift>
{
	private boolean drifting;

	public MessageDrift() {}

	public MessageDrift(boolean drifting)
	{
		this.drifting = drifting;
	}

	@Override
	public void encode(MessageDrift message, PacketBuffer buffer)
	{
		buffer.writeBoolean(message.drifting);
	}

	@Override
	public MessageDrift decode(PacketBuffer buffer)
	{
		return new MessageDrift(buffer.readBoolean());
	}

	@Override
	public void handle(MessageDrift message, Supplier<NetworkEvent.Context> supplier)
	{
		supplier.get().enqueueWork(() ->
		{
			ServerPlayerEntity player = supplier.get().getSender();
			if(player != null)
			{
				Entity riding = player.getVehicle();
				if(riding instanceof LandVehicleEntity)
				{
					((LandVehicleEntity) riding).setDrifting(message.drifting);
				}
			}
		});
		supplier.get().setPacketHandled(true);
	}
}
