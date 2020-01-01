package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.entity.EngineTier;
import com.mrcrayfish.vehicle.entity.EngineType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class EngineItem extends PartItem
{
    private EngineType engineType;
    private EngineTier engineTier;

    public EngineItem(String id, EngineType engineType, EngineTier engineTier)
    {
        this(id, engineType, engineTier, new Item.Properties().group(VehicleMod.CREATIVE_TAB));
    }

    public EngineItem(String id, EngineType engineType, EngineTier engineTier, Item.Properties properties)
    {
        super(id, properties);
        this.engineType = engineType;
        this.engineTier = engineTier;
    }

    public EngineType getEngineType()
    {
        return engineType;
    }

    public EngineTier getEngineTier()
    {
        return engineTier;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        String tierName = I18n.format("vehicle.engine_tier." + this.engineTier.getTierName() + ".name");
        tooltip.add(new StringTextComponent(this.engineTier.getTierColor() + TextFormatting.BOLD.toString() + tierName));
        if(Screen.hasShiftDown())
        {
            tooltip.add(new StringTextComponent(TextFormatting.YELLOW + I18n.format("vehicle.engine_info.acceleration") + ": " + TextFormatting.RESET + this.engineTier.getAccelerationMultiplier() + "x"));
            tooltip.add(new StringTextComponent(TextFormatting.YELLOW + I18n.format("vehicle.engine_info.additional_max_speed") + ": " + TextFormatting.RESET + (this.engineTier.getAdditionalMaxSpeed() * 3.6) + "kph"));
            tooltip.add(new StringTextComponent(TextFormatting.YELLOW + I18n.format("vehicle.engine_info.fuel_consumption") + ": " + TextFormatting.RESET + this.engineTier.getFuelConsumption() + "pt"));
        }
        else
        {
            tooltip.add(new StringTextComponent(TextFormatting.YELLOW + I18n.format("vehicle.info_help")));
        }
    }
}
