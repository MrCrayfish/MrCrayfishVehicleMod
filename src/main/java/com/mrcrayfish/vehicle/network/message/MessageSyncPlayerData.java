package com.mrcrayfish.vehicle.network.message;

import com.google.common.base.Optional;
import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.common.entity.SyncedPlayerData;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Author: MrCrayfish
 */
public class MessageSyncPlayerData implements IMessage, IMessageHandler<MessageSyncPlayerData, IMessage>
{
    private int entityId;
    private int trailer;
    private Optional<BlockPos> gasPumpPos;

    public MessageSyncPlayerData() {}

    public MessageSyncPlayerData(int entityId, SyncedPlayerData.Holder holder)
    {
        this.entityId = entityId;
        this.trailer = holder.getTrailer();
        this.gasPumpPos = holder.getGasPumpPos();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.entityId);
        buf.writeInt(this.trailer);
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
        this.trailer = buf.readInt();
        this.gasPumpPos = buf.readBoolean() ? Optional.of(BlockPos.fromLong(buf.readLong())) : Optional.absent();
    }

    @Override
    public IMessage onMessage(MessageSyncPlayerData message, MessageContext ctx)
    {
        Minecraft.getMinecraft().addScheduledTask(() -> VehicleMod.proxy.syncPlayerData(message.entityId, message.trailer, message.gasPumpPos));
        return null;
    }
}
