package com.mrcrayfish.vehicle.client.raytrace;

import com.mrcrayfish.vehicle.client.raytrace.data.RayTraceData;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Hand;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public class VehicleRayTraceResult extends EntityRayTraceResult
{
    private final RayTraceData data;
    private final double distanceToEyes;
    private final boolean rightClick;

    public VehicleRayTraceResult(VehicleEntity entity, Vector3d hitVec, double distanceToEyes, RayTraceData data, boolean rightClick)
    {
        super(entity, hitVec);
        this.distanceToEyes = distanceToEyes;
        this.data = data;
        this.rightClick = rightClick;
    }

    public RayTraceData getData()
    {
        return this.data;
    }

    public double getDistanceToEyes()
    {
        return this.distanceToEyes;
    }

    public boolean isRightClick()
    {
        return this.rightClick;
    }

    @Nullable
    public Hand performContinuousInteraction()
    {
        return Optional.ofNullable(this.data.getRayTraceFunction()).map(f -> f.apply(EntityRayTracer.instance(), this, Minecraft.getInstance().player)).orElse(null);
    }

    public boolean equalsContinuousInteraction(RayTraceFunction function)
    {
        return function.equals(this.data.getRayTraceFunction());
    }
}
