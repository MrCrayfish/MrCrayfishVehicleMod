package com.mrcrayfish.vehicle.network.play;

import com.mrcrayfish.obfuscate.common.data.SyncedPlayerData;
import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.block.VehicleCrateBlock;
import com.mrcrayfish.vehicle.common.CommonEvents;
import com.mrcrayfish.vehicle.common.CosmeticTracker;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.common.SeatTracker;
import com.mrcrayfish.vehicle.common.VehicleRegistry;
import com.mrcrayfish.vehicle.common.entity.HeldVehicleDataHandler;
import com.mrcrayfish.vehicle.common.inventory.IAttachableChest;
import com.mrcrayfish.vehicle.common.inventory.IStorage;
import com.mrcrayfish.vehicle.common.inventory.StorageInventory;
import com.mrcrayfish.vehicle.crafting.WorkstationRecipe;
import com.mrcrayfish.vehicle.crafting.WorkstationRecipes;
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.HelicopterEntity;
import com.mrcrayfish.vehicle.entity.IEngineType;
import com.mrcrayfish.vehicle.entity.PlaneEntity;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.entity.TrailerEntity;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.properties.VehicleProperties;
import com.mrcrayfish.vehicle.init.ModDataKeys;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.inventory.container.StorageContainer;
import com.mrcrayfish.vehicle.inventory.container.WorkstationContainer;
import com.mrcrayfish.vehicle.item.EngineItem;
import com.mrcrayfish.vehicle.item.WheelItem;
import com.mrcrayfish.vehicle.network.message.*;
import com.mrcrayfish.vehicle.tileentity.WorkstationTileEntity;
import com.mrcrayfish.vehicle.util.CommonUtils;
import net.minecraft.block.SoundType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Author: MrCrayfish
 */
