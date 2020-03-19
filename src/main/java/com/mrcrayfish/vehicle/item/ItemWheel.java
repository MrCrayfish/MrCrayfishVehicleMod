package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.entity.WheelType;
import com.mrcrayfish.vehicle.util.CommonUtils;
import com.mrcrayfish.vehicle.util.ItemNames;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

/**
 * Author: MrCrayfish
 */
public class ItemWheel extends ItemPart implements SubItems, IDyeable
{
    public ItemWheel()
    {
        super(ItemNames.WHEEL);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setCreativeTab(VehicleMod.CREATIVE_TAB);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        WheelType type = WheelType.getType(stack.getMetadata());
        tooltip.add(I18n.format("vehicle.wheel_type." + type.getId() + ".name"));
    }

    @Override
    public NonNullList<ResourceLocation> getModels()
    {
        NonNullList<ResourceLocation> modelLocations = NonNullList.create();
        for(WheelType type : WheelType.values())
        {
            modelLocations.add(new ResourceLocation(this.getRegistryName() + "/" + type.toString().toLowerCase(Locale.ENGLISH)));
        }
        return modelLocations;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if(this.isInCreativeTab(tab))
        {
            for(WheelType type : WheelType.values())
            {
                items.add(new ItemStack(this, 1, type.ordinal()));
            }
        }
    }

    @Override
    public boolean hasColor(ItemStack stack)
    {
        NBTTagCompound tagCompound = CommonUtils.getItemTagCompound(stack);
        return tagCompound.hasKey("color", Constants.NBT.TAG_INT);
    }

    @Override
    public int getColor(ItemStack stack)
    {
        NBTTagCompound tagCompound = CommonUtils.getItemTagCompound(stack);
        return tagCompound.getInteger("color");
    }

    @Override
    public void setColor(ItemStack stack, int color)
    {
        NBTTagCompound tagCompound = CommonUtils.getItemTagCompound(stack);
        tagCompound.setInteger("color", color);
    }
}
