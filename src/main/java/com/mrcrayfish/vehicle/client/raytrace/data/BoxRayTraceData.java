package com.mrcrayfish.vehicle.client.raytrace.data;

import com.mrcrayfish.vehicle.client.raytrace.RayTraceFunction;
import com.mrcrayfish.vehicle.client.raytrace.EntityRayTracer;
import com.mrcrayfish.vehicle.client.raytrace.TriangleList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Matrix4f;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class BoxRayTraceData extends RayTraceData
{
    private final AxisAlignedBB box;

    public BoxRayTraceData(AxisAlignedBB box)
    {
        this(box, null);
    }

    public BoxRayTraceData(AxisAlignedBB box, @Nullable RayTraceFunction function)
    {
        super(function);
        this.box = box;
    }

    public AxisAlignedBB getBox()
    {
        return this.box;
    }

    @Override
    public TriangleList createTriangleList(Matrix4f matrix)
    {
        return EntityRayTracer.boxToTriangles(this.box);
    }
}
