package com.mrcrayfish.vehicle.client.raytrace.data;

import com.mrcrayfish.vehicle.client.raytrace.RayTraceFunction;
import com.mrcrayfish.vehicle.client.raytrace.TriangleList;
import net.minecraft.util.math.vector.Matrix4f;

import javax.annotation.Nullable;

/**
 * A raytrace part representing either an item or a box
 */
public abstract class RayTraceData
{
    @Nullable
    private final RayTraceFunction function;

    public RayTraceData(@Nullable RayTraceFunction function)
    {
        this.function = function;
    }

    @Nullable
    public final RayTraceFunction getRayTraceFunction()
    {
        return this.function;
    }

    public abstract TriangleList createTriangleList(Matrix4f matrix);
}
