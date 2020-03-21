package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.common.CommonEvents;
import com.mrcrayfish.vehicle.common.entity.SyncedPlayerData;
import com.mrcrayfish.vehicle.entity.EntityTrailer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Author: MrCrayfish
 */
public class MessageAttachTrailer implements IMessage, IMessageHandler<MessageAttachTrailer, IMessage>
{
    private int trailerId;
    private int entityId;

    public MessageAttachTrailer() {}

    public MessageAttachTrailer(int trailerId, int entityId)
    {
        this.trailerId = trailerId;
        this.entityId = entityId;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.trailerId);
        buf.writeInt(this.entityId);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.trailerId = buf.readInt();
        this.entityId = buf.readInt();
    }

    @Override
    public IMessage onMessage(MessageAttachTrailer message, MessageContext ctx)
    {
        FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() ->
        {
            World world = ctx.getServerHandler().player.world;
            Entity trailerEntity = world.getEntityByID(message.trailerId);
            if(trailerEntity instanceof EntityTrailer)
            {
                EntityTrailer trailer = (EntityTrailer) trailerEntity;
                Entity entity = world.getEntityByID(message.entityId);
                if(entity instanceof EntityPlayer && entity.getRidingEntity() == null)
                {
                    trailer.setPullingEntity(entity);
                    SyncedPlayerData.setTrailer((EntityPlayer) entity, message.trailerId);
                }
            }
        });
        return null;
    }
}
