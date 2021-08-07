package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.item.WheelItem;
import net.minecraft.item.ItemStack;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public interface IWheelType
{
    default float getRoadFrictionFactor()
    {
        return 1.0F;
    }

    default float getDirtFrictionFactor()
    {
        return 1.0F;
    }

    default float getSnowFrictionFactor()
    {
        return 1.0F;
    }

    default float getBaseTraction()
    {
        return 1.0F;
    }

    default float getSlideTraction()
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
