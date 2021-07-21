package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.common.VehicleRegistry;
import com.mrcrayfish.vehicle.entity.EngineTier;
import com.mrcrayfish.vehicle.entity.IEngineTier;
import com.mrcrayfish.vehicle.entity.IEngineType;
import net.minecraft.client.gui.screen.Screen;
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
    private IEngineType type;
    private IEngineTier tier;

    public EngineItem(IEngineType type, IEngineTier tier, Item.Properties properties)
    {
        super(properties);
        VehicleRegistry.registerEngine(type, tier, this);
        this.type = type;
        this.tier = tier;
    }

    public IEngineType getEngineType()
    {
        return this.type;
    }

    public IEngineTier getEngineTier()
    {
        return this.tier;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        tooltip.add(new TranslationTextComponent("vehicle.engine_info.acceleration").append(": ").withStyle(TextFormatting.YELLOW).append(new StringTextComponent(this.tier.getAccelerationMultiplier() + "x").withStyle(TextFormatting.GRAY)));
        tooltip.add(new TranslationTextComponent("vehicle.engine_info.additional_max_speed").append(": ").withStyle(TextFormatting.YELLOW).append(new StringTextComponent((this.tier.getAdditionalMaxSpeed()) + "bps").withStyle(TextFormatting.GRAY)));
    }
}
