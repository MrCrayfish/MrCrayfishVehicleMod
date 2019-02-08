package com.mrcrayfish.vehicle.common.inventory;

import com.mrcrayfish.vehicle.util.InventoryUtil;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ReportedException;
import net.minecraftforge.common.util.Constants;

/**
 * Author: MrCrayfish
 */
public class StorageInventory extends InventoryBasic
{
    public StorageInventory(String title, boolean customName, int slotCount)
    {
        super(title, customName, slotCount);
    }

    public boolean addItemStack(final ItemStack stack)
    {
        if(stack.isEmpty())
        {
            return false;
        }
        else
        {
            try
            {
                if(stack.isItemDamaged())
                {
                    int slot = getFirstEmptyStack();
                    if(slot >= 0)
                    {
                        this.setInventorySlotContents(slot, stack.copy());
                        stack.setCount(0);
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
                else
                {
                    int i;
                    while(true)
                    {
                        i = stack.getCount();
                        stack.setCount(this.storePartialItemStack(stack));
                        if(stack.isEmpty() || stack.getCount() >= i)
                        {
                            break;
                        }
                    }
                    return stack.getCount() < i;
                }
            }
            catch(Throwable throwable)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Adding item to inventory");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Item being added");
                crashreportcategory.addCrashSection("Item ID", Item.getIdFromItem(stack.getItem()));
                crashreportcategory.addCrashSection("Item data", stack.getMetadata());
                crashreportcategory.addDetail("Item name", stack::getDisplayName);
                throw new ReportedException(crashreport);
            }
        }
    }

    private int getFirstEmptyStack()
    {
        for(int i = 0; i < this.getSizeInventory(); i++)
        {
            if(this.getStackInSlot(i).isEmpty())
            {
                return i;
            }
        }
        return -1;
    }

    private int storePartialItemStack(ItemStack itemStackIn)
    {
        int i = this.storeItemStack(itemStackIn);
        if(i == -1)
        {
            i = this.getFirstEmptyStack();
        }
        return i == -1 ? itemStackIn.getCount() : this.addResource(i, itemStackIn);
    }

    private int storeItemStack(ItemStack itemStackIn)
    {
        for(int i = 0; i < this.getSizeInventory(); i++)
        {
            if(this.canMergeStacks(this.getStackInSlot(i), itemStackIn))
            {
                return i;
            }
        }
        return -1;
    }

    private boolean canMergeStacks(ItemStack stack1, ItemStack stack2)
    {
        return !stack1.isEmpty() && this.stackEqualExact(stack1, stack2) && stack1.isStackable() && stack1.getCount() < stack1.getMaxStackSize() && stack1.getCount() < this.getInventoryStackLimit();
    }

    private boolean stackEqualExact(ItemStack stack1, ItemStack stack2)
    {
        return stack1.getItem() == stack2.getItem() && (!stack1.getHasSubtypes() || stack1.getMetadata() == stack2.getMetadata()) && ItemStack.areItemStackTagsEqual(stack1, stack2);
    }

    private int addResource(int slot, ItemStack stack)
    {
        int i = stack.getCount();
        ItemStack itemstack = this.getStackInSlot(slot);

        if (itemstack.isEmpty())
        {
            itemstack = stack.copy();
            itemstack.setCount(0);

            if (stack.hasTagCompound())
            {
                itemstack.setTagCompound(stack.getTagCompound().copy());
            }

            this.setInventorySlotContents(slot, itemstack);
        }

        int j = i;

        if (i > itemstack.getMaxStackSize() - itemstack.getCount())
        {
            j = itemstack.getMaxStackSize() - itemstack.getCount();
        }

        if (j > this.getInventoryStackLimit() - itemstack.getCount())
        {
            j = this.getInventoryStackLimit() - itemstack.getCount();
        }

        if (j == 0)
        {
            return i;
        }
        else
        {
            i = i - j;
            itemstack.grow(j);
            itemstack.setAnimationsToGo(5);
            return i;
        }
    }

    public NBTTagCompound writeToNBT()
    {
        NBTTagCompound tagCompound = new NBTTagCompound();
        NBTTagList tagList = new NBTTagList();
        for(int i = 0; i < this.getSizeInventory(); i++)
        {
            ItemStack stack = this.getStackInSlot(i);
            if(!stack.isEmpty())
            {
                NBTTagCompound slotTag = new NBTTagCompound();
                slotTag.setByte("Slot", (byte) i);
                stack.writeToNBT(slotTag);
                tagList.appendTag(slotTag);
            }
        }
        tagCompound.setTag("inventory", tagList);
        return tagCompound;
    }

    public void readFromNBT(NBTTagCompound tagCompound)
    {
        if(tagCompound.hasKey("inventory", Constants.NBT.TAG_LIST))
        {
            this.clear();
            NBTTagList tagList = tagCompound.getTagList("inventory", Constants.NBT.TAG_COMPOUND);
            for(int i = 0; i < tagList.tagCount(); i++)
            {
                NBTTagCompound slotTag = tagList.getCompoundTagAt(i);
                byte slot = slotTag.getByte("Slot");
                if(slot >= 0 && slot < this.getSizeInventory())
                {
                    this.setInventorySlotContents(slot, new ItemStack(slotTag));
                }
            }
        }
    }
}
