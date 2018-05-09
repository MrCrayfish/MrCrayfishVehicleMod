package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.entity.EngineType;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

/**
 * Author: MrCrayfish
 */
public class ItemEngine extends ItemPart implements SubItems
{
    public ItemEngine(String id)
    {
        super(id);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public NonNullList<ResourceLocation> getModels()
    {
        NonNullList<ResourceLocation> modelLocations = NonNullList.create();
        for(EngineType type : EngineType.values())
        {
            modelLocations.add(new ResourceLocation(Reference.MOD_ID, getUnlocalizedName().substring(5) + "/" + type.toString().toLowerCase()));
        }
        return modelLocations;
    }
}
