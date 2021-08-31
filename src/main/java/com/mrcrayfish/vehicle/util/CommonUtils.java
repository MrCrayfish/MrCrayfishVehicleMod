package com.mrcrayfish.vehicle.util;

import com.mrcrayfish.vehicle.Config;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
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

    public static float yaw(Vector3d vec)
    {
        return (float) (Math.toDegrees(Math.atan2(vec.z, vec.x)) - 90F);
    }

    public static float pitch(Vector3d vec)
    {
        if(vec.normalize().y != 0)
        {
            // Fixes the absolute of the value being slighter greater than 1.0
            double y = MathHelper.clamp(vec.normalize().y, -1.0, 1.0);
            // If abs of y is grater than 1.0, returns NaN when calling Math#asin and crashes world
            return (float) Math.toDegrees(Math.asin(y));
        }
        return 0F;
    }

    public static float yaw(Vector3f vec)
    {
        return yaw(new Vector3d(vec));
    }

    public static float pitch(Vector3f vec)
    {
        return pitch(new Vector3d(vec));
    }

    public static Vector3d lerp(Vector3d start, Vector3d end, float time)
    {
        double x = MathHelper.lerp(time, start.x, end.x);
        double y = MathHelper.lerp(time, start.y, end.y);
        double z = MathHelper.lerp(time, start.z, end.z);
        return new Vector3d(x, y, z);
    }

    public static Vector3d clampSpeed(Vector3d motion)
    {
        return motion.normalize().scale(MathHelper.clamp(motion.length(), 0F, Config.SERVER.globalSpeedLimit.get()));
    }
}
