package com.mrcrayfish.vehicle.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.util.Constants;

/**
 * Author: MrCrayfish
 */
public class CommonUtils
{
    public static NBTTagCompound getItemTagCompound(ItemStack stack)
    {
        if(!stack.hasTagCompound())
        {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }

    public static void writeItemStackToTag(NBTTagCompound parent, String key, ItemStack stack)
    {
        if(!stack.isEmpty())
        {
            NBTTagCompound tag = new NBTTagCompound();
            stack.writeToNBT(tag);
            parent.setTag(key, tag);
        }
    }

    public static ItemStack readItemStackFromTag(NBTTagCompound parent, String key)
    {
        if(parent.hasKey(key, Constants.NBT.TAG_COMPOUND))
        {
            return new ItemStack(parent.getCompoundTag(key));
        }
        return ItemStack.EMPTY;
    }

    public static void sendInfoMessage(EntityPlayer player, String message)
    {
        if(player instanceof EntityPlayerMP)
        {
            ((EntityPlayerMP) player).connection.sendPacket(new SPacketChat(new TextComponentTranslation(message), ChatType.GAME_INFO));
        }
    }
}
