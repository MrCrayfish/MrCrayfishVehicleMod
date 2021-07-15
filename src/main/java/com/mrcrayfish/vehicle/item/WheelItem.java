package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.entity.IWheelType;
import com.mrcrayfish.vehicle.entity.WheelType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class WheelItem extends PartItem implements IDyeable
{
    private IWheelType wheelType;

    public WheelItem(IWheelType wheelType, Item.Properties properties)
    {
        super(properties);
        this.wheelType = wheelType;
    }

    public IWheelType getWheelType()
    {
        return this.wheelType;
    }
}
