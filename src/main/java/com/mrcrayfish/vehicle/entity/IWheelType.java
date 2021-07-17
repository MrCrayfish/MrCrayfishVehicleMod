package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.item.WheelItem;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public interface IWheelType
{
    default float getRoadMultiplier()
    {
        return 1.0F;
    }

    default float getDirtMultiplier()
    {
        return 1.0F;
    }

    default float getSnowMultiplier()
    {
        return 1.0F;
    }

    default void applyPhysics(PoweredVehicleEntity vehicle) {}

    static Optional<IWheelType> fromStack(ItemStack stack)
    {
        if(stack.getItem() instanceof WheelItem)
        {
            return Optional.of(((WheelItem) stack.getItem()).getWheelType());
        }
        return Optional.empty();
    }
}
