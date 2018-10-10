package com.mrcrayfish.vehicle.network;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.network.message.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class PacketHandler
{
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);
    private static int messageId = 0;

    private static enum Side
    {
        CLIENT, SERVER, BOTH;
    }

    public static void init()
    {
        registerMessage(MessageTurn.class, Side.SERVER);
        registerMessage(MessageAccelerating.class, Side.SERVER);
        registerMessage(MessageDrift.class, Side.SERVER);
        registerMessage(MessageHorn.class, Side.SERVER);
        registerMessage(MessageThrowVehicle.class, Side.SERVER);
        registerMessage(MessagePickupVehicle.class, Side.SERVER);
        registerMessage(MessageFlaps.class, Side.SERVER);
        registerMessage(MessageVehicleChest.class, Side.SERVER);
        registerMessage(MessageAttachChest.class, Side.SERVER);
        registerMessage(MessageAttachTrailer.class, Side.SERVER);
        registerMessage(MessageFuelVehicle.class, Side.SERVER);
        registerMessage(MessageInteractKey.class, Side.SERVER);
        registerMessage(MessageAltitude.class, Side.SERVER);
    }

    private static void registerMessage(Class packet, Side side)
    {
        if (side != Side.CLIENT)
        {
            registerMessage(packet, net.minecraftforge.fml.relauncher.Side.SERVER);
        }

        if (side != Side.SERVER)
        {
            registerMessage(packet, net.minecraftforge.fml.relauncher.Side.CLIENT);
        }
    }

    private static void registerMessage(Class packet, net.minecraftforge.fml.relauncher.Side side)
    {
        INSTANCE.registerMessage(packet, packet, messageId++, side);
    }
}
