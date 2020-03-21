package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.VehicleMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Author: MrCrayfish
 */
public class MessageSyncPlayerTrailer implements IMessage, IMessageHandler<MessageSyncPlayerTrailer, IMessage>
{
    private int entityId;
    private int trailer;

    public MessageSyncPlayerTrailer() {}

    public MessageSyncPlayerTrailer(int entityId, int trailer)
    {
        this.entityId = entityId;
        this.trailer = trailer;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.entityId);
        buf.writeInt(this.trailer);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.entityId = buf.readInt();
        this.trailer = buf.readInt();
    }

    @Override
    public IMessage onMessage(MessageSyncPlayerTrailer message, MessageContext ctx)
    {
        Minecraft.getMinecraft().addScheduledTask(() -> VehicleMod.proxy.syncTrailer(message.entityId, message.trailer));
        return null;
    }
}
