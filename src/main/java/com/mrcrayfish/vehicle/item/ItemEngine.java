package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.entity.EngineType;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

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
        this.setCreativeTab(VehicleMod.CREATIVE_TAB);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        EngineType type = EngineType.getType(stack.getMetadata());
        String tierName = I18n.format("vehicle.engine_tier." + type.getTierName() + ".name");
        tooltip.add(type.getTierColor() + TextFormatting.BOLD.toString() + tierName);
        if(GuiScreen.isShiftKeyDown())
        {
            tooltip.add(TextFormatting.YELLOW + I18n.format("vehicle.engine_info.acceleration") + ": " + TextFormatting.RESET + type.getAccelerationMultiplier() + "x");
            tooltip.add(TextFormatting.YELLOW + I18n.format("vehicle.engine_info.additional_max_speed") + ": " + TextFormatting.RESET + (type.getAdditionalMaxSpeed() * 3.6) + "kph");
            tooltip.add(TextFormatting.YELLOW + I18n.format("vehicle.engine_info.fuel_consumption") + ": " + TextFormatting.RESET + type.getFuelConsumption() + "pt");
        }
        else
        {
            tooltip.add(TextFormatting.YELLOW + I18n.format("vehicle.info_help"));
        }
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

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if(this.isInCreativeTab(tab))
        {
            for(EngineType type : EngineType.values())
            {
                items.add(new ItemStack(this, 1, type.ordinal()));
            }
        }
    }
}
