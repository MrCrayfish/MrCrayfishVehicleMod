package com.mrcrayfish.vehicle.client.render;

import com.mrcrayfish.vehicle.entity.VehicleEntity;
import net.minecraft.entity.EntityType;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public final class VehicleRenderRegistry
{
    private static final Map<EntityType<? extends VehicleEntity>, AbstractVehicleRenderer<? extends VehicleEntity>> renderWrapperMap = new HashMap<>();

    @Nullable
    public static AbstractVehicleRenderer<?> getRender(EntityType<? extends VehicleEntity> type)
    {
        return renderWrapperMap.get(type);
    }

    public static void registerRenderWrapper(EntityType<? extends VehicleEntity> type, AbstractVehicleRenderer<? extends VehicleEntity> renderer)
    {
        renderWrapperMap.put(type, renderer);
    }

    @Nullable
    public static AbstractVehicleRenderer<?> getRenderWrapper(EntityType<? extends VehicleEntity> type)
    {
        return renderWrapperMap.get(type);
    }
}
