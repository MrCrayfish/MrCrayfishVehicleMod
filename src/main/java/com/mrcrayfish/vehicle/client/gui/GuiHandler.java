package com.mrcrayfish.vehicle.client.gui;

import com.mrcrayfish.vehicle.common.container.ContainerFluidExtractor;
import com.mrcrayfish.vehicle.common.container.ContainerFluidMixer;
import com.mrcrayfish.vehicle.common.container.ContainerWorkstation;
import com.mrcrayfish.vehicle.tileentity.TileEntityFluidExtractor;
import com.mrcrayfish.vehicle.tileentity.TileEntityFluidMixer;
import com.mrcrayfish.vehicle.tileentity.TileEntityWorkstation;
import net.minecraft.entity.player.EntityPlayer;
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
        if(tileEntity instanceof TileEntityFluidExtractor)
        {
            return new ContainerFluidExtractor(player.inventory, (TileEntityFluidExtractor) tileEntity);
        }
        if(tileEntity instanceof TileEntityFluidMixer)
        {
            return new ContainerFluidMixer(player.inventory, (TileEntityFluidMixer) tileEntity);
        }
        if(tileEntity instanceof TileEntityWorkstation)
        {
            return new ContainerWorkstation(player.inventory, (TileEntityWorkstation) tileEntity);
        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
        if(tileEntity instanceof TileEntityFluidExtractor)
        {
            return new GuiFluidExtractor(player.inventory, (TileEntityFluidExtractor) tileEntity);
        }
        if(tileEntity instanceof TileEntityFluidMixer)
        {
            return new GuiFluidMixer(player.inventory, (TileEntityFluidMixer) tileEntity);
        }
        if(tileEntity instanceof TileEntityWorkstation)
        {
            return new GuiWorkstation(player.inventory, (TileEntityWorkstation) tileEntity);
        }
        return null;
    }
}
