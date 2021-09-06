package com.mrcrayfish.vehicle.client.raytrace.data;

import com.mrcrayfish.vehicle.client.raytrace.RayTraceFunction;
import com.mrcrayfish.vehicle.client.model.ISpecialModel;
import com.mrcrayfish.vehicle.client.raytrace.EntityRayTracer;
import com.mrcrayfish.vehicle.client.raytrace.TriangleList;
import net.minecraft.util.math.vector.Matrix4f;

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
    public TriangleList createTriangleList(Matrix4f matrix)
    {
        return new TriangleList(EntityRayTracer.trianglesFromBakedModel(this.model.getModel(), matrix));
    }
}
