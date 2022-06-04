package com.mrcrayfish.vehicle.client.raytrace;

import com.mrcrayfish.vehicle.client.raytrace.data.RayTraceData;
import net.minecraft.entity.Entity;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public interface ITriangleList
{
    List<Triangle> getTriangles(RayTraceData data, Entity entity);

    List<Triangle> getTriangles();
}
