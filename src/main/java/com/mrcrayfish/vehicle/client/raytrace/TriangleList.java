package com.mrcrayfish.vehicle.client.raytrace;

import com.google.common.collect.Lists;
import com.mrcrayfish.vehicle.client.raytrace.data.RayTraceData;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Matrix4f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class TriangleList
{
    private final List<Triangle> triangles;
    private final BiFunction<RayTraceData, Entity, Matrix4f> dynamicMatrix;

    public TriangleList(List<Triangle> triangles)
    {
        this(triangles, null);
    }

    /**
     * Constructor for dynamic triangles
     *
     * @param triangles     raytraceable triangle list
     * @param dynamicMatrix function for dynamic triangles that takes the part and the raytraced
     *                      entity as arguments and outputs that part's dynamically generated transformation matrix
     */
    public TriangleList(List<Triangle> triangles, @Nullable BiFunction<RayTraceData, Entity, Matrix4f> dynamicMatrix)
    {
        this.triangles = triangles;
        this.dynamicMatrix = dynamicMatrix;
    }

    /**
     * Gets list of static pre-transformed triangles, or gets a new list of dynamically transformed triangles
     *
     * @param entity raytraced entity
     */
    public List<Triangle> getTriangles(RayTraceData data, Entity entity)
    {
        if(this.dynamicMatrix != null)
        {
            List<Triangle> triangles = new ArrayList<>();
            Matrix4f matrix = this.dynamicMatrix.apply(data, entity);
            for(Triangle triangle : this.triangles)
            {
                triangles.add(new Triangle(EntityRayTracer.getTransformedTriangle(triangle.getVertices(), matrix)));
            }
            return triangles;
        }
        return this.triangles;
    }

    public List<Triangle> getTriangles()
    {
        return this.triangles;
    }
}
