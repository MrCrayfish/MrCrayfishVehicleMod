package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.entity.PlaneEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessagePlaneInput implements IMessage<MessagePlaneInput>
{
	private float lift;
	private float forward;
	private float side;

	public MessagePlaneInput() {}

	public MessagePlaneInput(float lift, float forward, float side)
	{
		this.lift = lift;
		this.forward = forward;
		this.side = side;
	}

	@Override
	public void encode(MessagePlaneInput message, PacketBuffer buffer)
	{
		buffer.writeFloat(message.lift);
		buffer.writeFloat(message.forward);
		buffer.writeFloat(message.side);
	}

	@Override
	public MessagePlaneInput decode(PacketBuffer buffer)
	{
		return new MessagePlaneInput(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
	}

	@Override
	public void handle(MessagePlaneInput message, Supplier<NetworkEvent.Context> supplier)
	{
		supplier.get().enqueueWork(() ->
		{
			ServerPlayerEntity player = supplier.get().getSender();
			if(player != null)
			{
				Entity riding = player.getVehicle();
				if(riding instanceof PlaneEntity)
				{
					PlaneEntity plane = (PlaneEntity) riding;
					plane.setLift(message.lift);
					plane.setForwardInput(message.forward);
					plane.setSideInput(message.side);
				}
			}
		});
		supplier.get().setPacketHandled(true);
	}
}
