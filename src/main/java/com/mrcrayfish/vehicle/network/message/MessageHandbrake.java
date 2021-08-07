package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageHandbrake implements IMessage<MessageHandbrake>
{
	private boolean handbrake;

	public MessageHandbrake() {}

	public MessageHandbrake(boolean handbrake)
	{
		this.handbrake = handbrake;
	}

	@Override
	public void encode(MessageHandbrake message, PacketBuffer buffer)
	{
		buffer.writeBoolean(message.handbrake);
	}

	@Override
	public MessageHandbrake decode(PacketBuffer buffer)
	{
		return new MessageHandbrake(buffer.readBoolean());
	}

	@Override
	public void handle(MessageHandbrake message, Supplier<NetworkEvent.Context> supplier)
	{
		supplier.get().enqueueWork(() ->
		{
			ServerPlayerEntity player = supplier.get().getSender();
			if(player != null)
			{
				Entity riding = player.getVehicle();
				if(riding instanceof PoweredVehicleEntity)
				{
					((PoweredVehicleEntity) riding).setHandbraking(message.handbrake);
				}
			}
		});
		supplier.get().setPacketHandled(true);
	}
}
