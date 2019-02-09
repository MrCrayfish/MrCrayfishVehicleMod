package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.common.inventory.IAttachableChest;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.SoundType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Author: MrCrayfish
 */
public class MessageAttachChest implements IMessage, IMessageHandler<MessageAttachChest, IMessage>
{
    private int entityId;

    public MessageAttachChest() {}

    public MessageAttachChest(int entityId)
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
    public IMessage onMessage(MessageAttachChest message, MessageContext ctx)
    {
        FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() ->
        {
            EntityPlayerMP player = ctx.getServerHandler().player;
            World world = player.world;
            Entity targetEntity = world.getEntityByID(message.entityId);
            if(targetEntity != null && targetEntity instanceof IAttachableChest)
            {
                float reachDistance = (float) player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
                if(player.getDistance(targetEntity) < reachDistance)
                {
                    IAttachableChest attachableChest = (IAttachableChest) targetEntity;
                    if(!attachableChest.hasChest())
                    {
                        ItemStack stack = player.inventory.getCurrentItem();
                        if(!stack.isEmpty() && stack.getItem() == Item.getItemFromBlock(Blocks.CHEST))
                        {
                            attachableChest.attachChest(stack);
                            world.playSound(null, targetEntity.posX, targetEntity.posY, targetEntity.posZ, SoundType.WOOD.getPlaceSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
                        }
                    }
                }
            }
        });
        return null;
    }
}
