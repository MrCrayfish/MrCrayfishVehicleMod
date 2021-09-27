package com.mrcrayfish.vehicle.client.raytrace.data;

import com.mrcrayfish.vehicle.client.model.ISpecialModel;
import com.mrcrayfish.vehicle.client.raytrace.EntityRayTracer;
import com.mrcrayfish.vehicle.client.raytrace.RayTraceFunction;
import com.mrcrayfish.vehicle.client.raytrace.TriangleList;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class SpecialModelRayTraceData extends RayTraceData
{
    private final ISpecialModel model;

    public SpecialModelRayTraceData(ISpecialModel model)
    {
        this(model, null);
    }

    public SpecialModelRayTraceData(ISpecialModel model, @Nullable RayTraceFunction function)
    {
        super(function);
        this.model = model;
    }

    public ISpecialModel getModel()
    {
        return this.model;
    }

    @Override
    public TriangleList createTriangleList()
    {
        return new TriangleList(EntityRayTracer.createTrianglesFromBakedModel(this.model.getModel(), this.matrix));
    }
}
