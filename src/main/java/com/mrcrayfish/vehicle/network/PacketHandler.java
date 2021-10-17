package com.mrcrayfish.vehicle.network;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.network.message.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.FMLHandshakeHandler;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler
{
    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel HANDSHAKE_CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(Reference.MOD_ID, "handshake"), () -> PROTOCOL_VERSION, s -> true, s -> true);
    private static final SimpleChannel PLAY_CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(Reference.MOD_ID, "play"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
    private static int nextId = 0;

    public static void registerPlayMessage()
    {
        HANDSHAKE_CHANNEL.messageBuilder(HandshakeMessages.C2SAcknowledge.class, 99)
                .loginIndex(HandshakeMessages.LoginIndexedMessage::getLoginIndex, HandshakeMessages.LoginIndexedMessage::setLoginIndex)
                .decoder(HandshakeMessages.C2SAcknowledge::decode)
                .encoder(HandshakeMessages.C2SAcknowledge::encode)
                .consumer(FMLHandshakeHandler.indexFirst((handler, msg, s) -> HandshakeHandler.handleAcknowledge(msg, s)))
                .add();

        HANDSHAKE_CHANNEL.messageBuilder(HandshakeMessages.S2CVehicleProperties.class, 1)
                .loginIndex(HandshakeMessages.LoginIndexedMessage::getLoginIndex, HandshakeMessages.LoginIndexedMessage::setLoginIndex)
                .decoder(HandshakeMessages.S2CVehicleProperties::decode)
                .encoder(HandshakeMessages.S2CVehicleProperties::encode)
                .consumer(FMLHandshakeHandler.biConsumerFor((handler, msg, supplier) -> HandshakeHandler.handleVehicleProperties(msg, supplier)))
                .markAsLoginPacket()
                .add();

        registerPlayMessage(MessageTurnAngle.class, new MessageTurnAngle());
        registerPlayMessage(MessageHandbrake.class, new MessageHandbrake());
        registerPlayMessage(MessageHorn.class, new MessageHorn());
        registerPlayMessage(MessageThrowVehicle.class, new MessageThrowVehicle());
        registerPlayMessage(MessagePickupVehicle.class, new MessagePickupVehicle());
        registerPlayMessage(MessageAttachChest.class, new MessageAttachChest());
        registerPlayMessage(MessageAttachTrailer.class, new MessageAttachTrailer());
        registerPlayMessage(MessageFuelVehicle.class, new MessageFuelVehicle());
        registerPlayMessage(MessageInteractKey.class, new MessageInteractKey());
        registerPlayMessage(MessageHelicopterInput.class, new MessageHelicopterInput());
        registerPlayMessage(MessageCraftVehicle.class, new MessageCraftVehicle());
        registerPlayMessage(MessageHitchTrailer.class, new MessageHitchTrailer());
        registerPlayMessage(MessageSyncStorage.class, new MessageSyncStorage());
        registerPlayMessage(MessageOpenStorage.class, new MessageOpenStorage());
        registerPlayMessage(MessageThrottle.class, new MessageThrottle());
        registerPlayMessage(MessageEntityFluid.class, new MessageEntityFluid());
        registerPlayMessage(MessageSyncPlayerSeat.class, new MessageSyncPlayerSeat());
        registerPlayMessage(MessageCycleSeats.class, new MessageCycleSeats());
        registerPlayMessage(MessageSyncHeldVehicle.class, new MessageSyncHeldVehicle());
        registerPlayMessage(MessagePlaneInput.class, new MessagePlaneInput());
        registerPlayMessage(MessageSyncCosmetics.class, new MessageSyncCosmetics());
        registerPlayMessage(MessageInteractCosmetic.class, new MessageInteractCosmetic());
    }

    private static <T> void registerPlayMessage(Class<T> clazz, IMessage<T> message)
    {
        PLAY_CHANNEL.registerMessage(nextId++, clazz, message::encode, message::decode, message::handle);
    }

    /**
     * Gets the handshake network channel for MrCrayfish's Vehicle Mod
     */
    public static SimpleChannel getHandshakeChannel()
    {
        return HANDSHAKE_CHANNEL;
    }

    /**
     * Gets the play network channel for MrCrayfish's Vehicle Mod
     */
    public static SimpleChannel getPlayChannel()
    {
        return PLAY_CHANNEL;
    }
}
