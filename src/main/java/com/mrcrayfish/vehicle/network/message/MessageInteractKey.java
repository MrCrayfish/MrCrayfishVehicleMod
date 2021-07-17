package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.util.CommonUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageInteractKey implements IMessage<MessageInteractKey>
{
    private int entityId;

    public MessageInteractKey()
    {
    }

    public MessageInteractKey(Entity targetEntity)
    {
        this.entityId = targetEntity.getId();
    }

    private MessageInteractKey(int entityId)
    {
        this.entityId = entityId;
    }

    @Override
    public void encode(MessageInteractKey message, PacketBuffer buffer)
    {
        buffer.writeInt(message.entityId);
    }

    @Override
    public MessageInteractKey decode(PacketBuffer buffer)
    {
        return new MessageInteractKey(buffer.readInt());
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void handle(MessageInteractKey message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() ->
        {
            ServerPlayerEntity player = supplier.get().getSender();
            if(player != null)
            {
                Entity targetEntity = player.level.getEntity(message.entityId);
                if(targetEntity instanceof PoweredVehicleEntity)
                {
                    PoweredVehicleEntity poweredVehicle = (PoweredVehicleEntity) targetEntity;
                    if(poweredVehicle.isKeyNeeded())
                    {
                        ItemStack stack = player.getMainHandItem();
                        if(!stack.isEmpty() && stack.getItem() == ModItems.WRENCH.get())
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
                            if(!stack.isEmpty() && stack.getItem() == ModItems.KEY.get())
                            {
                                UUID keyUuid = CommonUtils.getOrCreateStackTag(stack).getUUID("VehicleId");
                                if(poweredVehicle.getUUID().equals(keyUuid))
                                {
                                    poweredVehicle.setKeyStack(stack.copy());
                                    player.setItemSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
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
        supplier.get().setPacketHandled(true);
    }
}
