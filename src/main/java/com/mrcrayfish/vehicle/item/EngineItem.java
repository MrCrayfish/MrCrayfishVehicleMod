package com.mrcrayfish.vehicle.item;

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
import net.minecraft.util.text.TranslationTextComponent;
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

    public EngineItem(EngineType engineType, EngineTier engineTier, Item.Properties properties)
    {
        super(properties);
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
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        tooltip.add(new TranslationTextComponent("vehicle.engine_tier." + this.engineTier.getTierName() + ".name").withStyle(this.engineTier.getTierColor(), TextFormatting.BOLD));
        if(Screen.hasShiftDown())
        {
            tooltip.add(new TranslationTextComponent("vehicle.engine_info.acceleration").append(": ").withStyle(TextFormatting.YELLOW).append(new StringTextComponent(this.engineTier.getAccelerationMultiplier() + "x").withStyle(TextFormatting.WHITE)));
            tooltip.add(new TranslationTextComponent("vehicle.engine_info.additional_max_speed").append(": ").withStyle(TextFormatting.YELLOW).append(new StringTextComponent((this.engineTier.getAdditionalMaxSpeed() * 3.6) + "kph").withStyle(TextFormatting.WHITE)));
            tooltip.add(new TranslationTextComponent("vehicle.engine_info.fuel_consumption").append(": ").withStyle(TextFormatting.YELLOW).append(new StringTextComponent(this.engineTier.getFuelConsumption() + "pt").withStyle(TextFormatting.WHITE)));
        }
        else
        {
            tooltip.add(new TranslationTextComponent("vehicle.info_help").withStyle(TextFormatting.YELLOW));
        }
    }
}
