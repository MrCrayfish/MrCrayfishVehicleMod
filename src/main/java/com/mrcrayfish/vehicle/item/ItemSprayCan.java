package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.VehicleMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class ItemSprayCan extends Item implements SubItems
{
    public static final int MAX_SPRAYS = 5;

    public ItemSprayCan()
    {
        this.setUnlocalizedName("spray_can");
        this.setRegistryName("spray_can");
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setCreativeTab(VehicleMod.CREATIVE_TAB);
    }

    @Override
    public NonNullList<ResourceLocation> getModels()
    {
        NonNullList<ResourceLocation> modelLocations = NonNullList.create();
        modelLocations.add(new ResourceLocation(Reference.MOD_ID, getUnlocalizedName().substring(5) + "_lid"));
        modelLocations.add(new ResourceLocation(Reference.MOD_ID, getUnlocalizedName().substring(5)));
        return modelLocations;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        if(GuiScreen.isShiftKeyDown())
        {
            String info = I18n.format("item.spray_can.info");
            tooltip.addAll(Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(info, 150));
        }
        else
        {
            if(hasColor(stack))
            {
                tooltip.add(I18n.format("item.color", TextFormatting.DARK_GRAY.toString() + String.format("#%06X", createTagCompound(stack).getInteger("color"))));
            }
            else
            {
                tooltip.add(I18n.format("item.spray_can.empty"));
            }
            tooltip.add(TextFormatting.YELLOW + I18n.format("vehicle.info_help"));
        }
    }

    public static NBTTagCompound createTagCompound(ItemStack stack)
    {
        if(!stack.hasTagCompound())
        {
            stack.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound tagCompound = stack.getTagCompound();
        if(tagCompound != null)
        {
            if(!tagCompound.hasKey("remainingSprays", Constants.NBT.TAG_INT))
            {
                tagCompound.setInteger("remainingSprays", MAX_SPRAYS);
            }
        }
        return tagCompound;
    }

    public boolean hasColor(ItemStack stack)
    {
        NBTTagCompound tagCompound = createTagCompound(stack);
        return tagCompound.hasKey("color", Constants.NBT.TAG_INT);
    }

    public int getColor(ItemStack stack)
    {
        NBTTagCompound tagCompound = createTagCompound(stack);
        return tagCompound.getInteger("color");
    }

    public void setColor(ItemStack stack, int color)
    {
        NBTTagCompound tagCompound = createTagCompound(stack);
        tagCompound.setInteger("color", color);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        NBTTagCompound tagCompound = createTagCompound(stack);
        int remainingSprays = tagCompound.getInteger("remainingSprays");
        return tagCompound.hasKey("color", Constants.NBT.TAG_INT) && remainingSprays >= 0 && remainingSprays < MAX_SPRAYS;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        NBTTagCompound tagCompound = createTagCompound(stack);
        return 1.0D - (tagCompound.getInteger("remainingSprays") / (double) MAX_SPRAYS);
    }

    public static float getRemainingSprays(ItemStack stack)
    {
        NBTTagCompound tagCompound = createTagCompound(stack);
        return (tagCompound.getInteger("remainingSprays") / (float) MAX_SPRAYS);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
         if(this.isInCreativeTab(tab))
         {
             ItemStack stack = new ItemStack(this, 1, 0);
             createTagCompound(stack);
             items.add(stack);
         }
    }
}
