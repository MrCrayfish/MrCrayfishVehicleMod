package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
public class SprayCanItem extends Item implements IDyeable
{
    public static final int MAX_SPRAYS = 5;

    public SprayCanItem(Item.Properties properties)
    {
        super(properties);
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items)
    {
        if (this.isInGroup(group))
        {
            ItemStack stack = new ItemStack(this);
            this.refill(stack);
            items.add(stack);
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag)
    {
        if (Screen.hasShiftDown())
        {
            tooltip.addAll(RenderUtil.lines(new TranslationTextComponent("item.vehicle.spray_can.info"), 150));
        }
        else
        {
            if (this.hasColor(stack))
            {
                tooltip.add(new TranslationTextComponent("item.color", new StringTextComponent(String.format("#%06X", this.getColor(stack))).mergeStyle(TextFormatting.DARK_GRAY)));
            }
            else
            {
                tooltip.add(new TranslationTextComponent("item.vehicle.spray_can.empty"));
            }
            tooltip.add(new TranslationTextComponent("vehicle.info_help").mergeStyle(TextFormatting.YELLOW));
        }
    }

    public static CompoundNBT getStackTag(ItemStack stack)
    {
        if (stack.getTag() == null)
        {
            stack.setTag(new CompoundNBT());
        }
        if (stack.getItem() instanceof SprayCanItem)
        {
            SprayCanItem sprayCan = (SprayCanItem) stack.getItem();
            CompoundNBT compound = stack.getTag();
            if (compound != null)
            {
                if (!compound.contains("RemainingSprays", Constants.NBT.TAG_INT))
                {
                    compound.putInt("RemainingSprays", sprayCan.getCapacity(stack));
                }
            }
        }
        return stack.getTag();
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        CompoundNBT compound = stack.getTag();
        if (compound != null && compound.contains("RemainingSprays", Constants.NBT.TAG_INT))
        {
            int remainingSprays = compound.getInt("RemainingSprays");
            return this.hasColor(stack) && remainingSprays < this.getCapacity(stack);
        }
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        CompoundNBT compound = stack.getTag();
        if (compound != null && compound.contains("RemainingSprays", Constants.NBT.TAG_INT))
        {
            return 1.0 - (compound.getInt("RemainingSprays") / (double) this.getCapacity(stack));
        }
        return 0.0;
    }

    public float getRemainingSprays(ItemStack stack)
    {
        CompoundNBT compound = stack.getTag();
        if (compound != null && compound.contains("RemainingSprays", Constants.NBT.TAG_INT))
        {
            return compound.getInt("RemainingSprays") / (float) this.getCapacity(stack);
        }
        return 0.0F;
    }

    public int getCapacity(ItemStack stack)
    {
        CompoundNBT compound = stack.getTag();
        if (compound != null && compound.contains("Capacity", Constants.NBT.TAG_INT))
        {
            return compound.getInt("Capacity");
        }
        return MAX_SPRAYS;
    }

    public void refill(ItemStack stack)
    {
        CompoundNBT compound = getStackTag(stack);
        compound.putInt("RemainingSprays", this.getCapacity(stack));
    }
}
