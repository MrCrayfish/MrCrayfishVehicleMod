package com.mrcrayfish.vehicle.common.entity;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageSyncHeldVehicle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;

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

    public static boolean isHoldingVehicle(PlayerEntity player)
    {
        IHeldVehicle handler = getHandler(player);
        if(handler != null)
        {
            return !handler.getVehicleTag().isEmpty();
        }
        return false;
    }

    public static CompoundNBT getHeldVehicle(PlayerEntity player)
    {
        IHeldVehicle handler = getHandler(player);
        if(handler != null)
        {
            return handler.getVehicleTag();
        }
        return new CompoundNBT();
    }

    public static void setHeldVehicle(PlayerEntity player, CompoundNBT vehicleTag)
    {
        IHeldVehicle handler = getHandler(player);
        if(handler != null)
        {
            handler.setVehicleTag(vehicleTag);
        }
        if(!player.level.isClientSide)
        {
            PacketHandler.instance.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new MessageSyncHeldVehicle(player.getId(), vehicleTag));
        }
    }

    @Nullable
    public static IHeldVehicle getHandler(PlayerEntity player)
    {
        return player.getCapability(CAPABILITY_HELD_VEHICLE, Direction.DOWN).orElse(null);
    }

    @SubscribeEvent
    public void attachCapabilities(AttachCapabilitiesEvent<Entity> event)
    {
        if (event.getObject() instanceof PlayerEntity)
        {
            event.addCapability(new ResourceLocation(Reference.MOD_ID, "held_vehicle"), new Provider());
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event)
    {
        if(event.isWasDeath())
            return;

        CompoundNBT vehicleTag = getHeldVehicle(event.getOriginal());
        if(!vehicleTag.isEmpty())
        {
            setHeldVehicle(event.getPlayer(), vehicleTag);
        }
    }

    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking event)
    {
        if(event.getTarget() instanceof PlayerEntity)
        {
            PlayerEntity player = (PlayerEntity) event.getTarget();
            CompoundNBT vehicleTag = getHeldVehicle(player);
            PacketHandler.instance.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getPlayer()), new MessageSyncHeldVehicle(player.getId(), vehicleTag));
        }
    }

    @SubscribeEvent
    public void onPlayerJoinWorld(EntityJoinWorldEvent event)
    {
        Entity entity = event.getEntity();
        if(entity instanceof PlayerEntity && !event.getWorld().isClientSide)
        {
            PlayerEntity player = (PlayerEntity) entity;
            CompoundNBT vehicleTag = getHeldVehicle(player);
            PacketHandler.instance.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new MessageSyncHeldVehicle(player.getId(), vehicleTag));
        }
    }

    public interface IHeldVehicle
    {
        void setVehicleTag(CompoundNBT tagCompound);
        CompoundNBT getVehicleTag();
    }

    public static class HeldVehicle implements IHeldVehicle
    {
        private CompoundNBT compound = new CompoundNBT();

        @Override
        public void setVehicleTag(CompoundNBT tagCompound)
        {
            this.compound = tagCompound;
        }

        @Override
        public CompoundNBT getVehicleTag()
        {
            return compound;
        }
    }

    public static class Storage implements Capability.IStorage<IHeldVehicle>
    {
        @Nullable
        @Override
        public INBT writeNBT(Capability<IHeldVehicle> capability, IHeldVehicle instance, Direction side)
        {
            return instance.getVehicleTag();
        }

        @Override
        public void readNBT(Capability<IHeldVehicle> capability, IHeldVehicle instance, Direction side, INBT nbt)
        {
            instance.setVehicleTag((CompoundNBT) nbt);
        }
    }

    public static class Provider implements ICapabilitySerializable<CompoundNBT>
    {
        final IHeldVehicle INSTANCE = CAPABILITY_HELD_VEHICLE.getDefaultInstance();

        @Override
        public CompoundNBT serializeNBT()
        {
            return (CompoundNBT) CAPABILITY_HELD_VEHICLE.getStorage().writeNBT(CAPABILITY_HELD_VEHICLE, INSTANCE, null);
        }

        @Override
        public void deserializeNBT(CompoundNBT compound)
        {
            CAPABILITY_HELD_VEHICLE.getStorage().readNBT(CAPABILITY_HELD_VEHICLE, INSTANCE, null, compound);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
        {
            return CAPABILITY_HELD_VEHICLE.orEmpty(cap, LazyOptional.of(() -> INSTANCE));
        }
    }
}
