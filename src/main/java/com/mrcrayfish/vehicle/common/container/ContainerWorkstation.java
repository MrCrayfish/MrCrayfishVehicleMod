package com.mrcrayfish.vehicle.common.container;

import com.mrcrayfish.vehicle.tileentity.TileEntityWorkstation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.util.math.BlockPos;

/**
 * Author: MrCrayfish
 */
public class ContainerWorkstation extends Container
{
    private BlockPos pos;

    public ContainerWorkstation(IInventory playerInventory, TileEntityWorkstation tileEntityWorkstation)
    {
        this.pos = tileEntityWorkstation.getPos();

        for(int x = 0; x < 3; x++)
        {
            for(int y = 0; y < 9; y++)
            {
                this.addSlotToContainer(new Slot(playerInventory, y + x * 9 + 9, 8 + y * 18, 120 + x * 18));
            }
        }

        for(int x = 0; x < 9; x++)
        {
            this.addSlotToContainer(new Slot(playerInventory, x, 8 + x * 18, 178));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return true;
    }

    public BlockPos getPos()
    {
        return pos;
    }
}
