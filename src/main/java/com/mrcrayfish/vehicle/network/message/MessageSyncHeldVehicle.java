package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.VehicleMod;
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
public class MessageSyncHeldVehicle implements IMessage, IMessageHandler<MessageSyncHeldVehicle, IMessage>
{
    private int entityId;
    private NBTTagCompound vehicleTag;

    public MessageSyncHeldVehicle() {}

    public MessageSyncHeldVehicle(int entityId, NBTTagCompound vehicleTag)
    {
        this.entityId = entityId;
        this.vehicleTag = vehicleTag;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.entityId);
        ByteBufUtils.writeTag(buf, this.vehicleTag);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.entityId = buf.readInt();
        this.vehicleTag = ByteBufUtils.readTag(buf);
    }

    @Override
    public IMessage onMessage(MessageSyncHeldVehicle message, MessageContext ctx)
    {
        Minecraft.getMinecraft().addScheduledTask(() -> VehicleMod.proxy.syncHeldVehicle(message.entityId, message.vehicleTag));
        return null;
    }
}
