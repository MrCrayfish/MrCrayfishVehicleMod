package com.mrcrayfish.vehicle.common;

import com.google.common.collect.ImmutableList;
import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.common.entity.HeldVehicleDataHandler;
import com.mrcrayfish.vehicle.entity.EntityJack;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.entity.TrailerEntity;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageThrowVehicle;
import com.mrcrayfish.vehicle.tileentity.JackTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public class CommonEvents
{
    private static final List<String> IGNORE_ITEMS;
    private static final List<String> IGNORE_SOUNDS;
    private static final List<String> IGNORE_ENTITIES;

    static
    {
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        builder.add("body");
        builder.add("atv");
        builder.add("go_kart");
        IGNORE_ITEMS = builder.build();

        builder = ImmutableList.builder();
        builder.add("idle");
        builder.add("driving");
        IGNORE_SOUNDS = builder.build();

        builder = ImmutableList.builder();
        builder.add("vehicle_atv");
        builder.add("couch");
        builder.add("bath");
        IGNORE_ENTITIES = builder.build();
    }

    @SubscribeEvent
    public void onMissingItem(RegistryEvent.MissingMappings<Item> event)
    {
        for(RegistryEvent.MissingMappings.Mapping<Item> missing : event.getMappings())
        {
            if(missing.key.getNamespace().equals(Reference.MOD_ID) && IGNORE_ITEMS.contains(missing.key.getPath()))
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
            if(missing.key.getNamespace().equals(Reference.MOD_ID) && IGNORE_SOUNDS.contains(missing.key.getPath()))
            {
                missing.ignore();
            }
        }
    }

    @SubscribeEvent
    public void onMissingEntity(RegistryEvent.MissingMappings<EntityType<?>> event)
    {
        for(RegistryEvent.MissingMappings.Mapping<EntityType<?>> missing : event.getMappings())
        {
            if(missing.key.getNamespace().equals(Reference.MOD_ID) && IGNORE_ENTITIES.contains(missing.key.getPath()))
            {
                missing.ignore();
            }
        }
    }

    @SubscribeEvent
    public void onEntityInit(EntityEvent.EntityConstructing event)
    {
        if(event.getEntity() instanceof PlayerEntity)
        {
            event.getEntity().getDataManager().register(CustomDataParameters.PUSHING_CART, false);
            event.getEntity().getDataManager().register(CustomDataParameters.HELD_VEHICLE, new CompoundNBT());
            event.getEntity().getDataManager().register(CustomDataParameters.TRAILER, -1);
            event.getEntity().getDataManager().register(CustomDataParameters.GAS_PUMP, Optional.empty());
        }
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.EntityInteractSpecific event)
    {
        if(pickUpVehicle(event.getWorld(), event.getPlayer(), event.getHand(), event.getTarget()))
        {
            event.setCanceled(true);
        }
    }

    public static boolean pickUpVehicle(World world, PlayerEntity player, Hand hand, Entity targetEntity)
    {
        if(hand == Hand.MAIN_HAND && !world.isRemote && player.isSneaking() && !player.isSpectator() && Config.SERVER.pickUpVehicles.get())
        {
            if(player.getDataManager().get(CustomDataParameters.HELD_VEHICLE).isEmpty())
            {
                if(targetEntity instanceof VehicleEntity && !targetEntity.isBeingRidden() && targetEntity.isAlive())
                {
                    CompoundNBT tagCompound = new CompoundNBT();
                    String id = getEntityString(targetEntity);
                    if(id != null)
                    {
                        ((VehicleEntity) targetEntity).setTrailer(null);

                        tagCompound.putString("id", id);
                        targetEntity.writeWithoutTypeId(tagCompound);
                        player.getDataManager().set(CustomDataParameters.HELD_VEHICLE, tagCompound);

                        //Updates the held vehicle capability
                        HeldVehicleDataHandler.IHeldVehicle heldVehicle = HeldVehicleDataHandler.getHandler(player);
                        if(heldVehicle != null)
                        {
                            heldVehicle.setVehicleTag(tagCompound);
                        }

                        //Removes the entity from the world
                        targetEntity.remove();

                        //Plays pick up sound
                        world.playSound(null, player.posX, player.posY, player.posZ, ModSounds.PICK_UP_VEHICLE, SoundCategory.PLAYERS, 1.0F, 1.0F);

                        return true;
                    }
                }
            }
            else if(targetEntity instanceof TrailerEntity && !targetEntity.isBeingRidden() && targetEntity.isAlive())
            {
                CompoundNBT tagCompound = player.getDataManager().get(CustomDataParameters.HELD_VEHICLE);
                Optional<EntityType<?>> optional = EntityType.byKey(tagCompound.getString("id"));
                if(optional.isPresent())
                {
                    EntityType<?> entityType = optional.get();
                    Entity vehicle = entityType.create(world);
                    if(vehicle instanceof VehicleEntity && ((VehicleEntity) vehicle).canMountTrailer())
                    {
                        vehicle.read(tagCompound);
                        vehicle.setPositionAndRotation(targetEntity.posX, targetEntity.posY, targetEntity.posZ, targetEntity.rotationYaw, targetEntity.rotationPitch);

                        //Updates the DataParameter
                        CompoundNBT tag = new CompoundNBT();
                        player.getDataManager().set(CustomDataParameters.HELD_VEHICLE, tag);

                        //Updates the player capability
                        HeldVehicleDataHandler.IHeldVehicle heldVehicle = HeldVehicleDataHandler.getHandler(player);
                        if(heldVehicle != null)
                        {
                            heldVehicle.setVehicleTag(tag);
                        }

                        //Plays place sound
                        world.addEntity(vehicle);
                        world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, SoundCategory.PLAYERS, 1.0F, 1.0F);
                        vehicle.startRiding(targetEntity);

                        return true;
                    }
                }
            }
        }
        return false;
    }

    private RayTraceResult rayTrace(PlayerEntity player, double distance, float partialTicks, boolean includeWater)
    {
        Vec3d eyeVec = player.getEyePosition(partialTicks);
        Vec3d lookVec = player.getLook(partialTicks);
        Vec3d combinedVec = eyeVec.add(lookVec.x * distance, lookVec.y * distance, lookVec.z * distance);
        return player.world.rayTraceBlocks(new RayTraceContext(eyeVec, combinedVec, RayTraceContext.BlockMode.OUTLINE, includeWater ? RayTraceContext.FluidMode.ANY : RayTraceContext.FluidMode.NONE, player));
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event)
    {
        if(event.getHand() == Hand.OFF_HAND) return;

        PlayerEntity player = event.getPlayer();
        World world = event.getWorld();
        if(!world.isRemote)
        {
            if(event.getFace() == Direction.UP)
            {
                if(!player.getDataManager().get(CustomDataParameters.HELD_VEHICLE).isEmpty())
                {
                    BlockPos pos = event.getPos();
                    TileEntity tileEntity = event.getWorld().getTileEntity(pos);
                    if(tileEntity instanceof JackTileEntity)
                    {
                        JackTileEntity jack = (JackTileEntity) tileEntity;
                        if(jack.getJack() == null)
                        {
                            CompoundNBT tagCompound = player.getDataManager().get(CustomDataParameters.HELD_VEHICLE);
                            EntityType.byKey(tagCompound.getString("id")).ifPresent(entityType ->
                            {
                                Entity entity = entityType.create(world);
                                if(entity instanceof VehicleEntity)
                                {
                                    entity.read(tagCompound);

                                    CompoundNBT tag = new CompoundNBT();
                                    player.getDataManager().set(CustomDataParameters.HELD_VEHICLE, tag);

                                    //Updates the player capability
                                    HeldVehicleDataHandler.IHeldVehicle heldVehicle = HeldVehicleDataHandler.getHandler(player);
                                    if(heldVehicle != null)
                                    {
                                        heldVehicle.setVehicleTag(tag);
                                    }

                                    entity.fallDistance = 0.0F;
                                    entity.rotationYaw = (player.getRotationYawHead() + 90F) % 360.0F;

                                    jack.setVehicle((VehicleEntity) entity);
                                    if(jack.getJack() != null)
                                    {
                                        EntityJack entityJack = jack.getJack();
                                        entityJack.updateRidden();
                                        entity.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
                                    }
                                    world.addEntity(entity);
                                    world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, SoundCategory.PLAYERS, 1.0F, 1.0F);
                                }
                            });
                        }
                        event.setCanceled(true);
                        event.setCancellationResult(ActionResultType.SUCCESS);
                        return;
                    }
                }
            }

            if(player.isSneaking())
            {
                if(!player.getDataManager().get(CustomDataParameters.HELD_VEHICLE).isEmpty())
                {
                    //Vec3d clickedVec = event.getHitVec(); //TODO WHY DID FORGE REMOVE THIS. GOING TO CREATE A PATCH
                    RayTraceResult result = this.rayTrace(player, 10.0, 0.0F, false);
                    Vec3d clickedVec = result.getHitVec();
                    if(clickedVec == null || event.getFace() != Direction.UP)
                    {
                        event.setCanceled(true);
                        return;
                    }

                    CompoundNBT tagCompound = player.getDataManager().get(CustomDataParameters.HELD_VEHICLE);
                    EntityType.byKey(tagCompound.getString("id")).ifPresent(entityType ->
                    {
                        Entity entity = entityType.create(player.world);
                        if(entity instanceof VehicleEntity)
                        {
                            entity.read(tagCompound);
                            MinecraftServer server = world.getServer();
                            if(server != null && world.getEntityByID(entity.getEntityId()) == null) //TODO check this. Actually might not need
                            {
                                server.deferTask(() ->
                                {
                                    //Updates the DataParameter
                                    CompoundNBT tag = new CompoundNBT();
                                    player.getDataManager().set(CustomDataParameters.HELD_VEHICLE, tag);

                                    //Updates the player capability
                                    HeldVehicleDataHandler.IHeldVehicle heldVehicle = HeldVehicleDataHandler.getHandler(player);
                                    if(heldVehicle != null)
                                    {
                                        heldVehicle.setVehicleTag(tag);
                                    }

                                    //Sets the positions and spawns the entity
                                    float rotation = (player.getRotationYawHead() + 90F) % 360.0F;
                                    Vec3d heldOffset = ((VehicleEntity) entity).getProperties().getHeldOffset().rotateYaw((float) Math.toRadians(-player.getRotationYawHead()));

                                    entity.setPositionAndRotation(clickedVec.x + heldOffset.x * 0.0625D, clickedVec.y, clickedVec.z + heldOffset.z * 0.0625D, rotation, 0F);
                                    entity.fallDistance = 0.0F;

                                    //Plays place sound
                                    world.addEntity(entity);
                                    world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, SoundCategory.PLAYERS, 1.0F, 1.0F);
                                });
                                event.setCanceled(true);
                                event.setCancellationResult(ActionResultType.SUCCESS);
                            }
                        }
                    });
                }
            }
        }
        else if(!player.getDataManager().get(CustomDataParameters.HELD_VEHICLE).isEmpty())
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        if(event.getHand() == Hand.OFF_HAND)
            return;

        World world = event.getWorld();
        if(world.isRemote)
        {
            if(event instanceof PlayerInteractEvent.RightClickEmpty || event instanceof PlayerInteractEvent.RightClickItem)
            {
                PlayerEntity player = event.getPlayer();
                float reach = (float) player.getAttribute(PlayerEntity.REACH_DISTANCE).getValue();
                reach = player.isCreative() ? reach : reach - 0.5F;
                RayTraceResult result = this.rayTrace(player, reach, 0.0F, false);
                if(result.getType() == RayTraceResult.Type.BLOCK)
                    return;

                if(!player.getDataManager().get(CustomDataParameters.HELD_VEHICLE).isEmpty())
                {
                    if(player.isSneaking())
                    {
                        PacketHandler.instance.sendToServer(new MessageThrowVehicle());
                    }
                    if(event.isCancelable())
                    {
                        event.setCanceled(true);
                        event.setCancellationResult(ActionResultType.SUCCESS);
                    }
                }
            }
        }
    }

    @Nullable
    private static String getEntityString(Entity entity)
    {
        ResourceLocation resourceLocation = entity.getType().getRegistryName();
        return resourceLocation == null ? null : resourceLocation.toString();
    }

    @SubscribeEvent
    public void onPlayerLoadData(PlayerEvent.LoadFromFile event)
    {
        PlayerEntity player = event.getPlayer();
        HeldVehicleDataHandler.IHeldVehicle heldVehicle = HeldVehicleDataHandler.getHandler(player);
        if(heldVehicle != null)
        {
            player.getDataManager().set(CustomDataParameters.HELD_VEHICLE, heldVehicle.getVehicleTag());
        }
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event)
    {
        Entity entity = event.getEntityLiving();
        if(entity instanceof PlayerEntity)
        {
            PlayerEntity player = (PlayerEntity) entity;
            this.dropVehicle(player);
        }
    }

    private void dropVehicle(PlayerEntity player)
    {
        CompoundNBT tagCompound = player.getDataManager().get(CustomDataParameters.HELD_VEHICLE);
        if(!tagCompound.isEmpty())
        {
            CompoundNBT blankTag = new CompoundNBT();
            HeldVehicleDataHandler.IHeldVehicle heldVehicle = HeldVehicleDataHandler.getHandler(player);
            if(heldVehicle != null)
            {
                heldVehicle.setVehicleTag(blankTag);
            }
            player.getDataManager().set(CustomDataParameters.HELD_VEHICLE, blankTag);

            EntityType.byKey(tagCompound.getString("id")).ifPresent(entityType ->
            {
                Entity vehicle = entityType.create(player.world);
                if(vehicle instanceof VehicleEntity)
                {
                    vehicle.read(tagCompound);
                    float rotation = (player.getRotationYawHead() + 90F) % 360.0F;
                    Vec3d heldOffset = ((VehicleEntity) vehicle).getProperties().getHeldOffset().rotateYaw((float) Math.toRadians(-player.getRotationYawHead()));
                    vehicle.setPositionAndRotation(player.posX + heldOffset.x * 0.0625D, player.posY + player.getEyeHeight() + heldOffset.y * 0.0625D, player.posZ + heldOffset.z * 0.0625D, rotation, 0F);
                    player.world.addEntity(vehicle);
                }
            });
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END)
        {
            PlayerEntity player = event.player;
            World world = player.world;
            if(player.isSneaking())
            {
                int trailerId = player.getDataManager().get(CustomDataParameters.TRAILER);
                if(trailerId != -1)
                {
                    Entity entity = world.getEntityByID(trailerId);
                    if(entity instanceof TrailerEntity)
                    {
                        ((TrailerEntity) entity).setPullingEntity(null);
                    }
                    player.getDataManager().set(CustomDataParameters.TRAILER, -1);
                }
            }
            if(!world.isRemote && player.isSpectator())
            {
                this.dropVehicle(player);
            }
        }
    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickItem event)
    {
        if(event.getPlayer().getDataManager().get(CustomDataParameters.GAS_PUMP).isPresent())
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickBlock event)
    {
        BlockState state = event.getWorld().getBlockState(event.getPos());
        if(state.getBlock() != ModBlocks.GAS_PUMP && event.getPlayer().getDataManager().get(CustomDataParameters.GAS_PUMP).isPresent())
        {
            event.setCanceled(true);
        }
    }
}
