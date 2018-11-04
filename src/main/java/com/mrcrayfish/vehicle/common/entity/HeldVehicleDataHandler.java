package com.mrcrayfish.vehicle.common.entity;

import com.mrcrayfish.vehicle.Reference;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
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
