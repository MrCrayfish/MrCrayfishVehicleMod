package com.mrcrayfish.vehicle.network;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.network.message.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler
{
    public static final String PROTOCOL_VERSION = "1";

    public static SimpleChannel instance;
    private static int nextId = 0;

    public static void register()
    {
        instance = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(Reference.MOD_ID, "play"))
                .networkProtocolVersion(() -> PROTOCOL_VERSION)
                .clientAcceptedVersions(PROTOCOL_VERSION::equals)
                .serverAcceptedVersions(PROTOCOL_VERSION::equals)
                .simpleChannel();

        register(MessageTurnDirection.class, new MessageTurnDirection());
        register(MessageTurnAngle.class, new MessageTurnAngle());
        register(MessageAccelerating.class, new MessageAccelerating());
        register(MessageDrift.class, new MessageDrift());
        register(MessageHorn.class, new MessageHorn());
        register(MessageThrowVehicle.class, new MessageThrowVehicle());
        register(MessagePickupVehicle.class, new MessagePickupVehicle());
        register(MessageFlaps.class, new MessageFlaps());
        register(MessageAttachChest.class, new MessageAttachChest());
        register(MessageAttachTrailer.class, new MessageAttachTrailer());
        register(MessageFuelVehicle.class, new MessageFuelVehicle());
        register(MessageInteractKey.class, new MessageInteractKey());
        register(MessageAltitude.class, new MessageAltitude());
        register(MessageCraftVehicle.class, new MessageCraftVehicle());
        register(MessageHitchTrailer.class, new MessageHitchTrailer());
        register(MessageSyncInventory.class, new MessageSyncInventory());
        register(MessageOpenStorage.class, new MessageOpenStorage());
        register(MessageTravelProperties.class, new MessageTravelProperties());
        register(MessagePower.class, new MessagePower());
        register(MessageEntityFluid.class, new MessageEntityFluid());
        register(MessageSyncPlayerSeat.class, new MessageSyncPlayerSeat());
        register(MessageCycleSeats.class, new MessageCycleSeats());
        register(MessageSyncHeldVehicle.class, new MessageSyncHeldVehicle());
    }

    private static <T> void register(Class<T> clazz, IMessage<T> message)
    {
        instance.registerMessage(nextId++, clazz, message::encode, message::decode, message::handle);
    }
}
