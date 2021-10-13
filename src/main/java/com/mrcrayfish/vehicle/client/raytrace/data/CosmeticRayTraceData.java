package com.mrcrayfish.vehicle.client.raytrace.data;

import com.mrcrayfish.vehicle.client.raytrace.EntityRayTracer;
import com.mrcrayfish.vehicle.client.raytrace.RayTraceFunction;
import com.mrcrayfish.vehicle.client.raytrace.TriangleList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class CosmeticRayTraceData extends RayTraceData
{
    private final ResourceLocation modelLocation;

    public CosmeticRayTraceData(ResourceLocation modelLocation)
    {
        this(modelLocation, null);
    }

    public CosmeticRayTraceData(ResourceLocation modelLocation, @Nullable RayTraceFunction function)
    {
        super(function);
        this.modelLocation = modelLocation;
    }

    @Override
    protected TriangleList createTriangleList()
    {
        IBakedModel model = Minecraft.getInstance().getModelManager().getModel(this.modelLocation);
        return new TriangleList(EntityRayTracer.createTrianglesFromBakedModel(model, this.matrix));
    }
}
