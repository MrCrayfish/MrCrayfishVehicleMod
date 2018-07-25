package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.entity.IChest;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Author: MrCrayfish
 */
public class MessageVehicleChest implements IMessage, IMessageHandler<MessageVehicleChest, IMessage>
{
    private int entityId;

    public MessageVehicleChest() {}

    public MessageVehicleChest(int entityId)
    {
        this.entityId = entityId;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.entityId);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.entityId = buf.readInt();
    }

    @Override
    public IMessage onMessage(MessageVehicleChest message, MessageContext ctx)
    {
        EntityPlayerMP player = ctx.getServerHandler().player;
        World world = player.world;
        Entity targetEntity = world.getEntityByID(message.entityId);
        if(targetEntity != null && targetEntity instanceof IChest)
        {
            float reachDistance = (float) player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
            if(player.getDistance(targetEntity) < reachDistance)
            {
                IInventory inventory = ((IChest) targetEntity).getChest();
                if(inventory != null)
                {
                    player.displayGUIChest(inventory);
                }
            }
        }
        return null;
    }
}
