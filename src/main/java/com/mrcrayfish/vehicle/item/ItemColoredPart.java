package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.Reference;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class ItemColoredPart extends ItemVehicleBody implements SubItems
{
    public ItemColoredPart(String id)
    {
        this.setUnlocalizedName(id);
        this.setRegistryName(id);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public NonNullList<ResourceLocation> getModels()
    {
        NonNullList<ResourceLocation> modelLocations = NonNullList.create();
        for(EnumDyeColor color : EnumDyeColor.values())
        {
            modelLocations.add(new ResourceLocation(Reference.MOD_ID, getUnlocalizedName().substring(5) + "/" + color.getName()));
        }
        return modelLocations;
    }

    public String getUnlocalizedName(ItemStack stack)
    {
        return this.getUnlocalizedName() + ".henlo";
    }
}
