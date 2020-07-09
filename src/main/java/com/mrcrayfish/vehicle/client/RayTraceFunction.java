package com.mrcrayfish.vehicle.client;

import com.mrcrayfish.obfuscate.common.data.SyncedPlayerData;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.init.ModDataKeys;
import com.mrcrayfish.vehicle.item.JerryCanItem;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageFuelVehicle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

/**
 * Author: MrCrayfish
 */
public interface RayTraceFunction
{
    Hand apply(EntityRayTracer rayTracer, EntityRayTracer.RayTraceResultRotated result, PlayerEntity player);

    /**
     * Checks if fuel can be transferred from a jerry can to a powered vehicle, and sends a packet to do so every other tick, if it can
     *
     * @return whether or not fueling can continue
     */
    RayTraceFunction FUNCTION_FUELING = (rayTracer, result, player) ->
    {
        if(SyncedPlayerData.instance().get(player, ModDataKeys.GAS_PUMP).isPresent() && ControllerEvents.isRightClicking())
        {
            Entity entity = result.getEntity();
            if(entity instanceof PoweredVehicleEntity)
            {
                PoweredVehicleEntity poweredVehicle = (PoweredVehicleEntity) entity;
                if(poweredVehicle.requiresFuel() && poweredVehicle.getCurrentFuel() < poweredVehicle.getFuelCapacity())
                {
                    if(rayTracer.getContinuousInteractionTickCounter() % 2 == 0)
                    {
                        PacketHandler.instance.sendToServer(new MessageFuelVehicle(result.getEntity().getEntityId(), Hand.MAIN_HAND));
                        poweredVehicle.fuelVehicle(player, Hand.MAIN_HAND);
                    }
                    return Hand.MAIN_HAND;
                }
            }
        }

        for(Hand hand : Hand.values())
        {
            ItemStack stack = player.getHeldItem(hand);
            if(!stack.isEmpty() && stack.getItem() instanceof JerryCanItem && ControllerEvents.isRightClicking())
            {
                Entity entity = result.getEntity();
                if(entity instanceof PoweredVehicleEntity)
                {
                    PoweredVehicleEntity poweredVehicle = (PoweredVehicleEntity) entity;
                    if(poweredVehicle.requiresFuel() && poweredVehicle.getCurrentFuel() < poweredVehicle.getFuelCapacity())
                    {
                        int fuel = ((JerryCanItem) stack.getItem()).getCurrentFuel(stack);
                        if(fuel > 0)
                        {
                            if(rayTracer.getContinuousInteractionTickCounter() % 2 == 0)
                            {
                                PacketHandler.instance.sendToServer(new MessageFuelVehicle(entity.getEntityId(), hand));
                            }
                            return hand;
                        }
                    }
                }
            }
        }
        return null;
    };
}
