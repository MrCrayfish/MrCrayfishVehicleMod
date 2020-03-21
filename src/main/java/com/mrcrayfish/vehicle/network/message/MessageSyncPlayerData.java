package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.common.entity.SyncedPlayerData;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageSyncPlayerData implements IMessage<MessageSyncPlayerData>
{
    private int entityId;
    private int trailerId;
    private Optional<BlockPos> gasPumpPos;

    public MessageSyncPlayerData() {}

    public MessageSyncPlayerData(int entityId, SyncedPlayerData.Holder holder)
    {
        this.entityId = entityId;
        this.trailerId = holder.getTrailer();
        this.gasPumpPos = holder.getGasPumpPos();
    }

    public MessageSyncPlayerData(int entityId, int trailerId, Optional<BlockPos> gasPumpPos)
    {
        this.entityId = entityId;
        this.trailerId = trailerId;
        this.gasPumpPos = gasPumpPos;
    }

    @Override
    public void encode(MessageSyncPlayerData message, PacketBuffer buffer)
    {
        buffer.writeVarInt(message.entityId);
        buffer.writeVarInt(message.trailerId);
        buffer.writeBoolean(message.gasPumpPos.isPresent());
        message.gasPumpPos.ifPresent(buffer::writeBlockPos);
    }

    @Override
    public MessageSyncPlayerData decode(PacketBuffer buffer)
    {
        int entityId = buffer.readVarInt();
        int trailerId = buffer.readVarInt();
        boolean present = buffer.readBoolean();
        Optional<BlockPos> gasPumpPos = present ? Optional.of(buffer.readBlockPos()) : Optional.empty();
        return new MessageSyncPlayerData(entityId, trailerId, gasPumpPos);
    }

    @Override
    public void handle(MessageSyncPlayerData message, Supplier<NetworkEvent.Context> supplier)
    {
        if(supplier.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT)
        {
            supplier.get().enqueueWork(() -> VehicleMod.PROXY.syncPlayerData(message.entityId, message.trailerId, message.gasPumpPos));
        }
    }
}