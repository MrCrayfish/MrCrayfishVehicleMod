package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.common.inventory.StorageInventory;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Author: MrCrayfish
 */
public class MessageSyncInventory implements IMessage, IMessageHandler<MessageSyncInventory, IMessage>
{
    private int entityId;
    private NBTTagCompound tagCompound;

    public MessageSyncInventory() {}

    public MessageSyncInventory(int entityId, StorageInventory storageInventory)
    {
        this.entityId = entityId;
        this.tagCompound = storageInventory.writeToNBT();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(entityId);
        ByteBufUtils.writeTag(buf, tagCompound);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        entityId = buf.readInt();
        tagCompound = ByteBufUtils.readTag(buf);
    }

    @Override
    public IMessage onMessage(MessageSyncInventory message, MessageContext ctx)
    {
        Minecraft.getMinecraft().addScheduledTask(() -> VehicleMod.proxy.syncStorageInventory(message.entityId, message.tagCompound));
        return null;
    }
}
