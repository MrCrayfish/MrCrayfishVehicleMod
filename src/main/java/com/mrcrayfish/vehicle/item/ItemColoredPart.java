package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.VehicleMod;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class ItemColoredPart extends ItemVehicleBody
{
    public ItemColoredPart(String id)
    {
        this.setUnlocalizedName(id);
        this.setRegistryName(id);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setCreativeTab(VehicleMod.CREATIVE_TAB);
    }

    public String getUnlocalizedName(ItemStack stack)
    {
        return this.getUnlocalizedName() + ".henlo";
    }
}
