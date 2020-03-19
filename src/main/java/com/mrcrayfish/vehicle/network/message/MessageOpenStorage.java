package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.common.inventory.IAttachableChest;
import com.mrcrayfish.vehicle.common.inventory.IStorage;
import com.mrcrayfish.vehicle.init.ModItems;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Author: MrCrayfish
 */
public class MessageOpenStorage implements IMessage, IMessageHandler<MessageOpenStorage, IMessage>
{
    private int entityId;

    public MessageOpenStorage() {}

    public MessageOpenStorage(int entityId)
    {
        this.entityId = entityId;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(entityId);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        entityId = buf.readInt();
    }

    @Override
    public IMessage onMessage(MessageOpenStorage message, MessageContext ctx)
    {
        FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() ->
        {
            EntityPlayerMP player = ctx.getServerHandler().player;
            World world = player.world;
            Entity targetEntity = world.getEntityByID(message.entityId);
            if(targetEntity instanceof IStorage && !player.isSneaking())
            {
                float reachDistance = (float) player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
                if(player.getDistance(targetEntity) < reachDistance)
                {
                    if(targetEntity instanceof IAttachableChest)
                    {
                        IAttachableChest attachableChest = (IAttachableChest) targetEntity;
                        if(attachableChest.hasChest())
                        {
                            ItemStack stack = player.inventory.getCurrentItem();
                            if(stack.getItem() == ModItems.WRENCH)
                            {
                                ((IAttachableChest) targetEntity).removeChest();
                            }
                            else
                            {
                                attachableChest.getInventory().openGui(player, targetEntity);
                            }
                        }
                    }
                    else
                    {
                        ((IStorage) targetEntity).getInventory().openGui(player, targetEntity);
                    }
                }
            }
        });
        return null;
    }
}
