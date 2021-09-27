package com.mrcrayfish.vehicle.client.raytrace.data;

import com.mrcrayfish.vehicle.client.raytrace.EntityRayTracer;
import com.mrcrayfish.vehicle.client.raytrace.RayTraceFunction;
import com.mrcrayfish.vehicle.client.raytrace.TriangleList;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class ItemStackRayTraceData extends RayTraceData
{
    private final ItemStack stack;

    public ItemStackRayTraceData(ItemStack stack)
    {
        this(stack, null);
    }

    public ItemStackRayTraceData(ItemStack stack, @Nullable RayTraceFunction function)
    {
        super(function);
        this.stack = stack;
    }

    public ItemStack getStack()
    {
        return this.stack;
    }

    @Override
    public TriangleList createTriangleList()
    {
        return new TriangleList(EntityRayTracer.createTrianglesFromBakedModel(RenderUtil.getModel(this.stack), this.matrix));
    }
}
