package com.mrcrayfish.vehicle.common;

import com.mrcrayfish.vehicle.entity.VehicleEntity;
import net.minecraft.entity.EntityType;

import java.util.HashSet;
import java.util.Set;

/**
 * Author: MrCrayfish
 */
public class VehicleRegistry
{
    private static final Set<EntityType<? extends VehicleEntity>> REGISTERED_VEHICLES = new HashSet<>();

    public static void register(EntityType<? extends VehicleEntity> entityType)
    {
        REGISTERED_VEHICLES.add(entityType);
    }

    public static Set<EntityType<? extends VehicleEntity>> getRegisteredVehicles()
    {
        return REGISTERED_VEHICLES;
    }
}
