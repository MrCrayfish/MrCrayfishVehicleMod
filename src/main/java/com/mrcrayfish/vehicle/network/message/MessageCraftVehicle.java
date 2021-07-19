package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.block.VehicleCrateBlock;
import com.mrcrayfish.vehicle.common.VehicleRegistry;
import com.mrcrayfish.vehicle.crafting.WorkstationRecipe;
import com.mrcrayfish.vehicle.crafting.WorkstationRecipes;
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.IEngineType;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.inventory.container.WorkstationContainer;
import com.mrcrayfish.vehicle.item.EngineItem;
import com.mrcrayfish.vehicle.item.WheelItem;
import com.mrcrayfish.vehicle.tileentity.WorkstationTileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
        buffer.writeUtf(message.vehicleId, 128);
        buffer.writeBlockPos(message.pos);
    }

    @Override
    public MessageCraftVehicle decode(PacketBuffer buffer)
    {
        return new MessageCraftVehicle(buffer.readUtf(128), buffer.readBlockPos());
    }

    @Override
    public void handle(MessageCraftVehicle message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() ->
        {
            ServerPlayerEntity player = supplier.get().getSender();
            if(player == null)
                return;

            World world = player.level;
            if(!(player.containerMenu instanceof WorkstationContainer))
                return;

            WorkstationContainer workstation = (WorkstationContainer) player.containerMenu;
            if(!workstation.getPos().equals(message.pos))
                return;

            ResourceLocation entityId = new ResourceLocation(message.vehicleId);
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
                engineType = entityPoweredVehicle.getProperties().getEngineType();

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
            if(vehicle.canBeColored())
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
            world.addFreshEntity(new ItemEntity(world, message.pos.getX() + 0.5, message.pos.getY() + 1.125, message.pos.getZ() + 0.5, stack));
        });
        supplier.get().setPacketHandled(true);
    }
}