public class ServerPlayHandler
{
    public static void handleAttachChestMessage(ServerPlayerEntity player, MessageAttachChest message)
    {
        World world = player.level;
        Entity targetEntity = world.getEntity(message.getEntityId());
        if(targetEntity instanceof IAttachableChest)
        {
            float reachDistance = (float) player.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue();
            if(player.distanceTo(targetEntity) < reachDistance)
            {
                IAttachableChest attachableChest = (IAttachableChest) targetEntity;
                if(!attachableChest.hasChest(message.getKey()))
                {
                    ItemStack stack = player.inventory.getSelected();
                    if(!stack.isEmpty() && stack.getItem() == Items.CHEST)
                    {
                        attachableChest.attachChest(message.getKey(), stack);
                        world.playSound(null, targetEntity.getX(), targetEntity.getY(), targetEntity.getZ(), SoundType.WOOD.getPlaceSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
                    }
                }
            }
        }
    }

    public static void handleAttachTrailerMessage(ServerPlayerEntity player, MessageAttachTrailer message)
    {
        Entity trailerEntity = player.level.getEntity(message.getTrailerId());
        if(trailerEntity instanceof TrailerEntity)
        {
            TrailerEntity trailer = (TrailerEntity) trailerEntity;
            if(player.getVehicle() == null)
            {
                trailer.setPullingEntity(player);
                SyncedPlayerData.instance().set(player, ModDataKeys.TRAILER, message.getTrailerId());
            }
        }
    }

    public static void handleCraftVehicleMessage(ServerPlayerEntity player, MessageCraftVehicle message)
    {
        World world = player.level;
        if(!(player.containerMenu instanceof WorkstationContainer))
            return;

        WorkstationContainer workstation = (WorkstationContainer) player.containerMenu;
        if(!workstation.getPos().equals(message.getPos()))
            return;

        ResourceLocation entityId = new ResourceLocation(message.getVehicleId());
        if(Config.SERVER.disabledVehicles.get().contains(entityId.toString()))
            return;

        EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(entityId);
        if(entityType == null)
            return;

        if(!VehicleRegistry.getRegisteredVehicleTypes().contains(entityType))
            return;

        WorkstationRecipe recipe = WorkstationRecipes.getRecipe(entityType, world);
        if(recipe == null || !recipe.hasMaterials(player))
            return;

        Entity entity = entityType.create(world);
        if(!(entity instanceof VehicleEntity))
            return;

        IEngineType engineType = EngineType.NONE;
        VehicleEntity vehicle = (VehicleEntity) entity;
        if(vehicle instanceof PoweredVehicleEntity)
        {
            PoweredVehicleEntity entityPoweredVehicle = (PoweredVehicleEntity) entity;
            engineType = entityPoweredVehicle.getEngineType();

            WorkstationTileEntity workstationTileEntity = workstation.getTileEntity();
            ItemStack workstationEngine = workstationTileEntity.getItem(1);
            if(workstationEngine.isEmpty() || !(workstationEngine.getItem() instanceof EngineItem))
                return;

            IEngineType engineType2 = ((EngineItem) workstationEngine.getItem()).getEngineType();
            if(engineType != EngineType.NONE && engineType != engineType2)
                return;

            if(entityPoweredVehicle.canChangeWheels())
            {
                ItemStack wheel = workstationTileEntity.getInventory().get(2);
                if(!(wheel.getItem() instanceof WheelItem))
                    return;
            }
        }

        /* At this point we have verified the crafting and can perform irreversible actions */

        recipe.consumeMaterials(player);

        WorkstationTileEntity workstationTileEntity = workstation.getTileEntity();

        /* Gets the color based on the dye */
        int color = VehicleEntity.DYE_TO_COLOR[0];
        if(vehicle.getProperties().canBePainted())
        {
            ItemStack workstationDyeStack = workstationTileEntity.getInventory().get(0);
            if(workstationDyeStack.getItem() instanceof DyeItem)
            {
                DyeItem dyeItem = (DyeItem) workstationDyeStack.getItem();
                color = dyeItem.getDyeColor().getColorValue();
                workstationTileEntity.getInventory().set(0, ItemStack.EMPTY);
            }
        }

        ItemStack engineStack = ItemStack.EMPTY;
        if(engineType != EngineType.NONE)
        {
            ItemStack workstationEngineStack = workstationTileEntity.getInventory().get(1);
            if(workstationEngineStack.getItem() instanceof EngineItem)
            {
                engineStack = workstationEngineStack.copy();
                workstationTileEntity.getInventory().set(1, ItemStack.EMPTY);
            }
        }

        ItemStack wheelStack = ItemStack.EMPTY;
        if(vehicle instanceof PoweredVehicleEntity && ((PoweredVehicleEntity) vehicle).canChangeWheels())
        {
            ItemStack workstationWheelStack = workstationTileEntity.getInventory().get(2);
            if(workstationWheelStack.getItem() instanceof WheelItem)
            {
                wheelStack = workstationWheelStack.copy();
                workstationTileEntity.getInventory().set(2, ItemStack.EMPTY);
            }
        }

        ItemStack stack = VehicleCrateBlock.create(entityId, color, engineStack, wheelStack);
        world.addFreshEntity(new ItemEntity(world, message.getPos().getX() + 0.5, message.getPos().getY() + 1.125, message.getPos().getZ() + 0.5, stack));
    }

    public static void handleCycleSeatsMessage(ServerPlayerEntity player, MessageCycleSeats message)
    {
        Entity entity = player.getVehicle();
        if(!(entity instanceof VehicleEntity))
            return;

        VehicleEntity vehicle = (VehicleEntity) player.getVehicle();
        List<Seat> seats = vehicle.getProperties().getSeats();

        /* No need to cycle if already full of passengers */
        if(vehicle.getPassengers().size() >= seats.size())
            return;

        SeatTracker tracker = vehicle.getSeatTracker();
        int seatIndex = tracker.getSeatIndex(player.getUUID());
        for(int i = 0; i < seats.size() - 1; i++)
        {
            int nextIndex = (seatIndex + (i + 1)) % seats.size();
            if(tracker.isSeatAvailable(nextIndex))
            {
                tracker.setSeatIndex(nextIndex, player.getUUID());
                vehicle.onPlayerChangeSeat(player, seatIndex, nextIndex);
                return;
            }
        }
    }

    public static void handleFuelVehicleMessage(ServerPlayerEntity player, MessageFuelVehicle message)
    {
        Entity targetEntity = player.level.getEntity(message.getEntityId());
        if(targetEntity instanceof PoweredVehicleEntity)
        {
            ((PoweredVehicleEntity) targetEntity).fuelVehicle(player, message.getHand());
        }
    }

    public static void handleHandbrakeMessage(ServerPlayerEntity player, MessageHandbrake message)
    {
        Entity riding = player.getVehicle();
        if(riding instanceof PoweredVehicleEntity)
        {
            ((PoweredVehicleEntity) riding).setHandbraking(message.isHandbrake());
        }
    }

    public static void handleHelicopterInputMessage(ServerPlayerEntity player, MessageHelicopterInput message)
    {
        Entity riding = player.getVehicle();
        if(riding instanceof HelicopterEntity)
        {
            HelicopterEntity helicopter = (HelicopterEntity) riding;
            helicopter.setLift(message.getLift());
            helicopter.setForwardInput(message.getForward());
            helicopter.setSideInput(message.getSide());
        }
    }

    public static void handleHitchTrailerMessage(ServerPlayerEntity player, MessageHitchTrailer message)
    {
        if(!(player.getVehicle() instanceof VehicleEntity))
            return;

        VehicleEntity vehicle = (VehicleEntity) player.getVehicle();
        if(!vehicle.canTowTrailers())
            return;

        if(!message.isHitch())
        {
            if(vehicle.getTrailer() != null)
            {
                vehicle.setTrailer(null);
                player.level.playSound(null, vehicle.blockPosition(), SoundEvents.ITEM_BREAK, SoundCategory.PLAYERS, 1.0F, 1.0F);
            }
        }
        else
        {
            VehicleProperties properties = vehicle.getProperties();
            Vector3d vehicleVec = vehicle.position();
            Vector3d towBarVec = properties.getTowBarOffset();
            towBarVec = new Vector3d(towBarVec.x * 0.0625, towBarVec.y * 0.0625, towBarVec.z * 0.0625 + properties.getBodyTransform().getZ());
            vehicleVec = vehicleVec.add(towBarVec.yRot((float) Math.toRadians(-vehicle.yRot)));

            AxisAlignedBB towBarBox = new AxisAlignedBB(vehicleVec.x, vehicleVec.y, vehicleVec.z, vehicleVec.x, vehicleVec.y, vehicleVec.z).inflate(0.25);
            List<TrailerEntity> trailers = player.level.getEntitiesOfClass(TrailerEntity.class, vehicle.getBoundingBox().inflate(5), input -> input.getPullingEntity() == null);
            for(TrailerEntity trailer : trailers)
            {
                if(trailer.getPullingEntity() != null)
                    continue;

                Vector3d trailerVec = trailer.position();
                Vector3d hitchVec = new Vector3d(0, 0, -trailer.getHitchOffset() / 16.0);
                trailerVec = trailerVec.add(hitchVec.yRot((float) Math.toRadians(-trailer.yRot)));
                AxisAlignedBB hitchBox = new AxisAlignedBB(trailerVec.x, trailerVec.y, trailerVec.z, trailerVec.x, trailerVec.y, trailerVec.z).inflate(0.25);
                if(towBarBox.intersects(hitchBox))
                {
                    vehicle.setTrailer(trailer);
                    player.level.playSound(null, vehicle.blockPosition(), SoundEvents.ANVIL_PLACE, SoundCategory.PLAYERS, 1.0F, 1.5F);
                    return;
                }
            }
        }
    }

    public static void handleHornMessage(ServerPlayerEntity player, MessageHorn message)
    {
        Entity riding = player.getVehicle();
        if(riding instanceof PoweredVehicleEntity && ((PoweredVehicleEntity) riding).hasHorn())
        {
            ((PoweredVehicleEntity) riding).setHorn(message.isHorn());
        }
    }

    public static void handleInteractKeyMessage(ServerPlayerEntity player, MessageInteractKey message)
    {
        Entity targetEntity = player.level.getEntity(message.getEntityId());
        if(targetEntity instanceof PoweredVehicleEntity)
        {
            PoweredVehicleEntity poweredVehicle = (PoweredVehicleEntity) targetEntity;
            if(poweredVehicle.isKeyNeeded())
            {
                ItemStack stack = player.getMainHandItem();
                if(!stack.isEmpty() && stack.getItem() == ModItems.WRENCH.get())
                {
                    if(poweredVehicle.isOwner(player))
                    {
                        poweredVehicle.ejectKey();
                        poweredVehicle.setKeyNeeded(false);
                        CommonUtils.sendInfoMessage(player, "vehicle.status.key_removed");
                    }
                    else
                    {
                        CommonUtils.sendInfoMessage(player, "vehicle.status.invalid_owner");
                    }
                    return;
                }
                if(poweredVehicle.getKeyStack().isEmpty())
                {
                    if(!stack.isEmpty() && stack.getItem() == ModItems.KEY.get())
                    {
                        UUID keyUuid = CommonUtils.getOrCreateStackTag(stack).getUUID("VehicleId");
                        if(poweredVehicle.getUUID().equals(keyUuid))
                        {
                            poweredVehicle.setKeyStack(stack.copy());
                            player.setItemSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
                        }
                        else
                        {
                            CommonUtils.sendInfoMessage(player, "vehicle.status.key_invalid");
                        }
                    }
                }
                else
                {
                    poweredVehicle.ejectKey();
                }
            }
        }
    }

    public static void handlePickupVehicleMessage(ServerPlayerEntity player, MessagePickupVehicle message)
    {
        if(player.isCrouching())
        {
            Entity targetEntity = player.level.getEntity(message.getEntityId());
            if(targetEntity != null)
            {
                CommonEvents.handleVehicleInteraction(player.level, player, Hand.MAIN_HAND, targetEntity);
            }
        }
    }

    public static void handlePlaneInputMessage(ServerPlayerEntity player, MessagePlaneInput message)
    {
        Entity riding = player.getVehicle();
        if(riding instanceof PlaneEntity)
        {
            PlaneEntity plane = (PlaneEntity) riding;
            plane.setLift(message.getLift());
            plane.setForwardInput(message.getForward());
            plane.setSideInput(message.getSide());
        }
    }

    public static void handleThrottleMessage(ServerPlayerEntity player, MessageThrottle message)
    {
        Entity riding = player.getVehicle();
        if(riding instanceof PoweredVehicleEntity)
        {
            ((PoweredVehicleEntity) riding).setThrottle(message.getPower());
        }
    }

    public static void handleThrowVehicle(ServerPlayerEntity player, MessageThrowVehicle message)
    {
        if(!player.isCrouching())
            return;

        //Spawns the vehicle and plays the placing sound
        if(!HeldVehicleDataHandler.isHoldingVehicle(player))
            return;

        CompoundNBT heldTag = HeldVehicleDataHandler.getHeldVehicle(player);
        Optional<EntityType<?>> optional = EntityType.byString(heldTag.getString("id"));
        if(!optional.isPresent())
            return;

        EntityType<?> entityType = optional.get();
        Entity entity = entityType.create(player.level);
        if(entity instanceof VehicleEntity)
        {
            entity.load(heldTag);

            //Updates the player capability
            HeldVehicleDataHandler.setHeldVehicle(player, new CompoundNBT());

            //Sets the positions and spawns the entity
            float rotation = (player.getYHeadRot() + 90F) % 360.0F;
            Vector3d heldOffset = ((VehicleEntity) entity).getProperties().getHeldOffset().yRot((float) Math.toRadians(-player.getYHeadRot()));

            //Gets the clicked vec if it was a right click block event
            Vector3d lookVec = player.getLookAngle();
            double posX = player.getX();
            double posY = player.getY() + player.getEyeHeight();
            double posZ = player.getZ();
            entity.absMoveTo(posX + heldOffset.x * 0.0625D, posY + heldOffset.y * 0.0625D, posZ + heldOffset.z * 0.0625D, rotation, 0F);

            entity.setDeltaMovement(lookVec);
            entity.fallDistance = 0.0F;

            player.level.addFreshEntity(entity);
            player.level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.ENTITY_VEHICLE_PICK_UP.get(), SoundCategory.PLAYERS, 1.0F, 1.0F);
        }
    }

