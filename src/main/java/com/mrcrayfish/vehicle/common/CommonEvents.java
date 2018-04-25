package com.mrcrayfish.vehicle.common;

import com.google.common.collect.ImmutableList;
import com.mrcrayfish.obfuscate.common.event.EntityLivingInitEvent;
import com.mrcrayfish.vehicle.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public class CommonEvents
{
    public static final DataParameter<Boolean> PUSHING_CART = EntityDataManager.createKey(EntityPlayer.class, DataSerializers.BOOLEAN);

    private static final List<String> IGNORE_ITEMS;
    private static final List<String> IGNORE_SOUNDS;
    private static final List<String> IGNORE_ENTITIES;

    static
    {
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        builder.add("body");
        IGNORE_ITEMS = builder.build();

        builder = ImmutableList.builder();
        builder.add("idle");
        builder.add("driving");
        IGNORE_SOUNDS = builder.build();

        builder = ImmutableList.builder();
        builder.add("vehicle_atv");
        builder.add("couch");
        IGNORE_ENTITIES = builder.build();
    }

    @SubscribeEvent
    public void onMissingItem(RegistryEvent.MissingMappings<Item> event)
    {
        for(RegistryEvent.MissingMappings.Mapping<Item> missing : event.getMappings())
        {
            if(missing.key.getResourceDomain().equals(Reference.MOD_ID) && IGNORE_ITEMS.contains(missing.key.getResourcePath()))
            {
                missing.ignore();
            }
        }
    }

    @SubscribeEvent
    public void onMissingSound(RegistryEvent.MissingMappings<SoundEvent> event)
    {
        for(RegistryEvent.MissingMappings.Mapping<SoundEvent> missing : event.getMappings())
        {
            if(missing.key.getResourceDomain().equals(Reference.MOD_ID) && IGNORE_SOUNDS.contains(missing.key.getResourcePath()))
            {
                missing.ignore();
            }
        }
    }

    @SubscribeEvent
    public void onMissingEntity(RegistryEvent.MissingMappings<EntityEntry> event)
    {
        for(RegistryEvent.MissingMappings.Mapping<EntityEntry> missing : event.getMappings())
        {
            if(missing.key.getResourceDomain().equals(Reference.MOD_ID) && IGNORE_ENTITIES.contains(missing.key.getResourcePath()))
            {
                missing.ignore();
            }
        }
    }

    @SubscribeEvent
    public void onEntityInit(EntityLivingInitEvent event)
    {
        if(event.getEntityLiving() instanceof EntityPlayer)
        {
            event.getEntityLiving().getDataManager().register(PUSHING_CART, false);
        }
    }
}
