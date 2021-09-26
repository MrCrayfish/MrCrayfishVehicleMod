package com.mrcrayfish.vehicle.client.raytrace.data;

import com.mrcrayfish.vehicle.client.raytrace.EntityRayTracer;
import com.mrcrayfish.vehicle.client.raytrace.RayTraceFunction;
import com.mrcrayfish.vehicle.client.raytrace.Triangle;
import com.mrcrayfish.vehicle.client.raytrace.TriangleList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Matrix4f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

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
    public TriangleList createTriangleList()
    {
        TriangleList triangleList = EntityRayTracer.boxToTriangles(this.getBox());
        List<Triangle> transformedTriangles = new ArrayList<>();
        for(Triangle triangle : triangleList.getTriangles())
        {
            transformedTriangles.add(new Triangle(EntityRayTracer.getTransformedTriangle(triangle.getVertices(), this.matrix)));
        }
        return new TriangleList(transformedTriangles);
    }
}
