package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.VehicleMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
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
            tooltip.add(TextFormatting.YELLOW + "Hold SHIFT for Info");
        }
    }

    private static NBTTagCompound createTagCompound(ItemStack stack)
    {
        if(!stack.hasTagCompound())
        {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
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
}
