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
    private static final Map<Class<? extends EntityVehicle>, RenderVehicleWrapper<? extends EntityVehicle, ? extends AbstractRenderVehicle>> renderWrapperMap = new HashMap<>();

    @Nullable
    public static AbstractRenderVehicle<?> getRender(Class<? extends EntityVehicle> clazz)
    {
        RenderVehicleWrapper wrapper = renderWrapperMap.get(clazz);
        return wrapper != null ? wrapper.getRenderVehicle() : null;
    }

    public static void registerRenderWrapper(Class<? extends EntityVehicle> clazz, RenderVehicleWrapper<? extends EntityVehicle, ? extends AbstractRenderVehicle> wrapper)
    {
        renderWrapperMap.put(clazz, wrapper);
    }

    @Nullable
    public static RenderVehicleWrapper<?, ?> getRenderWrapper(Class<? extends EntityVehicle> clazz)
    {
        return renderWrapperMap.get(clazz);
    }
}
