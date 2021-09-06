package com.mrcrayfish.vehicle.client.event;

import com.mrcrayfish.vehicle.client.raytrace.VehicleRayTraceResult;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * Author: MrCrayfish
 */
@Cancelable
public class VehicleRayTraceEvent extends PlayerEvent
{
    private final VehicleRayTraceResult result;

    public VehicleRayTraceEvent(PlayerEntity player, VehicleRayTraceResult result)
    {
        super(player);
        this.result = result;
    }

    public VehicleRayTraceResult getVehicleRayTraceResult()
    {
        return this.result;
    }
}
