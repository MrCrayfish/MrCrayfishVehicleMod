package com.mrcrayfish.vehicle.common;

import com.mrcrayfish.vehicle.entity.EngineTier;
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.entity.WheelType;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.item.WheelItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class ItemLookup
{
    private static final Map<WheelType, Item> WHEEL_LOOKUP = new HashMap<>();
    private static final Map<Pair<EngineType, EngineTier>, Item> ENGINE_LOOKUP = new HashMap<>();
    
    private static boolean init;
    
    public static void init()
    {
        if(init) return;
        WHEEL_LOOKUP.put(WheelType.STANDARD, ModItems.STANDARD_WHEEL.get());
        WHEEL_LOOKUP.put(WheelType.SPORTS, ModItems.SPORTS_WHEEL.get());
        WHEEL_LOOKUP.put(WheelType.RACING, ModItems.RACING_WHEEL.get());
        WHEEL_LOOKUP.put(WheelType.OFF_ROAD, ModItems.OFF_ROAD_WHEEL.get());
        WHEEL_LOOKUP.put(WheelType.SNOW, ModItems.SNOW_WHEEL.get());
        WHEEL_LOOKUP.put(WheelType.ALL_TERRAIN, ModItems.ALL_TERRAIN_WHEEL.get());
        WHEEL_LOOKUP.put(WheelType.PLASTIC, ModItems.PLASTIC_WHEEL.get());
        ENGINE_LOOKUP.put(Pair.of(EngineType.SMALL_MOTOR, EngineTier.IRON), ModItems.IRON_SMALL_ENGINE.get());
        ENGINE_LOOKUP.put(Pair.of(EngineType.SMALL_MOTOR, EngineTier.GOLD), ModItems.GOLD_SMALL_ENGINE.get());
        ENGINE_LOOKUP.put(Pair.of(EngineType.SMALL_MOTOR, EngineTier.DIAMOND), ModItems.DIAMOND_SMALL_ENGINE.get());
        ENGINE_LOOKUP.put(Pair.of(EngineType.LARGE_MOTOR, EngineTier.IRON), ModItems.IRON_LARGE_ENGINE.get());
        ENGINE_LOOKUP.put(Pair.of(EngineType.LARGE_MOTOR, EngineTier.GOLD), ModItems.GOLD_LARGE_ENGINE.get());
        ENGINE_LOOKUP.put(Pair.of(EngineType.LARGE_MOTOR, EngineTier.DIAMOND), ModItems.DIAMOND_LARGE_ENGINE.get());
        ENGINE_LOOKUP.put(Pair.of(EngineType.ELECTRIC_MOTOR, EngineTier.IRON), ModItems.IRON_ELECTRIC_ENGINE.get());
        ENGINE_LOOKUP.put(Pair.of(EngineType.ELECTRIC_MOTOR, EngineTier.GOLD), ModItems.GOLD_ELECTRIC_ENGINE.get());
        ENGINE_LOOKUP.put(Pair.of(EngineType.ELECTRIC_MOTOR, EngineTier.DIAMOND), ModItems.DIAMOND_ELECTRIC_ENGINE.get());
        init = true;
    }

    public static ItemStack getWheel(WheelType type, int color)
    {
        ItemStack wheel = new ItemStack(WHEEL_LOOKUP.getOrDefault(type, Items.AIR));
        if(wheel.getItem() instanceof WheelItem)
        {
            WheelItem wheelItem = (WheelItem) wheel.getItem();
            if(color != -1)
            {
                wheelItem.setColor(wheel, color);
            }
            return wheel;
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack getEngine(EngineType type, EngineTier tier)
    {
        return new ItemStack(ENGINE_LOOKUP.getOrDefault(Pair.of(type, tier), Items.AIR));
    }

    public static ItemStack getEngine(PoweredVehicleEntity entity)
    {
        if(entity.hasEngine())
        {
            return new ItemStack(ENGINE_LOOKUP.getOrDefault(Pair.of(entity.getProperties().getEngineType(), entity.getEngineTier()), Items.AIR));
        }
        return ItemStack.EMPTY;
    }
}