    public static void handleTurnAngleMessage(ServerPlayerEntity player, MessageTurnAngle message)
    {
        Entity riding = player.getVehicle();
        if(riding instanceof PoweredVehicleEntity)
        {
            ((PoweredVehicleEntity) riding).setSteeringAngle(message.getAngle());
        }
    }

    public static void handleInteractCosmeticMessage(ServerPlayerEntity player, MessageInteractCosmetic message)
    {
        Entity targetEntity = player.level.getEntity(message.getEntityId());
        if(!(targetEntity instanceof VehicleEntity))
            return;

        if(player.distanceTo(targetEntity) > 20.0D) //TODO determine a better condition to check if player is close to vehicle
            return;

        //TODO log if player tries to interact with cosmetic that doesn't exist?

        VehicleEntity vehicle = (VehicleEntity) targetEntity;
        CosmeticTracker tracker = vehicle.getCosmeticTracker();
        tracker.getActions(message.getCosmeticId()).forEach(action -> action.onInteract(vehicle, player));
    }

    public static void handleOpenStorageMessage(ServerPlayerEntity player, MessageOpenStorage message)
    {
        World world = player.level;
        Entity targetEntity = world.getEntity(message.getEntityId());
        if(!(targetEntity instanceof IStorage))
            return;

        IStorage storage = (IStorage) targetEntity;
        if(player.distanceTo(targetEntity) >= 64.0)
            return;

        StorageInventory inventory = storage.getStorageInventory(message.getKey());
        if(inventory == null)
            return;

        if(targetEntity instanceof IAttachableChest)
        {
            //TODO prevent non-owners from removing chest
            /*if(targetEntity instanceof VehicleEntity)
            {
                VehicleEntity vehicle = (VehicleEntity) targetEntity;
                vehicle.get
            }*/

            IAttachableChest attachableChest = (IAttachableChest) targetEntity;
            if(attachableChest.hasChest(message.getKey()))
            {
                ItemStack stack = player.inventory.getSelected();
                if(stack.getItem() == ModItems.WRENCH.get())
                {
                    ((IAttachableChest) targetEntity).removeChest(message.getKey());
                    return;
                }
            }
        }

        NetworkHooks.openGui(player, new SimpleNamedContainerProvider((windowId, playerInventory, playerEntity) -> {
            return new StorageContainer(windowId, playerInventory, inventory, playerEntity);
        }, inventory.getDisplayName()), buffer -> {
            buffer.writeVarInt(message.getEntityId());
            buffer.writeUtf(message.getKey());
        });
    }
}
