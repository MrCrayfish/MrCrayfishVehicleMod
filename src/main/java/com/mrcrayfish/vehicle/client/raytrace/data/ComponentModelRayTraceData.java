package com.mrcrayfish.vehicle.client.raytrace.data;

import com.mrcrayfish.vehicle.client.model.IComplexModel;
import com.mrcrayfish.vehicle.client.raytrace.EntityRayTracer;
import com.mrcrayfish.vehicle.client.raytrace.RayTraceFunction;
import com.mrcrayfish.vehicle.client.raytrace.TriangleList;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class ComponentModelRayTraceData extends RayTraceData
{
    private final IComplexModel model;

    public ComponentModelRayTraceData(IComplexModel model)
    {
        this(model, null);
    }

    public ComponentModelRayTraceData(IComplexModel model, @Nullable RayTraceFunction function)
    {
        super(function);
        this.model = model;
    }

    public IComplexModel getModel()
    {
        return this.model;
    }

    @Override
    public TriangleList createTriangleList()
    {
        return new TriangleList(EntityRayTracer.createTrianglesFromBakedModel(this.model.getBaseModel(), this.matrix));
    }
}
