package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.block.BlockVehicleCrate;
import com.mrcrayfish.vehicle.common.container.ContainerWorkstation;
import com.mrcrayfish.vehicle.crafting.VehicleRecipes;
import com.mrcrayfish.vehicle.util.InventoryUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

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
                            boolean canCraft = true;
                            for(ItemStack stack : recipe.getMaterials())
                            {
                                if(!InventoryUtil.hasItemStack(player, stack))
                                {
                                    canCraft = false;
                                    break;
                                }
                            }
                            if(canCraft)
                            {
                                for(ItemStack stack : recipe.getMaterials())
                                {
                                    InventoryUtil.removeItemStack(player, stack);
                                }
                                FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() ->
                                {
                                    ItemStack stack = BlockVehicleCrate.create(entityId);
                                    world.spawnEntity(new EntityItem(world, message.pos.getX() + 0.5, message.pos.getY() + 1.125, message.pos.getZ() + 0.5, stack));
                                });
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
