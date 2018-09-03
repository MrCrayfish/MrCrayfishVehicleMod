package com.mrcrayfish.vehicle.common.container;

import com.mrcrayfish.vehicle.tileentity.TileEntityRefinery;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Author: MrCrayfish
 */
public class ContainerRefinery extends Container
{
    private int remainingEthanolFuel;
    private int ethanolProgress;
    private int fueliumProgress;
    private int waterLevel;
    private int ethanolLevel;
    private int fueliumLevel;
    private int maxEthanolProgress;

    private TileEntityRefinery refinery;

    public ContainerRefinery(IInventory playerInventory, TileEntityRefinery refinery)
    {
        this.refinery = refinery;

        Slot bucket = new Slot(refinery, 0, 8, 8);
        bucket.setBackgroundLocation(new ResourceLocation("vehicle:fluids/fuelium_still"));
        this.addSlotToContainer(bucket);

        this.addSlotToContainer(new Slot(refinery, 1, 44, 29));
        this.addSlotToContainer(new Slot(refinery, 2, 44, 70));

        //107, 51
        for(int i = 0; i < 4; i++)
        {
            this.addSlotToContainer(new Slot(refinery, 3 + i, 107 + 18 * (i % 2), 51 + 18 * (i / 2)));
        }

        for (int x = 0; x < 3; x++)
        {
            for (int y = 0; y < 9; y++)
            {
                this.addSlotToContainer(new Slot(playerInventory, y + x * 9 + 9, 8 + y * 18, 107 + x * 18));
            }
        }

        for (int x = 0; x < 9; x++)
        {
            this.addSlotToContainer(new Slot(playerInventory, x, 8 + x * 18, 165));
        }
    }

    public void addListener(IContainerListener listener)
    {
        super.addListener(listener);
        listener.sendAllWindowProperties(this, this.refinery);
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return true;
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (int i = 0; i < this.listeners.size(); ++i)
        {
            IContainerListener listener = this.listeners.get(i);

            if (this.remainingEthanolFuel != this.refinery.getField(0))
            {
                listener.sendWindowProperty(this, 0, this.refinery.getField(0));
            }

            if (this.ethanolProgress != this.refinery.getField(1))
            {
                listener.sendWindowProperty(this, 1, this.refinery.getField(1));
            }

            if (this.fueliumProgress != this.refinery.getField(2))
            {
                listener.sendWindowProperty(this, 2, this.refinery.getField(2));
            }

            if (this.waterLevel != this.refinery.getField(3))
            {
                listener.sendWindowProperty(this, 3, this.refinery.getField(3));
            }

            if (this.waterLevel != this.refinery.getField(4))
            {
                listener.sendWindowProperty(this, 4, this.refinery.getField(4));
            }

            if (this.fueliumLevel != this.refinery.getField(5))
            {
                listener.sendWindowProperty(this, 5, this.refinery.getField(5));
            }

            if (this.maxEthanolProgress != this.refinery.getField(6))
            {
                listener.sendWindowProperty(this, 6, this.refinery.getField(6));
            }
        }

        this.remainingEthanolFuel = this.refinery.getField(0);
        this.ethanolProgress = this.refinery.getField(1);
        this.fueliumProgress = this.refinery.getField(2);
        this.waterLevel = this.refinery.getField(3);
        this.ethanolLevel = this.refinery.getField(4);
        this.fueliumLevel = this.refinery.getField(5);
        this.maxEthanolProgress = this.refinery.getField(6);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value)
    {
        this.refinery.setField(id, value);
    }
}
