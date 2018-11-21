package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.common.CommonEvents;
import com.mrcrayfish.vehicle.entity.vehicle.EntityTrailer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
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
        EntityPlayerMP player = ctx.getServerHandler().player;
        World world = player.world;
        Entity trailerEntity = world.getEntityByID(message.trailerId);
        if(trailerEntity instanceof EntityTrailer)
        {
            EntityTrailer trailer = (EntityTrailer) trailerEntity;
            Entity entity = world.getEntityByID(message.entityId);
            if(entity != null)
            {
                trailer.setPullingEntity(entity);
                if(entity instanceof EntityPlayer)
                {
                    entity.getDataManager().set(CommonEvents.TRAILER, message.trailerId);
                }
            }
        }
        return null;
    }
}
