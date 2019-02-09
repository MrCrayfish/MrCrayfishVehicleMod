package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.client.gui.GuiStorage;
import com.mrcrayfish.vehicle.common.inventory.StorageInventoryWrapper;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
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
        Minecraft.getMinecraft().addScheduledTask(() ->
        {
            Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.entityId);
            if(entity instanceof StorageInventoryWrapper)
            {
                EntityPlayer player = Minecraft.getMinecraft().player;
                StorageInventoryWrapper wrapper = (StorageInventoryWrapper) entity;
                Minecraft.getMinecraft().displayGuiScreen(new GuiStorage(player.inventory, wrapper.getInventory()));
                player.openContainer.windowId = message.windowId;
            }
        });
        return null;
    }
}
