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
    private static final Map<Class<? extends EntityVehicle>, RenderVehicleWrapper<? extends EntityVehicle, ? extends AbstractRenderVehicle>> renderWrapperMap = new HashMap<>();

    public static void registerRender(Class<? extends EntityVehicle> clazz, AbstractRenderVehicle<? extends EntityVehicle> render)
    {
        renderMap.put(clazz, render);
    }

    @Nullable
    public static AbstractRenderVehicle<?> getRender(Class<? extends EntityVehicle> clazz)
    {
        return renderMap.get(clazz);
    }

    public static void registerRenderWrapper(Class<? extends EntityVehicle> clazz, RenderVehicleWrapper<? extends EntityVehicle, ? extends AbstractRenderVehicle> wrapper)
    {
        renderWrapperMap.put(clazz, wrapper);
        renderMap.put(clazz, wrapper.getRenderVehicle());
    }

    @Nullable
    public static RenderVehicleWrapper<?, ?> getRenderWrapper(Class<? extends EntityVehicle> clazz)
    {
        return renderWrapperMap.get(clazz);
    }
}
