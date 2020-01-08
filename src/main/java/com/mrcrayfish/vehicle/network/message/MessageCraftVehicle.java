package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.block.BlockVehicleCrate;
import com.mrcrayfish.vehicle.crafting.VehicleRecipe;
import com.mrcrayfish.vehicle.inventory.container.WorkstationContainer;
import com.mrcrayfish.vehicle.crafting.VehicleRecipes;
import com.mrcrayfish.vehicle.entity.*;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.item.EngineItem;
import com.mrcrayfish.vehicle.item.WheelItem;
import com.mrcrayfish.vehicle.tileentity.WorkstationTileEntity;
import com.mrcrayfish.vehicle.util.InventoryUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageCraftVehicle implements IMessage<MessageCraftVehicle>
{
    private String vehicleId;
    private BlockPos pos;

    public MessageCraftVehicle() {}

    public MessageCraftVehicle(String vehicleId, BlockPos pos)
    {
        this.vehicleId = vehicleId;
        this.pos = pos;
    }

    @Override
    public void encode(MessageCraftVehicle message, PacketBuffer buffer)
    {
        buffer.writeString(message.vehicleId, 128);
        buffer.writeBlockPos(message.pos);
    }

    @Override
    public MessageCraftVehicle decode(PacketBuffer buffer)
    {
        return new MessageCraftVehicle(buffer.readString(128), buffer.readBlockPos());
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void handle(MessageCraftVehicle message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() ->
        {
            ServerPlayerEntity player = supplier.get().getSender();
            if(player != null)
            {
                World world = player.world;
                if(!(player.openContainer instanceof WorkstationContainer))
                    return;

                WorkstationContainer workstation = (WorkstationContainer) player.openContainer;
                if(!workstation.getPos().equals(message.pos))
                    return;

                ResourceLocation entityId = new ResourceLocation(message.vehicleId);
                if(!entityId.getNamespace().equals(Reference.MOD_ID))
                    return;

                EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(message.vehicleId));
                if(entityType == null)
                    return;

                VehicleRecipe recipe = VehicleRecipes.getRecipe(entityType, world);
                if(recipe == null)
                    return;

                for(ItemStack stack : recipe.getMaterials())
                {
                    if(!InventoryUtil.hasItemStack(player, stack))
                    {
                        return;
                    }
                }

                VehicleEntity vehicle = null;
                EngineType engineType = EngineType.NONE;
                Entity entity = entityType.create(world);
                if(entity instanceof VehicleEntity)
                {
                    vehicle = (VehicleEntity) entity;
                }
                if(entity instanceof PoweredVehicleEntity)
                {
                    PoweredVehicleEntity entityPoweredVehicle = (PoweredVehicleEntity) entity;
                    engineType = entityPoweredVehicle.getEngineType();

                    WorkstationTileEntity workstationTileEntity = workstation.getTileEntity();
                    ItemStack engine = workstationTileEntity.getStackInSlot(1);
                    if(engine.isEmpty() || !(engine.getItem() instanceof EngineItem))
                    {
                        return;
                    }

                    EngineType engineType2 = ((EngineItem) engine.getItem()).getEngineType();
                    if(entityPoweredVehicle.getEngineType() != EngineType.NONE && entityPoweredVehicle.getEngineType() != engineType2)
                    {
                        return;
                    }

                    if(entityPoweredVehicle.canChangeWheels())
                    {
                        ItemStack wheel = workstationTileEntity.getInventory().get(2);
                        if(!(wheel.getItem() instanceof WheelItem))
                        {
                            return;
                        }
                    }
                }

                if(vehicle == null)
                {
                    return;
                }

                for(ItemStack stack : recipe.getMaterials())
                {
                    InventoryUtil.removeItemStack(player, stack);
                }

                WorkstationTileEntity workstationTileEntity = workstation.getTileEntity();

                /* Gets the color based on the dye */
                int color = VehicleEntity.DYE_TO_COLOR[0];
                if(vehicle.canBeColored())
                {
                    ItemStack dyeStack = workstationTileEntity.getInventory().get(0);
                    if(dyeStack.getItem() instanceof DyeItem)
                    {
                        DyeItem dyeItem = (DyeItem) dyeStack.getItem();
                        color = dyeItem.getDyeColor().getColorValue();
                        workstationTileEntity.getInventory().set(0, ItemStack.EMPTY);
                    }
                }

                EngineTier engineTier = EngineTier.WOOD;
                if(engineType != EngineType.NONE)
                {
                    ItemStack engine = workstationTileEntity.getInventory().get(1);
                    if(engine.getItem() instanceof EngineItem)
                    {
                        EngineItem engineItem = (EngineItem) engine.getItem();
                        engineTier = engineItem.getEngineTier();
                        workstationTileEntity.getInventory().set(1, ItemStack.EMPTY);
                    }
                }

                int wheelColor = -1;
                WheelType wheelType = null;
                ItemStack wheel = workstationTileEntity.getInventory().get(2);
                if(vehicle instanceof PoweredVehicleEntity && ((PoweredVehicleEntity) vehicle).canChangeWheels())
                {
                    if(wheel.getItem() instanceof WheelItem)
                    {
                        WheelItem wheelItem = (WheelItem) wheel.getItem();
                        wheelType = wheelItem.getWheelType();
                        if(wheel.getTag() != null)
                        {
                            CompoundNBT compound = wheel.getTag();
                            if(compound.contains("Color", Constants.NBT.TAG_INT))
                            {
                                wheelColor = compound.getInt("Color");
                            }
                        }
                        workstationTileEntity.getInventory().set(2, ItemStack.EMPTY);
                    }
                }

                ItemStack stack = BlockVehicleCrate.create(entityId, color, engineTier, wheelType, wheelColor);
                world.addEntity(new ItemEntity(world, message.pos.getX() + 0.5, message.pos.getY() + 1.125, message.pos.getZ() + 0.5, stack));
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
