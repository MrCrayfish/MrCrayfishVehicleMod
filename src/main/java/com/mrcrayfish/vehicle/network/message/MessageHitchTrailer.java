package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.entity.TrailerEntity;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.properties.VehicleProperties;
import com.mrcrayfish.vehicle.network.play.ServerPlayHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageHitchTrailer implements IMessage<MessageHitchTrailer>
{
    private boolean hitch;

    public MessageHitchTrailer() {}

    public MessageHitchTrailer(boolean hitch)
    {
        this.hitch = hitch;
    }

    @Override
    public void encode(MessageHitchTrailer message, PacketBuffer buffer)
    {
        buffer.writeBoolean(message.hitch);
    }

    @Override
    public MessageHitchTrailer decode(PacketBuffer buffer)
    {
        return new MessageHitchTrailer(buffer.readBoolean());
    }

    @Override
    public void handle(MessageHitchTrailer message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() ->
        {
            ServerPlayerEntity player = supplier.get().getSender();
            if(player != null)
            {
                ServerPlayHandler.handleHitchTrailerMessage(player, message);
            }
        });
        supplier.get().setPacketHandled(true);
    }

    public boolean isHitch()
    {
        return this.hitch;
    }
}
