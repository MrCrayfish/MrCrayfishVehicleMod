package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.common.SeatTracker;
import com.mrcrayfish.vehicle.entity.EntityVehicle;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public class MessageCycleSeats implements IMessage, IMessageHandler<MessageCycleSeats, IMessage>
{
    public MessageCycleSeats() {}

    @Override
    public void toBytes(ByteBuf buf) {}

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public IMessage onMessage(MessageCycleSeats message, MessageContext ctx)
    {
        FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() ->
        {
            EntityPlayerMP player = ctx.getServerHandler().player;
            if(player != null && player.getRidingEntity() instanceof EntityVehicle)
            {
                EntityVehicle vehicle = (EntityVehicle) player.getRidingEntity();
                List<Seat> seats = vehicle.getProperties().getSeats();

                /* No need to cycle if already full of passengers */
                if(vehicle.getPassengers().size() >= seats.size())
                    return;

                SeatTracker tracker = vehicle.getSeatTracker();
                int seatIndex = tracker.getSeatIndex(player.getUniqueID());
                for(int i = 0; i < seats.size() - 1; i++)
                {
                    int nextIndex = (seatIndex + (i + 1)) % seats.size();
                    if(tracker.isSeatAvailable(nextIndex))
                    {
                        tracker.setSeatIndex(nextIndex, player.getUniqueID());
                        return;
                    }
                }
            }
        });
        return null;
    }
}
