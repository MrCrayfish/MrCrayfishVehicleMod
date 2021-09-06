package com.mrcrayfish.vehicle.client.raytrace.data;

import com.mrcrayfish.vehicle.client.raytrace.EntityRayTracer;
import com.mrcrayfish.vehicle.client.raytrace.InteractableBox;
import com.mrcrayfish.vehicle.client.raytrace.TriangleList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Matrix4f;

/**
 * Author: MrCrayfish
 */
public class InteractableBoxRayTraceData extends BoxRayTraceData
{
    private final InteractableBox<?> interactableBox;

    public InteractableBoxRayTraceData(InteractableBox<?> interactableBox)
    {
        super(interactableBox.getBoxSupplier().get());
        this.interactableBox = interactableBox;
    }

    @Override
    public AxisAlignedBB getBox()
    {
        return this.interactableBox.getBoxSupplier().get();
    }

    @Override
    public TriangleList createTriangleList(Matrix4f matrix)
    {
        return EntityRayTracer.boxToTriangles(this.getBox());
    }
}
