package com.mrcrayfish.vehicle.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;
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
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        if(Screen.hasShiftDown())
        {
            tooltip.addAll(Minecraft.getInstance().fontRenderer.func_238425_b_(new TranslationTextComponent("item.vehicle.spray_can.info"), 150).stream().map(text -> new StringTextComponent(text.getString())).collect(Collectors.toList()));
        }
        else
        {
            if(this.hasColor(stack))
            {
                tooltip.add(new StringTextComponent(I18n.format("item.color", TextFormatting.DARK_GRAY.toString() + String.format("#%06X", this.getColor(stack)))));
            }
            else
            {
                tooltip.add(new StringTextComponent(I18n.format("item.vehicle.spray_can.empty")));
            }
            tooltip.add(new StringTextComponent(TextFormatting.YELLOW + I18n.format("vehicle.info_help")));
        }
    }

    public static CompoundNBT getStackTag(ItemStack stack)
    {
        if(stack.getTag() == null)
        {
            stack.setTag(new CompoundNBT());
        }
        if(stack.getItem() instanceof SprayCanItem)
        {
            SprayCanItem sprayCan = (SprayCanItem) stack.getItem();
            CompoundNBT compound = stack.getTag();
            if(compound != null)
            {
                if(!compound.contains("RemainingSprays", Constants.NBT.TAG_INT))
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
        if(compound != null && compound.contains("RemainingSprays", Constants.NBT.TAG_INT))
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
        if(compound != null && compound.contains("RemainingSprays", Constants.NBT.TAG_INT))
        {
            return 1.0 - (compound.getInt("RemainingSprays") / (double) this.getCapacity(stack));
        }
        return 0.0;
    }

    public float getRemainingSprays(ItemStack stack)
    {
        CompoundNBT compound = stack.getTag();
        if(compound != null && compound.contains("RemainingSprays", Constants.NBT.TAG_INT))
        {
            return compound.getInt("RemainingSprays") / (float) this.getCapacity(stack);
        }
        return 0.0F;
    }

    public int getCapacity(ItemStack stack)
    {
        CompoundNBT compound = stack.getTag();
        if(compound != null && compound.contains("Capacity", Constants.NBT.TAG_INT))
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
