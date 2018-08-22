package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.VehicleMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
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

    private final float capacity;

    public ItemJerryCan(String id, float capacity)
    {
        this.setUnlocalizedName(id);
        this.setRegistryName(id);
        this.setMaxStackSize(1);
        this.setCreativeTab(VehicleMod.CREATIVE_TAB);
        this.capacity = capacity;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        if(GuiScreen.isShiftKeyDown())
        {
            String info = I18n.format("item.jerry_can.info");
            tooltip.addAll(Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(info, 150));
        }
        else
        {
            String currentFuel = TextFormatting.RESET + FUEL_FORMAT.format(getCurrentFuel(stack) / getCapacity(stack));
            tooltip.add(TextFormatting.AQUA + TextFormatting.BOLD.toString() + I18n.format("item.jerry_can.fuel", currentFuel));
            tooltip.add(TextFormatting.YELLOW + I18n.format("vehicle.info_help"));
        }
    }

    public static float getCurrentFuel(ItemStack stack)
    {
        if(!stack.isEmpty() && stack.getItem() instanceof ItemJerryCan)
        {
            NBTTagCompound tagCompound = stack.getTagCompound();
            if(tagCompound != null)
            {
                return tagCompound.getFloat("fuel");
            }
        }
        return 0F;
    }

    public static void setCurrentFuel(ItemStack stack, float fuel)
    {
        NBTTagCompound tagCompound = stack.getTagCompound();
        if(tagCompound != null)
        {
            tagCompound.setFloat("fuel", fuel);
        }
    }

    public float getCapacity(ItemStack stack)
    {
        if(!stack.isEmpty() && stack.getItem() instanceof ItemJerryCan)
        {
            NBTTagCompound tagCompound = stack.getTagCompound();
            if(tagCompound != null && tagCompound.hasKey("capacity", Constants.NBT.TAG_FLOAT))
            {
                float capacity = tagCompound.getFloat("capacity");
                return capacity > 0F ? capacity : this.capacity;
            }
        }
        return this.capacity;
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
            float fuel = tagCompound.getFloat("fuel");
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
            float fuel = tagCompound.getFloat("fuel");
            return 1.0D - (fuel / capacity);
        }
        return 1.0D;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
    {
        return slotChanged;
    }
}
