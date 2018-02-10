package com.mrcrayfish.vehicle.network;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.network.message.MessageAccelerating;
import com.mrcrayfish.vehicle.network.message.MessageDrift;
import com.mrcrayfish.vehicle.network.message.MessageTurn;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler
{
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);

	public static void init()
	{
		INSTANCE.registerMessage(MessageTurn.class, MessageTurn.class, 0, Side.SERVER);
		INSTANCE.registerMessage(MessageAccelerating.class, MessageAccelerating.class, 1, Side.SERVER);
		INSTANCE.registerMessage(MessageDrift.class, MessageDrift.class, 2, Side.SERVER);
	}
}
