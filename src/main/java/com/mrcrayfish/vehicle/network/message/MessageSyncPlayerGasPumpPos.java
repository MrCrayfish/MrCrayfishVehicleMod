package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.VehicleMod;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageSyncPlayerGasPumpPos implements IMessage<MessageSyncPlayerGasPumpPos>
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
    public void encode(MessageSyncPlayerGasPumpPos message, PacketBuffer buffer)
    {
        buffer.writeVarInt(message.entityId);
        buffer.writeBoolean(message.gasPumpPos.isPresent());
        message.gasPumpPos.ifPresent(buffer::writeBlockPos);
    }

    @Override
    public MessageSyncPlayerGasPumpPos decode(PacketBuffer buffer)
    {
        int entityId = buffer.readVarInt();
        boolean present = buffer.readBoolean();
        return new MessageSyncPlayerGasPumpPos(entityId, present ? Optional.of(buffer.readBlockPos()) : Optional.empty());
    }

    @Override
    public void handle(MessageSyncPlayerGasPumpPos message, Supplier<NetworkEvent.Context> supplier)
    {
        if(supplier.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT)
        {
            supplier.get().enqueueWork(() -> VehicleMod.PROXY.syncGasPumpPos(message.entityId, message.gasPumpPos));
        }
    }
}
