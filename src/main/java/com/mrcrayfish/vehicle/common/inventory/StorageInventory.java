package com.mrcrayfish.vehicle.common.inventory;

import com.mrcrayfish.vehicle.common.container.ContainerStorage;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageStorageWindow;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.ReportedException;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.storage.loot.ILootContainer;
import net.minecraftforge.common.util.Constants;

/**
 * Author: MrCrayfish
 */
public class StorageInventory extends InventoryBasic
{
    private IStorage wrapper;

    public StorageInventory(String title, boolean customName, int slotCount, IStorage wrapper)
    {
        super(title, customName, slotCount);
        this.wrapper = wrapper;
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
                    do
                    {
                        i = stack.getCount();
                        stack.setCount(this.storePartialItemStack(stack));
                    }
                    while(!stack.isEmpty() && stack.getCount() < i);
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

    @Override
    public boolean isUsableByPlayer(EntityPlayer player)
    {
        if(wrapper instanceof Entity)
        {
            Entity entity = (Entity) wrapper;
            return player.getDistanceSq(entity) <= 8.0D;
        }
        return true;
    }

    public boolean isStorageItem(ItemStack stack)
    {
        return wrapper.isStorageItem(stack);
    }

    public void openGui(EntityPlayerMP player, Entity entity)
    {
        if(entity instanceof IStorage)
        {
            if(this instanceof ILootContainer && player.isSpectator())
            {
                player.sendStatusMessage((new TextComponentTranslation("container.spectatorCantOpen", new Object[0])).setStyle((new Style()).setColor(TextFormatting.RED)), true);
            }
            else
            {
                if(player.openContainer != player.inventoryContainer)
                {
                    player.closeScreen();
                }

                if(this instanceof ILockableContainer)
                {
                    ILockableContainer lockableContainer = (ILockableContainer) this;

                    if(lockableContainer.isLocked() && !player.canOpen(lockableContainer.getLockCode()) && !player.isSpectator())
                    {
                        player.connection.sendPacket(new SPacketChat(new TextComponentTranslation("container.isLocked", this.getDisplayName()), ChatType.GAME_INFO));
                        player.connection.sendPacket(new SPacketSoundEffect(SoundEvents.BLOCK_CHEST_LOCKED, SoundCategory.BLOCKS, player.posX, player.posY, player.posZ, 1.0F, 1.0F));
                        return;
                    }
                }

                player.getNextWindowId();
                PacketHandler.INSTANCE.sendTo(new MessageStorageWindow(player.currentWindowId, entity.getEntityId()), player);
                player.openContainer = new ContainerStorage(player.inventory, this, player);
                player.openContainer.windowId = player.currentWindowId;
                player.openContainer.addListener(player);
                net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(player, player.openContainer));
            }
        }
    }
}
