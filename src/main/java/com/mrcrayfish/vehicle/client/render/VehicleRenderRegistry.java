package com.mrcrayfish.vehicle.client.render;

import com.mrcrayfish.vehicle.entity.properties.VehicleProperties;
import net.minecraft.entity.EntityType;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public final class VehicleRenderRegistry
{
    private static final Map<EntityType<?>, AbstractVehicleRenderer<?>> RENDERER_MAP = new HashMap<>();
    private static final Map<EntityType<?>, BiFunction<?, VehicleProperties, ?>> RENDERER_FUNCTION_MAP = new HashMap<>();

    public static synchronized void registerVehicleRendererFunction(EntityType<?> type, BiFunction<?, VehicleProperties, ?> rendererFunction, AbstractVehicleRenderer<?> defaultRenderer)
    {
        RENDERER_FUNCTION_MAP.put(type, rendererFunction);
        RENDERER_MAP.put(type, defaultRenderer);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static AbstractVehicleRenderer<?> getRendererFunction(EntityType<?> type)
    {
        VehicleProperties properties = VehicleProperties.get(type);
        BiFunction<EntityType<?>, VehicleProperties, AbstractVehicleRenderer<?>> rendererFunction = (BiFunction<EntityType<?>, VehicleProperties, AbstractVehicleRenderer<?>>) RENDERER_FUNCTION_MAP.get(type);
        return rendererFunction != null ? rendererFunction.apply(type, properties) : null;
    }

    @Nullable
    public static AbstractVehicleRenderer<?> getRenderer(EntityType<?> type)
    {
        return RENDERER_MAP.get(type);
    }
}
