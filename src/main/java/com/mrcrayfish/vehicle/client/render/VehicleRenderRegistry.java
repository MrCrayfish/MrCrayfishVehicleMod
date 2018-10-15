package com.mrcrayfish.vehicle.client.render;

import com.mrcrayfish.vehicle.entity.EntityVehicle;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public final class VehicleRenderRegistry
{
    private static final Map<Class<? extends EntityVehicle>, AbstractRenderVehicle<? extends EntityVehicle>> renderMap = new HashMap<>();

    public static void registerRender(Class<? extends EntityVehicle> clazz, AbstractRenderVehicle<? extends EntityVehicle> render)
    {
        renderMap.put(clazz, render);
    }

    @Nullable
    public static AbstractRenderVehicle<?> getRender(Class<? extends EntityVehicle> clazz)
    {
        return renderMap.get(clazz);
    }
}
