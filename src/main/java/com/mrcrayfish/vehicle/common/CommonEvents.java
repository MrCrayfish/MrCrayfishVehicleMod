package com.mrcrayfish.vehicle.common;

import com.google.common.collect.ImmutableList;
import com.mrcrayfish.obfuscate.common.data.SyncedPlayerData;
import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.common.entity.HeldVehicleDataHandler;
import com.mrcrayfish.vehicle.entity.EntityJack;
import com.mrcrayfish.vehicle.entity.TrailerEntity;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.init.ModDataKeys;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.item.FluidPipeItem;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageThrowVehicle;
import com.mrcrayfish.vehicle.tileentity.GasPumpTileEntity;
import com.mrcrayfish.vehicle.tileentity.JackTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        ImmutableList<RegistryEvent.MissingMappings.Mapping<Item>> mappings = ImmutableList.copyOf(event.getMappings().stream().filter(e -> e.key.getNamespace().equals(Reference.MOD_ID)).collect(Collectors.toList()));
        for(RegistryEvent.MissingMappings.Mapping<Item> missing : mappings)
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
        ImmutableList<RegistryEvent.MissingMappings.Mapping<SoundEvent>> mappings = ImmutableList.copyOf(event.getMappings().stream().filter(e -> e.key.getNamespace().equals(Reference.MOD_ID)).collect(Collectors.toList()));
        for(RegistryEvent.MissingMappings.Mapping<SoundEvent> missing : mappings)
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
        ImmutableList<RegistryEvent.MissingMappings.Mapping<EntityType<?>>> mappings = ImmutableList.copyOf(event.getMappings().stream().filter(e -> e.key.getNamespace().equals(Reference.MOD_ID)).collect(Collectors.toList()));
        for(RegistryEvent.MissingMappings.Mapping<EntityType<?>> missing : mappings)
        {
            if(missing.key.getNamespace().equals(Reference.MOD_ID) && IGNORE_ENTITIES.contains(missing.key.getPath()))
            {
                missing.ignore();
            }
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
        if(hand == Hand.MAIN_HAND && !world.isClientSide && player.isCrouching() && !player.isSpectator() && Config.SERVER.pickUpVehicles.get())
        {
            if(!HeldVehicleDataHandler.isHoldingVehicle(player))
            {
                if(targetEntity instanceof VehicleEntity && !targetEntity.isVehicle() && targetEntity.isAlive())
                {
                    CompoundNBT tagCompound = new CompoundNBT();
                    String id = getEntityString(targetEntity);
                    if(id != null)
                    {
                        ((VehicleEntity) targetEntity).setTrailer(null);

                        tagCompound.putString("id", id);
                        targetEntity.saveWithoutId(tagCompound);

                        //Updates the held vehicle capability
                        HeldVehicleDataHandler.setHeldVehicle(player, tagCompound);

                        //Removes the entity from the world
                        targetEntity.remove();

                        //Plays pick up sound
                        world.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.ENTITY_VEHICLE_PICK_UP.get(), SoundCategory.PLAYERS, 1.0F, 1.0F);

                        return true;
                    }
                }
            }
            else if(targetEntity instanceof TrailerEntity && !targetEntity.isVehicle() && targetEntity.isAlive())
            {
                CompoundNBT tagCompound = HeldVehicleDataHandler.getHeldVehicle(player);
                Optional<EntityType<?>> optional = EntityType.byString(tagCompound.getString("id"));
                if(optional.isPresent())
                {
                    EntityType<?> entityType = optional.get();
                    Entity vehicle = entityType.create(world);
                    if(vehicle instanceof VehicleEntity && ((VehicleEntity) vehicle).canMountTrailer())
                    {
                        vehicle.load(tagCompound);
                        vehicle.absMoveTo(targetEntity.getX(), targetEntity.getY(), targetEntity.getZ(), targetEntity.yRot, targetEntity.xRot);

                        //Updates the player capability
                        HeldVehicleDataHandler.setHeldVehicle(player, new CompoundNBT());

                        //Plays place sound
                        world.addFreshEntity(vehicle);
                        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_STRONG, SoundCategory.PLAYERS, 1.0F, 1.0F);
                        vehicle.startRiding(targetEntity);

                        return true;
                    }
                }
            }
        }
        return false;
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event)
    {
        if(event.getHand() == Hand.OFF_HAND) return;

        PlayerEntity player = event.getPlayer();
        World world = event.getWorld();
        if(!world.isClientSide())
        {
            if(HeldVehicleDataHandler.isHoldingVehicle(player))
            {
                if(event.getFace() == Direction.UP)
                {
                    BlockPos pos = event.getPos();
                    TileEntity tileEntity = event.getWorld().getBlockEntity(pos);
                    if(tileEntity instanceof JackTileEntity)
                    {
                        JackTileEntity jack = (JackTileEntity) tileEntity;
                        if(jack.getJack() == null)
                        {
                            CompoundNBT tagCompound = HeldVehicleDataHandler.getHeldVehicle(player);
                            EntityType.byString(tagCompound.getString("id")).ifPresent(entityType ->
                            {
                                Entity entity = entityType.create(world);
                                if(entity instanceof VehicleEntity)
                                {
                                    entity.load(tagCompound);

                                    //Updates the player capability
                                    HeldVehicleDataHandler.setHeldVehicle(player, new CompoundNBT());

                                    entity.fallDistance = 0.0F;
                                    entity.yRot = (player.getYHeadRot() + 90F) % 360.0F;

                                    jack.setVehicle((VehicleEntity) entity);
                                    if(jack.getJack() != null)
                                    {
                                        EntityJack entityJack = jack.getJack();
                                        entityJack.rideTick();
                                        entity.moveTo(entity.getX(), entity.getY(), entity.getZ(), entity.yRot, entity.xRot);
                                    }
                                    world.addFreshEntity(entity);
                                    world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_STRONG, SoundCategory.PLAYERS, 1.0F, 1.0F);
                                }
                            });
                        }
                        event.setCanceled(true);
                        event.setCancellationResult(ActionResultType.SUCCESS);
                        return;
                    }
                }

                if(player.isCrouching())
                {
                    //Vector3d clickedVec = event.getHitVec(); //TODO WHY DID FORGE REMOVE THIS. GOING TO CREATE A PATCH
                    RayTraceResult result = player.pick(10.0, 0.0F, false);
                    Vector3d clickedVec = result.getLocation();
                    if(clickedVec == null || event.getFace() != Direction.UP)
                    {
                        event.setCanceled(true);
                        return;
                    }

                    CompoundNBT tagCompound = HeldVehicleDataHandler.getHeldVehicle(player);
                    EntityType.byString(tagCompound.getString("id")).ifPresent(entityType ->
                    {
                        Entity entity = entityType.create(player.level);
                        if(entity instanceof VehicleEntity)
                        {
                            entity.load(tagCompound);

                            //Sets the positions and spawns the entity
                            float rotation = (player.getYHeadRot() + 90F) % 360.0F;
                            Vector3d heldOffset = ((VehicleEntity) entity).getProperties().getHeldOffset().yRot((float) Math.toRadians(-player.getYHeadRot()));

                            entity.absMoveTo(clickedVec.x + heldOffset.x * 0.0625D, clickedVec.y, clickedVec.z + heldOffset.z * 0.0625D, rotation, 0F);
                            entity.fallDistance = 0.0F;

                            //Checks if vehicle intersects with any blocks
                            if(!world.noCollision(entity, entity.getBoundingBox().inflate(0, -0.1, 0)))
                                return;

                            //Updates the player capability
                            HeldVehicleDataHandler.setHeldVehicle(player, new CompoundNBT());

                            //Plays place sound
                            world.addFreshEntity(entity);
                            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_STRONG, SoundCategory.PLAYERS, 1.0F, 1.0F);

                            event.setCanceled(true);
                            event.setCancellationResult(ActionResultType.SUCCESS);
                        }
                    });
                }
            }
        }
        else if(HeldVehicleDataHandler.isHoldingVehicle(player))
        {
            event.setCanceled(true);
            event.setCancellationResult(ActionResultType.FAIL);
        }
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        if(event.getHand() == Hand.OFF_HAND)
            return;

        World world = event.getWorld();
        if(world.isClientSide)
        {
            if(event instanceof PlayerInteractEvent.RightClickEmpty || event instanceof PlayerInteractEvent.RightClickItem)
            {
                PlayerEntity player = event.getPlayer();
                float reach = (float) player.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue();
                reach = player.isCreative() ? reach : reach - 0.5F;
                RayTraceResult result = player.pick(reach, 0.0F, false);
                if(result.getType() == RayTraceResult.Type.BLOCK)
                    return;

                if(HeldVehicleDataHandler.isHoldingVehicle(player))
                {
                    if(player.isCrouching())
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
        CompoundNBT tagCompound = HeldVehicleDataHandler.getHeldVehicle(player);
        if(!tagCompound.isEmpty())
        {
            HeldVehicleDataHandler.setHeldVehicle(player, new CompoundNBT());

            EntityType.byString(tagCompound.getString("id")).ifPresent(entityType ->
            {
                Entity vehicle = entityType.create(player.level);
                if(vehicle instanceof VehicleEntity)
                {
                    vehicle.load(tagCompound);
                    float rotation = (player.getYHeadRot() + 90F) % 360.0F;
                    Vector3d heldOffset = ((VehicleEntity) vehicle).getProperties().getHeldOffset().yRot((float) Math.toRadians(-player.getYHeadRot()));
                    vehicle.absMoveTo(player.getX() + heldOffset.x * 0.0625D, player.getY() + player.getEyeHeight() + heldOffset.y * 0.0625D, player.getZ() + heldOffset.z * 0.0625D, rotation, 0F);
                    player.level.addFreshEntity(vehicle);
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
            World world = player.level;
            if(player.isCrouching())
            {
                int trailerId = SyncedPlayerData.instance().get(player, ModDataKeys.TRAILER);
                if(trailerId != -1)
                {
                    Entity entity = world.getEntity(trailerId);
                    if(entity instanceof TrailerEntity)
                    {
                        ((TrailerEntity) entity).setPullingEntity(null);
                    }
                    SyncedPlayerData.instance().set(player, ModDataKeys.TRAILER, -1);
                }
            }

            if(!world.isClientSide && player.isSpectator())
            {
                this.dropVehicle(player);
            }

            Optional<BlockPos> pos = SyncedPlayerData.instance().get(player, ModDataKeys.GAS_PUMP);
            if(pos.isPresent())
            {
                TileEntity tileEntity = world.getBlockEntity(pos.get());
                if(!(tileEntity instanceof GasPumpTileEntity))
                {
                    SyncedPlayerData.instance().set(player, ModDataKeys.GAS_PUMP, Optional.empty());
                }
            }
        }
    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickItem event)
    {
        if(SyncedPlayerData.instance().get(event.getPlayer(), ModDataKeys.GAS_PUMP).isPresent())
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickBlock event)
    {
        BlockState state = event.getWorld().getBlockState(event.getPos());
        if(state.getBlock() != ModBlocks.GAS_PUMP.get() && SyncedPlayerData.instance().get(event.getPlayer(), ModDataKeys.GAS_PUMP).isPresent())
        {
            event.setCanceled(true);
        }
        else if(event.getItemStack().getItem() instanceof FluidPipeItem)
        {
            TileEntity relativeTileEntity = event.getWorld().getBlockEntity(event.getPos());
            if(relativeTileEntity != null && relativeTileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, event.getFace()).isPresent())
            {
                event.setUseBlock(Event.Result.DENY);
                event.setUseItem(Event.Result.ALLOW);
            }
        }
    }
}
