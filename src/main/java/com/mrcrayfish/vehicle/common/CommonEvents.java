package com.mrcrayfish.vehicle.common;

import com.google.common.collect.ImmutableList;
import com.mrcrayfish.obfuscate.common.event.EntityLivingInitEvent;
import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.common.entity.HeldVehicleDataHandler;
import com.mrcrayfish.vehicle.entity.EntityVehicle;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class CommonEvents
{
    public static final DataParameter<Boolean> PUSHING_CART = EntityDataManager.createKey(EntityPlayer.class, DataSerializers.BOOLEAN);
    public static final DataParameter<NBTTagCompound> HELD_VEHICLE = EntityDataManager.createKey(EntityPlayer.class, DataSerializers.COMPOUND_TAG);

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
            event.getEntityLiving().getDataManager().register(HELD_VEHICLE, new NBTTagCompound());
        }
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        World world = event.getWorld();
        EntityPlayer player = event.getEntityPlayer();
        if(player.isSneaking())
        {
            if(!player.getDataManager().get(HELD_VEHICLE).hasNoTags())
            {
                if(event instanceof PlayerInteractEvent.RightClickBlock)
                {
                    Vec3d clickedVec = ((PlayerInteractEvent.RightClickBlock) event).getHitVec();
                    if(!world.isRemote && event.getFace() == EnumFacing.UP)
                    {
                        BlockPos pos = event.getPos().up();
                        NBTTagCompound tagCompound = player.getDataManager().get(HELD_VEHICLE);
                        Entity entity = EntityList.createEntityFromNBT(tagCompound, world);
                        if(entity != null && entity instanceof EntityVehicle)
                        {
                            //Updates the DataParameter
                            NBTTagCompound tag = new NBTTagCompound();
                            player.getDataManager().set(HELD_VEHICLE, tag);

                            //Updates the player capability
                            HeldVehicleDataHandler.IHeldVehicle heldVehicle = HeldVehicleDataHandler.getHandler(player);
                            if(heldVehicle != null)
                            {
                                heldVehicle.setVehicleTag(tag);
                            }

                            //Sets the positions and spawns the entity
                            float rotation = (player.getRotationYawHead() + 90F) % 360.0F;
                            Vec3d heldOffset = ((EntityVehicle) entity).getHeldOffset().rotateYaw((float) Math.toRadians(-player.getRotationYawHead()));
                            entity.setPositionAndRotation(clickedVec.x + heldOffset.x * 0.0625D, clickedVec.y + heldOffset.y * 0.0625D, clickedVec.z + heldOffset.z * 0.0625D, rotation, 0F);
                            world.spawnEntity(entity);

                            //Plays place sound
                            world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, SoundCategory.PLAYERS, 1.0F, 1.0F);
                        }
                    }
                }
                else if(event.isCancelable())
                {
                    event.setCanceled(true);
                }
            }
            else if(event instanceof PlayerInteractEvent.EntityInteract)
            {
                if(!world.isRemote)
                {
                    Entity targetEntity = ((PlayerInteractEvent.EntityInteract) event).getTarget();
                    if(targetEntity instanceof EntityVehicle && !targetEntity.isDead)
                    {
                        NBTTagCompound tagCompound = new NBTTagCompound();
                        String id = getEntityString(targetEntity);
                        if(id != null)
                        {
                            tagCompound.setString("id", id);
                            targetEntity.writeToNBT(tagCompound);
                            player.getDataManager().set(HELD_VEHICLE, tagCompound);

                            //Updates the held vehicle capability
                            HeldVehicleDataHandler.IHeldVehicle heldVehicle = HeldVehicleDataHandler.getHandler(player);
                            if(heldVehicle != null)
                            {
                                heldVehicle.setVehicleTag(tagCompound);
                            }

                            //Removes the entity from the world
                            world.removeEntity(targetEntity);

                            //Plays pick up sound
                            world.playSound(null, player.posX, player.posY, player.posZ, ModSounds.PICK_UP_VEHICLE, SoundCategory.PLAYERS, 1.0F, 1.0F);
                        }
                    }
                }
            }
        }
        else if(!player.getDataManager().get(HELD_VEHICLE).hasNoTags())
        {
            if(event.isCancelable())
            {
                event.setCanceled(true);
            }
        }
    }

    @Nullable
    private String getEntityString(Entity entity)
    {
        ResourceLocation resourcelocation = EntityList.getKey(entity);
        return resourcelocation == null ? null : resourcelocation.toString();
    }

    @SubscribeEvent
    public void onPlayerLoadData(PlayerEvent.LoadFromFile event)
    {
        EntityPlayer player = event.getEntityPlayer();
        HeldVehicleDataHandler.IHeldVehicle heldVehicle = HeldVehicleDataHandler.getHandler(player);
        if(heldVehicle != null)
        {
            player.getDataManager().set(CommonEvents.HELD_VEHICLE, heldVehicle.getVehicleTag());
        }
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event)
    {
        Entity entity = event.getEntityLiving();
        if(entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;
            NBTTagCompound tagCompound = player.getDataManager().get(CommonEvents.HELD_VEHICLE);
            if(!tagCompound.hasNoTags())
            {
                NBTTagCompound blankTag = new NBTTagCompound();
                HeldVehicleDataHandler.IHeldVehicle heldVehicle = HeldVehicleDataHandler.getHandler(player);
                if(heldVehicle != null)
                {
                    heldVehicle.setVehicleTag(blankTag);
                }
                player.getDataManager().set(CommonEvents.HELD_VEHICLE, blankTag);

                Entity vehicle = EntityList.createEntityFromNBT(tagCompound, player.world);
                if(vehicle != null && vehicle instanceof EntityVehicle)
                {
                    float rotation = (player.getRotationYawHead() + 90F) % 360.0F;
                    Vec3d heldOffset = ((EntityVehicle) vehicle).getHeldOffset().rotateYaw((float) Math.toRadians(-player.getRotationYawHead()));
                    vehicle.setPositionAndRotation(player.posX + heldOffset.x * 0.0625D, player.posY + player.getEyeHeight() + heldOffset.y * 0.0625D, player.posZ + heldOffset.z * 0.0625D, rotation, 0F);
                    player.world.spawnEntity(vehicle);
                }
            }
        }
    }
}
