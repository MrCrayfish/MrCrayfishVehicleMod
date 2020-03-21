package com.mrcrayfish.vehicle.common.entity;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageSyncHeldVehicle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class HeldVehicleDataHandler
{
    @CapabilityInject(IHeldVehicle.class)
    public static final Capability<IHeldVehicle> CAPABILITY_HELD_VEHICLE = null;

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IHeldVehicle.class, new Storage(), HeldVehicle::new);
        MinecraftForge.EVENT_BUS.register(new HeldVehicleDataHandler());
    }

    public static boolean isHoldingVehicle(EntityPlayer player)
    {
        IHeldVehicle handler = getHandler(player);
        if(handler != null)
        {
            return !handler.getVehicleTag().hasNoTags();
        }
        return false;
    }

    public static NBTTagCompound getHeldVehicle(EntityPlayer player)
    {
        IHeldVehicle handler = getHandler(player);
        if(handler != null)
        {
            return handler.getVehicleTag();
        }
        return new NBTTagCompound();
    }

    public static void setHeldVehicle(EntityPlayer player, NBTTagCompound vehicleTag)
    {
        IHeldVehicle handler = getHandler(player);
        if(handler != null)
        {
            handler.setVehicleTag(vehicleTag);
        }
        if(!player.world.isRemote)
        {
            PacketHandler.INSTANCE.sendTo(new MessageSyncHeldVehicle(player.getEntityId(), vehicleTag), (EntityPlayerMP) player);
            PacketHandler.INSTANCE.sendToAllTracking(new MessageSyncHeldVehicle(player.getEntityId(), vehicleTag), player);
        }
    }

    @Nullable
    public static IHeldVehicle getHandler(EntityPlayer player)
    {
        if (player.hasCapability(CAPABILITY_HELD_VEHICLE, EnumFacing.DOWN))
        {
            return player.getCapability(CAPABILITY_HELD_VEHICLE, EnumFacing.DOWN);
        }
        return null;
    }

    @SubscribeEvent
    public void attachCapabilities(AttachCapabilitiesEvent<Entity> event)
    {
        if (event.getObject() instanceof EntityPlayer)
        {
            event.addCapability(new ResourceLocation(Reference.MOD_ID, "held_vehicle"), new Provider());
        }
    }

    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking event)
    {
        if(event.getTarget() instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) event.getTarget();
            NBTTagCompound vehicleTag = getHeldVehicle(player);
            PacketHandler.INSTANCE.sendTo(new MessageSyncHeldVehicle(player.getEntityId(), vehicleTag), (EntityPlayerMP) event.getEntityPlayer());
        }
    }

    @SubscribeEvent
    public void onPlayerJoinWorld(EntityJoinWorldEvent event)
    {
        Entity entity = event.getEntity();
        if(entity instanceof EntityPlayer && !event.getWorld().isRemote)
        {
            EntityPlayer player = (EntityPlayer) entity;
            NBTTagCompound vehicleTag = getHeldVehicle(player);
            PacketHandler.INSTANCE.sendTo(new MessageSyncHeldVehicle(player.getEntityId(), vehicleTag), (EntityPlayerMP) player);
            //PacketHandler.INSTANCE.sendToAllTracking(new MessageSyncHeldVehicle(player.getEntityId(), vehicleTag), player);
        }
    }

    public interface IHeldVehicle
    {
        void setVehicleTag(NBTTagCompound tagCompound);
        NBTTagCompound getVehicleTag();
    }

    public static class HeldVehicle implements IHeldVehicle
    {
        private NBTTagCompound tagCompound = new NBTTagCompound();

        @Override
        public void setVehicleTag(NBTTagCompound tagCompound)
        {
            this.tagCompound = tagCompound;
        }

        @Override
        public NBTTagCompound getVehicleTag()
        {
            return tagCompound;
        }
    }

    public static class Storage implements Capability.IStorage<IHeldVehicle>
    {
        @Nullable
        @Override
        public NBTBase writeNBT(Capability<IHeldVehicle> capability, IHeldVehicle instance, EnumFacing side)
        {
            return instance.getVehicleTag();
        }

        @Override
        public void readNBT(Capability<IHeldVehicle> capability, IHeldVehicle instance, EnumFacing side, NBTBase nbt)
        {
            instance.setVehicleTag((NBTTagCompound) nbt);
        }
    }

    public static class Provider implements ICapabilitySerializable<NBTTagCompound>
    {
        final IHeldVehicle INSTANCE = CAPABILITY_HELD_VEHICLE.getDefaultInstance();

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
        {
            return capability == CAPABILITY_HELD_VEHICLE;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
        {
            return hasCapability(capability, facing) ? CAPABILITY_HELD_VEHICLE.cast(INSTANCE) : null;
        }

        @Override
        public NBTTagCompound serializeNBT()
        {
            return (NBTTagCompound) CAPABILITY_HELD_VEHICLE.getStorage().writeNBT(CAPABILITY_HELD_VEHICLE, INSTANCE, null);
        }

        @Override
        public void deserializeNBT(NBTTagCompound tagCompound)
        {
            CAPABILITY_HELD_VEHICLE.getStorage().readNBT(CAPABILITY_HELD_VEHICLE, INSTANCE, null, tagCompound);
        }
    }
}
