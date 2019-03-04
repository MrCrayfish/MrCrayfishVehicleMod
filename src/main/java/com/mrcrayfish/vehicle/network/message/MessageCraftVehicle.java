package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.block.BlockVehicleCrate;
import com.mrcrayfish.vehicle.common.container.ContainerWorkstation;
import com.mrcrayfish.vehicle.crafting.VehicleRecipes;
import com.mrcrayfish.vehicle.entity.*;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.item.ItemEngine;
import com.mrcrayfish.vehicle.tileentity.TileEntityWorkstation;
import com.mrcrayfish.vehicle.util.InventoryUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Author: MrCrayfish
 */
public class MessageCraftVehicle implements IMessage, IMessageHandler<MessageCraftVehicle, IMessage>
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
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, vehicleId);
        buf.writeLong(pos.toLong());
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        vehicleId = ByteBufUtils.readUTF8String(buf);
        pos = BlockPos.fromLong(buf.readLong());
    }

    @Override
    public IMessage onMessage(MessageCraftVehicle message, MessageContext ctx)
    {
        FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() ->
        {
            EntityPlayer player = ctx.getServerHandler().player;
            World world = player.world;
            if(player.openContainer instanceof ContainerWorkstation)
            {
                ContainerWorkstation workstation = (ContainerWorkstation) player.openContainer;
                if(workstation.getPos().equals(message.pos))
                {
                    ResourceLocation entityId = new ResourceLocation(message.vehicleId);
                    if(entityId.getResourceDomain().equals(Reference.MOD_ID))
                    {
                        EntityEntry entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(message.vehicleId));
                        if(entry != null)
                        {
                            Class<? extends Entity> clazz = entry.getEntityClass();
                            VehicleRecipes.VehicleRecipe recipe = VehicleRecipes.getRecipe(clazz);
                            if(recipe != null)
                            {
                                for(ItemStack stack : recipe.getMaterials())
                                {
                                    if(!InventoryUtil.hasItemStack(player, stack))
                                    {
                                        return;
                                    }
                                }

                                EntityVehicle vehicle = null;
                                EngineType engineType = EngineType.NONE;
                                try
                                {
                                    Constructor<? extends Entity> constructor = clazz.getDeclaredConstructor(World.class);
                                    Entity entity = constructor.newInstance(world);
                                    if(entity instanceof EntityVehicle)
                                    {
                                        vehicle = (EntityVehicle) entity;
                                    }
                                    if(entity instanceof EntityPoweredVehicle)
                                    {
                                        EntityPoweredVehicle entityPoweredVehicle = (EntityPoweredVehicle) entity;
                                        engineType = entityPoweredVehicle.getEngineType();

                                        TileEntityWorkstation tileEntityWorkstation = workstation.getTileEntity();
                                        ItemStack engine = tileEntityWorkstation.getStackInSlot(1);
                                        if(!engine.isEmpty() && engine.getItem() instanceof ItemEngine)
                                        {
                                            EngineType engineType2 = ((ItemEngine) engine.getItem()).getEngineType();
                                            if(entityPoweredVehicle.getEngineType() != EngineType.NONE && entityPoweredVehicle.getEngineType() != engineType2)
                                            {
                                                return;
                                            }
                                        }
                                        else
                                        {
                                            return;
                                        }

                                        if(entityPoweredVehicle.canChangeWheels())
                                        {
                                            ItemStack wheel = tileEntityWorkstation.getInventory().get(2);
                                            if(wheel.getItem() != ModItems.WHEEL)
                                            {
                                                return;
                                            }
                                        }
                                    }
                                }
                                catch(NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e)
                                {
                                    e.printStackTrace();
                                    return;
                                }

                                if(vehicle == null)
                                {
                                    return;
                                }

                                for(ItemStack stack : recipe.getMaterials())
                                {
                                    InventoryUtil.removeItemStack(player, stack);
                                }

                                TileEntityWorkstation tileEntityWorkstation = workstation.getTileEntity();

                                /* Gets the color based on the dye */
                                int color = EntityVehicle.DYE_TO_COLOR[0];
                                if(vehicle.canBeColored())
                                {
                                    ItemStack dyeStack = tileEntityWorkstation.getInventory().get(0);
                                    if(dyeStack.getItem() instanceof ItemDye)
                                    {
                                        color = EntityVehicle.DYE_TO_COLOR[15 - dyeStack.getMetadata()];
                                        tileEntityWorkstation.getInventory().set(0, ItemStack.EMPTY);
                                    }
                                }

                                EngineTier engineTier = EngineTier.WOOD;
                                if(engineType != EngineType.NONE)
                                {
                                    ItemStack engine = tileEntityWorkstation.getInventory().get(1);
                                    if(engine.getItem() instanceof ItemEngine)
                                    {
                                        engineTier = EngineTier.getType(engine.getMetadata());
                                        tileEntityWorkstation.getInventory().set(1, ItemStack.EMPTY);
                                    }
                                }

                                int wheelColor = -1;
                                WheelType wheelType = null;
                                ItemStack wheel = tileEntityWorkstation.getInventory().get(2);
                                if(vehicle instanceof EntityPoweredVehicle && ((EntityPoweredVehicle) vehicle).canChangeWheels())
                                {
                                    if(wheel.getItem() == ModItems.WHEEL)
                                    {
                                        wheelType = WheelType.values()[wheel.getMetadata()];
                                        if(wheel.getTagCompound() != null)
                                        {
                                            NBTTagCompound tagCompound = wheel.getTagCompound();
                                            if(tagCompound.hasKey("color", Constants.NBT.TAG_INT))
                                            {
                                                wheelColor = tagCompound.getInteger("color");
                                            }
                                        }
                                        tileEntityWorkstation.getInventory().set(2, ItemStack.EMPTY);
                                    }
                                }

                                ItemStack stack = BlockVehicleCrate.create(entityId, color, engineTier, wheelType, wheelColor);
                                world.spawnEntity(new EntityItem(world, message.pos.getX() + 0.5, message.pos.getY() + 1.125, message.pos.getZ() + 0.5, stack));
                            }
                        }
                    }
                }
            }
        });
        return null;
    }
}
