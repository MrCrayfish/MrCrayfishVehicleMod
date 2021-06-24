package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.common.inventory.IAttachableChest;
import com.mrcrayfish.vehicle.common.inventory.IStorage;
import com.mrcrayfish.vehicle.init.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeMod;
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
                World world = player.level;
                Entity targetEntity = world.getEntity(message.entityId);
                if(targetEntity instanceof IStorage)
                {
                    IStorage storage = (IStorage) targetEntity;
                    float reachDistance = (float) player.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue();
                    if(player.distanceTo(targetEntity) < reachDistance)
                    {
                        if(targetEntity instanceof IAttachableChest)
                        {
                            IAttachableChest attachableChest = (IAttachableChest) targetEntity;
                            if(attachableChest.hasChest())
                            {
                                ItemStack stack = player.inventory.getSelected();
                                if(stack.getItem() == ModItems.WRENCH.get())
                                {
                                    ((IAttachableChest) targetEntity).removeChest();
                                }
                                else
                                {
                                    NetworkHooks.openGui(player, storage.getStorageContainerProvider(), buffer -> buffer.writeVarInt(message.entityId));
                                }
                            }
                        }
                        else
                        {
                            NetworkHooks.openGui(player, storage.getStorageContainerProvider(), buffer -> buffer.writeVarInt(message.entityId));
                        }
                    }
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
