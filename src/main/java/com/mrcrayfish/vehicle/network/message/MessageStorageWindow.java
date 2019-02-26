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
public class MessageStorageWindow implements IMessage, IMessageHandler<MessageStorageWindow, IMessage>
{
    private int windowId;
    private int entityId;

    public MessageStorageWindow() {}

    public MessageStorageWindow(int windowId, int entityId)
    {
        this.windowId = windowId;
        this.entityId = entityId;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(windowId);
        buf.writeInt(entityId);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        windowId = buf.readInt();
        entityId = buf.readInt();
    }

    @Override
    public IMessage onMessage(MessageStorageWindow message, MessageContext ctx)
    {
        Minecraft.getMinecraft().addScheduledTask(() -> VehicleMod.proxy.openStorageWindow(message.entityId, message.windowId));
        return null;
    }
}
