package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.common.inventory.IAttachableChest;
import com.mrcrayfish.vehicle.common.inventory.IStorage;
import com.mrcrayfish.vehicle.init.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageOpenStorage implements IMessage<MessageOpenStorage>
{
    private int entityId;

    public MessageOpenStorage() {}

    public MessageOpenStorage(int entityId)
    {
        this.entityId = entityId;
    }

    @Override
    public void encode(MessageOpenStorage message, PacketBuffer buffer)
    {
        buffer.writeInt(message.entityId);
    }

    @Override
    public MessageOpenStorage decode(PacketBuffer buffer)
    {
        return new MessageOpenStorage(buffer.readInt());
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void handle(MessageOpenStorage message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() ->
        {
            ServerPlayerEntity player = supplier.get().getSender();
            if(player != null)
            {
                World world = player.world;
                Entity targetEntity = world.getEntityByID(message.entityId);
                if(targetEntity instanceof IStorage)
                {
                    float reachDistance = (float) player.getAttribute(PlayerEntity.REACH_DISTANCE).getValue();
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
                                    NetworkHooks.openGui(player, attachableChest.getInventory(), buffer -> buffer.writeVarInt(message.entityId));
                                }
                            }
                        }
                        else
                        {
                            NetworkHooks.openGui(player, (IStorage) targetEntity, buffer -> buffer.writeVarInt(message.entityId));
                        }
                    }
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
