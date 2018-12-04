package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.entity.EntityPoweredVehicle;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.util.CommonUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

/**
 * Author: MrCrayfish
 */
public class MessageInteractKey implements IMessage, IMessageHandler<MessageInteractKey, IMessage>
{
    private UUID targetEntity;

    public MessageInteractKey()
    {
    }

    public MessageInteractKey(Entity targetEntity)
    {
        this.targetEntity = targetEntity.getUniqueID();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, targetEntity.toString());
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        targetEntity = UUID.fromString(ByteBufUtils.readUTF8String(buf));
    }

    @Override
    public IMessage onMessage(MessageInteractKey message, MessageContext ctx)
    {
        FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() ->
        {
            EntityPlayer player = ctx.getServerHandler().player;
            MinecraftServer server = player.world.getMinecraftServer();
            if(server != null)
            {
                Entity targetEntity = server.getEntityFromUuid(message.targetEntity);
                if(targetEntity != null && targetEntity instanceof EntityPoweredVehicle)
                {
                    EntityPoweredVehicle poweredVehicle = (EntityPoweredVehicle) targetEntity;
                    if(poweredVehicle.isKeyNeeded())
                    {
                        ItemStack stack = player.getHeldItemMainhand();
                        if(!stack.isEmpty() && stack.getItem() == ModItems.WRENCH)
                        {
                            if(poweredVehicle.isOwner(player))
                            {
                                poweredVehicle.ejectKey();
                                poweredVehicle.setKeyNeeded(false);
                                CommonUtils.sendInfoMessage(player, "vehicle.status.key_removed");
                            }
                            else
                            {
                                CommonUtils.sendInfoMessage(player, "vehicle.status.invalid_owner");
                            }
                            return;
                        }
                        if(poweredVehicle.getKeyStack().isEmpty())
                        {
                            if(!stack.isEmpty() && stack.getItem() == ModItems.KEY)
                            {
                                if(poweredVehicle.getUniqueID().equals(CommonUtils.getItemTagCompound(stack).getUniqueId("vehicleId")))
                                {
                                    poweredVehicle.setKeyStack(stack.copy());
                                    player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
                                }
                                else
                                {
                                    CommonUtils.sendInfoMessage(player, "vehicle.status.key_invalid");
                                }
                            }
                        }
                        else
                        {
                            poweredVehicle.ejectKey();
                        }
                    }
                }
            }
        });
        return null;
    }
}
