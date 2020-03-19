package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.entity.EngineTier;
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
import java.util.Locale;

/**
 * Author: MrCrayfish
 */
public class ItemEngine extends ItemPart implements SubItems
{
    private EngineType engineType;

    public ItemEngine(String id, EngineType engineType)
    {
        super(id);
        this.engineType = engineType;
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setCreativeTab(VehicleMod.CREATIVE_TAB);
    }

    public EngineType getEngineType()
    {
        return engineType;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        EngineTier tier = EngineTier.getType(stack.getMetadata());
        String tierName = I18n.format("vehicle.engine_tier." + tier.getTierName() + ".name");
        tooltip.add(tier.getTierColor() + TextFormatting.BOLD.toString() + tierName);
        if(GuiScreen.isShiftKeyDown())
        {
            tooltip.add(TextFormatting.YELLOW + I18n.format("vehicle.engine_info.acceleration") + ": " + TextFormatting.RESET + tier.getAccelerationMultiplier() + "x");
            tooltip.add(TextFormatting.YELLOW + I18n.format("vehicle.engine_info.additional_max_speed") + ": " + TextFormatting.RESET + (tier.getAdditionalMaxSpeed() * 3.6) + "kph");
            tooltip.add(TextFormatting.YELLOW + I18n.format("vehicle.engine_info.fuel_consumption") + ": " + TextFormatting.RESET + tier.getFuelConsumption() + "pt");
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
        for(EngineTier tier : EngineTier.values())
        {
            modelLocations.add(new ResourceLocation(this.getRegistryName() + "/" + tier.toString().toLowerCase(Locale.ENGLISH)));
        }
        return modelLocations;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if(this.isInCreativeTab(tab))
        {
            for(EngineTier tier : EngineTier.values())
            {
                items.add(new ItemStack(this, 1, tier.ordinal()));
            }
        }
    }
}
