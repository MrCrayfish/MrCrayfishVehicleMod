package com.mrcrayfish.vehicle.client.network;

import com.mrcrayfish.vehicle.common.entity.HeldVehicleDataHandler;
import com.mrcrayfish.vehicle.common.inventory.IStorage;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.network.message.MessageEntityFluid;
import com.mrcrayfish.vehicle.network.message.MessageSyncHeldVehicle;
import com.mrcrayfish.vehicle.network.message.MessageSyncInventory;
import com.mrcrayfish.vehicle.network.message.MessageSyncPlayerSeat;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

/**
 * Author: MrCrayfish
 */
public class ClientPlayHandler
{

    public static void handleSyncInventory(MessageSyncInventory message)
    {
        World world = Minecraft.getInstance().level;
        if(world == null)
            return;

        Entity entity = world.getEntity(message.getEntityId());
        if(!(entity instanceof IStorage))
            return;

        ((IStorage) entity).getInventory().fromTag(message.getCompound().getList("Inventory", Constants.NBT.TAG_COMPOUND));
    }

    public static void handleEntityFluid(MessageEntityFluid message)
    {
        World world = Minecraft.getInstance().level;
        if(world == null)
            return;

        Entity entity = world.getEntity(message.getEntityId());
        if(entity == null)
            return;

        LazyOptional<IFluidHandler> optional = entity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
        optional.ifPresent(handler ->
        {
            if(handler instanceof FluidTank)
            {
                FluidTank tank = (FluidTank) handler;
                tank.setFluid(message.getStack());
            }
        });
    }

    public static void handleSyncPlayerSeat(MessageSyncPlayerSeat message)
    {
        PlayerEntity player = Minecraft.getInstance().player;
        if(player != null)
        {
            Entity entity = player.getCommandSenderWorld().getEntity(message.getEntityId());
            if(entity instanceof VehicleEntity)
            {
                VehicleEntity vehicle = (VehicleEntity) entity;
                vehicle.getSeatTracker().setSeatIndex(message.getSeatIndex(), message.getUuid());
            }
        }
    }

    public static void handleSyncHeldVehicle(MessageSyncHeldVehicle message)
    {
        World world = Minecraft.getInstance().level;
        if(world != null)
        {
            Entity entity = world.getEntity(message.getEntityId());
            if(entity instanceof PlayerEntity)
            {
                HeldVehicleDataHandler.setHeldVehicle((PlayerEntity) entity, message.getVehicleTag());
            }
        }
    }
}
