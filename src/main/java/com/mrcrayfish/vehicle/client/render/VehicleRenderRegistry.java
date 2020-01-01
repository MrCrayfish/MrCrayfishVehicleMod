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
    private static final Map<EntityType<? extends VehicleEntity>, RenderVehicleWrapper<? extends VehicleEntity, ? extends AbstractRenderVehicle>> renderWrapperMap = new HashMap<>();

    @Nullable
    public static AbstractRenderVehicle<?> getRender(EntityType<? extends VehicleEntity> type)
    {
        RenderVehicleWrapper wrapper = renderWrapperMap.get(type);
        return wrapper != null ? wrapper.getRenderVehicle() : null;
    }

    public static void registerRenderWrapper(EntityType<? extends VehicleEntity> type, RenderVehicleWrapper<? extends VehicleEntity, ? extends AbstractRenderVehicle> wrapper)
    {
        renderWrapperMap.put(type, wrapper);
    }

    @Nullable
    public static RenderVehicleWrapper<?, ?> getRenderWrapper(EntityType<? extends VehicleEntity> type)
    {
        return renderWrapperMap.get(type);
    }
}
