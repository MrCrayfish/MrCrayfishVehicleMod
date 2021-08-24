package com.mrcrayfish.vehicle.client.render;

import com.mrcrayfish.vehicle.entity.VehicleProperties;
import net.minecraft.entity.EntityType;

/**
 * Author: MrCrayfish
 */
public class CachedVehicle
{
    private final EntityType<?> type;
    private final VehicleProperties properties;
    private final AbstractVehicleRenderer<?> renderer;

    public CachedVehicle(EntityType<?> type)
    {
        this.type = type;
        this.properties = VehicleProperties.get(type);
        this.renderer = VehicleRenderRegistry.getRendererFunction(type);
    }

    public EntityType<?> getType()
    {
        return this.type;
    }

    public VehicleProperties getProperties()
    {
        return this.properties;
    }

    public AbstractVehicleRenderer<?> getRenderer()
    {
        return this.renderer;
    }
}
