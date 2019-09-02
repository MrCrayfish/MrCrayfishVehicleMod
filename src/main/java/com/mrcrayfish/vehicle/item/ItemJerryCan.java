package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.VehicleMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class ItemJerryCan extends Item
{
    private final DecimalFormat FUEL_FORMAT = new DecimalFormat("0.#%");

    private final int capacity;
    private final int fillRate;

    public ItemJerryCan(String id, int capacity, int fillRate)
    {
        this.setUnlocalizedName(id.replace(":", "."));
        this.setRegistryName(id);
        this.setMaxStackSize(1);
        this.setCreativeTab(VehicleMod.CREATIVE_TAB);
        this.capacity = capacity;
        this.fillRate = fillRate;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        if(GuiScreen.isShiftKeyDown())
        {
            String info = I18n.format(this.getUnlocalizedName() + ".info");
            tooltip.addAll(Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(info, 150));
        }
        else
        {
            String currentFuel = TextFormatting.RESET + FUEL_FORMAT.format(getCurrentFuel(stack) / (float) getCapacity(stack));
            tooltip.add(TextFormatting.AQUA + TextFormatting.BOLD.toString() + I18n.format(this.getUnlocalizedName() + ".fuel", currentFuel));
            tooltip.add(TextFormatting.YELLOW + I18n.format("vehicle.info_help"));
        }
    }

    public int getCurrentFuel(ItemStack stack)
    {
        if(!stack.isEmpty() && stack.getItem() == this)
        {
            NBTTagCompound tagCompound = stack.getTagCompound();
            if(tagCompound != null)
            {
                return tagCompound.getInteger("fuel");
            }
        }
        return 0;
    }

    public boolean isFull(ItemStack stack)
    {
        return getCurrentFuel(stack) == getCapacity(stack);
    }

    public int fill(ItemStack stack, int fuel)
    {
        int capacity = getCapacity(stack);
        int currentFuel = getCurrentFuel(stack);
        int newFuel = Math.min(currentFuel + fuel, capacity);
        NBTTagCompound tagCompound = createTagCompound(stack);
        tagCompound.setInteger("fuel", newFuel);
        return Math.max(0, currentFuel + fuel - capacity);
    }

    public int drain(ItemStack stack, int maxAmount)
    {
        int currentFuel = getCurrentFuel(stack);
        int remainingFuel = Math.max(0, currentFuel - maxAmount);
        NBTTagCompound tagCompound = createTagCompound(stack);
        tagCompound.setInteger("fuel", remainingFuel);
        return currentFuel - remainingFuel;
    }

    public static void setCurrentFuel(ItemStack stack, int fuel)
    {
        NBTTagCompound tagCompound = createTagCompound(stack);
        tagCompound.setInteger("fuel", fuel);
    }

    public int getCapacity(ItemStack stack)
    {
        if(!stack.isEmpty() && stack.getItem() instanceof ItemJerryCan)
        {
            NBTTagCompound tagCompound = stack.getTagCompound();
            if(tagCompound != null && tagCompound.hasKey("capacity", Constants.NBT.TAG_INT))
            {
                int capacity = tagCompound.getInteger("capacity");
                return capacity > 0 ? capacity : this.capacity;
            }
        }
        return this.capacity;
    }

    public int getFillRate(ItemStack stack)
    {
        if(!stack.isEmpty() && stack.getItem() instanceof ItemJerryCan)
        {
            NBTTagCompound tagCompound = stack.getTagCompound();
            if(tagCompound != null && tagCompound.hasKey("fillRate", Constants.NBT.TAG_INT))
            {
                int fillRate = tagCompound.getInteger("fillRate");
                return fillRate > 0 ? fillRate : this.fillRate;
            }
        }
        return this.fillRate;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if(this.isInCreativeTab(tab))
        {
            NBTTagCompound tagCompound = new NBTTagCompound();
            tagCompound.setFloat("fuel", this.capacity);
            ItemStack stack = new ItemStack(this);
            stack.setTagCompound(tagCompound);
            items.add(stack);
        }
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        NBTTagCompound tagCompound = stack.getTagCompound();
        if(tagCompound != null)
        {
            float fuel = tagCompound.getInteger("fuel");
            return fuel < this.capacity;
        }
        return false;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        NBTTagCompound tagCompound = stack.getTagCompound();
        if(tagCompound != null)
        {
            float fuel = tagCompound.getInteger("fuel");
            return 1.0 - (fuel / (double) capacity);
        }
        return 1.0;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
    {
        return slotChanged;
    }

    public static NBTTagCompound createTagCompound(ItemStack stack)
    {
        NBTTagCompound tagCompound = stack.getTagCompound();
        if(tagCompound == null)
        {
            tagCompound = new NBTTagCompound();
            stack.setTagCompound(tagCompound);
        }
        return tagCompound;
    }
}
