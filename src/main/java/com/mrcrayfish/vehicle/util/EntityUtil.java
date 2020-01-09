package com.mrcrayfish.vehicle.util;

import com.mrcrayfish.vehicle.block.BlockVehicleCrate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public class EntityUtil
{
    public static <T extends Entity> EntityType<T> buildVehicleType(String id, Function<World, T> function, float width, float height)
    {
        EntityType<T> type = EntityType.Builder.<T>create((entityType, world) -> function.apply(world), EntityClassification.MISC).size(width, height).setTrackingRange(256).setUpdateInterval(1).immuneToFire().setShouldReceiveVelocityUpdates(true).setCustomClientFactory((spawnEntity, world) -> function.apply(world)).build(id);
        type.setRegistryName(id);
        BlockVehicleCrate.registerVehicle(id);
        return type;
    }
}
