package com.mrcrayfish.vehicle.client.gui;

import com.mrcrayfish.vehicle.common.container.ContainerRefinery;
import com.mrcrayfish.vehicle.tileentity.TileEntityRefinery;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class GuiHandler implements IGuiHandler
{
    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
        if(tileEntity instanceof TileEntityRefinery)
        {
            return new ContainerRefinery(player.inventory, (TileEntityRefinery) tileEntity);
        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
        if(tileEntity instanceof TileEntityRefinery)
        {
            return new GuiRefinery(player.inventory, (TileEntityRefinery) tileEntity);
        }
        return null;
    }
}
