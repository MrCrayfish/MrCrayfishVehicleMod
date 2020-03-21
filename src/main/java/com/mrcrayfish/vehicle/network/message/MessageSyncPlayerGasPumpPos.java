package com.mrcrayfish.vehicle.network.message;

import com.google.common.base.Optional;
import com.mrcrayfish.vehicle.VehicleMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Author: MrCrayfish
 */
public class MessageSyncPlayerGasPumpPos implements IMessage, IMessageHandler<MessageSyncPlayerGasPumpPos, IMessage>
{
    private int entityId;
    private Optional<BlockPos> gasPumpPos;

    public MessageSyncPlayerGasPumpPos() {}

    public MessageSyncPlayerGasPumpPos(int entityId, Optional<BlockPos> gasPumpPos)
    {
        this.entityId = entityId;
        this.gasPumpPos = gasPumpPos;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.entityId);
        buf.writeBoolean(this.gasPumpPos.isPresent());
        if(this.gasPumpPos.isPresent())
        {
            buf.writeLong(this.gasPumpPos.get().toLong());
        }
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.entityId = buf.readInt();
        this.gasPumpPos = buf.readBoolean() ? Optional.of(BlockPos.fromLong(buf.readLong())) : Optional.absent();
    }

    @Override
    public IMessage onMessage(MessageSyncPlayerGasPumpPos message, MessageContext ctx)
    {
        Minecraft.getMinecraft().addScheduledTask(() -> VehicleMod.proxy.syncGasPumpPos(message.entityId, message.gasPumpPos));
        return null;
    }
}
