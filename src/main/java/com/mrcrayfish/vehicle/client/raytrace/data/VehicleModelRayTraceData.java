package com.mrcrayfish.vehicle.client.raytrace.data;

import com.mrcrayfish.vehicle.client.model.IVehicleModel;
import com.mrcrayfish.vehicle.client.raytrace.EntityRayTracer;
import com.mrcrayfish.vehicle.client.raytrace.RayTraceFunction;
import com.mrcrayfish.vehicle.client.raytrace.TriangleList;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class VehicleModelRayTraceData extends RayTraceData
{
    private final IVehicleModel model;

    public VehicleModelRayTraceData(IVehicleModel model)
    {
        this(model, null);
    }

    public VehicleModelRayTraceData(IVehicleModel model, @Nullable RayTraceFunction function)
    {
        super(function);
        this.model = model;
    }

    public IVehicleModel getModel()
    {
        return this.model;
    }

    @Override
    public TriangleList createTriangleList()
    {
        return new TriangleList(EntityRayTracer.createTrianglesFromBakedModel(this.model.getModel(), this.matrix));
    }
}
