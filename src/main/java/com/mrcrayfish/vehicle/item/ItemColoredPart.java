package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.VehicleMod;
import net.minecraft.item.ItemStack;

import static com.mrcrayfish.vehicle.Reference.MOD_ID;

/**
 * Author: MrCrayfish
 */
public class ItemColoredPart extends ItemVehicleBody
{
    public ItemColoredPart(String id)
    {
        this.setUnlocalizedName(id);
        this.setRegistryName(MOD_ID, id);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setCreativeTab(VehicleMod.CREATIVE_TAB);
    }

    public String getUnlocalizedName(ItemStack stack)
    {
        return this.getUnlocalizedName() + ".henlo";
    }
}
