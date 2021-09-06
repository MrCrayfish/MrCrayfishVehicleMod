package com.mrcrayfish.vehicle.client.raytrace;

import com.mrcrayfish.vehicle.client.raytrace.data.RayTraceData;

import java.util.HashMap;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public interface RayTraceTransforms
{
    void load(EntityRayTracer tracer, List<MatrixTransform> transforms, HashMap<RayTraceData, List<MatrixTransform>> parts);
}
