package com.mrcrayfish.vehicle.util;

import com.mrcrayfish.vehicle.block.BlockVehicleCrate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.function.BiFunction;

/**
 * Author: MrCrayfish
 */
public class EntityUtil
{
    public static <T extends Entity> EntityType<T> buildVehicleType(ResourceLocation id, BiFunction<EntityType<T>, World, T> function, float width, float height)
    {
        EntityType<T> type = EntityType.Builder.create(function::apply, EntityClassification.MISC).size(width, height).setTrackingRange(256).setUpdateInterval(1).immuneToFire().setShouldReceiveVelocityUpdates(true).build(id.toString());
        BlockVehicleCrate.registerVehicle(id);
        return type;
    }
}
