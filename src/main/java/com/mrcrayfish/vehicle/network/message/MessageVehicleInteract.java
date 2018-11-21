package com.mrcrayfish.vehicle.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.UUID;

public class MessageVehicleInteract implements IMessage
{
    protected UUID targetEntityID;

    public MessageVehicleInteract() {}

    public MessageVehicleInteract(Entity targetEntity)
    {
        targetEntityID = targetEntity.getUniqueID();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, targetEntityID.toString());
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        targetEntityID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
    }
}