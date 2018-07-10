package com.mrcrayfish.vehicle.network;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.network.message.*;
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
		INSTANCE.registerMessage(MessageHorn.class, MessageHorn.class, 3, Side.SERVER);
		INSTANCE.registerMessage(MessageThrowVehicle.class, MessageThrowVehicle.class, 4, Side.SERVER);
		INSTANCE.registerMessage(MessageFlaps.class, MessageFlaps.class, 5, Side.SERVER);
	}
}
