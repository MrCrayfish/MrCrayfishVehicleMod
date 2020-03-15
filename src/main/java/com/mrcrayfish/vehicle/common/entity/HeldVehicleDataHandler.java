package com.mrcrayfish.vehicle.common.entity;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.common.CustomDataParameters;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
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
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

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

        IHeldVehicle handler = getHandler(event.getOriginal());
        if(handler != null)
        {
            IHeldVehicle newHandler = getHandler(event.getPlayer());
            if(newHandler != null)
            {
                CompoundNBT heldVehicleTag = handler.getVehicleTag();
                newHandler.setVehicleTag(heldVehicleTag);
                event.getPlayer().getDataManager().set(CustomDataParameters.HELD_VEHICLE, heldVehicleTag);
            }
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
