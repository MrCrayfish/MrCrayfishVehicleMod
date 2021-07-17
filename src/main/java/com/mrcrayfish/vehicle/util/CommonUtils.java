package com.mrcrayfish.vehicle.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants;

/**
 * Author: MrCrayfish
 */
public class CommonUtils
{
    public static CompoundNBT getOrCreateStackTag(ItemStack stack)
    {
        if(stack.getTag() == null)
        {
            stack.setTag(new CompoundNBT());
        }
        return stack.getTag();
    }

    public static void writeItemStackToTag(CompoundNBT compound, String key, ItemStack stack)
    {
        if(!stack.isEmpty())
        {
            compound.put(key, stack.save(new CompoundNBT()));
        }
    }

    public static ItemStack readItemStackFromTag(CompoundNBT compound, String key)
    {
        if(compound.contains(key, Constants.NBT.TAG_COMPOUND))
        {
            return ItemStack.of(compound.getCompound(key));
        }
        return ItemStack.EMPTY;
    }

    public static void sendInfoMessage(PlayerEntity player, String message)
    {
        if(player instanceof ServerPlayerEntity)
        {
            player.displayClientMessage(new TranslationTextComponent(message), true);
        }
    }

    public static boolean isMouseWithin(int mouseX, int mouseY, int x, int y, int width, int height)
    {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }
}
