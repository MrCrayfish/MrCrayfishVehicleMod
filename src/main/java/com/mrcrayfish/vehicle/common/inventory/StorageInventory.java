package com.mrcrayfish.vehicle.common.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerProvider;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.function.Predicate;

/**
 * Author: MrCrayfish
 */
public class StorageInventory extends Inventory implements INamedContainerProvider
{
    private final ITextComponent displayName;
    private final IContainerProvider containerProvider;
    private final Predicate<ItemStack> itemPredicate;

    public StorageInventory(IStorage wrapper, int size)
    {
        super(size);
        this.displayName = wrapper.getStorageName();
        this.containerProvider = wrapper.getStorageContainerProvider();
        this.itemPredicate = wrapper::isStorageItem;
    }

    public StorageInventory(ITextComponent displayName, int rows, IContainerProvider provider)
    {
        super(rows * 9);
        this.displayName = displayName;
        this.containerProvider = provider;
        this.itemPredicate = stack -> true;
    }

    public boolean isStorageItem(ItemStack stack)
    {
        return this.itemPredicate.test(stack);
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return this.displayName;
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity)
    {
        return this.containerProvider.createMenu(windowId, playerInventory, playerEntity);
    }

    public ListNBT createTag()
    {
        ListNBT tagList = new ListNBT();
        for(int i = 0; i < this.getContainerSize(); i++)
        {
            ItemStack stack = this.getItem(i);
            if(!stack.isEmpty())
            {
                CompoundNBT slotTag = new CompoundNBT();
                slotTag.putByte("Slot", (byte) i);
                stack.save(slotTag);
                tagList.add(slotTag);
            }
        }
        return tagList;
    }

    @Override
    public void fromTag(ListNBT tagList)
    {
        this.clearContent();
        for(int i = 0; i < tagList.size(); i++)
        {
            CompoundNBT slotTag = tagList.getCompound(i);
            byte slot = slotTag.getByte("Slot");
            if(slot >= 0 && slot < this.getContainerSize())
            {
                this.setItem(slot, ItemStack.of(slotTag));
            }
        }
    }

    private static IStorage createStorage(StorageInventory inventory, ITextComponent displayName)
    {
        return new IStorage()
        {
            @Override
            public StorageInventory getInventory()
            {
                return inventory;
            }

            @Override
            public ITextComponent getStorageName()
            {
                return displayName;
            }
        };
    }
}
