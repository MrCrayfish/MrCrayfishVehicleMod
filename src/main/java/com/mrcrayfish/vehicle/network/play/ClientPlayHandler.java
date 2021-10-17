package com.mrcrayfish.vehicle.network.play;

import com.mrcrayfish.vehicle.common.CosmeticTracker;
import com.mrcrayfish.vehicle.common.entity.HeldVehicleDataHandler;
import com.mrcrayfish.vehicle.common.inventory.IStorage;
import com.mrcrayfish.vehicle.common.inventory.StorageInventory;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.network.message.MessageEntityFluid;
import com.mrcrayfish.vehicle.network.message.MessageSyncCosmetics;
import com.mrcrayfish.vehicle.network.message.MessageSyncHeldVehicle;
import com.mrcrayfish.vehicle.network.message.MessageSyncStorage;
import com.mrcrayfish.vehicle.network.message.MessageSyncPlayerSeat;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

/**
 * Author: MrCrayfish
 */
@OnlyIn(Dist.CLIENT)
public class ClientPlayHandler
{
    public static void handleSyncStorage(MessageSyncStorage message)
    {
        World world = Minecraft.getInstance().level;
        if(world == null)
            return;

        Entity entity = world.getEntity(message.getEntityId());
        if(!(entity instanceof IStorage))
            return;

        IStorage storage = (IStorage) entity;
        String[] keys = message.getKeys();
        CompoundNBT[] tags = message.getTags();
        for(int i = 0; i < keys.length; i++)
        {
            StorageInventory inventory = storage.getStorageInventory(keys[i]);
            if(inventory != null)
            {
                CompoundNBT tag = tags[i];
                inventory.fromTag(tag.getList("Inventory", Constants.NBT.TAG_COMPOUND));
            }
        }
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
                int oldSeatIndex = vehicle.getSeatTracker().getSeatIndex(message.getUuid());
                vehicle.getSeatTracker().setSeatIndex(message.getSeatIndex(), message.getUuid());
                Entity passenger = vehicle.getPassengers().stream().filter(e -> e.getUUID().equals(message.getUuid())).findFirst().orElse(null);
                if(passenger instanceof PlayerEntity)
                {
                    vehicle.onPlayerChangeSeat((PlayerEntity) passenger, oldSeatIndex, message.getSeatIndex());
                }
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

    public static void handleSyncCosmetics(MessageSyncCosmetics message)
    {
        World world = Minecraft.getInstance().level;
        if(world == null)
            return;

        Entity entity = world.getEntity(message.getEntityId());
        if(!(entity instanceof VehicleEntity))
            return;

        CosmeticTracker tracker = ((VehicleEntity) entity).getCosmeticTracker();
        message.getDirtyEntries().forEach(pair -> {
            tracker.setSelectedModel(pair.getLeft(), pair.getRight());
        });
    }
}
